package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAODoctorSchedule;
import com.mycompany.isp490_gr3.dao.DAOSlot;
import com.mycompany.isp490_gr3.dto.SlotViewDTO;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "SlotController", urlPatterns = {"/slot", "/slot/add", "/slot/delete", "/slot/filterSlotDate"})
public class SlotController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SlotController.class.getName());

    // Kiểm tra quyền RECEPTIONIST
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

        String path = request.getServletPath();

        // Xử lý /slot
        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        if ("/slot/filterSlotDate".equals(path)) {
            // Xử lý endpoint /slot/filterSlotDate
            handleFilterSlotDate(request, response);
            return;
        }
        // Lọc (doctorId, slotDate truyền null)
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

        // Gọi DAO
        DAOSlot daoSlot = new DAOSlot();
        List<SlotViewDTO> currentPageSlots;
        int totalRecords;
        if (doctorId == null && (slotDateStr == null || slotDateStr.trim().isEmpty())) {
            currentPageSlots = daoSlot.getTodaySlotViewDTOs(offset, recordsPerPage);
            totalRecords = daoSlot.countTodaySlotViewDTOs();
            slotDate = LocalDate.now();
        } else {
            currentPageSlots = daoSlot.searchSlotViewDTOs(doctorId, slotDate, offset, recordsPerPage);
            totalRecords = daoSlot.countFilteredSlotViewDTOs(doctorId, slotDate);
        }

        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.findAllDoctors();

        request.setAttribute("doctors", doctors);
        request.setAttribute("currentPageSlots", currentPageSlots);
        request.setAttribute("doctorId", doctorIdStr != null ? doctorIdStr : "");
        request.setAttribute("slotDate", slotDate != null ? slotDate.toString() : "");
        request.setAttribute("searchPerformed", doctorId != null || (slotDateStr != null && !slotDateStr.isEmpty()));
        request.setAttribute("hasResults", !currentPageSlots.isEmpty());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("recordsPerPage", recordsPerPage);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", offset);
        request.setAttribute("endIndex", Math.min(offset + recordsPerPage, totalRecords));

        request.getRequestDispatcher("/jsp/slot.jsp").forward(request, response);
    }

    // Xử lý endpoint /slot/filterSlotDate
    private void handleFilterSlotDate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String doctorIdRaw = request.getParameter("doctorId");

        System.out.println("doctorId nhận được từ FE: " + doctorIdRaw);
        if (doctorIdRaw == null || doctorIdRaw.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing doctorId parameter.\"}");
            return;
        }

        try {
            int doctorId = Integer.parseInt(doctorIdRaw);
            DAODoctorSchedule dao = new DAODoctorSchedule();
            List<String> dates = dao.getWorkingDatesByDoctorId(doctorId);
            String json = new Gson().toJson(dates != null ? dates : new ArrayList<>());
            System.out.println("JSON response: " + json);
            response.getWriter().write(json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid doctorId format.\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy ngày làm việc: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Lỗi server khi lấy ngày làm việc.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String path = request.getServletPath();

        switch (path) {
            case "/slot":
                handleSearch(request, response);
                break;
            case "/slot/add":
                handleAddSlot(request, response);
                break;
            case "/slot/delete":
                handleDelete(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
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

        int currentPage = 1;
        int recordsPerPage = 10;
        int offset = (currentPage - 1) * recordsPerPage;

        DAOSlot daoSlot = new DAOSlot();
        List<SlotViewDTO> currentPageSlots = daoSlot.searchSlotViewDTOs(doctorId, slotDate, offset, recordsPerPage);
        int totalRecords = daoSlot.countFilteredSlotViewDTOs(doctorId, slotDate);
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        DAODoctor daoDoctor = new DAODoctor();
        List<Doctor> doctors = daoDoctor.findAllDoctors();

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

    private void handleAddSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(">> Đã vào handleAddSlot");

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String doctorIdStr = request.getParameter("doctorId");
        String slotDateStr = request.getParameter("slotDate");
        String addType = request.getParameter("addType");

        // Redirect mặc định sau xử lý
        String redirectUrl = request.getContextPath() + "/slot?doctorId=" + doctorIdStr + "&slotDate=" + slotDateStr;

        try {
            // Kiểm tra dữ liệu bắt buộc
            if (doctorIdStr == null || slotDateStr == null || addType == null
                    || doctorIdStr.isBlank() || slotDateStr.isBlank() || addType.isBlank()) {
                throw new IllegalArgumentException("Thiếu thông tin bắt buộc.");
            }

            int doctorId = Integer.parseInt(doctorIdStr);
            LocalDate slotDate = LocalDate.parse(slotDateStr);
            int maxPatients = Integer.parseInt(request.getParameter("maxPatients"));
            if (maxPatients <= 0) {
                throw new IllegalArgumentException("Số bệnh nhân phải lớn hơn 0.");
            }

            DAOSlot dao = new DAOSlot();

            if ("single".equals(addType)) {
                // Slot đơn
                String startTimeStr = request.getParameter("startTime");
                int duration = Integer.parseInt(request.getParameter("slotDuration"));
                if (duration <= 0) {
                    throw new IllegalArgumentException("Thời lượng phải lớn hơn 0.");
                }
                if (startTimeStr == null || startTimeStr.isBlank()) {
                    throw new IllegalArgumentException("Thiếu giờ bắt đầu.");
                }

                LocalTime startTime = LocalTime.parse(startTimeStr);

                boolean success = dao.insertSlotWithValidation(doctorId, slotDate, startTime, duration, maxPatients);

                if (success) {
                    session.setAttribute("message", "Đã thêm slot đơn thành công.");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Slot bị trùng giờ – không thể thêm.");
                    session.setAttribute("messageType", "warning");
                }

            } else if ("range".equals(addType)) {
                // Slot theo dải
                String startRange = request.getParameter("startRangeTime");
                String endRange = request.getParameter("endRangeTime");
                int duration = Integer.parseInt(request.getParameter("rangeSlotDuration"));

                if (duration <= 0) {
                    throw new IllegalArgumentException("Thời lượng phải lớn hơn 0.");
                }
                if (startRange == null || endRange == null || startRange.isBlank() || endRange.isBlank()) {
                    throw new IllegalArgumentException("Thiếu thời gian bắt đầu/kết thúc.");
                }

                int inserted = dao.insertRangeSlots(doctorId, slotDate, startRange, endRange, duration, maxPatients);

                if (inserted > 0) {
                    session.setAttribute("message", "Đã thêm " + inserted + " slot theo dải thành công.");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Tất cả slot bị trùng giờ – không có slot nào được thêm.");
                    session.setAttribute("messageType", "warning");
                }

            } else {
                throw new IllegalArgumentException("Loại slot không hợp lệ.");
            }

        } catch (NumberFormatException | DateTimeParseException e) {
            LOGGER.warning("Lỗi định dạng đầu vào: " + e.getMessage());
            session.setAttribute("message", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            session.setAttribute("messageType", "danger");

        } catch (IllegalArgumentException e) {
            session.setAttribute("message", e.getMessage());
            session.setAttribute("messageType", "danger");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm slot: " + e.getMessage(), e);
            session.setAttribute("message", "Đã xảy ra lỗi trong quá trình xử lý.");
            session.setAttribute("messageType", "danger");
        }

        response.sendRedirect(redirectUrl);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        System.out.println(">> action = " + action);

        DAOSlot dao = new DAOSlot();

        String redirectUrl = request.getContextPath() + "/slot";
        String message = null;
        String messageType = "info";

        try {
            if ("deleteSingle".equals(action)) {
                String slotId = request.getParameter("slotId");
                if (slotId == null || slotId.isEmpty()) {
                    message = "Thiếu ID slot để xóa.";
                    messageType = "danger";
                } else if (!dao.slotExists(slotId)) {
                    message = "Slot không tồn tại.";
                    messageType = "danger";
                } else if (dao.isSlotBooked(slotId)) {
                    message = "Không thể xóa slot vì đã được đặt.";
                    messageType = "warning";
                } else {
                    dao.deleteSlot(slotId);
                    message = "Xóa slot thành công.";
                    messageType = "success";
                }

            } else if ("deleteMultiple".equals(action)) {
                String[] slotIds = request.getParameterValues("slotIds");
                if (slotIds == null || slotIds.length == 0) {
                    message = "Không có slot nào được chọn để xóa.";
                    messageType = "warning";
                } else {
                    boolean canDeleteAll = true;
                    for (String id : slotIds) {
                        if (!dao.slotExists(id)) {
                            message = "Slot ID " + id + " không tồn tại.";
                            messageType = "danger";
                            canDeleteAll = false;
                            break;
                        } else if (dao.isSlotBooked(id)) {
                            message = "Slot ID " + id + " đã được đặt, không thể xóa.";
                            messageType = "warning";
                            canDeleteAll = false;
                            break;
                        }
                    }

                    if (canDeleteAll) {
                        int deletedCount = dao.deleteMultipleSlots(slotIds);
                        message = "Đã xóa " + deletedCount + " slot thành công.";
                        messageType = "success";
                    }
                }

            } else {
                message = "Hành động xóa không hợp lệ.";
                messageType = "danger";
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa slot: " + e.getMessage(), e);
            message = "Đã xảy ra lỗi khi xóa slot.";
            messageType = "danger";
        }

        session.setAttribute("message", message);
        session.setAttribute("messageType", messageType);
        response.sendRedirect(redirectUrl);
    }
}
