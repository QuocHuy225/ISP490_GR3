package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.Schedule; // Import lớp Schedule
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate; // Import LocalDate
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter
import java.util.List;

@WebServlet(name = "DoctorController", urlPatterns = {"/doctors", "/viewdoctor", "/book-appointment"})
public class DoctorController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DAODoctor DAOdoctor;

    @Override
    public void init() throws ServletException {
        super.init();
        DAOdoctor = new DAODoctor();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        // Xử lý trang danh sách bác sĩ
        if ("/doctors".equals(path)) {
            String searchQuery = request.getParameter("search");
            String pageStr = request.getParameter("page");
            String limitStr = request.getParameter("limit");

            int currentPage = 1;
            if (pageStr != null && pageStr.matches("\\d+")) {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            }

            int itemsPerPage = 10;
            if (limitStr != null && limitStr.matches("\\d+")) {
                itemsPerPage = Integer.parseInt(limitStr);
                if (itemsPerPage < 1) itemsPerPage = 10;
            }

            int offset = (currentPage - 1) * itemsPerPage;
            List<Doctor> doctors = DAOdoctor.getAllDoctors(searchQuery, itemsPerPage, offset);
            int totalDoctors = DAOdoctor.getTotalDoctors(searchQuery);

            request.setAttribute("doctors", doctors);
            request.setAttribute("totalDoctors", totalDoctors);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("itemsPerPage", itemsPerPage);
            request.setAttribute("searchQuery", searchQuery != null ? searchQuery : "");

            request.getRequestDispatcher("/jsp/doctor-list.jsp").forward(request, response);
            return;
        }

        // Xử lý trang chi tiết bác sĩ và đặt lịch
        if ("/book-appointment".equals(path) || "/viewdoctor".equals(path)) { // Cả /viewdoctor cũng dẫn đến trang chi tiết
            String doctorIdStr = request.getParameter("id"); // Sử dụng "id" thay vì "doctorId" nếu link của bạn là /book-appointment?id=X

            if (doctorIdStr != null && !doctorIdStr.isEmpty()) {
                try {
                    int doctorId = Integer.parseInt(doctorIdStr);
                    Doctor doctor = DAOdoctor.getDoctorById(doctorId); // Lấy thông tin bác sĩ

                    if (doctor != null) {
                        // Lấy lịch trình của bác sĩ cho 60 ngày tới (bao gồm cả ngày hiện tại)
                        LocalDate startDate = LocalDate.now();
                        LocalDate endDate = startDate.plusDays(59); // 0-indexed, 59 là ngày thứ 60
                        
                        List<Schedule> schedules = DAOdoctor.getDoctorSchedules(doctorId, startDate, endDate);

                        // Tạo DateTimeFormatter và đặt vào request attribute
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                        request.setAttribute("timeFormatter", timeFormatter);

                        request.setAttribute("doctor", doctor);
                        request.setAttribute("schedules", schedules); // Truyền lịch trình sang JSP

                        request.getRequestDispatcher("/jsp/doctor-detail.jsp").forward(request, response);
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy bác sĩ với ID: " + doctorId);
                    }
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID bác sĩ không hợp lệ.");
                } catch (Exception e) {
                    e.printStackTrace(); // Log lỗi để gỡ lỗi
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi tải chi tiết bác sĩ.");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID bác sĩ để xem chi tiết hoặc đặt lịch.");
            }
            return;
        }

        // Nếu không khớp với bất kỳ path nào, có thể chuyển hướng đến trang chủ hoặc báo lỗi
        response.sendRedirect(request.getContextPath() + "/homepage");
    }

    // Bạn có thể thêm doPost nếu có form submit để đặt lịch
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Xử lý logic đặt lịch tại đây
        // Ví dụ: lấy doctorId, date, timeSlot từ request parameters
        // Gọi DAO để lưu thông tin đặt lịch vào DB
        // Sau đó chuyển hướng người dùng đến trang xác nhận hoặc trang lịch hẹn của họ
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported yet for this path.");
    }
}
