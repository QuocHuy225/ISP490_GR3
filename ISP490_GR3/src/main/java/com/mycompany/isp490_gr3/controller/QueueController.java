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
@WebServlet(name = "QueueController", urlPatterns = {"/queue", "/api/queue"})
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
            return;
        }

        // Xử lý /queue (fallback hoặc load ban đầu)
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

        // Lấy danh sách bác sĩ
        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.findAllDoctors();

        // Lấy danh sách hàng đợi
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
        request.setAttribute("doctorId", doctorId);
        request.setAttribute("slotDate", slotDate != null ? slotDate.toString() : null);
        request.setAttribute("appointmentCode", appointmentCode);
        request.setAttribute("patientCode", patientCode);
        request.setAttribute("totalRecords", totalRecords);

        // Chuyển tiếp đến JSP
        request.getRequestDispatcher("/jsp/queue.jsp").forward(request, response);
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

        LOGGER.log(Level.INFO, "Received API request - doctorIdParam: {0}, slotDateStr: {1}, appointmentCode: {2}, patientCode: {3}, page: {4}, offset: {5}", new Object[]{doctorIdParam, slotDateStr, appointmentCode, patientCode, pageParam, offset});

        // Lấy thời gian hiện tại
        LocalTime currentTime = LocalTime.now(); // Ví dụ: 13:30
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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

        if (queueList == null) {
            queueList = new ArrayList<>(); // Đảm bảo không null
            LOGGER.warning("queueList is null, initialized as empty list");
        }
        // Sắp xếp queueList theo thời gian khám gần nhất và ưu tiên cao

        try {
            queueList.sort(Comparator
                    .comparing((QueueViewDTO item) -> {
                        if (item == null || item.getSlotTimeRange() == null || !item.getSlotTimeRange().contains("-")) {
                            LOGGER.warning("Invalid or null slotTimeRange for item: " + (item != null ? item.getAppointmentCode() : "null"));
                            return Long.MAX_VALUE; // Đẩy ra cuối nếu dữ liệu không hợp lệ
                        }
                        String[] timeRange = item.getSlotTimeRange().trim().split("-");
                        if (timeRange.length < 2 || timeRange[0].trim().isEmpty()) {
                            LOGGER.warning("Invalid timeRange format for item: " + item.getAppointmentCode());
                            return Long.MAX_VALUE;
                        }
                        LocalTime slotStartTime = LocalTime.parse(timeRange[0].trim(), timeFormatter);
                        long minutesDiff = Math.abs(slotStartTime.toSecondOfDay() - currentTime.toSecondOfDay()) / 60;
                        return minutesDiff;
                    })
                    .thenComparing(QueueViewDTO::getPriority, Comparator.reverseOrder())
            );
            LOGGER.info("Sorting completed successfully for " + queueList.size() + " items");
        } catch (Exception e) {
            LOGGER.severe("Error during sorting: " + e.getMessage());
            // Sắp xếp lại theo priority nếu lỗi
            queueList.sort(Comparator.comparing(QueueViewDTO::getPriority, Comparator.reverseOrder()));
        }

        // Xử lý vòng lặp với try-catch
        try {
            for (QueueViewDTO item : queueList) {
                if (item == null) {
                    LOGGER.warning("Null item encountered in queueList");
                    continue; // Bỏ qua phần tử null
                }
                String[] timeRange = item.getSlotTimeRange() != null ? item.getSlotTimeRange().trim().split("-") : new String[0];
                if (timeRange.length < 1 || timeRange[0].trim().isEmpty()) {
                    LOGGER.warning("Invalid timeRange for item: " + item.getAppointmentCode());
                    item.setBeforeCurrentTime(false);
                } else {
                    LocalTime slotStartTime = LocalTime.parse(timeRange[0].trim(), timeFormatter);
                    item.setBeforeCurrentTime(slotStartTime.isBefore(currentTime));
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error in setting beforeCurrentTime: " + e.getMessage());
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("queueList", queueList);
        responseData.put("totalRecords", totalRecords);

        String json = new Gson().toJson(responseData);
        response.getWriter().print(json);
    }
}
