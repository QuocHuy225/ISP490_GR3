package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.model.Slot;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "SlotController", urlPatterns = {"/slot"})
public class SlotController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SlotController.class.getName());

    /**
     * Kiểm tra quyền truy cập cho Receptionist.
     * @param request HttpServletRequest để lấy session
     * @param response HttpServletResponse để chuyển hướng hoặc trả lỗi
     * @return true nếu là Receptionist, false nếu không
     * @throws IOException nếu có lỗi chuyển hướng
     */
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

        // Set character encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Kiểm tra phân quyền
        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        // Lấy các tham số lọc
        String slotCode = request.getParameter("slotCode");
        String doctorIdStr = request.getParameter("doctorId");
        String slotDate = request.getParameter("slotDate");
        String status = request.getParameter("status");

        // Chuyển đổi doctorId
        Integer doctorId = null;
        if (doctorIdStr != null && !doctorIdStr.trim().isEmpty()) {
            try {
                doctorId = Integer.parseInt(doctorIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid doctorId parameter: " + doctorIdStr, e);
            }
        }

        status = (status != null && !status.trim().isEmpty() && !status.equals("all")) ? status.trim() : null;

        // Xác định hành động tìm kiếm hoặc phân trang
        boolean isSearchTriggered = request.getParameter("submitSearch") != null
                || request.getParameter("page") != null
                || request.getParameter("recordsPerPage") != null;

        // Thiết lập phân trang
        int currentPage = 1;
        int recordsPerPage = 10;
        try {
            if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
                currentPage = Integer.parseInt(request.getParameter("page"));
            }
            if (request.getParameter("recordsPerPage") != null && !request.getParameter("recordsPerPage").isEmpty()) {
                recordsPerPage = Integer.parseInt(request.getParameter("recordsPerPage"));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid pagination parameters", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tham số không hợp lệ.");
            return;
        }

        // Tạo dữ liệu mẫu (mock data) cho slot
        List<Slot> allSlots = createMockSlots();
        List<Slot> filteredSlots = new ArrayList<>();

        // Lọc dữ liệu
        if (isSearchTriggered) {
            for (Slot slot : allSlots) {
                boolean matches = true;
                if (slotCode != null && !slotCode.trim().isEmpty() && !slot.getSlotCode().toLowerCase().contains(slotCode.toLowerCase())) {
                    matches = false;
                }
                if (doctorId != null && slot.getDoctorId() != doctorId) {
                    matches = false;
                }
                if (slotDate != null && !slotDate.trim().isEmpty() && !slot.getSlotDate().equals(slotDate)) {
                    matches = false;
                }
                if (status != null && !slot.getStatus().equals(status)) {
                    matches = false;
                }
                if (matches) {
                    filteredSlots.add(slot);
                }
            }
        } else {
            filteredSlots.addAll(allSlots); // Hiển thị tất cả nếu không tìm kiếm
        }

        // Tính toán phân trang
        int totalRecords = filteredSlots.size();
        int totalPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
        int startIndex = (currentPage - 1) * recordsPerPage;
        int endIndex = Math.min(startIndex + recordsPerPage, totalRecords);

        // Lấy danh sách slot cho trang hiện tại
        List<Slot> currentPageSlots = new ArrayList<>();
        for (int i = startIndex; i < endIndex && i < filteredSlots.size(); i++) {
            currentPageSlots.add(filteredSlots.get(i));
        }

        // Set thuộc tính cho JSP
        request.setAttribute("currentPageSlots", currentPageSlots);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("startIndex", startIndex);
        request.setAttribute("endIndex", endIndex);
        request.setAttribute("slotCode", slotCode != null ? slotCode : "");
        request.setAttribute("doctorId", doctorIdStr != null ? doctorIdStr : "");
        request.setAttribute("slotDate", slotDate != null ? slotDate : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("searchPerformed", isSearchTriggered);
        request.setAttribute("hasResults", !currentPageSlots.isEmpty());

        // Chuyển hướng đến slot.jsp
        request.getRequestDispatcher("/jsp/slot.jsp").forward(request, response);
    }

    // Hàm tạo dữ liệu mẫu
    private List<Slot> createMockSlots() {
        List<Slot> slots = new ArrayList<>();
        slots.add(new Slot(1, "SLOT001", "2025-06-26", "08:00", 1, "Dr. Nguyen", "available"));
        slots.add(new Slot(2, "SLOT002", "2025-06-26", "09:00", 2, "Dr. Tran", "booked"));
        slots.add(new Slot(3, "SLOT003", "2025-06-27", "10:00", 1, "Dr. Nguyen", "available"));
        slots.add(new Slot(4, "SLOT004", "2025-06-27", "11:00", 3, "Dr. Le", "cancelled"));
        return slots;
    }
}