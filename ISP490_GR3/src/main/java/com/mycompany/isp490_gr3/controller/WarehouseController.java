package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOWarehouse;
import com.mycompany.isp490_gr3.model.MedicalSupply;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
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

        } else {
            // XỬ LÝ YÊU CẦU POST LIÊN QUAN ĐÊN VẬT TƯ Y TẾ
            handleSupplyPostRequests(request, response);
        }
    }
    
    // =====================================================
    // PHẦN XỬ LÝ YÊU CẦU POST CHO VẬT TƯ Y TẾ
    // Các action: add, update, delete, addStock
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
            case "addStock":
                handleAddStock(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies");
                break;
        }
    }
    
    private boolean checkAdminAccess(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }
        
        // Check if user is admin
        Object userRoleObj = session.getAttribute("userRole");
        User.Role currentRole = null;
        
        if (userRoleObj != null) {
            if (userRoleObj instanceof User.Role) {
                currentRole = (User.Role) userRoleObj;
            } else {
                try {
                    currentRole = User.Role.valueOf(userRoleObj.toString().toUpperCase());
                } catch (Exception e) {
                    currentRole = User.Role.fromString(userRoleObj.toString());
                }
            }
        }
        
        if (currentRole != User.Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/homepage?error=access_denied");
            return false;
        }
        
        return true;
    }
    
    // =====================================================
    // CÁC PHƯƠNG THỨC XỬ LÝ CHO VẬT TƯ Y TẾ
    // =====================================================
    
    private void handleListSupplies(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<MedicalSupply> supplies = warehouseDAO.getAllSupplies();
        List<String> supplyGroups = warehouseDAO.getAllSupplyGroups();
        
        request.setAttribute("supplies", supplies);
        request.setAttribute("supplyGroups", supplyGroups);
        
        request.getRequestDispatcher("/jsp/medical-supply.jsp").forward(request, response);
    }
    
    private void handleSearchSupplies(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
        
        request.getRequestDispatcher("/jsp/medical-supply.jsp").forward(request, response);
    }
    
    private void handleGetSupply(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int supplyId = Integer.parseInt(request.getParameter("id"));
            MedicalSupply supply = warehouseDAO.getSupplyById(supplyId);
            
            if (supply != null) {
                // Return JSON response
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{" +
                    "\"supplyId\":" + supply.getSupplyId() + "," +
                    "\"supplyGroup\":\"" + supply.getSupplyGroup() + "\"," +
                    "\"supplyName\":\"" + supply.getSupplyName() + "\"," +
                    "\"unitPrice\":" + supply.getUnitPrice() + "," +
                    "\"stockQuantity\":" + supply.getStockQuantity() +
                    "}");
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
            
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0 || stockQuantity < 0) {
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
    
    private void handleAddStock(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int supplyId = Integer.parseInt(request.getParameter("supplyId"));
            int additionalQuantity = Integer.parseInt(request.getParameter("additionalQuantity"));
            
            if (additionalQuantity <= 0) {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=invalid_quantity");
                return;
            }
            
            boolean success = warehouseDAO.updateStockQuantity(supplyId, additionalQuantity);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?success=stock_added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/medical-supplies?error=stock_add_failed");
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
} 