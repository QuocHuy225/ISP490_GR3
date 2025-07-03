package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // Nếu bạn dùng annotation thay vì web.xml
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// Nếu bạn dùng annotation, hãy đảm bảo nó khớp với web.xml
// @WebServlet("/receptionist/manage-doctor-schedule")
public class ReceptionistScheduleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chuyển hướng đến trang JSP của lịch làm việc bác sĩ
        // Đảm bảo đường dẫn này đúng với vị trí của file JSP của bạn
        if (!checkReceptionistAccess(request, response)) {
            return;
        }
        request.getRequestDispatcher("/jsp/manage-doctor-schedule.jsp").forward(request, response);
    }

    // Nếu bạn có các phương thức POST, PUT, DELETE khác cho trang này, bạn có thể thêm vào đây
    // Ví dụ, nếu có form trên trang JSP này gửi dữ liệu POST về
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Xử lý các yêu cầu POST nếu có form trên JSP này
        // Ví dụ: request.getRequestDispatcher("/jsp/manage-doctor-schedule.jsp").forward(request, response);
    }
    private boolean checkReceptionistAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }

        // Get current user
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }

        // Allow both Admin and Doctor to access
        if (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.RECEPTIONIST) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }

        return true;
    }
}