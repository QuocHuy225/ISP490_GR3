package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOUser;
import com.mycompany.isp490_gr3.model.User;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for handling user profile operations
 */
@WebServlet(name = "UserProfileController", urlPatterns = {"/user/*"})
public class UserProfileController extends HttpServlet {
    
    private DAOUser daoUser;
    
    @Override
    public void init() throws ServletException {
        super.init();
        daoUser = new DAOUser();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp");
            return;
        }
        
        switch (pathInfo) {
            case "/profile":
                showProfile(request, response);
                break;
            case "/edit-profile":
                showEditProfile(request, response);
                break;
            case "/change-password":
                showChangePassword(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp");
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp");
            return;
        }
        
        switch (pathInfo) {
            case "/update-profile":
                updateProfile(request, response);
                break;
            case "/change-password":
                changePassword(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp");
                break;
        }
    }
    
    /**
     * Show user profile page
     */
    private void showProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        User user = daoUser.getUserById(userId);
        
        if (user != null) {
            request.setAttribute("user", user);
            request.getRequestDispatcher("/jsp/user-profile.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Không tìm thấy thông tin người dùng");
            request.getRequestDispatcher("/jsp/homepage.jsp").forward(request, response);
        }
    }
    
    /**
     * Show edit profile page
     */
    private void showEditProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        User user = daoUser.getUserById(userId);
        
        if (user != null) {
            request.setAttribute("user", user);
            request.getRequestDispatcher("/jsp/edit-profile.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Không tìm thấy thông tin người dùng");
            request.getRequestDispatcher("/jsp/homepage.jsp").forward(request, response);
        }
    }
    
    /**
     * Show change password page
     */
    private void showChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        request.getRequestDispatcher("/jsp/change-password.jsp").forward(request, response);
    }
    
    /**
     * Update user profile
     */
    private void updateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        User user = daoUser.getUserById(userId);
        
        if (user == null) {
            session.setAttribute("errorMessage", "Không tìm thấy thông tin người dùng");
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }
        
        // Get form data
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String genderStr = request.getParameter("gender");
        String address = request.getParameter("address");
        
        // Validate input
        if (fullName == null || fullName.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Họ tên không được để trống");
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }
        
        // Update user information
        user.setFullName(fullName.trim());
        user.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
        user.setAddress(address != null && !address.trim().isEmpty() ? address.trim() : null);
        
        // Set date of birth
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = sdf.parse(dobStr);
                user.setDob(new Date(utilDate.getTime()));
            } catch (ParseException e) {
                session.setAttribute("errorMessage", "Định dạng ngày sinh không hợp lệ");
                response.sendRedirect(request.getContextPath() + "/user/profile");
                return;
            }
        } else {
            user.setDob(null);
        }
        
        // Set gender
        if (genderStr != null && !genderStr.trim().isEmpty()) {
            user.setGender(User.Gender.fromString(genderStr));
        } else {
            user.setGender(null);
        }
        
        // Update in database
        boolean updateSuccess = daoUser.updateUser(user);
        
        if (updateSuccess) {
            // Update session with new info
            session.setAttribute("userFullName", user.getFullName());
            session.setAttribute("successMessage", "Cập nhật thông tin thành công!");
        } else {
            session.setAttribute("errorMessage", "Cập nhật thông tin thất bại. Vui lòng thử lại.");
        }
        
        response.sendRedirect(request.getContextPath() + "/user/profile");
    }
    
    /**
     * Change user password
     */
    private void changePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Vui lòng nhập mật khẩu hiện tại");
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            session.setAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 6 ký tự");
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp");
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }
        
        // Get user and verify current password
        User user = daoUser.getUserById(userId);
        if (user == null) {
            session.setAttribute("errorMessage", "Không tìm thấy thông tin người dùng");
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }
        
        // Verify current password by attempting login
        User verifyUser = daoUser.login(user.getEmail(), currentPassword);
        if (verifyUser == null) {
            session.setAttribute("errorMessage", "Mật khẩu hiện tại không đúng");
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }
        
        // Change password
        boolean changeSuccess = daoUser.changePassword(userId, newPassword);
        
        if (changeSuccess) {
            session.setAttribute("successMessage", "Đổi mật khẩu thành công!");
        } else {
            session.setAttribute("errorMessage", "Đổi mật khẩu thất bại. Vui lòng thử lại.");
        }
        
        response.sendRedirect(request.getContextPath() + "/user/profile");
    }
} 