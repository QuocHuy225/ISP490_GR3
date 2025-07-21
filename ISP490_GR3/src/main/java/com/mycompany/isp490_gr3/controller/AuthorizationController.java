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
import java.util.Map;
import java.util.HashMap;

/**
 * =====================================================
 * AuthorizationController - CONTROLLER PHÂN QUYỀN NGƯỜI DÙNG
 * 
 * Chức năng: Xử lý các request liên quan đến phân quyền và quản lý người dùng
 * URL patterns: 
 * - /admin/authorization (xem danh sách)
 * - /admin/authorization/view (xem chi tiết)
 * - /admin/authorization/delete (xóa người dùng)
 * - /admin/authorization/restore (khôi phục người dùng)
 * 
 * DAO sử dụng: DAOUser
 * JSP tương ứng: authorization.jsp
 * 
 * Các chức năng chính:
 * - Xem danh sách người dùng với lọc/tìm kiếm
 * - Xóa/khôi phục người dùng (soft delete)
 * - Thống kê số lượng người dùng theo role
 * - Chỉ Admin mới được truy cập
 * =====================================================
 */
@WebServlet(name = "AuthorizationController", urlPatterns = {
    "/admin/authorization",
    "/admin/authorization/view",
    "/admin/authorization/delete",
    "/admin/authorization/restore",
    "/admin/authorization/create"
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
        
        // Check admin access
        if (!checkAdminAccess(request, response)) {
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
        
        // Check admin access
        if (!checkAdminAccess(request, response)) {
            return;
        }
        
        // Get current user for operations
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        String pathInfo = request.getServletPath();
        
        switch (pathInfo) {
            case "/admin/authorization/delete":
                deleteUser(request, response, currentUser);
                break;
            case "/admin/authorization/restore":
                restoreUser(request, response, currentUser);
                break;
            case "/admin/authorization/create":
                createNewUser(request, response, currentUser);
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
                

            }
            
            // Get user counts by role for dropdown display
            Map<String, Integer> userCountsByRole = new HashMap<>();
            userCountsByRole.put("Admin", daoUser.getUserCountByRole(User.Role.ADMIN));
            userCountsByRole.put("Doctor", daoUser.getUserCountByRole(User.Role.DOCTOR));
            userCountsByRole.put("Receptionist", daoUser.getUserCountByRole(User.Role.RECEPTIONIST));
            userCountsByRole.put("Patient", daoUser.getUserCountByRole(User.Role.PATIENT));
            
            // Set filter parameters for JSP
            request.setAttribute("roleFilter", roleFilter);
            request.setAttribute("sortOrder", sortOrder);
            request.setAttribute("emailSearch", emailSearch);
            request.setAttribute("userCountsByRole", userCountsByRole);
            
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
    
    /**
     * Create new user with pre-verified email
     */
    private void createNewUser(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        
        // Get form parameters
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String otherContact = request.getParameter("other_contact");
        String roleStr = request.getParameter("role");
        
        // Validate required fields
        if (fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            roleStr == null || roleStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showAuthorizationPage(request, response, false);
            return;
        }
        
        // Validate email format
        if (!email.trim().toLowerCase().endsWith("@gmail.com")) {
            request.setAttribute("errorMessage", "Vui lòng sử dụng email Gmail.");
            showAuthorizationPage(request, response, false);
            return;
        }
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            showAuthorizationPage(request, response, false);
            return;
        }
        
        // Validate password length
        if (password.length() < 6) {
            request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự.");
            showAuthorizationPage(request, response, false);
            return;
        }
        
        // Validate role
        User.Role role;
        switch (roleStr.toLowerCase()) {
            case "doctor":
                role = User.Role.DOCTOR;
                break;
            case "receptionist":
                role = User.Role.RECEPTIONIST;
                break;
            default:
                request.setAttribute("errorMessage", "Quyền hạn không hợp lệ. Chỉ có thể tạo tài khoản Doctor hoặc Receptionist.");
                showAuthorizationPage(request, response, false);
                return;
        }
        
        try {
            // Create new user object
            User newUser = new User();
            newUser.setFullName(fullName.trim());
            newUser.setEmail(email.trim());
            newUser.setPassword(password);
            newUser.setOtherContact(otherContact != null ? otherContact.trim() : null);
            newUser.setRole(role);
            
            // Create user with pre-verified email
            boolean success = daoUser.createVerifiedUser(newUser, currentUser.getId());

            // Nếu là Doctor và tạo user thành công thì thêm vào bảng doctors
            if (success && role == User.Role.DOCTOR) {
                // Lấy lại user vừa tạo để lấy id
                User createdUser = daoUser.getUserByEmail(email.trim());
                if (createdUser != null) {
                    com.mycompany.isp490_gr3.model.Doctor doctor = new com.mycompany.isp490_gr3.model.Doctor();
                    doctor.setAccountId(createdUser.getId());
                    doctor.setFullName(fullName.trim());
                    doctor.setPhone("");
                    // Nếu muốn lấy gender từ form thì cần thêm trường, tạm để mặc định 0
                    doctor.setGender(0);
                    new com.mycompany.isp490_gr3.dao.DAODoctor().addDoctor(doctor);
                }
            }
            
            if (success) {
                request.setAttribute("successMessage", 
                    String.format("Đã tạo tài khoản %s cho %s thành công. Email đã được xác thực sẵn.", 
                    role.getValue(), fullName.trim()));
                
                // Log the action
                System.out.println(String.format("Admin %s created new %s account: %s (%s)", 
                    currentUser.getEmail(), role.getValue(), newUser.getEmail(), newUser.getFullName()));
            } else {
                request.setAttribute("errorMessage", "Không thể tạo tài khoản. Email có thể đã tồn tại.");
            }
            
        } catch (Exception e) {
            System.err.println("Error creating new user: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: Không thể tạo tài khoản.");
        }
        
        // Redirect to authorization page
        showAuthorizationPage(request, response, false);
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
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        
        return true;
    }
} 