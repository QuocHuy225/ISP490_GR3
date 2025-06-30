package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAOAppointment;
import com.mycompany.isp490_gr3.dao.DAOService;
import com.mycompany.isp490_gr3.dto.AppointmentViewDTO;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.MedicalService;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AppointmentController", urlPatterns = {"/appointments", "/appointments/delete"})
public class AppointmentController extends HttpServlet {

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
        // quyền + setup offset, limit
        // gọi dao.getAppointmentViewDTOs(offset, limit)
        // gọi dao.countAllAppointments()
        // set attributes + forward manageappointment.jsp

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        int currentPage = 1;
        int recordsPerPage = 10;
        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        int offset = (currentPage - 1) * recordsPerPage;

        DAOAppointment dao = new DAOAppointment();
        List<AppointmentViewDTO> appointments = dao.getAppointmentViewDTOs(offset, recordsPerPage);
        int totalRecords = dao.countAppointmentViewDTOs();  // đếm tổng số record
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.getAllDoctors();

        DAOService daoService = new DAOService();
        List<MedicalService> services = daoService.getAllServices();

        request.setAttribute("doctorList", doctors);
        request.setAttribute("serviceList", services);
        request.setAttribute("statusList", List.of("pending", "confirmed", "done", "cancelled"));

        request.setAttribute("currentPageAppointments", appointments);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", offset);
        request.setAttribute("endIndex", Math.min(offset + recordsPerPage, totalRecords));

        // Đặt cờ để JSP không hiển thị "Kết quả tìm kiếm"
        request.setAttribute("searchPerformed", false);
        request.setAttribute("hasResults", !appointments.isEmpty());
        request.getRequestDispatcher("/jsp/manageappointment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String path = request.getServletPath();

        switch (path) {
            case "/appointments":
                handleSearch(request, response);
                break;
            case "/appointments/delete":
                handleDelete(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException, ServletException {

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String appointmentCode = request.getParameter("appointmentCode");
        String slotDate = request.getParameter("slotDate");
        String patientCode = request.getParameter("patientCode");
        String doctorIdStr = request.getParameter("doctorId");
        String servicesIdStr = request.getParameter("servicesId");
        String status = request.getParameter("status");

        System.out.println("appointmentCode = " + appointmentCode);
        System.out.println("slotDate = " + slotDate);
        System.out.println("patientCode = " + patientCode);
        System.out.println("doctorId = " + doctorIdStr);
        System.out.println("servicesId = " + servicesIdStr);
        System.out.println("status = " + status);

        Integer doctorId = null;
        if (doctorIdStr != null && !doctorIdStr.isBlank()) {
            try {
                doctorId = Integer.parseInt(doctorIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid doctorId: " + doctorIdStr);
            }
        }

        Integer servicesId = null;
        if (servicesIdStr != null && !servicesIdStr.isBlank()) {
            try {
                servicesId = Integer.parseInt(servicesIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid servicesId: " + servicesIdStr);
            }
        }

        int currentPage = 1;
        int recordsPerPage = 10;
        int offset = (currentPage - 1) * recordsPerPage;

        DAOAppointment dao = new DAOAppointment();
        List<AppointmentViewDTO> appointments = dao.searchAppointmentViewDTOs(appointmentCode, slotDate, patientCode, doctorId, servicesId, status, offset, recordsPerPage);
        System.out.println("Tìm thấy: " + appointments.size() + " kết quả");

        int totalRecords = dao.countSearchAppointmentViewDTOs(appointmentCode, slotDate, patientCode, doctorId, servicesId, status);

        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        // doctor + service list
        DAODoctor daoDoctor = new DAODoctor();
        DAOService daoService = new DAOService();
        List<Doctor> doctors = daoDoctor.getAllDoctors();
        List<MedicalService> services = daoService.getAllServices();

        // truyền thuộc tính về JSP
        request.setAttribute("doctorList", doctors);
        request.setAttribute("serviceList", services);
        request.setAttribute("statusList", List.of("pending", "confirmed", "done", "cancelled"));

        request.setAttribute("currentPageAppointments", appointments);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalRecords", totalRecords);

        request.setAttribute("appointmentCode", appointmentCode);
        request.setAttribute("slotDate", slotDate);
        request.setAttribute("patientCode", patientCode);
        request.setAttribute("doctorId", doctorIdStr);
        request.setAttribute("servicesId", servicesIdStr);
        request.setAttribute("status", status);
        request.setAttribute("searchPerformed", true);
        request.setAttribute("hasResults", !appointments.isEmpty());
        request.setAttribute("startIndex", offset);
        request.setAttribute("endIndex", Math.min(offset + recordsPerPage, totalRecords));

        request.getRequestDispatcher("/jsp/manageappointment.jsp").forward(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        DAOAppointment dao = new DAOAppointment();

        String singleIdStr = request.getParameter("appointmentId"); // Xóa 1
        String[] multiIds = request.getParameterValues("appointmentIds"); // Xóa nhiều

        try {
            if (singleIdStr != null && !singleIdStr.isBlank()) {
                int appointmentId = Integer.parseInt(singleIdStr);
                boolean deleted = dao.deleteAppointmentById(appointmentId);

                if (deleted) {
                    session.setAttribute("message", "Xóa lịch hẹn thành công.");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Chỉ có thể xóa lịch hẹn ở trạng thái pending hoặc confirmed và chưa quá thời gian khám.");
                    session.setAttribute("messageType", "warning");
                }

            } else if (multiIds != null && multiIds.length > 0) {
                List<Integer> ids = new ArrayList<>();
                for (String idStr : multiIds) {
                    try {
                        ids.add(Integer.parseInt(idStr));
                    } catch (NumberFormatException e) {
                        LOGGER.warning("ID không hợp lệ: " + idStr);
                    }
                }

                int deletedCount = dao.deleteAppointmentsByIds(ids);
                if (deletedCount > 0) {
                    session.setAttribute("message", "Đã xóa " + deletedCount + " lịch hẹn thành công.");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Không có lịch hẹn nào đủ điều kiện để xóa.");
                    session.setAttribute("messageType", "warning");
                }

            } else {
                session.setAttribute("message", "Không có lịch hẹn nào được chọn để xóa.");
                session.setAttribute("messageType", "warning");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xử lý xóa lịch hẹn: " + e.getMessage(), e);
            session.setAttribute("message", "Đã xảy ra lỗi khi xóa lịch hẹn.");
            session.setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/appointments");
    }

}

