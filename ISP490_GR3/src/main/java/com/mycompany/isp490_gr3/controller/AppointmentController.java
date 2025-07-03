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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AppointmentController", urlPatterns = {"/appointments", "/appointments/add", "/patient/search"})
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

        DAOAppointment daoAppointment = new DAOAppointment();
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
            case "/appointments/add":
//                handleAddAppointment(request, response);
                break;
            case "/patient/search":
                handlePatientSearch(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
        List<AppointmentViewDTO> currentPageAppointments = daoAppointment.searchAppointmentViewDTOs(
                appointmentCode, patientCode, doctorId, servicesId, status, slotDate, offset, recordsPerPage);
        int totalRecords = daoAppointment.countSearchAppointmentViewDTOs(
                appointmentCode, patientCode, doctorId, servicesId, status, slotDate);
        int totalPages = totalRecords > 0 ? (int) Math.ceil((double) totalRecords / recordsPerPage) : 1;

        LOGGER.info("handleSearch returned " + currentPageAppointments.size() + " appointments, total records: " + totalRecords);

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
//
//    private void handleAddAppointment(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try (Connection conn = YourDataSource.getConnection()) {
//            conn.setAutoCommit(false);
//            long appointmentId = Long.parseLong(request.getParameter("appointmentId"));
//            long patientId = Long.parseLong(request.getParameter("patientId"));
//
//            // Kiểm tra lịch hẹn có tồn tại và trống không
//            String sql = "SELECT patient_id FROM appointments WHERE id = ?";
//            PreparedStatement stmt = conn.prepareStatement(sql);
//            stmt.setLong(1, appointmentId);
//            ResultSet rs = stmt.executeQuery();
//            if (!rs.next()) {
//                conn.rollback();
//                request.getSession().setAttribute("message", "Lịch hẹn không tồn tại.");
//                request.getSession().setAttribute("messageType", "danger");
//                response.sendRedirect(request.getContextPath() + "/appointments");
//                return;
//            }
//            if (!rs.wasNull()) {
//                conn.rollback();
//                request.getSession().setAttribute("message", "Lịch hẹn đã được gán bệnh nhân.");
//                request.getSession().setAttribute("messageType", "danger");
//                response.sendRedirect(request.getContextPath() + "/appointments");
//                return;
//            }
//
//            // Gán bệnh nhân
//            sql = "UPDATE appointments SET patient_id = ?, status = 'pending' WHERE id = ?";
//            stmt = conn.prepareStatement(sql);
//            stmt.setLong(1, patientId);
//            stmt.setLong(2, appointmentId);
//            int rowsAffected = stmt.executeUpdate();
//            if (rowsAffected > 0) {
//                conn.commit();
//                request.getSession().setAttribute("message", "Gán bệnh nhân thành công.");
//                request.getSession().setAttribute("messageType", "success");
//            } else {
//                conn.rollback();
//                request.getSession().setAttribute("message", "Không thể gán bệnh nhân.");
//                request.getSession().setAttribute("messageType", "danger");
//            }
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error assigning patient to appointment", e);
//            request.getSession().setAttribute("message", "Lỗi khi gán bệnh nhân.");
//            request.getSession().setAttribute("messageType", "danger");
//        } catch (NumberFormatException e) {
//            LOGGER.log(Level.SEVERE, "Invalid input parameters", e);
//            request.getSession().setAttribute("message", "Dữ liệu không hợp lệ.");
//            request.getSession().setAttribute("messageType", "danger");
//        }
//        response.sendRedirect(request.getContextPath() + "/appointments");
//    }

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
}