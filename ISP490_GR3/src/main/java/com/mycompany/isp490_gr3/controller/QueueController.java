/*
 * Click netbeans://netbeans/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click netbeans://netbeans/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAOQueue;
import com.mycompany.isp490_gr3.dto.QueueViewDTO;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
 *
 * @author FPT SHOP
 */
@WebServlet(name = "QueueController", urlPatterns = {"/queue", "/api/queue", "/api/queue/remove" })
public class QueueController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(QueueController.class.getName());

    private boolean checkRoleAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != User.Role.RECEPTIONIST && currentUser.getRole() != User.Role.DOCTOR) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!checkRoleAccess(request, response)) {
            return;
        }

        String path = request.getServletPath();
        if ("/api/queue".equals(path)) {
            handleApiRequest(request, response);
        } else if ("/api/queue/remove".equals(path)) {
            handleRemoveRequest(request, response);
        } else {
            // Xử lý /queue (fallback hoặc load ban đầu)
            int doctorId = 0;
            String doctorIdParam = request.getParameter("doctorId");
            if (doctorIdParam != null && doctorIdParam.matches("\\d+")) {
                doctorId = Integer.parseInt(doctorIdParam);
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

            // Lấy danh sách bác sĩ
            DAODoctor daoDoctor = new DAODoctor();
            List<Doctor> doctors = daoDoctor.findAllDoctors();

            // Lấy danh sách hàng đợi
            DAOQueue dao = new DAOQueue();
            List<QueueViewDTO> queueList;
            int totalRecords;

            if (appointmentCode != null || patientCode != null || doctorId > 0 || slotDate != null) {
                queueList = dao.searchQueueViewDTOs(appointmentCode, patientCode, doctorId, slotDate, 0, Integer.MAX_VALUE);
                totalRecords = dao.countSearchQueueViewDTOs(appointmentCode, patientCode, doctorId, slotDate);
            } else {
                queueList = dao.getTodayQueueViewDTOs(doctorId, slotDate, 0, Integer.MAX_VALUE);
                totalRecords = dao.countTodayQueueViewDTOs(doctorId, slotDate);
            }

            request.setAttribute("doctorList", doctors);
            request.setAttribute("queueList", queueList);
            request.setAttribute("doctorId", doctorId);
            request.setAttribute("slotDate", slotDate != null ? slotDate.toString() : null);
            request.setAttribute("appointmentCode", appointmentCode);
            request.setAttribute("patientCode", patientCode);
            request.setAttribute("totalRecords", totalRecords);

            // Chuyển tiếp đến JSP
            request.getRequestDispatcher("/jsp/queue.jsp").forward(request, response);
        }
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

        int doctorId = 0;
        String doctorIdParam = request.getParameter("doctorId");
        if (doctorIdParam != null && doctorIdParam.matches("\\d+")) {
            doctorId = Integer.parseInt(doctorIdParam);
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

        LOGGER.info("Received API request - doctorIdParam: " + doctorIdParam + ", slotDateStr: " + slotDateStr
                + ", appointmentCode: " + appointmentCode + ", patientCode: " + patientCode
                + ", page: " + pageParam + ", offset: " + offset);

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

        // Sắp xếp mới: Ưu tiên lịch hẹn tại currentTime, sau đó ưu tiên cao gần viền
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
                        long minutesDiff = slotStartTime.toSecondOfDay() - currentTime.toSecondOfDay(); // Không dùng abs, ưu tiên 0 phút
                        return minutesDiff; // Lịch hẹn tại currentTime (0) lên đầu
                    })
                    .thenComparing(QueueViewDTO::getPriority, Comparator.reverseOrder()) // Ưu tiên cao sau
                    .thenComparing(QueueViewDTO::getSlotTimeRange, Comparator.naturalOrder()) // Sắp xếp tăng dần theo giờ
            );
            LOGGER.info("Sorting completed successfully for " + queueList.size() + " items");
        } catch (Exception e) {
            LOGGER.severe("Error during sorting: " + e.getMessage());
            queueList.sort(Comparator.comparing(QueueViewDTO::getPriority, Comparator.reverseOrder()));
        }

        // Xử lý trước current time
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

        String json = new Gson().toJson(responseData);
        response.getWriter().print(json);
    }
    
    private void handleRemoveRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        if (!checkRoleAccess(request, response)) {
            responseData.put("success", false);
            responseData.put("message", "Bạn không có quyền thực hiện hành động này.");
            response.getWriter().print(new Gson().toJson(responseData));
            return;
        }

        String appointmentCode = request.getParameter("appointmentCode");
        LOGGER.log(Level.INFO, "Received remove request for appointmentCode: {0}", appointmentCode);

        if (appointmentCode == null || appointmentCode.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("success", false);
            responseData.put("message", "Mã lịch hẹn không hợp lệ.");
            response.getWriter().print(new Gson().toJson(responseData));
            return;
        }

        DAOQueue dao = new DAOQueue();
        try {
            boolean success = dao.removeFromQueue(appointmentCode);
            if (success) {
                responseData.put("success", true);
                responseData.put("message", "Đã gỡ bệnh nhân khỏi hàng đợi thành công.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                responseData.put("success", false);
                responseData.put("message", "Không tìm thấy lịch hẹn với mã: " + appointmentCode);
            }
        } catch (Exception e) {
            LOGGER.severe("Error removing from queue: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("success", false);
            responseData.put("message", "Lỗi hệ thống khi gỡ bệnh nhân khỏi hàng đợi.");
        }

        response.getWriter().print(new Gson().toJson(responseData));
    }
}
