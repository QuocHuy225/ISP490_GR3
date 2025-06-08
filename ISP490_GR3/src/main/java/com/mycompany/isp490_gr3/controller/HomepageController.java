package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "HomepageController", urlPatterns = {"/homepage", "/home", "/dashboard"})
public class HomepageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set character encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            // Redirect to login page if not logged in
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        // Get user information from session
        User user = (User) session.getAttribute("user");
        Object userRole = session.getAttribute("userRole");
        
        // Add user info to request for JSP access
        request.setAttribute("user", user);
        request.setAttribute("userRole", userRole);
        
        // Forward to homepage JSP
        request.getRequestDispatcher("/jsp/homepage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect POST requests to GET
        doGet(request, response);
    }
} 