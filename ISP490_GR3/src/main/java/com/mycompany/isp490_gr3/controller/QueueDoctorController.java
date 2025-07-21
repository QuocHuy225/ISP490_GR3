package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAOQueue;
import com.mycompany.isp490_gr3.dto.QueueViewDTO;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for managing queue operations specifically for Doctors. This includes
 * viewing, searching, and updating queue statuses.
 */
@WebServlet(name = "QueueDoctorController", urlPatterns = {"/doctor/queue", "/api/doctor/queue", "/api/doctor/queue/status", "/api/doctor/queue/details"})
public class QueueDoctorController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(QueueDoctorController.class.getName());

    // Helper method to check if the user is a doctor
    private boolean isDoctor(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") instanceof User) {
            User currentUser = (User) session.getAttribute("user");
            return currentUser.getRole() == User.Role.DOCTOR;
        }
        return false;
    }

    // Check if the user has access to this doctor-specific controller
    private boolean checkDoctorAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != User.Role.DOCTOR) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkDoctorAccess(request, response)) {
            return;
        }
        String path = request.getServletPath();
        if ("/api/doctor/queue/details".equals(path)) { // NEW: Handle details API
            handleDetailsRequest(request, response);
        } else {
            processRequest(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkDoctorAccess(request, response)) {
            return;
        }

        String path = request.getServletPath();
        if ("/api/doctor/queue/status".equals(path)) {
            handleStatusUpdate(request, response);
        } else {
            handleApiRequest(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkDoctorAccess(request, response)) {
            return;
        }
        String path = request.getServletPath();
        if ("/api/doctor/queue/status".equals(path)) {
            handleStatusUpdate(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "PUT method not supported for this URL.");
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();
        if ("/api/doctor/queue".equals(path)) {
            handleApiRequest(request, response);
            return;
        }

        int page = 1;
        int limit = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null && pageParam.matches("\\d+")) {
            page = Integer.parseInt(pageParam);
        }
        int offset = (page - 1) * limit;

        int doctorId = 0; // This will store the int doctor_id from the 'doctors' table
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser != null && currentUser.getRole() == User.Role.DOCTOR) {
            DAODoctor daoDoctor = new DAODoctor();
            // FIX: Pass String currentUser.getId() directly to getDoctorByUserId
            Doctor currentDoctor = daoDoctor.getDoctorByUserId(currentUser.getId());

            if (currentDoctor != null) {
                doctorId = currentDoctor.getId(); // Get the int ID from the Doctor object
            } else {
                LOGGER.warning("Logged-in user is a doctor but no associated Doctor found for user: " + currentUser.getId());
            }

            String doctorIdParam = request.getParameter("doctorId");
            if (doctorIdParam != null && doctorIdParam.matches("\\d+")) {
                int requestedDoctorId = Integer.parseInt(doctorIdParam);
                if (requestedDoctorId == doctorId) {
                    // Use the requested doctorId if it matches the logged-in doctor
                } else {
                    LOGGER.warning("Doctor " + currentUser.getId() + " attempted to view another doctor's queue (ID: " + requestedDoctorId + "). Enforcing own queue.");
                }
            }
            request.setAttribute("doctorId", doctorId);
        } else {
            String doctorIdParam = request.getParameter("doctorId");
            if (doctorIdParam != null && doctorIdParam.matches("\\d+")) {
                doctorId = Integer.parseInt(doctorIdParam);
            }
            request.setAttribute("doctorId", doctorId);
        }

        String slotDateStr = request.getParameter("slotDate");
        LocalDate slotDate = null;
        if (slotDateStr != null && !slotDateStr.trim().isEmpty()) {
            try {
                slotDate = LocalDate.parse(slotDateStr);
            } catch (DateTimeParseException e) {
                LOGGER.warning("Invalid slotDate format: " + slotDateStr);
            }
        }

        String appointmentCode = request.getParameter("appointmentCode");
        String patientCode = request.getParameter("patientCode");

        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.findAllDoctors();

        DAOQueue dao = new DAOQueue();
        List<QueueViewDTO> queueList;
        int totalRecords;

        if (appointmentCode != null || patientCode != null || doctorId > 0 || slotDate != null) {
            queueList = dao.searchQueueViewDTOs(appointmentCode, patientCode, doctorId, slotDate, offset, limit);
            totalRecords = dao.countSearchQueueViewDTOs(appointmentCode, patientCode, doctorId, slotDate);
        } else {
            queueList = dao.getTodayQueueViewDTOs(doctorId, slotDate, offset, limit);
            totalRecords = dao.countTodayQueueViewDTOs(doctorId, slotDate);
        }

        request.setAttribute("doctorList", doctors);
        request.setAttribute("queueList", queueList);
        request.setAttribute("currentPage", page);
        request.setAttribute("slotDate", slotDate != null ? slotDate.toString() : null);
        request.setAttribute("appointmentCode", appointmentCode);
        request.setAttribute("patientCode", patientCode);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("isDoctor", true);

        request.getRequestDispatcher("/jsp/doctor-queue.jsp").forward(request, response);
    }

    private void handleApiRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        int page = 1;
        int limit = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null && pageParam.matches("\\d+")) {
            page = Integer.parseInt(pageParam);
        }
        int offset = (page - 1) * limit;

        int doctorId = 0; // This will store the int doctor_id from the 'doctors' table
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser != null && currentUser.getRole() == User.Role.DOCTOR) {
            DAODoctor daoDoctor = new DAODoctor();
            // FIX: Pass String currentUser.getId() directly to getDoctorByUserId
            Doctor currentDoctor = daoDoctor.getDoctorByUserId(currentUser.getId());

            if (currentDoctor != null) {
                doctorId = currentDoctor.getId();
            } else {
                LOGGER.warning("Logged-in user is a doctor but no associated Doctor ID found for user: " + currentUser.getId());
                // If a doctor user has no linked Doctor profile, you might want to forbid access
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Tài khoản bác sĩ không liên kết với thông tin bác sĩ. Vui lòng liên hệ quản trị viên.");
                response.getWriter().print(new Gson().toJson(errorResponse));
                return;
            }
            String doctorIdParam = request.getParameter("doctorId");
            if (doctorIdParam != null && doctorIdParam.matches("\\d+")) {
                int requestedDoctorId = Integer.parseInt(doctorIdParam);
                if (requestedDoctorId == doctorId) {
                    // Use the requested doctorId if it matches the logged-in doctor
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Bạn không có quyền truy cập dữ liệu này.");
            response.getWriter().print(new Gson().toJson(errorResponse));
            return;
        }

        String slotDateStr = request.getParameter("slotDate");
        LocalDate slotDate = null;
        if (slotDateStr != null && !slotDateStr.trim().isEmpty()) {
            try {
                slotDate = LocalDate.parse(slotDateStr);
            } catch (DateTimeParseException e) {
                LOGGER.warning("Invalid slotDate format: " + slotDateStr);
            }
        }

        String appointmentCode = request.getParameter("appointmentCode");
        String patientCode = request.getParameter("patientCode");

        LOGGER.info("Received API request - doctorIdParam: " + request.getParameter("doctorId") + ", slotDateStr: " + slotDateStr
                + ", appointmentCode: " + appointmentCode + ", patientCode: " + patientCode
                + ", page: " + pageParam + ", offset: " + offset + ", Logged-in Doctor ID: " + doctorId);

        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalTime currentTime = LocalTime.now(vietnamZone);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        DAOQueue dao = new DAOQueue();
        List<QueueViewDTO> queueList = null;
        int totalRecords = 0;

        try {
            if (appointmentCode != null || patientCode != null || doctorId > 0 || slotDate != null) {
                queueList = dao.searchQueueViewDTOs(appointmentCode, patientCode, doctorId, slotDate, offset, limit);
                totalRecords = dao.countSearchQueueViewDTOs(appointmentCode, patientCode, doctorId, slotDate);
                LOGGER.info("Fetched " + (queueList != null ? queueList.size() : 0) + " records from search with total: " + totalRecords);
            } else {
                queueList = dao.getTodayQueueViewDTOs(doctorId, slotDate, offset, limit);
                totalRecords = dao.countTodayQueueViewDTOs(doctorId, slotDate);
                LOGGER.info("Fetched " + (queueList != null ? queueList.size() : 0) + " records from today with total: " + totalRecords);
            }
        } catch (Exception e) {
            LOGGER.severe("Error fetching queue list: " + e.getMessage());
            queueList = new ArrayList<>();
        }

        if (queueList == null) {
            queueList = new ArrayList<>();
            LOGGER.warning("queueList is null, initialized as empty list");
        }

        try {
            queueList.sort(Comparator
                    .comparing((QueueViewDTO item) -> {
                        if (item == null || item.getSlotTimeRange() == null || !item.getSlotTimeRange().contains("-")) {
                            LOGGER.warning("Invalid or null slotTimeRange for item: " + (item != null ? item.getAppointmentCode() : "null"));
                            return Long.MAX_VALUE;
                        }
                        String[] timeRange = item.getSlotTimeRange().trim().split("-");
                        if (timeRange.length < 2 || timeRange[0].trim().isEmpty()) {
                            LOGGER.warning("Invalid timeRange format for item: " + item.getAppointmentCode());
                            return Long.MAX_VALUE;
                        }
                        LocalTime slotStartTime = LocalTime.parse(timeRange[0].trim(), timeFormatter);
                        long minutesDiff = slotStartTime.toSecondOfDay() - currentTime.toSecondOfDay();
                        return minutesDiff;
                    })
                    .thenComparing(QueueViewDTO::getPriority, Comparator.reverseOrder())
                    .thenComparing(QueueViewDTO::getSlotTimeRange, Comparator.naturalOrder())
            );
            LOGGER.info("Sorting completed successfully for " + queueList.size() + " items");
        } catch (Exception e) {
            LOGGER.severe("Error during sorting: " + e.getMessage());
            queueList.sort(Comparator.comparing(QueueViewDTO::getPriority, Comparator.reverseOrder()));
        }

        try {
            for (QueueViewDTO item : queueList) {
                if (item == null) {
                    LOGGER.warning("Null item encountered in queueList");
                    continue;
                }
                String slotTimeRange = item.getSlotTimeRange();
                if (slotTimeRange == null || !slotTimeRange.contains("-")) {
                    LOGGER.warning("Invalid slotTimeRange for item: " + item.getAppointmentCode());
                    item.setBeforeCurrentTime(false);
                } else {
                    String[] timeRange = slotTimeRange.trim().split("-");
                    if (timeRange.length > 0 && !timeRange[0].trim().isEmpty()) {
                        LocalTime slotStartTime = LocalTime.parse(timeRange[0].trim(), timeFormatter);
                        item.setBeforeCurrentTime(slotStartTime.isBefore(currentTime));
                        LOGGER.info("Item " + item.getAppointmentCode() + " - slotStartTime: " + slotStartTime + ", isBeforeCurrentTime: " + item.isBeforeCurrentTime());
                    } else {
                        LOGGER.warning("Empty timeRange[0] for item: " + item.getAppointmentCode());
                        item.setBeforeCurrentTime(false);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error setting beforeCurrentTime: " + e.getMessage());
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("queueList", queueList);
        responseData.put("totalRecords", totalRecords);
        responseData.put("currentTime", currentTime.format(timeFormatter));
        responseData.put("isDoctor", true);

        String json = new Gson().toJson(responseData);
        response.getWriter().print(json);
    }

    private void handleStatusUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, String> responseMap = new HashMap<>();

        if (!isDoctor(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseMap.put("status", "error");
            responseMap.put("message", "Bạn không có quyền để thay đổi trạng thái hàng đợi.");
            out.print(new Gson().toJson(responseMap));
            return;
        }

        String queueIdParam = request.getParameter("queueId");
        String newStatus = request.getParameter("newStatus");

        if (queueIdParam == null || newStatus == null || queueIdParam.trim().isEmpty() || newStatus.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("status", "error");
            responseMap.put("message", "Thiếu thông tin: queueId hoặc newStatus.");
            out.print(new Gson().toJson(responseMap));
            return;
        }

        int queueId;
        try {
            queueId = Integer.parseInt(queueIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("status", "error");
            responseMap.put("message", "ID hàng đợi không hợp lệ.");
            out.print(new Gson().toJson(responseMap));
            LOGGER.log(Level.WARNING, "Invalid queueId format: " + queueIdParam, e);
            return;
        }

        boolean isValidStatus = false;
        String[] allowedStatuses = {"waiting", "in_progress", "completed", "skipped", "rejected"};
        for (String status : allowedStatuses) {
            if (status.equalsIgnoreCase(newStatus)) {
                isValidStatus = true;
                break;
            }
        }

        if (!isValidStatus) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("status", "error");
            responseMap.put("message", "Trạng thái mới không hợp lệ.");
            out.print(new Gson().toJson(responseMap));
            LOGGER.warning("Invalid newStatus: " + newStatus + " for Queue ID: " + queueId);
            return;
        }

        DAOQueue dao = new DAOQueue();
        boolean success = dao.updateQueueStatus(queueId, newStatus);

        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            responseMap.put("status", "success");
            responseMap.put("message", "Cập nhật trạng thái thành công.");
            LOGGER.info("Successfully updated queue " + queueId + " to status " + newStatus);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("status", "error");
            responseMap.put("message", "Cập nhật trạng thái thất bại.");
            LOGGER.severe("Failed to update queue " + queueId + " to status " + newStatus);
        }
        out.print(new Gson().toJson(responseMap));
    }

    private void handleDetailsRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        if (!isDoctor(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print(gson.toJson(Map.of("status", "error", "message", "Bạn không có quyền truy cập dữ liệu này.")));
            return;
        }

        String queueIdParam = request.getParameter("queueId");
        if (queueIdParam == null || queueIdParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("status", "error", "message", "Thiếu ID hàng đợi.")));
            return;
        }

        int queueId;
        try {
            queueId = Integer.parseInt(queueIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("status", "error", "message", "ID hàng đợi không hợp lệ.")));
            LOGGER.log(Level.WARNING, "Invalid queueId format for details: " + queueIdParam, e);
            return;
        }

        DAOQueue daoQueue = new DAOQueue();
        QueueViewDTO details = daoQueue.getQueueViewDTOById(queueId); // Bạn cần thêm phương thức này vào DAOQueue

        if (details != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(details));
            LOGGER.info("Fetched details for queueId: " + queueId);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(gson.toJson(Map.of("status", "error", "message", "Không tìm thấy chi tiết hàng đợi.")));
            LOGGER.warning("No details found for queueId: " + queueId);
        }
    }
}
