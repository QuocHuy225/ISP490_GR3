package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.mycompany.isp490_gr3.dao.DAOReport;
import com.mycompany.isp490_gr3.model.ReportData;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "ReportController", urlPatterns = {"/admin/report/*"})
public class ReportController extends HttpServlet {

    private DAOReport daoReport;

    @Override
    public void init() throws ServletException {
        daoReport = new DAOReport();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkAdminAccess(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            showReportDashboard(request, response);
        } else {
            switch (pathInfo) {
                case "/patient":
                    showPatientReport(request, response);
                    break;
                case "/medical-record":
                    showMedicalRecordReport(request, response);
                    break;
                case "/invoice":
                    showInvoiceReport(request, response);
                    break;
                case "/appointment":
                    showAppointmentReport(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/report");
                    break;
            }
        }
    }

    private void showAppointmentReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, Object> appointmentStats = daoReport.getAppointmentStatistics();

        // Chuyển dữ liệu danh sách sang JSON để vẽ biểu đồ
        Gson gson = new Gson();
        String appointmentsByDateJson = gson.toJson(appointmentStats.get("appointmentsByDate"));
        String appointmentsByDoctorJson = gson.toJson(appointmentStats.get("appointmentsByDoctor"));
        String appointmentsByServiceJson = gson.toJson(appointmentStats.get("appointmentsByService"));
        String appointmentsByStatusJson = gson.toJson(appointmentStats.get("appointmentsByStatus")); // Mới

        // Đưa vào attribute để sử dụng ở JSP
        request.setAttribute("appointmentStats", appointmentStats);
        request.setAttribute("appointmentsByDateJson", appointmentsByDateJson);
        request.setAttribute("appointmentsByDoctorJson", appointmentsByDoctorJson);
        request.setAttribute("appointmentsByServiceJson", appointmentsByServiceJson);
        request.setAttribute("appointmentsByStatusJson", appointmentsByStatusJson); // Mới

        request.getRequestDispatcher("/jsp/report/appointment-report.jsp").forward(request, response);
    }

    private void showReportDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ReportData summaryData = daoReport.getSummaryData();
        request.setAttribute("summaryData", summaryData);
        request.getRequestDispatcher("/jsp/report/dashboard.jsp").forward(request, response);
    }

    private void showPatientReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> patientStats = daoReport.getPatientStatistics();
        request.setAttribute("patientStats", patientStats);
        request.getRequestDispatcher("/jsp/report/patient-report.jsp").forward(request, response);
    }

    private void showMedicalRecordReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> medicalRecordStats = daoReport.getMedicalRecordStatistics();
        request.setAttribute("medicalRecordStats", medicalRecordStats);
        request.getRequestDispatcher("/jsp/report/medical-record-report.jsp").forward(request, response);
    }

    private void showInvoiceReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> invoiceStats = daoReport.getInvoiceStatistics();
        request.setAttribute("invoiceStats", invoiceStats);
        request.getRequestDispatcher("/jsp/report/invoice-report.jsp").forward(request, response);
    }

    private boolean checkAdminAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }

        return true;
    }
}
