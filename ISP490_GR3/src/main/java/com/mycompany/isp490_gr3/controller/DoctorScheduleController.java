package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.DoctorSchedule;
import com.mycompany.isp490_gr3.model.User;
import com.mycompany.isp490_gr3.service.DoctorScheduleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Map this servlet to handle API requests for doctor schedules and doctors
//@WebServlet(urlPatterns = {"/api/doctor-schedules", "/api/doctor-schedules/*", "/api/doctors"})
public class DoctorScheduleController extends HttpServlet {

    private DoctorScheduleService scheduleService = new DoctorScheduleService();
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create(); // For date formatting

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Check user access first
        if (!checkReceptionistAccess(request, response)) {
            // Error response already sent by checkReceptionistAccess
            return;
        }

        String pathInfo = request.getPathInfo(); // e.g., /123 for /api/doctor-schedules/123
        String servletPath = request.getServletPath(); // e.g., /api/doctor-schedules or /api/doctors

        try {
            if ("/api/doctors".equals(servletPath)) {
                // Handle GET /api/doctors
                List<Doctor> doctors = scheduleService.getAllDoctors();
                out.print(gson.toJson(doctors));
            } else if ("/api/doctor-schedules".equals(servletPath)) {
                if (pathInfo == null || pathInfo.equals("/")) {
                    // Handle GET /api/doctor-schedules?year=YYYY&month=MM
                    String yearStr = request.getParameter("year");
                    String monthStr = request.getParameter("month");

                    if (yearStr == null || monthStr == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"status\":\"error\",\"message\":\"Thiếu tham số năm hoặc tháng.\"}");
                        return;
                    }

                    int year = Integer.parseInt(yearStr);
                    int month = Integer.parseInt(monthStr);
                    List<DoctorSchedule> schedules = scheduleService.getSchedulesByMonth(year, month);
                    out.print(gson.toJson(schedules));
                } else {
                    // Handle GET /api/doctor-schedules/{id}
                    String scheduleId = pathInfo.substring(1); // Remove leading slash
                    DoctorSchedule schedule = scheduleService.findScheduleById(scheduleId);
                    if (schedule != null) {
                        out.print(gson.toJson(schedule));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"status\":\"error\",\"message\":\"Không tìm thấy lịch làm việc.\"}");
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"status\":\"error\",\"message\":\"Điểm cuối API không tìm thấy.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Định dạng số không hợp lệ cho năm/tháng/ID.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Lỗi máy chủ nội bộ: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Check user access first
        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        // Ensure this is for /api/doctor-schedules
        if (!"/api/doctor-schedules".equals(request.getServletPath())) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"status\":\"error\",\"message\":\"Điểm cuối API không tìm thấy cho POST.\"}");
            return;
        }

        try {
            // Read JSON from request body
            DoctorScheduleRequest requestData = gson.fromJson(request.getReader(), DoctorScheduleRequest.class);

            // Basic validation
            if (requestData.getDoctorId() == 0 || requestData.getWorkDate() == null || requestData.getWorkDate().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"Thiếu ID bác sĩ hoặc ngày làm việc.\"}");
                return;
            }

            String result = scheduleService.createSchedule(
                    requestData.getDoctorId(),
                    requestData.getWorkDate(),
                    requestData.isActive(),
                    requestData.getEventName()
            );

            if ("success".equals(result)) {
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                out.print("{\"status\":\"success\",\"message\":\"Lịch làm việc đã được tạo thành công.\"}");
            } else {
                // If result is not "success", it's an error message from the service
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict (e.g., schedule already exists or date out of range)
                out.print("{\"status\":\"error\",\"message\":\"" + result + "\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Lỗi máy chủ khi tạo lịch: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Check user access first
        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Thiếu ID lịch làm việc để cập nhật.\"}");
            return;
        }
        String scheduleId = pathInfo.substring(1); // Get ID from path

        try {
            // Read JSON from request body
            DoctorScheduleRequest requestData = gson.fromJson(request.getReader(), DoctorScheduleRequest.class);

            // Basic validation
            if (requestData.getDoctorId() == 0 || requestData.getWorkDate() == null || requestData.getWorkDate().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"Thiếu ID bác sĩ hoặc ngày làm việc.\"}");
                return;
            }

            String result = scheduleService.updateSchedule(
                    scheduleId,
                    requestData.getDoctorId(),
                    requestData.getWorkDate(),
                    requestData.isActive(),
                    requestData.getEventName()
            );

            if ("success".equals(result)) {
                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                out.print("{\"status\":\"success\",\"message\":\"Lịch làm việc đã được cập nhật thành công.\"}");
            } else {
                // Determine appropriate status code based on error message
                int statusCode = HttpServletResponse.SC_BAD_REQUEST; // Default to Bad Request
                if (result.contains("không tìm thấy")) {
                    statusCode = HttpServletResponse.SC_NOT_FOUND; // 404 Not Found
                } else if (result.contains("đã tồn tại")) {
                    statusCode = HttpServletResponse.SC_CONFLICT; // 409 Conflict
                } else if (result.contains("nằm ngoài khoảng thời gian cho phép")) {
                    statusCode = HttpServletResponse.SC_FORBIDDEN; // 403 Forbidden or 400 Bad Request
                }
                response.setStatus(statusCode);
                out.print("{\"status\":\"error\",\"message\":\"" + result + "\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Định dạng ID lịch làm việc không hợp lệ.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Lỗi máy chủ khi cập nhật lịch: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Check user access first
        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Thiếu ID lịch làm việc để xóa.\"}");
            return;
        }
        String scheduleId = pathInfo.substring(1); // Get ID from path

        try {
            boolean success = scheduleService.deleteSchedule(scheduleId);
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                out.print("{\"status\":\"success\",\"message\":\"Lịch làm việc đã được xóa thành công.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Or 500 if DAO failed for other reasons
                out.print("{\"status\":\"error\",\"message\":\"Lịch làm việc không tìm thấy hoặc có lỗi khi xóa.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Định dạng ID lịch làm việc không hợp lệ.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Lỗi máy chủ khi xóa lịch: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Checks if the current user has Receptionist or Admin access.
     * If not, sends an appropriate error response and returns false.
     *
     * @param request The HttpServletRequest.
     * @param response The HttpServletResponse.
     * @return true if access is granted, false otherwise.
     * @throws IOException If an input or output exception occurs.
     */
    private boolean checkReceptionistAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        PrintWriter out = response.getWriter();

        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            out.print("{\"status\":\"error\",\"message\":\"Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn.\"}");
            return false;
        }

        // Get current user
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            out.print("{\"status\":\"error\",\"message\":\"Thông tin người dùng không hợp lệ trong phiên.\"}");
            return false;
        }

        // Allow both Admin and Receptionist to access
        if (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.RECEPTIONIST) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
            out.print("{\"status\":\"error\",\"message\":\"Bạn không có quyền truy cập chức năng này.\"}");
            return false;
        }
        return true;
    }

    // Inner class to map incoming JSON request body for POST/PUT
    private static class DoctorScheduleRequest {

        private int doctorId;
        private String workDate; // Expecting "YYYY-MM-DD"
        private boolean isActive;
        private String eventName;

        public int getDoctorId() {
            return doctorId;
        }

        public String getWorkDate() {
            return workDate;
        }

        public boolean isActive() {
            return isActive;
        }

        public String getEventName() {
            return eventName;
        }
    }
}