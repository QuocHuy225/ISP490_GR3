/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOAppointment;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAOService;
import com.mycompany.isp490_gr3.dto.AppointmentViewDTO;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.MedicalService;
import com.mycompany.isp490_gr3.model.Slot;
import com.mycompany.isp490_gr3.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FPT SHOP
 */
@WebServlet(name = "CheckinController", urlPatterns = {"/checkin", "/checkin/confirm"})
public class CheckinController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AppointmentController.class.getName());

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

            currentPageAppointments = daoAppointment.getTodayCheckinAppointments(offset, recordsPerPage);
            totalRecords = daoAppointment.countTodayCheckinAppointments();

        } else {
            LOGGER.info("Fetching appointments with parameters: appointmentCode=" + appointmentCode + ", patientCode=" + patientCode
                    + ", doctorId=" + doctorId + ", servicesId=" + servicesId + ", status=" + status + ", slotDate=" + slotDate);
            currentPageAppointments = daoAppointment.searchCheckinAppointments(appointmentCode, patientCode, doctorId, servicesId, offset, recordsPerPage);
            totalRecords = daoAppointment.countCheckinAppointments(appointmentCode, patientCode, doctorId, servicesId);
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

        request.getRequestDispatcher("/jsp/checkin.jsp").forward(request, response);
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
            case "/checkin":
                handleSearch(request, response);
                break;
            case "/checkin/confirm":
                handleCheckin(request, response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

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

       
        if (appointmentCode == null && patientCode == null && doctorId == null && servicesId == null && status == null && slotDate == null) {
            currentPageAppointments = daoAppointment.getTodayCheckinAppointments(offset, recordsPerPage);
            totalRecords = daoAppointment.countTodayCheckinAppointments();
        } else {
            currentPageAppointments = daoAppointment.searchCheckinAppointments(appointmentCode, patientCode, doctorId, servicesId, offset, recordsPerPage);
            totalRecords = daoAppointment.countCheckinAppointments(appointmentCode, patientCode, doctorId, servicesId);
        }

        int totalPages = totalRecords > 0 ? (int) Math.ceil((double) totalRecords / recordsPerPage) : 1;

        // Set attributes for the view
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

        request.getRequestDispatcher("/jsp/checkin.jsp").forward(request, response);
    }

    private void handleCheckin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println(">>> ĐÃ GỌI HANDLE CHECKIN <<<");

        HttpSession session = request.getSession();

        try {
            String appointmentIdStr = request.getParameter("appointmentId");
            String priorityStr = request.getParameter("priority");

            System.out.println("DEBUG: appointmentIdStr = " + appointmentIdStr);

            if (appointmentIdStr == null || appointmentIdStr.isBlank()) {
                session.setAttribute("message", "Thiếu thông tin cần thiết để check-in.");
                session.setAttribute("messageType", "warning");
                response.sendRedirect(request.getContextPath() + "/checkin");
                return;
            }

            int appointmentId = Integer.parseInt(appointmentIdStr);

            // Nếu không có độ ưu tiên, mặc định = 0
            int priority = "High".equalsIgnoreCase(priorityStr) ? 1 : 0;

            String description = request.getParameter("note");
            if (description != null) {
                description = description.trim();
                if (description.length() > 500) {
                    description = description.substring(0, 500);
                }
            }

            DAOAppointment daoAppointment = new DAOAppointment();
            Slot slot = daoAppointment.getSlotByAppointmentId(appointmentId);

            
            if (slot.getSlotDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(slot.getEndTime())) {
               session.setAttribute("message", "Quá thời gian check-in");
                session.setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/checkin");
                return; 
            }
         

            boolean success = daoAppointment.checkinAppointment(appointmentId, priority, description);

            if (success) {
                session.setAttribute("message", "Check-in thành công!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Đã có lỗi khi thực hiện check-in.");
                session.setAttribute("messageType", "danger");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "Đã có lỗi xảy ra khi xử lý dữ liệu.");
            session.setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/checkin");
    }

}
