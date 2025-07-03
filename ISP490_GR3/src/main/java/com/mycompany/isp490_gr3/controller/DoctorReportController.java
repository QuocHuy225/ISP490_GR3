package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.mycompany.isp490_gr3.dao.DAOReport;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "DoctorReportController", urlPatterns = {"/doctor/report/*"})
public class DoctorReportController extends HttpServlet {

    private DAOReport daoReport;

    @Override
    public void init() throws ServletException {
        daoReport = new DAOReport();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkDoctorAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            showDoctorDashboard(request, response);
        } else {
            switch (pathInfo) {
                case "/appointment":
                    showDoctorAppointmentReport(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/doctor/homepage");
                    break;
            }
        }
    }

    private void showDoctorDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        int doctorId = daoReport.getDoctorIdByAccountId(currentUser.getId());

        // Gọi DAO để lấy thống kê
        Map<String, Object> stats = daoReport.getDoctorStatistics(doctorId);

        // Đưa dữ liệu vào attribute
        request.setAttribute("doctorStats", stats);
        request.setAttribute("appointmentsByServiceJson", new Gson().toJson(stats.get("appointmentsByService")));
        request.setAttribute("appointmentsByStatusJson", new Gson().toJson(stats.get("appointmentsByStatus")));

        // Gửi sang JSP
        request.getRequestDispatcher("/jsp/report/doctor-dashboard.jsp").forward(request, response);
    }

    private void showDoctorAppointmentReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        int doctorId = daoReport.getDoctorIdByAccountId(currentUser.getId()); // thêm hàm getDoctorIdByAccountId()

        Map<String, Object> stats = daoReport.getDoctorStatistics(doctorId);

        request.setAttribute("doctorStats", stats);
        request.setAttribute("appointmentsByServiceJson", new Gson().toJson(stats.get("appointmentsByService")));
        request.setAttribute("appointmentsByStatusJson", new Gson().toJson(stats.get("appointmentsByStatus")));

        request.getRequestDispatcher("/jsp/report/doctor-report.jsp").forward(request, response);
    }

    private boolean checkDoctorAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRole() != User.Role.DOCTOR) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }

        return true;
    }
}
