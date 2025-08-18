    package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.model.User;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
    import java.io.IOException;

    
    @WebServlet(name = "PatientAppointmentPageController", urlPatterns = {"/patient/my-appointments"})
    public class PatientAppointmentPageController extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            // Chuyển tiếp yêu cầu đến file JSP
            if (!checkPatientAccess(request, response)) {
            return;
        }
            request.getRequestDispatcher("/jsp/patient-appointment-schedule.jsp").forward(request, response);
        }
        private boolean checkPatientAccess(HttpServletRequest request, HttpServletResponse response)
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
        if (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.PATIENT) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }

        return true;
    }
    }
    