package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.AppointmentDAO;
import com.mycompany.isp490_gr3.dao.DBContext;
import com.mycompany.isp490_gr3.model.User; // Import this if not already present
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

        boolean isSearchTriggered = request.getParameter("submitSearch") != null
                || request.getParameter("page") != null
                || request.getParameter("recordsPerPage") != null;

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

            if (isSearchTriggered) {
                totalRecords = dao.getTotalFilteredAppointmentCount(appointmentCode, patientId, doctorId, status, showDeleted);
                startIndex = (currentPage - 1) * recordsPerPage;

                totalPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);

                if (totalPages == 0 && totalRecords == 0) {
                    currentPage = 1;
                } else if (currentPage > totalPages) {
                    currentPage = totalPages;
                    startIndex = (currentPage - 1) * recordsPerPage;
                }

                appointments = dao.getFilteredAppointmentsByPage(appointmentCode, patientId, doctorId, status, showDeleted, startIndex, recordsPerPage);

                endIndex = Math.min(startIndex + recordsPerPage, totalRecords);
                if (totalRecords == 0) {
                    startIndex = 0;
                    endIndex = 0;
                }
            } else {
                // Tải trang lần đầu mà không có tìm kiếm hay phân trang cụ thể.
                // 'appointments' vẫn trống, totalRecords = 0, totalPages = 0.
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

            request.setAttribute("searchPerformed", isSearchTriggered);
            request.setAttribute("hasResults", !appointments.isEmpty());

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
        String currentUserUserId = "user_admin_001"; // Thay thế bằng logic lấy user ID thật từ session của bạn
        /*
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login"); // Chuyển hướng về trang login nếu chưa đăng nhập
            return;
        }
        String currentUserUserId = currentUser.getId(); // Giả định User.getId() trả về String
         */

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            AppointmentDAO dao = new AppointmentDAO(conn);

            if ("addAppointment".equals(action)) {
                String appointmentCode = request.getParameter("appointmentCode");
                String patientIdStr = request.getParameter("patientId");
                String doctorIdStr = request.getParameter("doctorId");
                String slotIdStr = request.getParameter("slotId");
                String status = request.getParameter("status");

                // Kiểm tra và xác thực đầu vào
                if (patientIdStr == null || patientIdStr.trim().isEmpty() || doctorIdStr == null || doctorIdStr.trim().isEmpty() || slotIdStr == null || slotIdStr.trim().isEmpty()) {
                    session.setAttribute("message", "Vui lòng điền đầy đủ Mã bệnh nhân, Mã bác sĩ và Mã Slot.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }

                int patientId = Integer.parseInt(patientIdStr);
                Integer doctorId = Integer.parseInt(doctorIdStr);
                Integer slotId = Integer.parseInt(slotIdStr);

                // Kiểm tra sự tồn tại của các khóa ngoại
                if (!dao.doesPatientExist(patientId)) {
                    session.setAttribute("message", "Mã bệnh nhân không tồn tại. Vui lòng kiểm tra lại.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }
                if (!dao.doesDoctorExist(doctorId)) {
                    session.setAttribute("message", "Mã bác sĩ không tồn tại. Vui lòng kiểm tra lại.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }
                if (!dao.doesSlotExist(slotId)) {
                    session.setAttribute("message", "Mã Slot không tồn tại. Vui lòng kiểm tra lại.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }

                if (status == null || status.isEmpty()) {
                    status = "pending";
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
                } else {
                    session.setAttribute("message", "Thêm lịch hẹn thất bại. Vui lòng kiểm tra lại thông tin.");
                    session.setAttribute("messageType", "danger");
                }
                response.sendRedirect(request.getContextPath() + "/appointments");

            } else if ("updateAppointment".equals(action)) {
                String idStr = request.getParameter("id");
                String patientIdStr = request.getParameter("patientId");
                String doctorIdStr = request.getParameter("doctorId");
                String slotIdStr = request.getParameter("slotId");
                String status = request.getParameter("status");

                // Kiểm tra xem các ID bắt buộc có được cung cấp hay không
                if (idStr == null || idStr.isEmpty() || patientIdStr == null || patientIdStr.trim().isEmpty() || doctorIdStr == null || doctorIdStr.trim().isEmpty() || slotIdStr == null || slotIdStr.trim().isEmpty()) {
                    session.setAttribute("message", "Thông tin lịch hẹn cập nhật không đầy đủ.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }

                int id = Integer.parseInt(idStr);

                // Lấy appointmentCode hiện tại từ database
                Appointment existingApp = dao.getAppointmentById(id);
                if (existingApp == null) {
                    session.setAttribute("message", "Không tìm thấy lịch hẹn để cập nhật (ID: " + id + ").");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }
                String currentAppointmentCode = existingApp.getAppointmentCode(); // Lấy mã lịch hẹn hiện tại

                int patientId = Integer.parseInt(patientIdStr);
                Integer doctorId = Integer.parseInt(doctorIdStr);
                Integer slotId = Integer.parseInt(slotIdStr);

                // Kiểm tra sự tồn tại của các khóa ngoại
                if (!dao.doesPatientExist(patientId)) {
                    session.setAttribute("message", "Mã bệnh nhân không tồn tại. Vui lòng kiểm tra lại.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }
                if (!dao.doesDoctorExist(doctorId)) {
                    session.setAttribute("message", "Mã bác sĩ không tồn tại. Vui lòng kiểm tra lại.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }
                if (!dao.doesSlotExist(slotId)) {
                    session.setAttribute("message", "Mã Slot không tồn tại. Vui lòng kiểm tra lại.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }

                if (status == null || status.isEmpty()) {
                    status = "pending";
                }

                Appointment updatedApp = new Appointment();
                updatedApp.setId(id);
                updatedApp.setAppointmentCode(currentAppointmentCode); // Sử dụng mã lịch hẹn từ DB, không phải từ form
                updatedApp.setPatientId(patientId);
                updatedApp.setDoctorId(doctorId);
                updatedApp.setSlotId(slotId);
                updatedApp.setStatus(status);
                updatedApp.setUpdatedBy(currentUserUserId); // Set người cập nhật
                updatedApp.setIsDeleted(existingApp.isIsDeleted()); // Giữ nguyên trạng thái is_deleted

                boolean success = dao.updateAppointment(updatedApp);

                if (success) {
                    session.setAttribute("message", "Cập nhật lịch hẹn thành công!");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Cập nhật lịch hẹn thất bại. Vui lòng kiểm tra lại thông tin.");
                    session.setAttribute("messageType", "danger");
                }
                response.sendRedirect(request.getContextPath() + "/appointments");

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

            } else if ("deleteSingle".equals(action)) {
                String idStr = request.getParameter("appointmentId");
                if (idStr == null || idStr.isEmpty()) {
                    session.setAttribute("message", "ID lịch hẹn để xóa không được cung cấp.");
                    session.setAttribute("messageType", "danger");
                    response.sendRedirect(request.getContextPath() + "/appointments");
                    return;
                }

                int appointmentId = Integer.parseInt(idStr);
                boolean success = dao.softDeleteAppointment(appointmentId, currentUserUserId);

                if (success) {
                    session.setAttribute("message", "Xóa mềm lịch hẹn thành công (ID: " + appointmentId + ").");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Không tìm thấy lịch hẹn để xóa hoặc có lỗi xảy ra.");
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
