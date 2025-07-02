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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ReceptionistReportController", urlPatterns = {"/receptionist/report/*"})
public class ReceptionistReportController extends HttpServlet {

    private DAOReport daoReport;

    @Override
    public void init() throws ServletException {
        daoReport = new DAOReport();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkReceptionistAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            showReceptionistDashboard(request, response);
        } else {
            switch (pathInfo) {
                case "/appointment":
                    showReceptionistAppointmentReport(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/receptionist/homepage");
                    break;
            }
        }
    }

    private void showReceptionistDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Tạo một Map để chứa tất cả các số liệu thống kê cho dashboard
        Map<String, Object> dashboardStats = new HashMap<>();

        // 1. Lấy tổng số lịch hẹn và lịch hẹn hôm nay từ getAppointmentStatistics()
        Map<String, Object> appointmentStats = daoReport.getAppointmentStatistics();
        if (appointmentStats != null) {
            dashboardStats.put("totalAppointments", appointmentStats.get("totalAppointments"));
            dashboardStats.put("todayAppointments", appointmentStats.get("todayAppointments"));
        } else {
            dashboardStats.put("totalAppointments", 0);
            dashboardStats.put("todayAppointments", 0);
        }

        // 2. Lấy tổng số bệnh nhân từ getPatientStatistics()
        Map<String, Object> patientStats = daoReport.getPatientStatistics();
        if (patientStats != null) {
            dashboardStats.put("totalPatients", patientStats.get("totalPatients"));
        } else {
            dashboardStats.put("totalPatients", 0);
        }

        // 3. Lấy bệnh nhân mới trong tuần từ phương thức mới thêm vào DAO
        int newPatientsThisWeek = daoReport.getNewPatientsThisWeek();
        dashboardStats.put("newPatientsThisWeek", newPatientsThisWeek);

        // Đặt Map chứa tất cả các số liệu vào request scope với tên "receptionistStats"
        // Tên này phải khớp với tên bạn sử dụng trong JSP: ${receptionistStats.<thuoc_tinh>}
        request.setAttribute("receptionistStats", dashboardStats);

        // Chuyển tiếp đến JSP dashboard của tiếp tân
        request.getRequestDispatcher("/jsp/report/receptionist-dashboard.jsp").forward(request, response);
    }

    private void showReceptionistAppointmentReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, Object> stats = daoReport.getAppointmentStatistics();
        List<Map<String, Object>> newPatientsByMonth = daoReport.getNewPatientsByMonth();

        Gson gson = new Gson();
        request.setAttribute("appointmentStats", stats);
        request.setAttribute("appointmentsByDateJson", gson.toJson(stats.get("appointmentsByDate")));
        request.setAttribute("appointmentsByDoctorJson", gson.toJson(stats.get("appointmentsByDoctor")));
        request.setAttribute("appointmentsByServiceJson", gson.toJson(stats.get("appointmentsByService")));
        request.setAttribute("appointmentsByStatusJson", gson.toJson(stats.get("appointmentsByStatus")));
        request.setAttribute("newPatientsByMonthJson", gson.toJson(newPatientsByMonth));

        request.getRequestDispatcher("/jsp/report/receptionist-report.jsp").forward(request, response);
    }

    private boolean checkReceptionistAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRole() != User.Role.RECEPTIONIST) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }

        return true;
    }
}
