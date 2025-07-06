package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOUser;
import com.mycompany.isp490_gr3.model.User;
import com.mycompany.isp490_gr3.service.EmailService;
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
 * Controller for handling authentication requests (login and register)
 */
@WebServlet(name = "AuthenticationController", urlPatterns = {"/auth/*"})
/**
 * =====================================================
 * AuthenticationController - CONTROLLER XÁC THỰC NGƯỜI DÙNG
 * 
 * Chức năng: Xử lý các request liên quan đến đăng nhập/đăng ký
 * URL patterns: /auth/*, /login, /register, /logout
 * DAO sử dụng: DAOUser
 * JSP tương ứng: login.jsp, register.jsp, landing.jsp
 * 
 * Các chức năng chính:
 * - Đăng nhập (email/password)
 * - Đăng ký tài khoản mới
 * - Đăng xuất và quản lý session
 * - Xác thực và phân quyền
 * - Chuyển hướng sau đăng nhập theo role
 * =====================================================
 */
public class AuthenticationController extends HttpServlet {
    
    private DAOUser daoUser;
    private EmailService emailService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        daoUser = new DAOUser();
        emailService = new EmailService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return;
        }
        
        switch (pathInfo) {
            case "/logout":
                handleLogout(request, response);
                break;
            case "/reset-password":
                handleResetPasswordPage(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            case "/verify-email":
                handleEmailVerification(request, response);
                break;
            case "/resend-verification":
                handleResendVerification(request, response);
                break;
            case "/forgot-password":
                handleForgotPassword(request, response);
                break;
            case "/reset-password":
                handleResetPassword(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Handle login request
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validate input
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("loginError", "Email và mật khẩu không được để trống");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Attempt login
        User user = daoUser.login(email.trim(), password);
        
        if (user != null) {
            // Login successful
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("userFullName", user.getFullName());
            
            // Redirect to homepage after successful login
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp");
            
        } else {
            // Login failed
            request.setAttribute("loginError", "Email hoặc mật khẩu không đúng");
            request.setAttribute("loginEmail", email); // Keep email for user convenience
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle register request
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String fullName = request.getParameter("fullname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String genderStr = request.getParameter("gender");
        String address = request.getParameter("address");
        
        // Validate input
        String validationError = validateRegistrationInput(fullName, email, password, confirmPassword);
        if (validationError != null) {
            request.setAttribute("registerError", validationError);
            setRegistrationFormData(request, fullName, email, phone, dobStr, genderStr, address);
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Validate Gmail requirement
        if (!email.trim().toLowerCase().endsWith("@gmail.com")) {
            request.setAttribute("registerError", "Vui lòng sử dụng email Gmail để đăng ký");
            setRegistrationFormData(request, fullName, email, phone, dobStr, genderStr, address);
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Check if email already exists
        if (daoUser.isEmailExists(email.trim())) {
            request.setAttribute("registerError", "Email đã được sử dụng");
            setRegistrationFormData(request, fullName, email, phone, dobStr, genderStr, address);
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Generate verification code
        String verificationCode = emailService.generateVerificationCode();
        
        // Create new user
        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(email.trim());
        newUser.setPassword(password);
        newUser.setPhone(phone != null ? phone.trim() : null);
        
        // Set default role as Patient
        newUser.setRole(User.Role.PATIENT);
        
        // Attempt registration with verification
        boolean registrationSuccess = daoUser.registerWithVerification(newUser, verificationCode);
        
        if (registrationSuccess) {
            // Send verification email
            boolean emailSent = emailService.sendVerificationEmail(email.trim(), verificationCode, fullName.trim());
            
            if (emailSent) {
                // Registration and email successful - show email verification popup
                request.setAttribute("verificationEmail", email.trim());
                request.setAttribute("needEmailVerification", "true");
                request.setAttribute("registerSuccess", "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
                request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            } else {
                // Registration successful but email failed
                request.setAttribute("registerError", "Đăng ký thành công nhưng không thể gửi email xác thực. Vui lòng thử lại.");
                request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            }
        } else {
            // Registration failed
            request.setAttribute("registerError", "Đăng ký thất bại. Vui lòng thử lại.");
            setRegistrationFormData(request, fullName, email, phone, dobStr, genderStr, address);
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle logout request
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get current session
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Invalidate session
            session.invalidate();
        }
        
        // Set logout success message and redirect to landing page
        request.setAttribute("logoutSuccess", "Đăng xuất thành công!");
        request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
    }
    
    /**
     * Handle email verification request
     */
    private void handleEmailVerification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String verificationCode = request.getParameter("verificationCode");
        
        // Validate input
        if (email == null || email.trim().isEmpty() || 
            verificationCode == null || verificationCode.trim().isEmpty()) {
            
            request.setAttribute("verificationError", "Email và mã xác thực không được để trống");
            request.setAttribute("verificationEmail", email);
            request.setAttribute("needEmailVerification", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Attempt email verification
        boolean verificationSuccess = daoUser.verifyEmail(email.trim(), verificationCode.trim());
        
        if (verificationSuccess) {
            // Verification successful - show login modal
            request.setAttribute("registerSuccess", "Xác thực email thành công! Bạn có thể đăng nhập ngay bây giờ.");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        } else {
            // Verification failed - show email verification modal with error
            request.setAttribute("verificationError", "Mã xác thực không đúng hoặc đã hết hạn");
            request.setAttribute("verificationEmail", email);
            request.setAttribute("needEmailVerification", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle resend verification code request
     */
    private void handleResendVerification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("verificationError", "Email không được để trống");
            request.setAttribute("needEmailVerification", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Generate new verification code
        String newVerificationCode = emailService.generateVerificationCode();
        
        // Update verification code in database
        boolean updateSuccess = daoUser.resendVerificationCode(email.trim(), newVerificationCode);
        
        if (updateSuccess) {
            // Get user info for email
            User user = daoUser.getUserByEmail(email.trim());
            if (user != null) {
                // Send new verification email
                boolean emailSent = emailService.sendVerificationEmail(email.trim(), newVerificationCode, user.getFullName());
                
                if (emailSent) {
                    request.setAttribute("verificationSuccess", "Mã xác thực mới đã được gửi đến email của bạn");
                } else {
                    request.setAttribute("verificationError", "Không thể gửi email xác thực. Vui lòng thử lại.");
                }
            } else {
                request.setAttribute("verificationError", "Không tìm thấy tài khoản với email này");
            }
        } else {
            request.setAttribute("verificationError", "Không thể tạo mã xác thực mới. Vui lòng thử lại.");
        }
        
        request.setAttribute("verificationEmail", email);
        request.setAttribute("needEmailVerification", "true");
        request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
    }
    
    /**
     * Validate registration input
     */
    private String validateRegistrationInput(String fullName, String email, String password, String confirmPassword) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Họ tên không được để trống";
        }
        
        if (email == null || email.trim().isEmpty()) {
            return "Email không được để trống";
        }
        
        if (!isValidEmail(email)) {
            return "Email không hợp lệ";
        }
        
        if (password == null || password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }
        
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp";
        }
        
        return null; // No validation errors
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Set registration form data back to request for display
     */
    private void setRegistrationFormData(HttpServletRequest request, String fullName, 
                                       String email, String phone, String dob, 
                                       String gender, String address) {
        request.setAttribute("regFullName", fullName);
        request.setAttribute("regEmail", email);
        request.setAttribute("regPhone", phone);
        request.setAttribute("regDob", dob);
        request.setAttribute("regGender", gender);
        request.setAttribute("regAddress", address);
    }
    
    /**
     * Handle forgot password request
     */
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("forgotPasswordError", "Email không được để trống");
            request.setAttribute("showForgotPasswordModal", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        if (!isValidEmail(email)) {
            request.setAttribute("forgotPasswordError", "Email không hợp lệ");
            request.setAttribute("showForgotPasswordModal", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Check if user exists
        User user = daoUser.getUserByEmail(email.trim());
        if (user == null) {
            request.setAttribute("forgotPasswordError", "Không tìm thấy tài khoản với email này");
            request.setAttribute("showForgotPasswordModal", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Generate reset token
        String resetToken = daoUser.generateResetPasswordToken();
        
        // Set token expiry time (30 minutes from now)
        java.sql.Timestamp expiryTime = new java.sql.Timestamp(System.currentTimeMillis() + (30 * 60 * 1000));
        
        // Update user with reset token
        boolean updateSuccess = daoUser.updateResetPasswordToken(email.trim(), resetToken, expiryTime);
        
        if (updateSuccess) {
            // Send reset password email
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            boolean emailSent = emailService.sendResetPasswordEmail(email.trim(), resetToken, user.getFullName(), baseUrl);
            
            if (emailSent) {
                request.setAttribute("forgotPasswordSuccess", "Chúng tôi đã gửi link khôi phục mật khẩu đến email của bạn. Vui lòng kiểm tra email.");
                request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            } else {
                request.setAttribute("forgotPasswordError", "Không thể gửi email khôi phục mật khẩu. Vui lòng thử lại.");
                request.setAttribute("showForgotPasswordModal", "true");
                request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("forgotPasswordError", "Có lỗi xảy ra. Vui lòng thử lại.");
            request.setAttribute("showForgotPasswordModal", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle reset password page (GET request)
     */
    private void handleResetPasswordPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        
        // Validate token
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("resetPasswordError", "Link khôi phục mật khẩu không hợp lệ");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Check if token is valid
        User user = daoUser.getUserByResetToken(token.trim());
        if (user == null) {
            request.setAttribute("resetPasswordError", "Link khôi phục mật khẩu không hợp lệ hoặc đã hết hạn");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Token is valid, show reset password form
        request.setAttribute("resetPasswordToken", token.trim());
        request.setAttribute("resetPasswordEmail", user.getEmail());
        request.setAttribute("showResetPasswordModal", "true");
        request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
    }
    
    /**
     * Handle reset password form submission (POST request)
     */
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("resetPasswordError", "Token không hợp lệ");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            request.setAttribute("resetPasswordError", "Mật khẩu mới phải có ít nhất 6 ký tự");
            request.setAttribute("resetPasswordToken", token.trim());
            request.setAttribute("showResetPasswordModal", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            request.setAttribute("resetPasswordError", "Mật khẩu xác nhận không khớp");
            request.setAttribute("resetPasswordToken", token.trim());
            request.setAttribute("showResetPasswordModal", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Reset password
        boolean resetSuccess = daoUser.resetPassword(token.trim(), newPassword);
        
        if (resetSuccess) {
            request.setAttribute("resetPasswordSuccess", "Mật khẩu đã được đặt lại thành công! Bạn có thể đăng nhập với mật khẩu mới.");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        } else {
            request.setAttribute("resetPasswordError", "Không thể đặt lại mật khẩu. Link có thể đã hết hạn hoặc đã được sử dụng.");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        }
    }
} 