package com.mycompany.isp490_gr3.controller;

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
        super.init();
        daoReport = new DAOReport();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check admin access
        if (!checkAdminAccess(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Default report page
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
                default:
                    response.sendRedirect(request.getContextPath() + "/report");
                    break;
            }
        }
    }

    private void showReportDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get summary data for dashboard
        ReportData summaryData = daoReport.getSummaryData();
        request.setAttribute("summaryData", summaryData);
        request.getRequestDispatcher("/jsp/report/dashboard.jsp").forward(request, response);
    }

    private void showPatientReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get patient statistics
        Map<String, Object> patientStats = daoReport.getPatientStatistics();
        request.setAttribute("patientStats", patientStats);
        request.getRequestDispatcher("/jsp/report/patient-report.jsp").forward(request, response);
    }

    private void showMedicalRecordReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get medical record statistics
        Map<String, Object> medicalRecordStats = daoReport.getMedicalRecordStatistics();
        request.setAttribute("medicalRecordStats", medicalRecordStats);
        request.getRequestDispatcher("/jsp/report/medical-record-report.jsp").forward(request, response);
    }

    private void showInvoiceReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get invoice statistics
        Map<String, Object> invoiceStats = daoReport.getInvoiceStatistics();
        request.setAttribute("invoiceStats", invoiceStats);
        request.getRequestDispatcher("/jsp/report/invoice-report.jsp").forward(request, response);
    }
    
    /**
     * Check admin access - standardized across all controllers
     */
    private boolean checkAdminAccess(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
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
        
        // Check if user is admin
        if (currentUser.getRole() != User.Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }
        
        return true;
    }
} 