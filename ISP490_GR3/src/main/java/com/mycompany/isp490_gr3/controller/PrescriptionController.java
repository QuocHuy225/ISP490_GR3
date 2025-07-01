package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOPrescription;
import com.mycompany.isp490_gr3.model.PrescriptionMedicine;
import com.mycompany.isp490_gr3.model.User;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * =====================================================
 * PrescriptionController - CONTROLLER QUẢN LÝ THUỐC
 * 
 * Chức năng: Xử lý các request liên quan đến quản lý thuốc
 * URL pattern: /admin/prescriptions, /admin/prescriptions/*
 * DAO sử dụng: DAOPrescription
 * JSP tương ứng: prescription-management.jsp
 * 
 * Các chức năng chính:
 * - Hiển thị trang quản lý thuốc
 * - Quản lý thuốc trên thị trường (CRUD + search)
 * - Ajax endpoints cho các thao tác
 * - Chỉ Admin mới được truy cập
 * =====================================================
 */

@WebServlet(name = "PrescriptionController", urlPatterns = {"/admin/prescriptions", "/admin/prescriptions/*"})
public class PrescriptionController extends HttpServlet {

    private DAOPrescription prescriptionDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        prescriptionDAO = new DAOPrescription();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set character encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // Check admin access
        if (!checkAdminAccess(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");
        
        if (pathInfo != null) {
            if (pathInfo.equals("/medicines")) {
                handleMedicineActions(request, response, action);
            } else {
                handleMainPage(request, response);
            }
        } else {
            // Handle search actions on main page
            if ("searchMedicine".equals(action)) {
                handleSearchMedicinesMain(request, response);
            } else {
                handleMainPage(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set character encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Check admin access
        if (!checkAdminAccess(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");
        
        if (pathInfo != null) {
            if (pathInfo.equals("/medicines")) {
                handleMedicinePostActions(request, response, action);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
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
    
    private void handleMainPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editMedicineId = request.getParameter("editMedicine");
        PrescriptionMedicine editMedicine = null;
        
        if (editMedicineId != null && !editMedicineId.trim().isEmpty()) {
            try {
                int medicineId = Integer.parseInt(editMedicineId);
                editMedicine = prescriptionDAO.getMedicineById(medicineId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        // Load medicines for the main page
        List<PrescriptionMedicine> medicines = prescriptionDAO.getAllMedicines();
        
        request.setAttribute("medicines", medicines);
        request.setAttribute("editMedicine", editMedicine);
        
        request.getRequestDispatcher("/jsp/prescription-management.jsp").forward(request, response);
    }
    
    private void handleSearchMedicinesMain(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editMedicineId = request.getParameter("editMedicine");
        PrescriptionMedicine editMedicine = null;
        
        if (editMedicineId != null && !editMedicineId.trim().isEmpty()) {
            try {
                int medicineId = Integer.parseInt(editMedicineId);
                editMedicine = prescriptionDAO.getMedicineById(medicineId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        String keyword = request.getParameter("keyword");
        List<PrescriptionMedicine> medicines;
        
        if (keyword == null || keyword.trim().isEmpty()) {
            medicines = prescriptionDAO.getAllMedicines();
        } else {
            medicines = prescriptionDAO.searchMedicines(keyword);
            request.setAttribute("medicineSearchKeyword", keyword);
        }
        
        request.setAttribute("medicines", medicines);
        request.setAttribute("editMedicine", editMedicine);
        
        request.getRequestDispatcher("/jsp/prescription-management.jsp").forward(request, response);
    }
    
    // ===============================
    // MEDICINE HANDLERS
    // ===============================
    
    private void handleMedicineActions(HttpServletRequest request, HttpServletResponse response, String action)
            throws ServletException, IOException {
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "search":
                handleSearchMedicines(request, response);
                break;
            case "get":
                handleGetMedicine(request, response);
                break;
            default:
                handleListMedicines(request, response);
                break;
        }
    }
    
    private void handleMedicinePostActions(HttpServletRequest request, HttpServletResponse response, String action)
            throws ServletException, IOException {
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=no_action");
            return;
        }
        
        switch (action) {
            case "add":
                handleAddMedicine(request, response);
                break;
            case "update":
                handleUpdateMedicine(request, response);
                break;
            case "delete":
                handleDeleteMedicine(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=invalid_action");
                break;
        }
    }
    
    private void handleListMedicines(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<PrescriptionMedicine> medicines = prescriptionDAO.getAllMedicines();
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.print(gson.toJson(medicines));
    }
    
    private void handleSearchMedicines(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        List<PrescriptionMedicine> medicines;
        
        if (keyword == null || keyword.trim().isEmpty()) {
            medicines = prescriptionDAO.getAllMedicines();
        } else {
            medicines = prescriptionDAO.searchMedicines(keyword);
        }
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.print(gson.toJson(medicines));
    }
    
    private void handleGetMedicine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing medicine ID");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            PrescriptionMedicine medicine = prescriptionDAO.getMedicineById(id);
            
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            out.print(gson.toJson(medicine));
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid medicine ID");
        }
    }
    
    private void handleAddMedicine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String medicineName = request.getParameter("medicineName");
        String unitOfMeasure = request.getParameter("unitOfMeasure");
        String administrationRoute = request.getParameter("administrationRoute");
        String daysOfTreatmentStr = request.getParameter("daysOfTreatment");
        String unitsPerDayStr = request.getParameter("unitsPerDay");
        String totalQuantityStr = request.getParameter("totalQuantity");
        String usageInstructions = request.getParameter("usageInstructions");
        
        // Validate required fields
        if (medicineName == null || medicineName.trim().isEmpty() ||
            unitOfMeasure == null || unitOfMeasure.trim().isEmpty() ||
            administrationRoute == null || administrationRoute.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=missing_fields");
            return;
        }
        
        // Check if medicine name already exists
        if (prescriptionDAO.isMedicineNameExists(medicineName.trim(), 0)) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=medicine_exists");
            return;
        }
        
        try {
            PrescriptionMedicine medicine = new PrescriptionMedicine();
            medicine.setMedicineName(medicineName.trim());
            medicine.setUnitOfMeasure(unitOfMeasure.trim());
            medicine.setAdministrationRoute(administrationRoute.trim());
            medicine.setUsageInstructions(usageInstructions != null ? usageInstructions.trim() : "");
            
            // Parse optional numeric fields
            if (daysOfTreatmentStr != null && !daysOfTreatmentStr.trim().isEmpty()) {
                medicine.setDaysOfTreatment(Integer.parseInt(daysOfTreatmentStr.trim()));
            }
            if (unitsPerDayStr != null && !unitsPerDayStr.trim().isEmpty()) {
                medicine.setUnitsPerDay(Integer.parseInt(unitsPerDayStr.trim()));
            }
            if (totalQuantityStr != null && !totalQuantityStr.trim().isEmpty()) {
                medicine.setTotalQuantity(Integer.parseInt(totalQuantityStr.trim()));
            }
            
            boolean success = prescriptionDAO.addMedicine(medicine);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?success=medicine_added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=add_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=invalid_format");
        }
    }
    
    private void handleUpdateMedicine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("medicineId");
        String medicineName = request.getParameter("medicineName");
        String unitOfMeasure = request.getParameter("unitOfMeasure");
        String administrationRoute = request.getParameter("administrationRoute");
        String daysOfTreatmentStr = request.getParameter("daysOfTreatment");
        String unitsPerDayStr = request.getParameter("unitsPerDay");
        String totalQuantityStr = request.getParameter("totalQuantity");
        String usageInstructions = request.getParameter("usageInstructions");
        
        if (idParam == null || medicineName == null || medicineName.trim().isEmpty() ||
            unitOfMeasure == null || unitOfMeasure.trim().isEmpty() ||
            administrationRoute == null || administrationRoute.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=missing_fields");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            
            // Check if medicine name already exists (excluding current medicine)
            if (prescriptionDAO.isMedicineNameExists(medicineName.trim(), id)) {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=medicine_exists");
                return;
            }
            
            PrescriptionMedicine medicine = new PrescriptionMedicine();
            medicine.setPreMedicineId(id);
            medicine.setMedicineName(medicineName.trim());
            medicine.setUnitOfMeasure(unitOfMeasure.trim());
            medicine.setAdministrationRoute(administrationRoute.trim());
            medicine.setUsageInstructions(usageInstructions != null ? usageInstructions.trim() : "");
            
            // Parse optional numeric fields
            if (daysOfTreatmentStr != null && !daysOfTreatmentStr.trim().isEmpty()) {
                medicine.setDaysOfTreatment(Integer.parseInt(daysOfTreatmentStr.trim()));
            }
            if (unitsPerDayStr != null && !unitsPerDayStr.trim().isEmpty()) {
                medicine.setUnitsPerDay(Integer.parseInt(unitsPerDayStr.trim()));
            }
            if (totalQuantityStr != null && !totalQuantityStr.trim().isEmpty()) {
                medicine.setTotalQuantity(Integer.parseInt(totalQuantityStr.trim()));
            }
            
            boolean success = prescriptionDAO.updateMedicine(medicine);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?success=medicine_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=invalid_format");
        }
    }
    
    private void handleDeleteMedicine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("medicineId");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=missing_id");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            boolean success = prescriptionDAO.deleteMedicine(id);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?success=medicine_deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=invalid_format");
        }
    }
} 