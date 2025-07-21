package com.mycompany.isp490_gr3.controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.isp490_gr3.dao.DAOAppointment;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAOService;
import com.mycompany.isp490_gr3.dao.DAOSlot;
import com.mycompany.isp490_gr3.model.Appointment;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.MedicalService;
import com.mycompany.isp490_gr3.model.Slot;
import com.mycompany.isp490_gr3.model.User;
import com.mycompany.isp490_gr3.util.LocalDateAdapter;
import com.mycompany.isp490_gr3.util.LocalDateTimeAdapter;
import com.mycompany.isp490_gr3.util.LocalTimeAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.SQLException; // THÊM DÒNG NÀY ĐỂ IMPORT SQLException
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentScheduleController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AppointmentScheduleController.class.getName());
    private Gson gson;
    private DAOAppointment appointmentDAO;
    private DAODoctor doctorDAO;
    private DAOService medicalServiceDAO;
    private DAOSlot slotDAO;

 @Override
public void init() throws ServletException {
    super.init();

    GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
    
    // Đăng ký các bộ chuyển đổi
    gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
    gsonBuilder.registerTypeAdapter(LocalTime.class, new LocalTimeAdapter());
    gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()); 
    
    gson = gsonBuilder.create();

    // Khởi tạo DAO
    appointmentDAO = new DAOAppointment();
    doctorDAO = new DAODoctor();
    medicalServiceDAO = new DAOService();
    slotDAO = new DAOSlot();
}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        if (!checkPatientAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("message", "Đường dẫn API không hợp lệ.")));
                return;
            }

            if (pathInfo.startsWith("/appointments/patient/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 4) {
                    String userAccountId = pathParts[3];
                    Integer patientId = appointmentDAO.getPatientIdByAccountId(userAccountId);
                    if (patientId == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.toJson(Map.of("message", "Không tìm thấy bệnh nhân.")));
                        return;
                    }
                    List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(patientId);
                    out.print(gson.toJson(appointments));
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Map.of("message", "Định dạng đường dẫn không đúng.")));
                }
            } else if (pathInfo.equals("/doctors")) {
                List<Doctor> doctors = doctorDAO.findAllDoctors();
                out.print(gson.toJson(doctors));
                response.setStatus(HttpServletResponse.SC_OK);
            } else if (pathInfo.equals("/services")) {
                List<MedicalService> services = medicalServiceDAO.getAllServices();
                out.print(gson.toJson(services));
                response.setStatus(HttpServletResponse.SC_OK);
            } else if (pathInfo.matches("/doctors/\\d+/available-dates")) {
                String[] pathParts = pathInfo.split("/");
                int doctorId = Integer.parseInt(pathParts[2]);
                List<java.time.LocalDate> availableDates = slotDAO.getAvailableDatesForDoctorNewLogic(doctorId); 
                out.print(gson.toJson(availableDates));
                response.setStatus(HttpServletResponse.SC_OK);
            } else if (pathInfo.equals("/slots/available")) {
                String doctorIdParam = request.getParameter("doctorId");
                String dateParam = request.getParameter("date");
                if (doctorIdParam == null || dateParam == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Map.of("message", "Thiếu tham số 'doctorId' hoặc 'date'.")));
                    return;
                }
                int doctorId = Integer.parseInt(doctorIdParam);
                List<Slot> availableSlots = slotDAO.getAvailableSlotsNewLogic(doctorId, dateParam); 
                out.print(gson.toJson(availableSlots));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("message", "Không tìm thấy tài nguyên API.")));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi trong doGet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("message", "Lỗi máy chủ nội bộ.")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        if (!checkPatientAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();
        try {
            // Xử lý tạo slot và appointment trống (DÀNH CHO LỄ TÂN/ADMIN)
            if (pathInfo != null && pathInfo.equals("/admin/slots/create")) { 
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = request.getReader()) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                String jsonInput = sb.toString();
                Map<String, Object> inputMap = gson.fromJson(jsonInput, Map.class);

                Double doctorIdDouble = (Double) inputMap.get("doctorId");
                String slotDateStr = (String) inputMap.get("slotDate");
                String startTimeStr = (String) inputMap.get("startTime");
                Double durationDouble = (Double) inputMap.get("duration");
                Double maxPatientsDouble = (Double) inputMap.get("maxPatients");
                String type = (String) inputMap.get("type"); 
                String endTimeStr = (String) inputMap.get("endTime"); 

                if (doctorIdDouble == null || slotDateStr == null || startTimeStr == null || durationDouble == null || maxPatientsDouble == null || type == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Map.of("message", "Thiếu tham số bắt buộc để tạo slot.")));
                    return;
                }

                int doctorId = doctorIdDouble.intValue();
                LocalDate slotDate = LocalDate.parse(slotDateStr);
                LocalTime startTime = LocalTime.parse(startTimeStr);
                int duration = durationDouble.intValue();
                int maxPatients = maxPatientsDouble.intValue();

                boolean success = false;
                int insertedCount = 0;

                if ("single".equalsIgnoreCase(type)) {
                    success = slotDAO.createSingleSlotAndEmptyAppointments(doctorId, slotDate, startTime, duration, maxPatients);
                    if(success) insertedCount = 1;
                } else if ("range".equalsIgnoreCase(type)) {
                    if (endTimeStr == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(gson.toJson(Map.of("message", "Thiếu endTime cho việc tạo dải slot.")));
                        return;
                    }
                    insertedCount = slotDAO.createRangeSlotsAndEmptyAppointments(doctorId, slotDate, startTimeStr, endTimeStr, duration, maxPatients);
                    success = (insertedCount > 0);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Map.of("message", "Loại tạo slot không hợp lệ.")));
                    return;
                }

                if (success) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    out.print(gson.toJson(Map.of("message", "Đã tạo " + insertedCount + " slot và các lịch hẹn trống thành công!")));
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print(gson.toJson(Map.of("message", "Không thể tạo slot hoặc lịch hẹn trống. Có thể có xung đột giờ hoặc lỗi database.")));
                }

            } 
            // Xử lý việc bệnh nhân đặt lịch hẹn vào slot đã có (pre-booked appointment)
            else if (pathInfo != null && pathInfo.equals("/appointments")) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = request.getReader()) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                String jsonInput = sb.toString();
                Map<String, Object> inputMap = gson.fromJson(jsonInput, Map.class);

                String userAccountId = (String) inputMap.get("patient_id");
                Double serviceIdDouble = (Double) inputMap.get("service_id");
                Double slotIdDouble = (Double) inputMap.get("slot_id");

                if (userAccountId == null || serviceIdDouble == null || slotIdDouble == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Map.of("message", "Thiếu ID bệnh nhân, ID dịch vụ hoặc ID khung giờ.")));
                    return;
                }

                int serviceId = serviceIdDouble.intValue();
                int slotId = slotIdDouble.intValue();

                Integer patientId = appointmentDAO.getPatientIdByAccountId(userAccountId);
                if (patientId == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(Map.of("message", "Không tìm thấy bệnh nhân liên kết với tài khoản này.")));
                    return;
                }

                Appointment updatedAppointment = null;
                try {
                    updatedAppointment = appointmentDAO.updateEmptyAppointment(patientId, serviceId, slotId);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    out.print(gson.toJson(updatedAppointment));
                } catch (SQLException e) {
                    // CATCH CỤ THỂ SQLException TỪ DAO VÀ TRẢ VỀ THÔNG BÁO LỖI CỦA DAO
                    LOGGER.log(Level.WARNING, "Lỗi khi đặt lịch hẹn từ bệnh nhân: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Sử dụng 400 Bad Request cho lỗi nghiệp vụ
                    out.print(gson.toJson(Map.of("message", e.getMessage()))); // Trả về message từ DAO
                }


            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("message", "Đường dẫn API không hợp lệ cho POST.")));
            }
        } catch (Exception e) { // Bắt các Exception khác không phải SQLException
            LOGGER.log(Level.SEVERE, "Lỗi trong doPost (unhandled): " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("message", "Lỗi máy chủ nội bộ không xác định.")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        if (!checkPatientAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.matches("/appointments/\\d+/cancel")) {
                String[] pathParts = pathInfo.split("/");
                int appointmentId = Integer.parseInt(pathParts[2]);
                boolean success = appointmentDAO.cancelAppointment(appointmentId);
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(Map.of("message", "Lịch hẹn đã được hủy thành công.")));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(Map.of("message", "Không tìm thấy lịch hẹn hoặc không thể hủy.")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("message", "Đường dẫn API không hợp lệ cho PUT.")));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi trong doPut: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("message", "Lỗi máy chủ nội bộ.")));
        } finally {
            out.flush();
        }
    }

    private boolean checkPatientAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Gửi lỗi JSON thay vì redirect để client xử lý
            response.getWriter().print(gson.toJson(Map.of("message", "Yêu cầu đăng nhập.")));
            return false;
        }
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != User.Role.PATIENT && currentUser.getRole() != User.Role.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            // Gửi lỗi JSON thay vì redirect
            response.getWriter().print(gson.toJson(Map.of("message", "Không có quyền truy cập.")));
            return false;
        }
        return true;
    }
}