package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAOSlot;
import com.mycompany.isp490_gr3.dto.SlotViewDTO;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "SlotController", urlPatterns = {"/slot"})
public class SlotController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SlotController.class.getName());

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

        // Lấy tham số lọc
        String doctorIdStr = request.getParameter("doctorId");
        String slotDateStr = request.getParameter("slotDate");

        Integer doctorId = null;
        if (doctorIdStr != null && !doctorIdStr.trim().isEmpty()) {
            try {
                doctorId = Integer.parseInt(doctorIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid doctorId format: " + doctorIdStr);
            }
        }

        LocalDate slotDate = null;
        if (slotDateStr != null && !slotDateStr.trim().isEmpty()) {
            try {
                slotDate = LocalDate.parse(slotDateStr);
            } catch (DateTimeParseException e) {
                LOGGER.warning("Invalid date format: " + slotDateStr);
            }
        }

        if (slotDate == null) {
            slotDate = LocalDate.now();
        }

        // Phân trang
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

        // DAO
        DAOSlot daoSlot = new DAOSlot();
        List<SlotViewDTO> currentPageSlots = daoSlot.searchSlotViewDTOs(doctorId, slotDate, offset, recordsPerPage);
        int totalRecords = daoSlot.countFilteredSlotViewDTOs(doctorId, slotDate);
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.getAllDoctors();

        // Gán dữ liệu cho JSP
        request.setAttribute("doctors", doctors);
        request.setAttribute("currentPageSlots", currentPageSlots);
        request.setAttribute("doctorId", doctorIdStr != null ? doctorIdStr : "");
        request.setAttribute("slotDate", slotDate.toString());
        request.setAttribute("searchPerformed", doctorId != null || slotDateStr != null);
        request.setAttribute("hasResults", !currentPageSlots.isEmpty());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", offset);
        request.setAttribute("endIndex", Math.min(offset + recordsPerPage, totalRecords));

        request.getRequestDispatcher("/jsp/slot.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.equals("search")) {
            handleSearch(request, response);
            return;
        }

        switch (action) {
            case "addSlot":
                // TODO: Xử lý thêm slot
                break;
            case "updateSlot":
                // TODO: Xử lý sửa slot
                break;
            case "delete":
                // TODO: Xử lý xóa slot
                break;
            case "deleteMultiple":
                // TODO: Xử lý xóa nhiều slot
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ");
        }
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String doctorIdStr = request.getParameter("doctorId");
        String slotDateStr = request.getParameter("slotDate");

        Integer doctorId = null;
        if (doctorIdStr != null && !doctorIdStr.trim().isEmpty()) {
            try {
                doctorId = Integer.parseInt(doctorIdStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid doctorId format: " + doctorIdStr);
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

        // Phân trang mặc định trang 1
        int currentPage = 1;
        int recordsPerPage = 10;
        int offset = (currentPage - 1) * recordsPerPage;

        DAOSlot daoSlot = new DAOSlot();
        List<SlotViewDTO> currentPageSlots = daoSlot.searchSlotViewDTOs(doctorId, slotDate, offset, recordsPerPage);
        int totalRecords = daoSlot.countFilteredSlotViewDTOs(doctorId, slotDate);
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.getAllDoctors();

        request.setAttribute("doctors", doctors);
        request.setAttribute("currentPageSlots", currentPageSlots);
        request.setAttribute("doctorId", doctorIdStr != null ? doctorIdStr : "");
        request.setAttribute("slotDate", slotDate != null ? slotDate.toString() : "");
        request.setAttribute("searchPerformed", true);
        request.setAttribute("hasResults", !currentPageSlots.isEmpty());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", offset);
        request.setAttribute("endIndex", Math.min(offset + recordsPerPage, totalRecords));

        request.getRequestDispatcher("/jsp/slot.jsp").forward(request, response);
    }
}
