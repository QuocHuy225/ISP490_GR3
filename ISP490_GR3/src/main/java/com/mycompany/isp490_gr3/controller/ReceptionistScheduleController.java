package com.mycompany.isp490_gr3.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // Nếu bạn dùng annotation thay vì web.xml
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Nếu bạn dùng annotation, hãy đảm bảo nó khớp với web.xml
// @WebServlet("/receptionist/manage-doctor-schedule")
public class ReceptionistScheduleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chuyển hướng đến trang JSP của lịch làm việc bác sĩ
        // Đảm bảo đường dẫn này đúng với vị trí của file JSP của bạn
        request.getRequestDispatcher("/jsp/manage-doctor-schedule.jsp").forward(request, response);
    }

    // Nếu bạn có các phương thức POST, PUT, DELETE khác cho trang này, bạn có thể thêm vào đây
    // Ví dụ, nếu có form trên trang JSP này gửi dữ liệu POST về
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Xử lý các yêu cầu POST nếu có form trên JSP này
        // Ví dụ: request.getRequestDispatcher("/jsp/manage-doctor-schedule.jsp").forward(request, response);
    }
}