package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.AppointmentDAO;
import com.mycompany.isp490_gr3.dao.DBContext;
import com.mycompany.isp490_gr3.model.Appointment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AppointmentController", urlPatterns = {"/appointments", "/appointments/add", "/appointments/delete"})
public class AppointmentController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AppointmentController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy các tham số lọc từ request
        String appointmentCode = request.getParameter("appointmentCode");
        String patientIdStr = request.getParameter("patientId");
        String doctorIdStr = request.getParameter("doctorId");
        String status = request.getParameter("status");
        String showDeletedStr = request.getParameter("showDeleted");

        // Chuyển đổi và xử lý tham số lọc
        Integer patientId = null;
        if (patientIdStr != null && !patientIdStr.trim().isEmpty()) {
            try {
                patientId = Integer.parseInt(patientIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid patientId parameter: " + patientIdStr, e);
                // Có thể xử lý lỗi ở đây, ví dụ: đặt patientId về null hoặc gửi lỗi về client
            }
        }

        Integer doctorId = null;
        if (doctorIdStr != null && !doctorIdStr.trim().isEmpty()) {
            try {
                doctorId = Integer.parseInt(doctorIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid doctorId parameter: " + doctorIdStr, e);
            }
        }
        
        status = (status != null && !status.trim().isEmpty() && !status.equals("all")) ? status.trim() : null;
        
        Boolean showDeleted = false;
        if (showDeletedStr != null && showDeletedStr.equalsIgnoreCase("true")) {
            showDeleted = true;
        }

        boolean isFormSubmitted = request.getParameter("submitSearch") != null || // Kiểm tra nút tìm kiếm
                                  (appointmentCode != null && !appointmentCode.isEmpty()) ||
                                  patientId != null ||
                                  doctorId != null ||
                                  status != null ||
                                  showDeleted ||
                                  request.getParameter("page") != null ||
                                  request.getParameter("recordsPerPage") != null;

        int currentPage = 1;
        int recordsPerPage = 10;

        Connection conn = null;
        AppointmentDAO dao = null;
        List<Appointment> appointments = new ArrayList<>();
        int totalRecords = 0;
        int totalPages = 0;
        int startIndex = 0;
        int endIndex = 0;

        try {
            if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
                currentPage = Integer.parseInt(request.getParameter("page"));
            }
            if (request.getParameter("recordsPerPage") != null && !request.getParameter("recordsPerPage").isEmpty()) {
                recordsPerPage = Integer.parseInt(request.getParameter("recordsPerPage"));
            }

            conn = DBContext.getConnection();
            dao = new AppointmentDAO(conn);

            // Nếu form đã được gửi hoặc là yêu cầu mặc định, thực hiện tìm kiếm/lấy dữ liệu
            if (isFormSubmitted || request.getParameter("page") != null || request.getParameter("recordsPerPage") != null) {
                totalRecords = dao.getTotalFilteredAppointmentCount(appointmentCode, patientId, doctorId, status, showDeleted);
                startIndex = (currentPage - 1) * recordsPerPage;

                totalPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
                
                if (totalPages == 0 && totalRecords == 0) {
                    currentPage = 1; // Đảm bảo trang hiện tại là 1 nếu không có dữ liệu
                } else if (currentPage > totalPages) {
                    currentPage = totalPages; // Đảm bảo trang hiện tại không vượt quá tổng số trang
                    startIndex = (currentPage - 1) * recordsPerPage;
                }
                
                appointments = dao.getFilteredAppointmentsByPage(appointmentCode, patientId, doctorId, status, showDeleted, startIndex, recordsPerPage);
                
                endIndex = Math.min(startIndex + recordsPerPage, totalRecords);
                if (totalRecords == 0) {
                    startIndex = 0;
                    endIndex = 0;
                }
            } else {
                
            }


            request.setAttribute("currentPageAppointments", appointments);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("recordsPerPage", recordsPerPage);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("startIndex", startIndex);
            request.setAttribute("endIndex", endIndex);

            // Gửi lại các tham số lọc để giữ trạng thái trên form
            request.setAttribute("appointmentCode", appointmentCode != null ? appointmentCode : "");
            request.setAttribute("patientId", patientIdStr != null ? patientIdStr : "");
            request.setAttribute("doctorId", doctorIdStr != null ? doctorIdStr : "");
            request.setAttribute("status", status != null ? status : "");
            request.setAttribute("showDeleted", showDeleted);
            request.setAttribute("shouldShowTable", isFormSubmitted && !appointments.isEmpty());
            // Biến này quan trọng để JavaScript biết liệu một tìm kiếm đã được thực hiện hay chưa
            request.setAttribute("searchPerformed", isFormSubmitted);


            request.getRequestDispatcher("/jsp/manageappointment.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid pagination or filter parameters: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tham số không hợp lệ.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database access error: " + e.getMessage(), e);
            throw new ServletException("Lỗi khi xử lý lịch hẹn: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing database connection: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();       
        String currentUserUserId = "user_admin_001"; 

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            AppointmentDAO dao = new AppointmentDAO(conn);

            if ("addAppointment".equals(action)) {
                // Xử lý thêm lịch hẹn mới từ modal
                String appointmentCode = request.getParameter("appointmentCode");
                String patientIdStr = request.getParameter("patientId");
                String doctorIdStr = request.getParameter("doctorId");
                String slotIdStr = request.getParameter("slotId");
                String status = request.getParameter("status");

                // Chuyển đổi sang kiểu dữ liệu phù hợp
                int patientId = Integer.parseInt(patientIdStr);
                Integer doctorId = null;
                if (doctorIdStr != null && !doctorIdStr.isEmpty()) {
                    doctorId = Integer.parseInt(doctorIdStr);
                }
                Integer slotId = null;
                if (slotIdStr != null && !slotIdStr.isEmpty()) {
                    slotId = Integer.parseInt(slotIdStr);
                }
                if (status == null || status.isEmpty()) {
                    status = "pending"; // Mặc định trạng thái nếu không có
                }

                Appointment newApp = new Appointment();
                newApp.setAppointmentCode(appointmentCode);
                newApp.setPatientId(patientId);
                newApp.setDoctorId(doctorId);
                newApp.setSlotId(slotId);
                newApp.setStatus(status);
                newApp.setCreatedBy(currentUserUserId);
                newApp.setUpdatedBy(currentUserUserId);
                newApp.setIsDeleted(false);

                boolean success = dao.addAppointment(newApp);

                if (success) {
                    session.setAttribute("message", "Thêm lịch hẹn mới thành công!");
                    session.setAttribute("messageType", "success");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                } else {
                    session.setAttribute("message", "Thêm lịch hẹn thất bại. Vui lòng kiểm tra lại thông tin.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                }

            } else if ("deleteMultiple".equals(action)) {
                String[] appointmentIdsArray = request.getParameterValues("selectedAppointments");

                if (appointmentIdsArray == null || appointmentIdsArray.length == 0) {
                    session.setAttribute("message", "Vui lòng chọn ít nhất một lịch hẹn để xóa.");
                    session.setAttribute("messageType", "warning");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }

                List<Integer> appointmentIdsToDelete = new ArrayList<>();
                for (String idStr : appointmentIdsArray) {
                    try {
                        appointmentIdsToDelete.add(Integer.parseInt(idStr));
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Invalid appointment ID in deletion list: " + idStr, e);
                        session.setAttribute("message", "Có ID lịch hẹn không hợp lệ trong danh sách xóa.");
                        session.setAttribute("messageType", "danger");
                        response.sendRedirect(request.getContextPath() + "/appointments");
                        return;
                    }
                }

                boolean success = dao.softDeleteMultipleAppointments(appointmentIdsToDelete, currentUserUserId);

                if (success) {
                    session.setAttribute("message", "Xóa mềm lịch hẹn đã chọn thành công (" + appointmentIdsToDelete.size() + " bản ghi).");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Không tìm thấy lịch hẹn nào để xóa hoặc có lỗi xảy ra.");
                    session.setAttribute("messageType", "danger");
                }
                response.sendRedirect(request.getContextPath() + "/appointments");

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi CSDL trong POST: " + e.getMessage(), e);
            session.setAttribute("message", "Lỗi CSDL: " + e.getMessage());
            session.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/appointments");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Lỗi định dạng số khi xử lý dữ liệu form: " + e.getMessage(), e);
            session.setAttribute("message", "Lỗi định dạng số trong dữ liệu. Vui lòng kiểm tra lại.");
            session.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/appointments");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Lỗi đóng kết nối CSDL: " + e.getMessage(), e);
                }
            }
        }
    }
}