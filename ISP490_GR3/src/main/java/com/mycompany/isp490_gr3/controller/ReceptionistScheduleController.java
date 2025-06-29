package com.mycompany.isp490_gr3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.model.Doctor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ReceptionistScheduleController", urlPatterns = {"/receptionist/manage-doctor-schedule"})
public class ReceptionistScheduleController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ReceptionistScheduleController.class.getName());

    private DAODoctor daoDoctor;
    private ObjectMapper objectMapper; 

    @Override
    public void init() throws ServletException {
        super.init();
        daoDoctor = new DAODoctor();
        objectMapper = new ObjectMapper(); 
        LOGGER.info("ReceptionistScheduleController initialized.");
    }

    private boolean checkAdminOrReceptionistRole(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {
        HttpSession session = request.getSession();
        Object userRole = session.getAttribute("userRole");

        if (userRole == null || (!"ADMIN".equalsIgnoreCase(userRole.toString()) && !"RECEPTIONIST".equalsIgnoreCase(userRole.toString()))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.write("{\"error\": \"Truy cập bị từ chối. Cần quyền Admin hoặc Lễ tân.\"}");
            LOGGER.log(Level.WARNING, "Truy cập bị từ chối cho người dùng. Vai trò người dùng: {0}. Cần quyền Admin hoặc Lễ tân.", userRole);
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        if (!checkAdminOrReceptionistRole(request, response, out)) {
            return;
        }

        try {
            List<Doctor> doctors = daoDoctor.getAllDoctors();
            request.setAttribute("doctors", doctors);
            LOGGER.info("Loaded " + doctors.size() + " doctors for schedule management page.");

            request.getRequestDispatcher("/jsp/manage-doctor-schedule.jsp").forward(request, response);
            LOGGER.info("Forwarded to manage-doctor-schedule.jsp.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in doGet for receptionist schedule: " + e.getMessage(), e);
            request.setAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn khi tải trang.");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.warning("POST request received by ReceptionistScheduleController, but this controller no longer handles schedule updates directly.");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().write("{\"message\": \"Phương thức POST không được hỗ trợ trực tiếp trên trang này. Vui lòng sử dụng modal.\"}}");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "86400");
        resp.setStatus(HttpServletResponse.SC_OK);
        LOGGER.info("Handled OPTIONS pre-flight request for ReceptionistScheduleController.");
    }

    @Override
    public void destroy() {
        if (daoDoctor != null) {
            daoDoctor.closeConnection();
        }
        super.destroy();
        LOGGER.info("ReceptionistScheduleController destroyed. DAO connections closed.");
    }
}