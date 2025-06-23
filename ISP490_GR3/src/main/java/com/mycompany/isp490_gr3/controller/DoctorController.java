/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.controller;
import com.mycompany.isp490_gr3.dao.DAODoctor; // Import DoctorDAO
import com.mycompany.isp490_gr3.model.Doctor; // Import Doctor model

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "DoctorController", urlPatterns = {"/doctors"})
public class DoctorController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DAODoctor DAOdoctor; // Khởi tạo đối tượng DAO

    @Override
    public void init() throws ServletException {
        super.init();
        DAOdoctor = new DAODoctor(); // Khởi tạo DoctorDAO khi Servlet được khởi tạo
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy các tham số từ URL (search query, số trang, số mục trên mỗi trang)
        String searchQuery = request.getParameter("search");
        String pageStr = request.getParameter("page");
        String limitStr = request.getParameter("limit");

        // Xử lý tham số số trang (mặc định là 1)
        int currentPage = 1;
        if (pageStr != null && pageStr.matches("\\d+")) {
            currentPage = Integer.parseInt(pageStr);
            if (currentPage < 1) currentPage = 1; // Đảm bảo số trang không âm
        }

        // Xử lý tham số số mục trên mỗi trang (mặc định là 10)
        int itemsPerPage = 10;
        if (limitStr != null && limitStr.matches("\\d+")) {
            itemsPerPage = Integer.parseInt(limitStr);
            if (itemsPerPage < 1) itemsPerPage = 10; // Đảm bảo số mục không âm
        }

        // Tính toán offset (vị trí bắt đầu lấy dữ liệu từ database)
        int offset = (currentPage - 1) * itemsPerPage;

        // Lấy danh sách bác sĩ từ DAO, áp dụng tìm kiếm và phân trang
        List<Doctor> doctors = DAOdoctor.getAllDoctors(searchQuery, itemsPerPage, offset);
        // Lấy tổng số bác sĩ (dùng cho phân trang)
        int totalDoctors = DAOdoctor.getTotalDoctors(searchQuery);

        // Đặt các thuộc tính vào request để JSP có thể truy cập
        request.setAttribute("doctors", doctors);
        request.setAttribute("totalDoctors", totalDoctors);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("itemsPerPage", itemsPerPage);
        request.setAttribute("searchQuery", searchQuery != null ? searchQuery : ""); // Giữ lại giá trị tìm kiếm trên input

        // Chuyển tiếp yêu cầu và dữ liệu tới trang JSP hiển thị danh sách bác sĩ
        request.getRequestDispatcher("/jsp/doctor-list.jsp").forward(request, response);
    }

    // Các phương thức khác như doPost, doPut, doDelete nếu cần cho các thao tác CRUD khác
}
