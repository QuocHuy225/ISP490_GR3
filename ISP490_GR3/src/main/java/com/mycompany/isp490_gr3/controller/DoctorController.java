package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.model.Doctor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "DoctorController", urlPatterns = {"/doctors", "/viewdoctor"})
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

        // Nếu người dùng truy cập /viewdoctor (ví dụ từ nút "Xem tất cả"),
        // chúng ta sẽ chuyển hướng họ đến /doctors để hiển thị danh sách đầy đủ
        if ("/viewdoctor".equals(path)) {
            // Chuyển hướng sang /doctors để hiển thị danh sách tất cả bác sĩ với phân trang
            // Bạn có thể thêm các tham số mặc định nếu muốn, ví dụ: ?page=1&limit=10
            response.sendRedirect(request.getContextPath() + "/doctors");
            return; // Rất quan trọng: dừng xử lý sau khi chuyển hướng
        } 
        
        // Logic cho /doctors (danh sách tất cả bác sĩ với tìm kiếm/phân trang)
        if ("/doctors".equals(path)) { 
            String searchQuery = request.getParameter("search");
            String pageStr = request.getParameter("page");
            String limitStr = request.getParameter("limit");

            int currentPage = 1;
            if (pageStr != null && pageStr.matches("\\d+")) {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            }

            int itemsPerPage = 10; // Số lượng bác sĩ mỗi trang mặc định
            if (limitStr != null && limitStr.matches("\\d+")) {
                itemsPerPage = Integer.parseInt(limitStr);
                if (itemsPerPage < 1) itemsPerPage = 10;
            }

            int offset = (currentPage - 1) * itemsPerPage;
            List<Doctor> doctors = DAOdoctor.getAllDoctors(searchQuery, itemsPerPage, offset);
            int totalDoctors = DAOdoctor.getTotalDoctors(searchQuery);

            // Đặt các attribute vào request để JSP có thể hiển thị
            request.setAttribute("doctors", doctors);
            request.setAttribute("totalDoctors", totalDoctors);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("itemsPerPage", itemsPerPage);
            request.setAttribute("searchQuery", searchQuery != null ? searchQuery : "");

            // Chuyển tiếp đến doctor-list.jsp của bạn
            request.getRequestDispatcher("/jsp/doctor-list.jsp").forward(request, response);
        }
    }
}