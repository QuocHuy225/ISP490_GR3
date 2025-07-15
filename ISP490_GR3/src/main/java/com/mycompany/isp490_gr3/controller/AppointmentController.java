package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.mycompany.isp490_gr3.dao.DAOAppointment;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAOPatient;
import com.mycompany.isp490_gr3.dao.DAOService;
import com.mycompany.isp490_gr3.dto.AppointmentViewDTO;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.MedicalService;
import com.mycompany.isp490_gr3.model.Patient;
import com.mycompany.isp490_gr3.model.Slot;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AppointmentController", urlPatterns = {"/appointments", "/appointments/add", "/patient/search", "/appointments/remove-patient"})
public class AppointmentController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AppointmentController.class.getName());
    private final Gson gson = new Gson(); // Khởi tạo Gson

    private boolean checkReceptionistAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != User.Role.RECEPTIONIST) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        DAOAppointment daoAppointment = new DAOAppointment();
        daoAppointment.updateNoShowAppointments();
        String path = request.getServletPath();
        if ("/patient/search".equals(path)) {
            handlePatientSearch(request, response);
            return;
        }

        String appointmentCode = request.getParameter("appointmentCode");
        String patientCode = request.getParameter("patientCode");
        String doctorIdStr = request.getParameter("doctorId");
        String servicesIdStr = request.getParameter("servicesId");
        String status = request.getParameter("status");
        String slotDateStr = request.getParameter("slotDate");

        Integer doctorId = null;
        if (doctorIdStr != null && !doctorIdStr.trim().isEmpty()) {
            try {
                doctorId = Integer.parseInt(doctorIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid doctorId format: " + doctorIdStr);
            }
        }

        Integer servicesId = null;
        if (servicesIdStr != null && !servicesIdStr.trim().isEmpty()) {
            try {
                servicesId = Integer.parseInt(servicesIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid servicesId format: " + servicesIdStr);
            }
        }

        LocalDate slotDate = null;
        if (slotDateStr != null && !slotDateStr.trim().isEmpty()) {
            try {
                slotDate = LocalDate.parse(slotDateStr);
            } catch (DateTimeParseException e) {
                LOGGER.warning("Invalid slotDate format: " + slotDateStr);
            }
        }

        if (slotDate == null) {
            slotDate = LocalDate.now();
        }

        int currentPage = 1;
        int recordsPerPage = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        int offset = (currentPage - 1) * recordsPerPage;

        List<AppointmentViewDTO> currentPageAppointments;
        int totalRecords;
        if (appointmentCode == null && patientCode == null && doctorId == null && servicesId == null && status == null
                && (slotDateStr == null || slotDateStr.trim().isEmpty())) {
            LOGGER.info("No search parameters provided, fetching today's appointments");
            currentPageAppointments = daoAppointment.getTodayAppointmentViewDTOs(offset, recordsPerPage);
            totalRecords = daoAppointment.countTodayAppointmentViewDTOs();
        } else {
            LOGGER.info("Fetching appointments with parameters: appointmentCode=" + appointmentCode + ", patientCode=" + patientCode
                    + ", doctorId=" + doctorId + ", servicesId=" + servicesId + ", status=" + status + ", slotDate=" + slotDate);
            currentPageAppointments = daoAppointment.searchAppointmentViewDTOs(
                    appointmentCode, patientCode, doctorId, servicesId, status, slotDate, offset, recordsPerPage);
            totalRecords = daoAppointment.countSearchAppointmentViewDTOs(
                    appointmentCode, patientCode, doctorId, servicesId, status, slotDate);
        }

        int totalPages = totalRecords > 0 ? (int) Math.ceil((double) totalRecords / recordsPerPage) : 1;

        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.findAllDoctors();
        DAOService daoService = new DAOService();
        List<MedicalService> services = daoService.getAllServices();

        request.setAttribute("doctorList", doctors);
        request.setAttribute("serviceList", services);
        request.setAttribute("statusList", List.of("pending", "confirmed", "done", "cancelled"));
        request.setAttribute("currentPageAppointments", currentPageAppointments);
        request.setAttribute("appointmentCode", appointmentCode != null ? appointmentCode : "");
        request.setAttribute("patientCode", patientCode != null ? patientCode : "");
        request.setAttribute("doctorId", doctorIdStr != null ? doctorIdStr : "");
        request.setAttribute("servicesId", servicesIdStr != null ? servicesIdStr : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("slotDate", slotDate != null ? slotDate.toString() : "");
        request.setAttribute("searchPerformed", appointmentCode != null || patientCode != null || doctorId != null
                || servicesId != null || status != null || slotDate != null);
        request.setAttribute("hasResults", !currentPageAppointments.isEmpty());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", offset);
        request.setAttribute("endIndex", Math.min(offset + recordsPerPage, totalRecords));

        request.getRequestDispatcher("/jsp/manageappointment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String path = request.getServletPath();

        switch (path) {
            case "/appointments":
                handleSearch(request, response);
                break;
            case "/appointments/add": {
                try {
                    handleAddAppointment(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(AppointmentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case "/patient/search":
                handlePatientSearch(request, response);
                break;
            case "/appointments/remove-patient": {
                try {
                    handleRemovePatient(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(AppointmentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;

            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(new ResponseJson(false, "Đường dẫn không hợp lệ")));
        }

    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String appointmentCode = request.getParameter("appointmentCode");
        String patientCode = request.getParameter("patientCode");
        String doctorIdStr = request.getParameter("doctorId");
        String servicesIdStr = request.getParameter("servicesId");
        String status = request.getParameter("status");
        String slotDateStr = request.getParameter("slotDate");

        LOGGER.info("Searching with parameters: appointmentCode=" + appointmentCode + ", patientCode=" + patientCode
                + ", doctorId=" + doctorIdStr + ", servicesId=" + servicesIdStr + ", status=" + status + ", slotDate=" + slotDateStr);

        Integer doctorId = null;
        if (doctorIdStr != null && !doctorIdStr.trim().isEmpty()) {
            try {
                doctorId = Integer.parseInt(doctorIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid doctorId format: " + doctorIdStr);
            }
        }

        Integer servicesId = null;
        if (servicesIdStr != null && !servicesIdStr.trim().isEmpty()) {
            try {
                servicesId = Integer.parseInt(servicesIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid servicesId format: " + servicesIdStr);
            }
        }

        LocalDate slotDate = null;
        if (slotDateStr != null && !slotDateStr.trim().isEmpty()) {
            try {
                slotDate = LocalDate.parse(slotDateStr);
            } catch (DateTimeParseException e) {
                LOGGER.warning("Invalid slotDate format: " + slotDateStr);
            }
        }

        // Handle pagination
        int currentPage = 1;
        int recordsPerPage = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        int offset = (currentPage - 1) * recordsPerPage;

        DAOAppointment daoAppointment = new DAOAppointment();
        List<AppointmentViewDTO> currentPageAppointments;
        int totalRecords;

        // If no filters are provided, show today's appointments
        if (appointmentCode == null && patientCode == null && doctorId == null && servicesId == null && status == null && slotDate == null) {
            currentPageAppointments = daoAppointment.getTodayAppointmentViewDTOs(offset, recordsPerPage);
            totalRecords = daoAppointment.countTodayAppointmentViewDTOs();
        } else {
            currentPageAppointments = daoAppointment.searchAppointmentViewDTOs(appointmentCode, patientCode, doctorId, servicesId, status, slotDate, offset, recordsPerPage);
            totalRecords = daoAppointment.countSearchAppointmentViewDTOs(appointmentCode, patientCode, doctorId, servicesId, status, slotDate);
        }

        int totalPages = totalRecords > 0 ? (int) Math.ceil((double) totalRecords / recordsPerPage) : 1;

       
        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.findAllDoctors();
        DAOService daoService = new DAOService();
        List<MedicalService> services = daoService.getAllServices();

        request.setAttribute("doctorList", doctors);
        request.setAttribute("serviceList", services);
        request.setAttribute("statusList", List.of("pending", "confirmed", "done", "cancelled"));
        request.setAttribute("currentPageAppointments", currentPageAppointments);
        request.setAttribute("appointmentCode", appointmentCode != null ? appointmentCode : "");
        request.setAttribute("patientCode", patientCode != null ? patientCode : "");
        request.setAttribute("doctorId", doctorIdStr != null ? doctorIdStr : "");
        request.setAttribute("servicesId", servicesIdStr != null ? servicesIdStr : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("slotDate", slotDate != null ? slotDate.toString() : "");
        request.setAttribute("searchPerformed", true);
        request.setAttribute("hasResults", !currentPageAppointments.isEmpty());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", offset);
        request.setAttribute("endIndex", Math.min(offset + recordsPerPage, totalRecords));

        request.getRequestDispatcher("/jsp/manageappointment.jsp").forward(request, response);
    }

    private void handlePatientSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String code = request.getParameter("code");
            String cccd = request.getParameter("cccd");
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");

            DAOPatient daoPatient = new DAOPatient();
            List<Patient> patients = daoPatient.searchPatients(code, name, phone, cccd);
            out.print(gson.toJson(patients));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching patients: {0}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi tìm kiếm bệnh nhân.");
        }
    }

    private void handleAddAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String action = request.getParameter("action");
            System.out.println("Received action: " + action);

            // Lấy các tham số appointmentId, patientId, serviceId
            String appointmentIdStr = request.getParameter("appointmentId");
            String patientIdStr = request.getParameter("patientId");
            String serviceIdStr = request.getParameter("servicesId");

            System.out.println("Received parameters: serviceId=" + serviceIdStr + ", appointmentId=" + appointmentIdStr + ", patientId=" + patientIdStr);

            // Kiểm tra nếu thiếu tham số
            if (appointmentIdStr == null || patientIdStr == null || serviceIdStr == null) {
                out.write(gson.toJson(new ResponseJson(false, "Thiếu appointmentId hoặc patientId hoặc serviceId")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int appointmentId, patientId, servicesId;

            try {
                appointmentId = Integer.parseInt(appointmentIdStr);
                patientId = Integer.parseInt(patientIdStr);
                servicesId = Integer.parseInt(serviceIdStr);
            } catch (NumberFormatException e) {
                out.write(gson.toJson(new ResponseJson(false, "appointmentId, patientId, hoặc serviceId không hợp lệ")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            DAOAppointment dao = new DAOAppointment();

            //Kiểm tra 6 
            //1.Kiểm tra lịch hẹn có tồn tại không
            if (!dao.exists(appointmentId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ResponseJson(false, "Lịch hẹn không tồn tại")));
                return;
            }

            //2.Lịch đã được gán chưa?
            if (dao.isAssigned(appointmentId)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write(gson.toJson(new ResponseJson(false, "Lịch đã được gán bệnh nhân")));
                return;
            }

            Slot slot = dao.getSlotByAppointmentId(appointmentId);

            //3. Kiểm tra slot chưa bị khóa
            if (slot.isDeleted()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ResponseJson(false, "Slot đã bị xóa, không thể gán bệnh nhân")));
                return;
            }

            //4. Kiểm tra slot chưa kết thúc
            if (slot.getSlotDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(slot.getEndTime())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ResponseJson(false, "Slot đã kết thúc, không thể gán bệnh nhân")));
                return;
            }

            //5.Kiểm tra bệnh nhân có cùng dịch vụ trong ngày không?
            if (dao.hasServiceInSameDay(patientId, servicesId, slot.getSlotDate())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write(gson.toJson(new ResponseJson(false, "Bệnh nhân đã có dịch vụ này trong ngày")));
                return;
            }

            //6.Kiểm tra bệnh nhân chưa vượt quá số lượng lịch hẹn trong ngày
            int countTodayAppointments = dao.countAppointmentsInDateByPatient(patientId, slot.getSlotDate());
            if (countTodayAppointments >= 2) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write(gson.toJson(new ResponseJson(false, "Bệnh nhân đã đặt tối đa 2 lịch trong ngày")));
                return;
            }

            //7. Thực hiện gán
            boolean success = dao.assignPatient(appointmentId, patientId, servicesId);
            if (success) {
                out.write(gson.toJson(new ResponseJson(true, "Gán bệnh nhân thành công")));
            } else {
                out.write(gson.toJson(new ResponseJson(false, "Không thể gán bệnh nhân")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void handleRemovePatient(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {

            String action = request.getParameter("action");
            String appointmentIdStr = request.getParameter("appointmentId");

            System.out.println("Received parameters: " + appointmentIdStr);
            // Kiểm tra tham số
            if (!"removePatient".equals(action) || appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
                out.print(gson.toJson(new ResponseJson(false, "Thiếu hoặc tham số không hợp lệ")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int appointmentId;

            try {
                appointmentId = Integer.parseInt(appointmentIdStr);
            } catch (NumberFormatException e) {
                out.print(gson.toJson(new ResponseJson(false, "appointmentId không hợp lệ")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            DAOAppointment dao = new DAOAppointment();
            Slot slot = dao.getSlotByAppointmentId(appointmentId);

            //Ktra slot chưa quá thời gian bỏ gán
            if (slot.getSlotDate().isEqual(LocalDate.now()) && slot.getStartTime().isBefore(LocalTime.now())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ResponseJson(false, "Slot đã quá thời gian")));
                return;
            }

            // 3. Thực hiện bỏ gán
            boolean success = dao.unassignPatient(appointmentId);
            if (success) {
                out.print(gson.toJson(new ResponseJson(true, "Bỏ gán bệnh nhân thành công")));
            } else {
                out.print(gson.toJson(new ResponseJson(false, "Không thể bỏ gán bệnh nhân")));
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        }

    }

    private static class ResponseJson {

        boolean success;
        String message;

        ResponseJson(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

}
