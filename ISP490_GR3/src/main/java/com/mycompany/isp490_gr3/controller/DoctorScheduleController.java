package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.DoctorSchedule;
import com.mycompany.isp490_gr3.service.DoctorScheduleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                        out.print("{\"status\":\"error\",\"message\":\"Missing year or month parameter.\"}");
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
                        out.print("{\"status\":\"error\",\"message\":\"Schedule not found.\"}");
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"status\":\"error\",\"message\":\"API endpoint not found.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Invalid number format for year/month/ID.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Internal server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Ensure this is for /api/doctor-schedules
        if (!"/api/doctor-schedules".equals(request.getServletPath())) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"status\":\"error\",\"message\":\"API endpoint not found for POST.\"}");
            return;
        }

        try {
            // Read JSON from request body
            DoctorScheduleRequest requestData = gson.fromJson(request.getReader(), DoctorScheduleRequest.class);

            // Basic validation
            if (requestData.getDoctorId() == 0 || requestData.getWorkDate() == null || requestData.getWorkDate().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"Missing doctor ID or work date.\"}");
                return;
            }

            boolean success = scheduleService.createSchedule(
                requestData.getDoctorId(),
                requestData.getWorkDate(),
                requestData.isActive(),
                requestData.getEventName()
            );

            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                out.print("{\"status\":\"success\",\"message\":\"Lịch làm việc đã được tạo thành công.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict (e.g., schedule already exists)
                out.print("{\"status\":\"error\",\"message\":\"Lịch làm việc đã tồn tại hoặc có lỗi khi tạo.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Lỗi server khi tạo lịch: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Missing schedule ID for update.\"}");
            return;
        }
        String scheduleId = pathInfo.substring(1); // Get ID from path

        try {
            // Read JSON from request body
            DoctorScheduleRequest requestData = gson.fromJson(request.getReader(), DoctorScheduleRequest.class);

            // Basic validation
            if (requestData.getDoctorId() == 0 || requestData.getWorkDate() == null || requestData.getWorkDate().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"Missing doctor ID or work date.\"}");
                return;
            }

            boolean success = scheduleService.updateSchedule(
                scheduleId,
                requestData.getDoctorId(),
                requestData.getWorkDate(),
                requestData.isActive(),
                requestData.getEventName()
            );

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                out.print("{\"status\":\"success\",\"message\":\"Lịch làm việc đã được cập nhật thành công.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Or 409 Conflict if update fails due to conflict
                out.print("{\"status\":\"error\",\"message\":\"Lịch làm việc không tìm thấy hoặc có lỗi khi cập nhật.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Invalid schedule ID format.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Lỗi server khi cập nhật lịch: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Missing schedule ID for delete.\"}");
            return;
        }
        String scheduleId = pathInfo.substring(1); // Get ID from path

        try {
            boolean success = scheduleService.deleteSchedule(scheduleId);
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                out.print("{\"status\":\"success\",\"message\":\"Lịch làm việc đã được xóa thành công.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"status\":\"error\",\"message\":\"Lịch làm việc không tìm thấy hoặc có lỗi khi xóa.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"Invalid schedule ID format.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Lỗi server khi xóa lịch: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    // Inner class to map incoming JSON request body for POST/PUT
    private static class DoctorScheduleRequest {
        private int doctorId;
        private String workDate; // Expecting "YYYY-MM-DD"
        private boolean isActive;
        private String eventName;

        public int getDoctorId() { return doctorId; }
        public String getWorkDate() { return workDate; }
        public boolean isActive() { return isActive; }
        public String getEventName() { return eventName; }
    }
}
