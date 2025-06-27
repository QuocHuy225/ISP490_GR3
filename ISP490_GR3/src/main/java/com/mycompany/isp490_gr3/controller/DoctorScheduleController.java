/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAODoctorSchedule;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FPT SHOP
 */
@WebServlet(name = "DoctorScheduleController", urlPatterns = {"/doctor/schedule"})
public class DoctorScheduleController extends HttpServlet {

    private final DAODoctorSchedule dao = new DAODoctorSchedule();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            List<String> dates = dao.getWorkingDatesByDoctorId(doctorId);
            String json = new com.google.gson.Gson().toJson(dates != null ? dates : new ArrayList<>());
            System.out.println(json);
            response.getWriter().write(json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid doctorId format.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().write("{\"error\": \"POST method not supported.\"}");
    }

    @Override
    public String getServletInfo() {
        return "Handles doctor schedule retrieval";
    }
}
