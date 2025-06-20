/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author Acer
 */
@WebServlet(name = "MakeAppointmentController", urlPatterns = {"/makeappointments"})
public class MakeAppointmentController extends HttpServlet {
     private static final long serialVersionUID = 1L;
     
     protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
         request.getRequestDispatcher("/jsp/make-appointment.jsp").forward(request, response);
    }
}
