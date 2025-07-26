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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AppointmentController", urlPatterns = {"/appointments", "/appointments/add", "/patient/search", "/appointments/remove-patient", "/appointments/delete"})
public class AppointmentController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AppointmentController.class.getName());
    private final Gson gson = new Gson();

    private boolean checkReceptionistAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String path = request.getServletPath();
        if ("/patient/search".equals(path)) {
            handlePatientSearch(request, response);
            return;
        }

        // Lấy tham số từ request
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

        boolean hasSearchParams = (appointmentCode != null && !appointmentCode.trim().isEmpty())
                || (patientCode != null && !patientCode.trim().isEmpty())
                || doctorId != null || servicesId != null
                || (status != null && !status.trim().isEmpty())
                || slotDate != null;

        int currentPage = 1;
        int recordsPerPage = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid page format: " + pageParam);
                currentPage = 1;
            }
        }
        String recordsPerPageParam = request.getParameter("recordsPerPage");
        if (recordsPerPageParam != null) {
            try {
                recordsPerPage = Integer.parseInt(recordsPerPageParam);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid recordsPerPage format: " + recordsPerPageParam);
            }
        }
        int offset = (currentPage - 1) * recordsPerPage;

        DAOAppointment daoAppointment = new DAOAppointment();
        daoAppointment.updateNoShowAppointments();
        List<AppointmentViewDTO> currentPageAppointments;
        int totalRecords;
        if (!hasSearchParams) {
            LOGGER.info("No search parameters provided, fetching all appointments");
            currentPageAppointments = daoAppointment.searchAppointmentViewDTOs(null, null, null, null, null, null, offset, recordsPerPage);
            totalRecords = daoAppointment.countSearchAppointmentViewDTOs(null, null, null, null, null, null);
        } else {
            LOGGER.info("Fetching appointments with parameters: appointmentCode=" + appointmentCode
                    + ", patientCode=" + patientCode + ", doctorId=" + doctorId
                    + ", servicesId=" + servicesId + ", status=" + status
                    + ", slotDate=" + slotDate);
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
        request.setAttribute("statusList", List.of("pending", "confirmed", "done", "cancelled", "no_show", "expired"));
        request.setAttribute("currentPageAppointments", currentPageAppointments);
        request.setAttribute("appointmentCode", appointmentCode != null ? appointmentCode : "");
        request.setAttribute("patientCode", patientCode != null ? patientCode : "");
        request.setAttribute("doctorId", doctorIdStr != null ? doctorIdStr : "");
        request.setAttribute("servicesId", servicesIdStr != null ? servicesIdStr : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("slotDate", slotDate != null ? slotDate.toString() : "");
        request.setAttribute("searchPerformed", hasSearchParams);
        request.setAttribute("hasResults", !currentPageAppointments.isEmpty());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", offset);
        request.setAttribute("endIndex", Math.min(offset + recordsPerPage, totalRecords));

        LOGGER.info("doGet: totalRecords=" + totalRecords + ", totalPages=" + totalPages + ", currentPage=" + currentPage + ", offset=" + offset);

        request.getRequestDispatcher("/jsp/manageappointment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String path = request.getServletPath();
        String action = request.getParameter("action");

        LOGGER.info("doPost: path=" + path + ", action=" + action);

        switch (path) {
            case "/appointments/add":
                try {
                handleAddAppointment(request, response);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error adding appointment: " + ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi thêm lịch hẹn.");
            }
            break;
            case "/patient/search":
                handlePatientSearch(request, response);
                break;
            case "/appointments/remove-patient":
                try {
                handleRemovePatient(request, response);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error removing patient: " + ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi bỏ gán bệnh nhân.");
            }
            break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Đường dẫn không hợp lệ.");
        }
    }

    private void handlePatientSearch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            LOGGER.log(Level.SEVERE, "Error searching patients: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi tìm kiếm bệnh nhân.");
        }
    }

    private void handleAddAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String action = request.getParameter("action");
            String appointmentIdStr = request.getParameter("appointmentId");
            String patientIdStr = request.getParameter("patientId");
            String serviceIdStr = request.getParameter("servicesId");
            boolean ignoreWarning = "true".equals(request.getParameter("ignoreWarning"));  // Thêm param để ignore warning

            if (appointmentIdStr == null || patientIdStr == null || serviceIdStr == null) {
                out.print(gson.toJson(new ResponseJson(false, "Thiếu appointmentId hoặc patientId hoặc serviceId")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int appointmentId, patientId, servicesId;
            try {
                appointmentId = Integer.parseInt(appointmentIdStr);
                patientId = Integer.parseInt(patientIdStr);
                servicesId = Integer.parseInt(serviceIdStr);
            } catch (NumberFormatException e) {
                out.print(gson.toJson(new ResponseJson(false, "appointmentId, patientId, hoặc serviceId không hợp lệ")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            DAOAppointment dao = new DAOAppointment();

            if (!dao.exists(appointmentId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ResponseJson(false, "Lịch hẹn không tồn tại")));
                return;
            }

            if (dao.isAssigned(appointmentId)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print(gson.toJson(new ResponseJson(false, "Lịch đã được gán bệnh nhân")));
                return;
            }

            Slot slot = dao.getSlotByAppointmentId(appointmentId);
            if (slot.isDeleted()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ResponseJson(false, "Slot đã bị xóa, không thể gán bệnh nhân")));
                return;
            }

            if (slot.getSlotDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(slot.getEndTime())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ResponseJson(false, "Slot đã kết thúc, không thể gán bệnh nhân")));
                return;
            }

            // Kiểm tra cùng dịch vụ trong ngày
            boolean hasSameService = dao.hasServiceInSameDay(patientId, servicesId, slot.getSlotDate());
            if (hasSameService && !ignoreWarning) {
                out.print(gson.toJson(new ResponseJson(false, "Bệnh nhân đã có dịch vụ này trong ngày. Bạn có muốn tiếp tục gán không?", "warning")));
                response.setStatus(HttpServletResponse.SC_OK);  // Trả 200 để UI handle warning
                return;
            }

            // Kiểm tra số lịch trong ngày
            int countTodayAppointments = dao.countAppointmentsInDateByPatient(patientId, slot.getSlotDate());
            if (countTodayAppointments >= 2 && !ignoreWarning) {
                out.print(gson.toJson(new ResponseJson(false, "Bệnh nhân đã đặt tối đa 2 lịch trong ngày. Bạn có muốn tiếp tục gán không?", "warning")));
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            // Nếu ignoreWarning=true hoặc không vi phạm, gán bình thường
            boolean success = dao.assignPatient(appointmentId, patientId, servicesId);
            if (success) {
                // Log override nếu ignoreWarning=true
                if (ignoreWarning) {
                    LOGGER.info("Override warning for appointmentId=" + appointmentId + ", patientId=" + patientId + " by receptionist");
                    // Optional: Lưu log override vào DB nếu cần audit
                }
                out.print(gson.toJson(new ResponseJson(true, "Gán bệnh nhân thành công")));
            } else {
                out.print(gson.toJson(new ResponseJson(false, "Không thể gán bệnh nhân")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void handleRemovePatient(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String action = request.getParameter("action");
            String appointmentIdStr = request.getParameter("appointmentId");

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

            if (slot.getSlotDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(slot.getEndTime())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ResponseJson(false, "Slot đã quá thời gian")));
                return;
            }

            boolean isCheckedIn = dao.isCheckedIn(appointmentId);
            if (isCheckedIn) {
                out.print(gson.toJson(new ResponseJson(false, "Không thể bỏ gán vì lịch hẹn đã được check-in")));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            boolean success = dao.unassignPatient(appointmentId);
            if (success) {
                out.print(gson.toJson(new ResponseJson(true, "Bỏ gán bệnh nhân thành công")));
            } else {
                out.print(gson.toJson(new ResponseJson(false, "Không thể bỏ gán bệnh nhân")));
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    // Cập nhật class ResponseJson để thêm type
    private static class ResponseJson {

        boolean success;
        String message;
        String type;  // Thêm type để UI biết là warning hay error

        ResponseJson(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        ResponseJson(boolean success, String message, String type) {
            this.success = success;
            this.message = message;
            this.type = type;
        }
    }
}
