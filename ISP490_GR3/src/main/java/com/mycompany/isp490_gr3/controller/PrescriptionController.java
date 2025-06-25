package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOPrescription;
import com.mycompany.isp490_gr3.model.PrescriptionMedicine;
import com.mycompany.isp490_gr3.model.PrescriptionForm;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * =====================================================
 * PrescriptionController - CONTROLLER QUẢN LÝ ĐƠN THUỐC
 * 
 * Chức năng: Xử lý các request liên quan đến quản lý đơn thuốc
 * URL pattern: /admin/prescriptions, /admin/prescriptions/*
 * DAO sử dụng: DAOPrescription
 * JSP tương ứng: prescription-management.jsp
 * 
 * Các chức năng chính:
 * - Hiển thị trang quản lý đơn thuốc
 * - Quản lý thuốc trên thị trường (CRUD + search)
 * - Quản lý đơn thuốc mẫu (CRUD + search)
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
            } else if (pathInfo.equals("/forms")) {
                handleFormActions(request, response, action);
            } else {
                handleMainPage(request, response);
            }
        } else {
            // Handle search actions on main page
            if ("searchMedicine".equals(action)) {
                handleSearchMedicinesMain(request, response);
            } else if ("searchForm".equals(action)) {
                handleSearchFormsMain(request, response);
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
            } else if (pathInfo.equals("/forms")) {
                handleFormPostActions(request, response, action);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private boolean checkAdminAccess(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return false;
        }
        
        Object userRole = session.getAttribute("userRole");
        if (userRole == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return false;
        }
        
        User.Role role = null;
        if (userRole instanceof User.Role) {
            role = (User.Role) userRole;
        } else {
            try {
                role = User.Role.valueOf(userRole.toString().toUpperCase());
            } catch (Exception e) {
                role = User.Role.fromString(userRole.toString());
            }
        }
        
        if (role != User.Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }
        
        return true;
    }
    
    private void handleMainPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editMedicineId = request.getParameter("editMedicine");
        String editFormId = request.getParameter("editForm");
        
        PrescriptionMedicine editMedicine = null;
        PrescriptionForm editForm = null;
        
        if (editMedicineId != null && !editMedicineId.trim().isEmpty()) {
            try {
                int medicineId = Integer.parseInt(editMedicineId);
                editMedicine = prescriptionDAO.getMedicineById(medicineId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        if (editFormId != null && !editFormId.trim().isEmpty()) {
            try {
                int formId = Integer.parseInt(editFormId);
                editForm = prescriptionDAO.getPrescriptionFormById(formId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        // Load both medicines and prescription forms for the main page
        List<PrescriptionMedicine> medicines = prescriptionDAO.getAllMedicines();
        List<PrescriptionForm> prescriptionForms = prescriptionDAO.getAllPrescriptionForms();
        
        request.setAttribute("medicines", medicines);
        request.setAttribute("prescriptionForms", prescriptionForms);
        request.setAttribute("editMedicine", editMedicine);
        request.setAttribute("editForm", editForm);
        request.setAttribute("allMedicines", medicines); // For form editing
        
        // Check if tab parameter is present
        String tab = request.getParameter("tab");
        if ("forms".equals(tab)) {
            request.setAttribute("activeTab", "forms");
        }
        
        request.getRequestDispatcher("/jsp/prescription-management.jsp").forward(request, response);
    }
    
    private void handleSearchMedicinesMain(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editMedicineId = request.getParameter("editMedicine");
        String editFormId = request.getParameter("editForm");
        
        PrescriptionMedicine editMedicine = null;
        PrescriptionForm editForm = null;
        
        if (editMedicineId != null && !editMedicineId.trim().isEmpty()) {
            try {
                int medicineId = Integer.parseInt(editMedicineId);
                editMedicine = prescriptionDAO.getMedicineById(medicineId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        if (editFormId != null && !editFormId.trim().isEmpty()) {
            try {
                int formId = Integer.parseInt(editFormId);
                editForm = prescriptionDAO.getPrescriptionFormById(formId);
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
        
        List<PrescriptionForm> prescriptionForms = prescriptionDAO.getAllPrescriptionForms();
        
        request.setAttribute("medicines", medicines);
        request.setAttribute("prescriptionForms", prescriptionForms);
        request.setAttribute("editMedicine", editMedicine);
        request.setAttribute("editForm", editForm);
        request.setAttribute("allMedicines", medicines); // For form editing
        
        request.getRequestDispatcher("/jsp/prescription-management.jsp").forward(request, response);
    }
    
    private void handleSearchFormsMain(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editMedicineId = request.getParameter("editMedicine");
        String editFormId = request.getParameter("editForm");
        
        PrescriptionMedicine editMedicine = null;
        PrescriptionForm editForm = null;
        
        if (editMedicineId != null && !editMedicineId.trim().isEmpty()) {
            try {
                int medicineId = Integer.parseInt(editMedicineId);
                editMedicine = prescriptionDAO.getMedicineById(medicineId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        if (editFormId != null && !editFormId.trim().isEmpty()) {
            try {
                int formId = Integer.parseInt(editFormId);
                editForm = prescriptionDAO.getPrescriptionFormById(formId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        String keyword = request.getParameter("keyword");
        List<PrescriptionForm> prescriptionForms;
        
        if (keyword == null || keyword.trim().isEmpty()) {
            prescriptionForms = prescriptionDAO.getAllPrescriptionForms();
        } else {
            prescriptionForms = prescriptionDAO.searchPrescriptionForms(keyword);
            request.setAttribute("formSearchKeyword", keyword);
        }
        
        List<PrescriptionMedicine> medicines = prescriptionDAO.getAllMedicines();
        
        request.setAttribute("medicines", medicines);
        request.setAttribute("prescriptionForms", prescriptionForms);
        request.setAttribute("editMedicine", editMedicine);
        request.setAttribute("editForm", editForm);
        request.setAttribute("allMedicines", medicines); // For form editing
        request.setAttribute("activeTab", "forms"); // Set active tab to forms
        
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
    
    // ===============================
    // PRESCRIPTION FORM HANDLERS
    // ===============================
    
    private void handleFormActions(HttpServletRequest request, HttpServletResponse response, String action)
            throws ServletException, IOException {
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "search":
                handleSearchForms(request, response);
                break;
            case "get":
                handleGetForm(request, response);
                break;
            default:
                handleListForms(request, response);
                break;
        }
    }
    
    private void handleFormPostActions(HttpServletRequest request, HttpServletResponse response, String action)
            throws ServletException, IOException {
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=no_action");
            return;
        }
        
        switch (action) {
            case "add":
                handleAddForm(request, response);
                break;
            case "update":
                handleUpdateForm(request, response);
                break;
            case "delete":
                handleDeleteForm(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=invalid_action");
                break;
        }
    }
    
    private void handleListForms(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<PrescriptionForm> forms = prescriptionDAO.getAllPrescriptionForms();
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.print(gson.toJson(forms));
    }
    
    private void handleSearchForms(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        List<PrescriptionForm> forms;
        
        if (keyword == null || keyword.trim().isEmpty()) {
            forms = prescriptionDAO.getAllPrescriptionForms();
        } else {
            forms = prescriptionDAO.searchPrescriptionForms(keyword);
        }
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.print(gson.toJson(forms));
    }
    
    private void handleGetForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing form ID");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            PrescriptionForm form = prescriptionDAO.getPrescriptionFormById(id);
            
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            out.print(gson.toJson(form));
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid form ID");
        }
    }
    
    private void handleAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String formName = request.getParameter("formName");
        String notes = request.getParameter("notes");
        String[] medicineIdsArray = request.getParameterValues("medicineIds");
        
        // Validate required fields
        if (formName == null || formName.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=missing_fields&tab=forms");
            return;
        }
        
        // Check if form name already exists
        if (prescriptionDAO.isFormNameExists(formName.trim(), 0)) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=form_exists&tab=forms");
            return;
        }
        
        try {
            PrescriptionForm form = new PrescriptionForm();
            form.setFormName(formName.trim());
            form.setNotes(notes != null ? notes.trim() : "");
            
            int formId = prescriptionDAO.addPrescriptionForm(form);
            
            if (formId > 0) {
                // Add selected medicines to the form
                if (medicineIdsArray != null && medicineIdsArray.length > 0) {
                    List<Integer> medicineIds = Arrays.stream(medicineIdsArray)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    prescriptionDAO.updateFormMedicines(formId, medicineIds);
                }
                
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?success=form_added&tab=forms");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=add_failed&tab=forms");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=invalid_format&tab=forms");
        }
    }
    
    private void handleUpdateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("formId");
        String formName = request.getParameter("formName");
        String notes = request.getParameter("notes");
        String[] medicineIdsArray = request.getParameterValues("medicineIds");
        
        if (idParam == null || formName == null || formName.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=missing_fields&tab=forms");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            
            // Check if form name already exists (excluding current form)
            if (prescriptionDAO.isFormNameExists(formName.trim(), id)) {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=form_exists&tab=forms");
                return;
            }
            
            PrescriptionForm form = new PrescriptionForm();
            form.setPrescriptionFormId(id);
            form.setFormName(formName.trim());
            form.setNotes(notes != null ? notes.trim() : "");
            
            boolean success = prescriptionDAO.updatePrescriptionForm(form);
            
            if (success) {
                // Update medicines in the form
                List<Integer> medicineIds = null;
                if (medicineIdsArray != null && medicineIdsArray.length > 0) {
                    medicineIds = Arrays.stream(medicineIdsArray)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                }
                prescriptionDAO.updateFormMedicines(id, medicineIds);
                
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?success=form_updated&tab=forms");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=update_failed&tab=forms");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=invalid_format&tab=forms");
        }
    }
    
    private void handleDeleteForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("formId");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=missing_id&tab=forms");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            boolean success = prescriptionDAO.deletePrescriptionForm(id);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?success=form_deleted&tab=forms");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=delete_failed&tab=forms");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prescriptions?error=invalid_format&tab=forms");
        }
    }


} 