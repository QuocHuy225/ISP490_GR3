package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOService;
import com.mycompany.isp490_gr3.model.MedicalService;
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

/**
 * =====================================================
 * ServiceController - CONTROLLER QUẢN LÝ DỊCH VỤ Y TẾ
 * 
 * Chức năng: Xử lý các request liên quan đến dịch vụ y tế
 * URL pattern: /admin/services, /admin/services/*
 * DAO sử dụng: DAOService
 * JSP tương ứng: service.jsp
 * 
 * Các chức năng chính:
 * - Hiển thị danh sách dịch vụ y tế
 * - Tìm kiếm dịch vụ theo tên/nhóm
 * - Lọc dịch vụ theo nhóm
 * - Thêm/sửa/xóa dịch vụ y tế
 * - Quản lý giá dịch vụ
 * - Chỉ Admin mới được truy cập
 * =====================================================
 */
@WebServlet(name = "ServiceController", urlPatterns = {"/admin/services", "/admin/services/*"})
public class ServiceController extends HttpServlet {

    private DAOService serviceDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        serviceDAO = new DAOService();
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
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                handleListServices(request, response);
                break;
            case "search":
                handleSearchServices(request, response);
                break;
            case "filter":
                handleFilterServices(request, response);
                break;
            case "get":
                handleGetService(request, response);
                break;
            default:
                handleListServices(request, response);
                break;
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
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/services?error=no_action");
            return;
        }
        
        switch (action) {
            case "add":
                handleAddService(request, response);
                break;
            case "update":
                handleUpdateService(request, response);
                break;
            case "delete":
                handleDeleteService(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/services?error=invalid_action");
                break;
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
    
    private void handleListServices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<MedicalService> services = serviceDAO.getAllServices();
        List<String> serviceGroups = serviceDAO.getAllServiceGroups();
        
        request.setAttribute("services", services);
        request.setAttribute("serviceGroups", serviceGroups);
        
        request.getRequestDispatcher("/jsp/service.jsp").forward(request, response);
    }
    
    private void handleSearchServices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        if (keyword == null || keyword.trim().isEmpty()) {
            handleListServices(request, response);
            return;
        }
        
        List<MedicalService> services = serviceDAO.searchServices(keyword);
        List<String> serviceGroups = serviceDAO.getAllServiceGroups();
        
        request.setAttribute("services", services);
        request.setAttribute("serviceGroups", serviceGroups);
        request.setAttribute("searchKeyword", keyword);
        
        request.getRequestDispatcher("/jsp/service.jsp").forward(request, response);
    }
    
    private void handleFilterServices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String serviceGroup = request.getParameter("serviceGroup");
        if (serviceGroup == null || serviceGroup.trim().isEmpty()) {
            handleListServices(request, response);
            return;
        }
        
        List<MedicalService> services = serviceDAO.filterServicesByGroup(serviceGroup);
        List<String> serviceGroups = serviceDAO.getAllServiceGroups();
        
        request.setAttribute("services", services);
        request.setAttribute("serviceGroups", serviceGroups);
        request.setAttribute("selectedGroup", serviceGroup);
        
        request.getRequestDispatcher("/jsp/service.jsp").forward(request, response);
    }
    
    private void handleGetService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            int serviceId = Integer.parseInt(idStr);
            MedicalService service = serviceDAO.getServiceById(serviceId);
            
            if (service != null) {
                response.setContentType("application/json; charset=UTF-8");
                PrintWriter out = response.getWriter();
                Gson gson = new Gson();
                out.print(gson.toJson(service));
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void handleAddService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String serviceGroup = request.getParameter("serviceGroup");
            String serviceName = request.getParameter("serviceName");
            String priceStr = request.getParameter("price");
            
            // Validate input
            if (serviceGroup == null || serviceGroup.trim().isEmpty() ||
                serviceName == null || serviceName.trim().isEmpty() ||
                priceStr == null || priceStr.trim().isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/admin/services?error=missing_fields");
                return;
            }
            
            BigDecimal price = new BigDecimal(priceStr);
            
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                response.sendRedirect(request.getContextPath() + "/admin/services?error=invalid_price");
                return;
            }
            
            // Check if service already exists
            MedicalService existingService = serviceDAO.findExistingService(serviceGroup, serviceName);
            if (existingService != null) {
                response.sendRedirect(request.getContextPath() + "/admin/services?error=service_exists");
                return;
            }
            
            // Create new service
            MedicalService newService = new MedicalService(serviceGroup, serviceName, price);
            
            boolean success = serviceDAO.addService(newService);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/services?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/services?error=add_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/services?error=invalid_format");
        }
    }
    
    private void handleUpdateService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));
            String serviceGroup = request.getParameter("serviceGroup");
            String serviceName = request.getParameter("serviceName");
            String priceStr = request.getParameter("price");
            
            // Validate input
            if (serviceGroup == null || serviceGroup.trim().isEmpty() ||
                serviceName == null || serviceName.trim().isEmpty() ||
                priceStr == null || priceStr.trim().isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/admin/services?error=missing_fields");
                return;
            }
            
            BigDecimal price = new BigDecimal(priceStr);
            
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                response.sendRedirect(request.getContextPath() + "/admin/services?error=invalid_price");
                return;
            }
            
            // Check if another service with same group and name exists
            MedicalService existingService = serviceDAO.findExistingService(serviceGroup, serviceName);
            if (existingService != null && existingService.getServicesId() != serviceId) {
                response.sendRedirect(request.getContextPath() + "/admin/services?error=service_exists");
                return;
            }
            
            // Create service with updated information
            MedicalService service = new MedicalService();
            service.setServicesId(serviceId);
            service.setServiceGroup(serviceGroup);
            service.setServiceName(serviceName);
            service.setPrice(price);
            
            boolean success = serviceDAO.updateService(service);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/services?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/services?error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/services?error=invalid_format");
        }
    }
    
    private void handleDeleteService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));
            
            boolean success = serviceDAO.deleteService(serviceId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/services?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/services?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/services?error=invalid_format");
        }
    }
} 