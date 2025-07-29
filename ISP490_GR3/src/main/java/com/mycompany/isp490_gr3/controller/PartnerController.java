package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOPartner;
import com.mycompany.isp490_gr3.model.Partner;
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
 * PartnerController - CONTROLLER QUẢN LÝ ĐỐI TÁC
 * 
 * Chức năng: Xử lý các request liên quan đến đối tác
 * URL pattern: /admin/partners, /admin/partners/*
 * DAO sử dụng: DAOPartner
 * JSP tương ứng: partner.jsp
 * 
 * Các chức năng chính:
 * - Hiển thị danh sách đối tác
 * - Tìm kiếm đối tác theo tên/số điện thoại
 * - Thêm/sửa/xóa đối tác
 * - Quản lý thông tin đối tác
 * - Chỉ Admin mới được truy cập
 * =====================================================
 */

public class PartnerController extends HttpServlet {

    private DAOPartner partnerDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        partnerDAO = new DAOPartner();
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
                handleListPartners(request, response);
                break;
            case "search":
                handleSearchPartners(request, response);
                break;
            case "get":
                handleGetPartner(request, response);
                break;
            default:
                handleListPartners(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admin/partners?error=no_action");
            return;
        }
        
        switch (action) {
            case "add":
                handleAddPartner(request, response);
                break;
            case "update":
                handleUpdatePartner(request, response);
                break;
            case "delete":
                handleDeletePartner(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=invalid_action");
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
    
    private void handleListPartners(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Partner> partners = partnerDAO.getAllPartners();
        
        request.setAttribute("partners", partners);
        
        request.getRequestDispatcher("/jsp/partner.jsp").forward(request, response);
    }
    
    private void handleSearchPartners(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        if (keyword == null || keyword.trim().isEmpty()) {
            handleListPartners(request, response);
            return;
        }
        
        List<Partner> partners = partnerDAO.searchPartners(keyword);
        
        request.setAttribute("partners", partners);
        request.setAttribute("searchKeyword", keyword);
        
        request.getRequestDispatcher("/jsp/partner.jsp").forward(request, response);
    }
    
    private void handleGetPartner(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            int partnerId = Integer.parseInt(idStr);
            Partner partner = partnerDAO.getPartnerById(partnerId);
            
            if (partner != null) {
                response.setContentType("application/json; charset=UTF-8");
                PrintWriter out = response.getWriter();
                Gson gson = new Gson();
                out.print(gson.toJson(partner));
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void handleAddPartner(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String description = request.getParameter("description");
            
            // Validate input
            if (name == null || name.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                address == null || address.trim().isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=missing_fields");
                return;
            }
            
            // Validate phone number
            if (!isValidPhoneNumber(phone.trim())) {
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=invalid_phone");
                return;
            }
            
            // Check if partner already exists
            Partner existingPartner = partnerDAO.findExistingPartner(name);
            if (existingPartner != null) {
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=partner_exists");
                return;
            }
            
            // Create new partner
            Partner newPartner = new Partner(name, phone.trim(), address, description);
            
            boolean success = partnerDAO.addPartner(newPartner);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/partners?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=add_failed");
            }
            
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/partners?error=invalid_format");
        }
    }
    
    private void handleUpdatePartner(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int partnerId = Integer.parseInt(request.getParameter("partnerId"));
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String description = request.getParameter("description");
            
            // Validate input
            if (name == null || name.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                address == null || address.trim().isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=missing_fields");
                return;
            }
            
            // Validate phone number
            if (!isValidPhoneNumber(phone.trim())) {
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=invalid_phone");
                return;
            }
            
            // Check if another partner with same name exists
            Partner existingPartner = partnerDAO.findExistingPartner(name);
            if (existingPartner != null && existingPartner.getPartnerId() != partnerId) {
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=partner_exists");
                return;
            }
            
            // Create partner with updated information
            Partner partner = new Partner();
            partner.setPartnerId(partnerId);
            partner.setName(name);
            partner.setPhone(phone.trim());
            partner.setAddress(address);
            partner.setDescription(description);
            
            boolean success = partnerDAO.updatePartner(partner);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/partners?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/partners?error=invalid_format");
        }
    }
    
    private void handleDeletePartner(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int partnerId = Integer.parseInt(request.getParameter("partnerId"));
            
            boolean success = partnerDAO.deletePartner(partnerId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/partners?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/partners?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/partners?error=invalid_format");
        }
    }
    
    /**
     * Validate phone number format
     * Accepts Vietnamese phone number formats:
     * - 10 digits: 0123456789
     * - 11 digits: 01234567890
     * - With country code: +84xxxxxxxxx
     * - With spaces/dashes: 0123 456 789 or 0123-456-789
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Remove all non-digit characters
        String cleanedPhone = phone.replaceAll("[^\\d]", "");
        
        // Check if it's a valid Vietnamese phone number
        // Must be 10-11 digits and start with 0
        if (cleanedPhone.length() == 10 || cleanedPhone.length() == 11) {
            return cleanedPhone.startsWith("0");
        }
        
        return false;
    }
} 