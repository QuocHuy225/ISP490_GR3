package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "HomepageController", urlPatterns = {"/homepage"})
public class HomepageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        Object userRole = session.getAttribute("userRole");

        request.setAttribute("user", user);
        request.setAttribute("userRole", userRole);

        // ✅ DEBUG: In ra role và kiểu dữ liệu
        System.out.println("🔍 userRole (session): " + userRole);
        System.out.println("🔍 userRole type: " + (userRole != null ? userRole.getClass().getName() : "null"));

        // ✅ Kiểm tra nếu là lễ tân
        if (userRole instanceof User.Role && ((User.Role) userRole) == User.Role.RECEPTIONIST) {
            System.out.println("✅ Role là lễ tân. Tiến hành lấy danh sách bác sĩ chưa có lịch...");
            try {
                DAODoctor daoDoctor = new DAODoctor();
                List<Doctor> doctorsWithoutSchedule = daoDoctor.getDoctorsWithoutSchedule();

                System.out.println("👉 Số bác sĩ chưa có lịch: " + doctorsWithoutSchedule.size());
                for (Doctor d : doctorsWithoutSchedule) {
                    System.out.println("⚠️ Bác sĩ chưa có lịch: " + d.getFullName());
                }

                request.setAttribute("doctorsWithoutSchedule", doctorsWithoutSchedule);
                if(doctorsWithoutSchedule != null && !doctorsWithoutSchedule.isEmpty()){
                    session.setAttribute("scheduleToastMessage","Có" + doctorsWithoutSchedule.size()+ " bác sĩ chưa có lịch làm việc ! ");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Không phải role lễ tân, không thực hiện kiểm tra bác sĩ chưa có lịch.");
        }

        request.getRequestDispatcher("/jsp/homepage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

