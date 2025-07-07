package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOWarehouse;
import com.mycompany.isp490_gr3.model.MedicalSupply;
import com.mycompany.isp490_gr3.model.Medicine;
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
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "WarehouseController", urlPatterns = {"/admin/medical-supplies", "/admin/medical-supplies/*", "/admin/medicines", "/admin/medicines/*"})
public class WarehouseController extends HttpServlet {

    private DAOWarehouse warehouseDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        warehouseDAO = new DAOWarehouse();
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
        
        String path = request.getRequestURI();
        
        // Phân chia xử lý theo đường dẫn URL
        if (path.contains("/admin/medicines")) {
            // XỬ LÝ YÊU CẦU LIÊN QUAN ĐÊN KHO THUỐC
            handleMedicineRequests(request, response);
        } else {
            // XỬ LÝ YÊU CẦU LIÊN QUAN ĐÊN VẬT TƯ Y TẾ
            handleSupplyRequests(request, response);
        }
    }
    
    // =====================================================
    // PHẦN XỬ LÝ YÊU CẦU GET CHO VẬT TƯ Y TẾ
    // URL: /admin/medical-supplies
    // =====================================================
    private void handleSupplyRequests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                handleListSupplies(request, response);
                break;
            case "search":
                handleSearchSupplies(request, response);
                break;
            case "get":
                handleGetSupply(request, response);
                break;
            default:
                handleListSupplies(request, response);
                break;
        }
    }
    
    // =====================================================
    // PHẦN XỬ LÝ YÊU CẦU GET CHO KHO THUỐC
    // URL: /admin/medicines
    // =====================================================
    private void handleMedicineRequests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                handleListMedicines(request, response);
                break;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set character encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // Check admin access
        if (!checkAdminAccess(request, response)) {
            return;
        }
        
        String path = request.getRequestURI();
        
        // Phân chia xử lý theo đường dẫn URL
        if (path.contains("/admin/medicines")) {
            // XỬ LÝ YÊU CẦU POST LIÊN QUAN ĐÊN KHO THUỐC
            handleMedicinePostRequests(request, response);
        } else {
            // XỬ LÝ YÊU CẦU POST LIÊN QUAN ĐÊN VẬT TƯ Y TẾ
            handleSupplyPostRequests(request, response);
        }
    }
    
    // =====================================================
    // PHẦN XỬ LÝ YÊU CẦU POST CHO VẬT TƯ Y TẾ
    // Các action: add, update, delete
    // =====================================================
    private void handleSupplyPostRequests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/medical-supplies");
            return;
        }
        
        switch (action) {
            case "add":
                handleAddSupply(request, response);
                break;
            case "update":
                handleUpdateSupply(request, response);
                break;
            case "delete":
                handleDeleteSupply(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies");
                break;
        }
    }
    
    // =====================================================
    // PHẦN XỬ LÝ YÊU CẦU POST CHO KHO THUỐC
    // Các action: add, update, delete
    // =====================================================
    private void handleMedicinePostRequests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/medicines");
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
                response.sendRedirect(request.getContextPath() + "/admin/medicines");
                break;
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
    
    // =====================================================
    // CÁC PHƯƠNG THỨC XỬ LÝ CHO VẬT TƯ Y TẾ
    // =====================================================
    
    private void handleListSupplies(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editId = request.getParameter("edit");
        MedicalSupply editSupply = null;
        
        if (editId != null && !editId.trim().isEmpty()) {
            try {
                int supplyId = Integer.parseInt(editId);
                editSupply = warehouseDAO.getSupplyById(supplyId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        List<MedicalSupply> supplies = warehouseDAO.getAllSupplies();
        List<String> supplyGroups = warehouseDAO.getAllSupplyGroups();
        
        request.setAttribute("supplies", supplies);
        request.setAttribute("supplyGroups", supplyGroups);
        request.setAttribute("editSupply", editSupply);
        
        request.getRequestDispatcher("/jsp/medical-supply.jsp").forward(request, response);
    }
    
    private void handleSearchSupplies(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editId = request.getParameter("edit");
        MedicalSupply editSupply = null;
        
        if (editId != null && !editId.trim().isEmpty()) {
            try {
                int supplyId = Integer.parseInt(editId);
                editSupply = warehouseDAO.getSupplyById(supplyId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        String keyword = request.getParameter("keyword");
        List<MedicalSupply> supplies;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            supplies = warehouseDAO.searchSupplies(keyword.trim());
        } else {
            supplies = warehouseDAO.getAllSupplies();
        }
        
        List<String> supplyGroups = warehouseDAO.getAllSupplyGroups();
        
        request.setAttribute("supplies", supplies);
        request.setAttribute("supplyGroups", supplyGroups);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("editSupply", editSupply);
        
        request.getRequestDispatcher("/jsp/medical-supply.jsp").forward(request, response);
    }
    
    private void handleGetSupply(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            int supplyId = Integer.parseInt(idStr);
            MedicalSupply supply = warehouseDAO.getSupplyById(supplyId);
            
            if (supply != null) {
                response.setContentType("application/json; charset=UTF-8");
                PrintWriter out = response.getWriter();
                Gson gson = new Gson();
                out.print(gson.toJson(supply));
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void handleAddSupply(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String supplyGroup = request.getParameter("supplyGroup");
            String supplyName = request.getParameter("supplyName");
            String unitPriceStr = request.getParameter("unitPrice");
            String stockQuantityStr = request.getParameter("stockQuantity");
            
            // Validate input
            if (supplyGroup == null || supplyGroup.trim().isEmpty() ||
                supplyName == null || supplyName.trim().isEmpty() ||
                unitPriceStr == null || unitPriceStr.trim().isEmpty() ||
                stockQuantityStr == null || stockQuantityStr.trim().isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=missing_fields");
                return;
            }
            
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);
            int stockQuantity = Integer.parseInt(stockQuantityStr);
            
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0 || stockQuantity <= 0) {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=invalid_values");
                return;
            }
            
            // Check if supply already exists
            MedicalSupply existingSupply = warehouseDAO.findExistingSupply(supplyGroup.trim(), supplyName.trim());
            
            if (existingSupply != null) {
                // Supply already exists, show error message
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=supply_exists");
                return;
            }
            
            // Add new supply
            MedicalSupply newSupply = new MedicalSupply(supplyGroup.trim(), supplyName.trim(), unitPrice, stockQuantity);
            boolean success = warehouseDAO.addSupply(newSupply);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=add_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=invalid_format");
        }
    }
    
    private void handleUpdateSupply(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int supplyId = Integer.parseInt(request.getParameter("supplyId"));
            String supplyGroup = request.getParameter("supplyGroup");
            String supplyName = request.getParameter("supplyName");
            String unitPriceStr = request.getParameter("unitPrice");
            String stockQuantityStr = request.getParameter("stockQuantity");
            
            // Validate input
            if (supplyGroup == null || supplyGroup.trim().isEmpty() ||
                supplyName == null || supplyName.trim().isEmpty() ||
                unitPriceStr == null || unitPriceStr.trim().isEmpty() ||
                stockQuantityStr == null || stockQuantityStr.trim().isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=missing_fields");
                return;
            }
            
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);
            int stockQuantity = Integer.parseInt(stockQuantityStr);
            
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0 || stockQuantity < 1) {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=invalid_values");
                return;
            }
            
            MedicalSupply supply = new MedicalSupply();
            supply.setSupplyId(supplyId);
            supply.setSupplyGroup(supplyGroup.trim());
            supply.setSupplyName(supplyName.trim());
            supply.setUnitPrice(unitPrice);
            supply.setStockQuantity(stockQuantity);
            
            boolean success = warehouseDAO.updateSupply(supply);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=invalid_format");
        }
    }
    
    private void handleDeleteSupply(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int supplyId = Integer.parseInt(request.getParameter("supplyId"));
            boolean success = warehouseDAO.deleteSupply(supplyId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=invalid_format");
        }
    }
    

    
    // =====================================================
    // CÁC PHƯƠNG THỨC XỬ LÝ CHO KHO THUỐC
    // =====================================================
    
    private void handleListMedicines(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editId = request.getParameter("edit");
        Medicine editMedicine = null;
        
        if (editId != null && !editId.trim().isEmpty()) {
            try {
                int medicineId = Integer.parseInt(editId);
                editMedicine = warehouseDAO.getMedicineById(medicineId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        List<Medicine> medicines = warehouseDAO.getAllMedicines();
        List<String> medicineUnits = warehouseDAO.getAllMedicineUnits();
        
        request.setAttribute("medicines", medicines);
        request.setAttribute("medicineUnits", medicineUnits);
        request.setAttribute("editMedicine", editMedicine);
        
        request.getRequestDispatcher("/jsp/medicine.jsp").forward(request, response);
    }
    
    private void handleSearchMedicines(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if this is an edit request
        String editId = request.getParameter("edit");
        Medicine editMedicine = null;
        
        if (editId != null && !editId.trim().isEmpty()) {
            try {
                int medicineId = Integer.parseInt(editId);
                editMedicine = warehouseDAO.getMedicineById(medicineId);
            } catch (NumberFormatException e) {
                // Invalid ID, ignore edit request
            }
        }
        
        String keyword = request.getParameter("keyword");
        List<Medicine> medicines;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            medicines = warehouseDAO.searchMedicines(keyword.trim());
        } else {
            medicines = warehouseDAO.getAllMedicines();
        }
        
        List<String> medicineUnits = warehouseDAO.getAllMedicineUnits();
        
        request.setAttribute("medicines", medicines);
        request.setAttribute("medicineUnits", medicineUnits);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("editMedicine", editMedicine);
        
        request.getRequestDispatcher("/jsp/medicine.jsp").forward(request, response);
    }
    
    private void handleGetMedicine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            int medicineId = Integer.parseInt(idStr);
            Medicine medicine = warehouseDAO.getMedicineById(medicineId);
            
            if (medicine != null) {
                response.setContentType("application/json; charset=UTF-8");
                PrintWriter out = response.getWriter();
                Gson gson = new Gson();
                out.print(gson.toJson(medicine));
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void handleAddMedicine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String medicineName = request.getParameter("medicineName");
            String unitOfMeasure = request.getParameter("unitOfMeasure");
            String unitPriceStr = request.getParameter("unitPrice");
            String stockQuantityStr = request.getParameter("stockQuantity");
            
            // Validate input
            if (medicineName == null || medicineName.trim().isEmpty() ||
                unitOfMeasure == null || unitOfMeasure.trim().isEmpty() ||
                unitPriceStr == null || unitPriceStr.trim().isEmpty() ||
                stockQuantityStr == null || stockQuantityStr.trim().isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=missing_fields");
                return;
            }
            
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);
            int stockQuantity = Integer.parseInt(stockQuantityStr);
            
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0 || stockQuantity <= 0) {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=invalid_values");
                return;
            }
            
            // Check if medicine already exists
            Medicine existingMedicine = warehouseDAO.findExistingMedicine(medicineName.trim(), unitOfMeasure.trim());
            
            if (existingMedicine != null) {
                // Medicine already exists, show error message
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=medicine_exists");
                return;
            }
            
            // Add new medicine
            Medicine newMedicine = new Medicine(medicineName.trim(), unitOfMeasure.trim(), unitPrice, stockQuantity);
            boolean success = warehouseDAO.addMedicine(newMedicine);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=add_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medicines?error=invalid_format");
        }
    }
    
    private void handleUpdateMedicine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int medicineId = Integer.parseInt(request.getParameter("medicineId"));
            String medicineName = request.getParameter("medicineName");
            String unitOfMeasure = request.getParameter("unitOfMeasure");
            String unitPriceStr = request.getParameter("unitPrice");
            String stockQuantityStr = request.getParameter("stockQuantity");
            
            // Validate input
            if (medicineName == null || medicineName.trim().isEmpty() ||
                unitOfMeasure == null || unitOfMeasure.trim().isEmpty() ||
                unitPriceStr == null || unitPriceStr.trim().isEmpty() ||
                stockQuantityStr == null || stockQuantityStr.trim().isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=missing_fields");
                return;
            }
            
            BigDecimal unitPrice = new BigDecimal(unitPriceStr);
            int stockQuantity = Integer.parseInt(stockQuantityStr);
            
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0 || stockQuantity < 1) {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=invalid_values");
                return;
            }
            
            Medicine medicine = new Medicine();
            medicine.setExamMedicineId(medicineId);
            medicine.setMedicineName(medicineName.trim());
            medicine.setUnitOfMeasure(unitOfMeasure.trim());
            medicine.setUnitPrice(unitPrice);
            medicine.setStockQuantity(stockQuantity);
            
            boolean success = warehouseDAO.updateMedicine(medicine);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medicines?error=invalid_format");
        }
    }
    
    private void handleDeleteMedicine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int medicineId = Integer.parseInt(request.getParameter("medicineId"));
            boolean success = warehouseDAO.deleteMedicine(medicineId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medicines?error=invalid_format");
        }
    }
} 