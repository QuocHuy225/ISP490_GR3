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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.TimeZone; // Import TimeZone

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

        // Debugging logs for controller flow
        System.out.println("Controller_DEBUG: userRole (session): " + userRole);
        System.out.println("Controller_DEBUG: userRole type: " + (userRole != null ? userRole.getClass().getName() : "null"));

        if (userRole instanceof User.Role && ((User.Role) userRole) == User.Role.RECEPTIONIST) {
            System.out.println("Controller_DEBUG: ✅ Role là lễ tân. Tiến hành lấy danh sách bác sĩ chưa có lịch trong tuần...");
            try {
                DAODoctor daoDoctor = new DAODoctor();

                LocalDate today = LocalDate.now();
                // Calculate startOfPeriod to be Monday of the current week
                LocalDate startOfPeriod = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                // Calculate endOfPeriod to be Sunday of the current week
                LocalDate endOfPeriod = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                System.out.println("Controller_DEBUG: Ngày hiện tại (LocalDate.now()): " + today);
                System.out.println("Controller_DEBUG: Ngày bắt đầu kiểm tra lịch (Thứ Hai tuần này): " + startOfPeriod);
                System.out.println("Controller_DEBUG: Ngày kết thúc kiểm tra lịch (Chủ Nhật tuần này): " + endOfPeriod);
                System.out.println("Controller_DEBUG: Java Default TimeZone: " + TimeZone.getDefault().getID());

                List<Doctor> doctorsWithoutSchedule = daoDoctor.getDoctorsWithoutScheduleInPeriod(startOfPeriod, endOfPeriod);

                // Setting attributes in REQUEST scope for toast display on direct page load/reload
                request.setAttribute("doctorsWithoutSchedule", doctorsWithoutSchedule); 

                if (doctorsWithoutSchedule != null && !doctorsWithoutSchedule.isEmpty()) {
                    // Set toast message in REQUEST scope
                    request.setAttribute("scheduleToastMessage", "Có " + doctorsWithoutSchedule.size() + " bác sĩ chưa có lịch làm việc trong tuần này!"); 
                    System.out.println("Controller_DEBUG: 👉 Số bác sĩ chưa có lịch trong tuần: " + doctorsWithoutSchedule.size());
                    for (Doctor d : doctorsWithoutSchedule) {
                        System.out.println("Controller_DEBUG: ⚠️ Bác sĩ chưa có lịch trong tuần: ID=" + d.getId() + ", Name=" + d.getFullName());
                    }
                } else {
                    // Remove attribute if no message needed
                    request.removeAttribute("scheduleToastMessage"); 
                    System.out.println("Controller_DEBUG: Không có bác sĩ thiếu lịch. scheduleToastMessage được xóa khỏi request.");
                }

            } catch (SQLException e) {
                System.err.println("Controller_ERROR: Lỗi SQL khi kiểm tra lịch bác sĩ: " + e.getMessage());
                e.printStackTrace();
                // Set error message in REQUEST scope
                request.setAttribute("scheduleToastMessage", "Lỗi khi kiểm tra lịch bác sĩ."); 
            }
        } else {
            System.out.println("Controller_DEBUG: ❌ Không phải role lễ tân, không thực hiện kiểm tra bác sĩ chưa có lịch.");
            // Ensure no toast message is set in request for other roles
            request.removeAttribute("scheduleToastMessage"); 
        }

        request.getRequestDispatcher("/jsp/homepage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}