package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOUser;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller for handling user authorization/role management
 */
@WebServlet(name = "AuthorizationController", urlPatterns = {
    "/admin/authorization",
    "/admin/authorization/view",
    "/admin/authorization/update",
    "/admin/authorization/delete",
    "/admin/authorization/restore"
})
public class AuthorizationController extends HttpServlet {
    
    private DAOUser daoUser;
    
    @Override
    public void init() throws ServletException {
        super.init();
        daoUser = new DAOUser();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Check if user is logged in
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        // Check if user is admin
        if (currentUser.getRole() != User.Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return;
        }
        
        String pathInfo = request.getServletPath();
        String showDeleted = request.getParameter("showDeleted");
        
        switch (pathInfo) {
            case "/admin/authorization":
            case "/admin/authorization/view":
                showAuthorizationPage(request, response, showDeleted != null);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Check if user is logged in
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        // Check if user is admin
        if (currentUser.getRole() != User.Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
            return;
        }
        
        String pathInfo = request.getServletPath();
        
        switch (pathInfo) {
            case "/admin/authorization/update":
                updateUserRole(request, response, currentUser);
                break;
            case "/admin/authorization/delete":
                deleteUser(request, response, currentUser);
                break;
            case "/admin/authorization/restore":
                restoreUser(request, response, currentUser);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Show authorization management page
     */
    private void showAuthorizationPage(HttpServletRequest request, HttpServletResponse response, boolean showDeleted)
            throws ServletException, IOException {
        
        try {
            // Get filter and search parameters
            String roleFilter = request.getParameter("roleFilter");
            String sortOrder = request.getParameter("sortOrder"); // "newest" or "oldest"
            String emailSearch = request.getParameter("emailSearch");
            
            List<User> users;
            if (showDeleted) {
                // Get deleted users with filters
                users = daoUser.getDeletedUsersWithFilters(roleFilter, sortOrder, emailSearch);
                request.setAttribute("showDeleted", true);
                request.setAttribute("deletedUsers", users);
            } else {
                // Get active users with filters
                users = daoUser.getAllUsersWithFilters(roleFilter, sortOrder, emailSearch);
                request.setAttribute("allUsers", users);
                
                // Get user counts by role for statistics (only for unfiltered results)
                if (roleFilter == null && emailSearch == null) {
                    int doctorCount = daoUser.getUserCountByRole(User.Role.DOCTOR);
                    int receptionistCount = daoUser.getUserCountByRole(User.Role.RECEPTIONIST);
                    int patientCount = daoUser.getUserCountByRole(User.Role.PATIENT);
                    int adminCount = daoUser.getUserCountByRole(User.Role.ADMIN);
                    
                    request.setAttribute("doctorCount", doctorCount);
                    request.setAttribute("receptionistCount", receptionistCount);
                    request.setAttribute("patientCount", patientCount);
                    request.setAttribute("adminCount", adminCount);
                    request.setAttribute("totalUsers", users.size());
                } else {
                    // For filtered results, show actual filtered count
                    request.setAttribute("totalUsers", users.size());
                }
            }
            
            // Set filter parameters for JSP
            request.setAttribute("roleFilter", roleFilter);
            request.setAttribute("sortOrder", sortOrder);
            request.setAttribute("emailSearch", emailSearch);
            
            // Forward to authorization page
            request.getRequestDispatcher("/jsp/authorization.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error loading authorization page: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: Không thể tải danh sách người dùng.");
            request.getRequestDispatcher("/jsp/authorization.jsp").forward(request, response);
        }
    }
    
    /**
     * Update user role
     */
    private void updateUserRole(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        String userId = request.getParameter("userId");
        String newRoleStr = request.getParameter("newRole");
        
        // Validate parameters
        if (userId == null || userId.trim().isEmpty() || 
            newRoleStr == null || newRoleStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thông tin không hợp lệ.");
            showAuthorizationPage(request, response, false);
            return;
        }
        
        try {
            // Parse new role
            User.Role newRole;
            switch (newRoleStr.toLowerCase()) {
                case "doctor":
                    newRole = User.Role.DOCTOR;
                    break;
                case "receptionist":
                    newRole = User.Role.RECEPTIONIST;
                    break;
                case "patient":
                    newRole = User.Role.PATIENT;
                    break;
                default:
                    request.setAttribute("errorMessage", "Quyền hạn không hợp lệ.");
                    showAuthorizationPage(request, response, false);
                    return;
            }
            
            // Get target user to validate
            User targetUser = daoUser.getUserById(userId);
            if (targetUser == null) {
                request.setAttribute("errorMessage", "Không tìm thấy người dùng.");
                showAuthorizationPage(request, response, false);
                return;
            }
            
            // Prevent updating admin users
            if (targetUser.getRole() == User.Role.ADMIN) {
                request.setAttribute("errorMessage", "Không thể thay đổi quyền hạn của tài khoản Admin.");
                showAuthorizationPage(request, response, false);
                return;
            }
            
            // Prevent self-update
            if (targetUser.getId().equals(currentUser.getId())) {
                request.setAttribute("errorMessage", "Bạn không thể thay đổi quyền hạn của chính mình.");
                showAuthorizationPage(request, response, false);
                return;
            }
            
            // Update user role
            boolean success = daoUser.updateUserRole(userId, newRole, currentUser.getId());
            
            if (success) {
                request.setAttribute("successMessage", 
                    String.format("Đã cập nhật quyền hạn cho %s thành %s.", 
                    targetUser.getFullName(), newRole.getValue()));
                
                // Log the action
                System.out.println(String.format("Admin %s updated role for user %s (%s) to %s", 
                    currentUser.getEmail(), targetUser.getEmail(), targetUser.getId(), newRole.getValue()));
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật quyền hạn. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            System.err.println("Error updating user role: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: Không thể cập nhật quyền hạn.");
        }
        
        // Redirect to authorization page
        showAuthorizationPage(request, response, false);
    }
    
    /**
     * Delete/Disable user (soft delete)
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        String userId = request.getParameter("userId");
        
        if (userId == null || userId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thông tin không hợp lệ.");
            showAuthorizationPage(request, response, false);
            return;
        }
        
        try {
            User targetUser = daoUser.getUserById(userId);
            if (targetUser == null) {
                request.setAttribute("errorMessage", "Không tìm thấy người dùng.");
                showAuthorizationPage(request, response, false);
                return;
            }
            
            // Prevent deleting admin users
            if (targetUser.getRole() == User.Role.ADMIN) {
                request.setAttribute("errorMessage", "Không thể xóa tài khoản Admin.");
                showAuthorizationPage(request, response, false);
                return;
            }
            
            // Prevent self-delete  
            if (targetUser.getId().equals(currentUser.getId())) {
                request.setAttribute("errorMessage", "Bạn không thể xóa tài khoản của chính mình.");
                showAuthorizationPage(request, response, false);
                return;
            }
            
            boolean success = daoUser.deleteUser(userId);
            if (success) {
                request.setAttribute("successMessage", "Đã xóa tài khoản " + targetUser.getFullName());
            } else {
                request.setAttribute("errorMessage", "Không thể xóa tài khoản. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi hệ thống: Không thể xóa tài khoản.");
        }
        
        showAuthorizationPage(request, response, false);
    }
    
    /**
     * Restore deleted user
     */
    private void restoreUser(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        String userId = request.getParameter("userId");
        
        if (userId == null || userId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thông tin không hợp lệ.");
            showAuthorizationPage(request, response, true);
            return;
        }
        
        try {
            boolean success = daoUser.restoreUser(userId);
            if (success) {
                request.setAttribute("successMessage", "Đã khôi phục tài khoản thành công.");
            } else {
                request.setAttribute("errorMessage", "Không thể khôi phục tài khoản. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            System.err.println("Error restoring user: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi hệ thống: Không thể khôi phục tài khoản.");
        }
        
        showAuthorizationPage(request, response, true);
    }
} 