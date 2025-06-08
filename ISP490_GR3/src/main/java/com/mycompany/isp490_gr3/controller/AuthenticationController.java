package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOUser;
import com.mycompany.isp490_gr3.model.GoogleUserInfo;
import com.mycompany.isp490_gr3.model.User;
import com.mycompany.isp490_gr3.service.GoogleOAuthService;
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
public class AuthenticationController extends HttpServlet {
    
    private DAOUser daoUser;
    private GoogleOAuthService googleOAuthService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        daoUser = new DAOUser();
        googleOAuthService = new GoogleOAuthService();
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
            case "/google":
                handleGoogleLogin(request, response);
                break;
            case "/google/callback":
                handleGoogleCallback(request, response);
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
        
        // Check if email already exists
        if (daoUser.isEmailExists(email.trim())) {
            request.setAttribute("registerError", "Email đã được sử dụng");
            setRegistrationFormData(request, fullName, email, phone, dobStr, genderStr, address);
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        // Create new user
        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(email.trim());
        newUser.setPassword(password);
        newUser.setPhone(phone != null ? phone.trim() : null);
        newUser.setAddress(address != null ? address.trim() : null);
        
        // Set date of birth
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = sdf.parse(dobStr);
                newUser.setDob(new Date(utilDate.getTime()));
            } catch (ParseException e) {
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }
        
        // Set gender
        if (genderStr != null && !genderStr.trim().isEmpty()) {
            newUser.setGender(User.Gender.fromString(genderStr));
        }
        
        // Set default role as Patient
        newUser.setRole(User.Role.PATIENT);
        
        // Attempt registration
        boolean registrationSuccess = daoUser.register(newUser);
        
        if (registrationSuccess) {
            // Registration successful - show success message and open login modal
            request.setAttribute("registerSuccess", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.setAttribute("showLoginModal", "true");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
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
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        // Add success message and redirect
        request.setAttribute("logoutSuccess", "Đăng xuất thành công! Cảm ơn bạn đã sử dụng dịch vụ.");
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
     * Handle Google OAuth login initiation
     */
    private void handleGoogleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!googleOAuthService.isConfigured()) {
            request.setAttribute("loginError", "Google OAuth chưa được cấu hình. Vui lòng liên hệ quản trị viên.");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        String authorizationUrl = googleOAuthService.getAuthorizationUrl();
        if (authorizationUrl != null) {
            response.sendRedirect(authorizationUrl);
        } else {
            request.setAttribute("loginError", "Không thể tạo liên kết đăng nhập Google. Vui lòng thử lại.");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle Google OAuth callback
     */
    private void handleGoogleCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String authorizationCode = request.getParameter("code");
        String error = request.getParameter("error");
        
        // Check for OAuth errors
        if (error != null) {
            String errorMessage = "access_denied".equals(error) ? 
                "Bạn đã từ chối quyền truy cập Google. Vui lòng thử lại." :
                "Lỗi xác thực Google: " + error;
            request.setAttribute("loginError", errorMessage);
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        if (authorizationCode == null || authorizationCode.trim().isEmpty()) {
            request.setAttribute("loginError", "Không nhận được mã xác thực từ Google. Vui lòng thử lại.");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            return;
        }
        
        try {
            // Get user info from Google
            GoogleUserInfo googleUserInfo = googleOAuthService.getUserInfoFromCode(authorizationCode);
            
            if (googleUserInfo == null || googleUserInfo.getEmail() == null) {
                request.setAttribute("loginError", "Không thể lấy thông tin người dùng từ Google. Vui lòng thử lại.");
                request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
                return;
            }
            
            // Check if user exists by Google ID
            User existingUser = daoUser.getUserByGoogleId(googleUserInfo.getId());
            
            if (existingUser != null) {
                // User exists with Google ID - login directly
                loginUser(request, response, existingUser);
                return;
            }
            
            // Check if user exists by email
            existingUser = daoUser.getUserByEmail(googleUserInfo.getEmail());
            
            if (existingUser != null) {
                // User exists but no Google ID - link accounts
                if (daoUser.linkGoogleAccount(googleUserInfo.getEmail(), googleUserInfo.getId())) {
                    existingUser.setGoogleId(googleUserInfo.getId());
                    loginUser(request, response, existingUser);
                } else {
                    request.setAttribute("loginError", "Không thể liên kết tài khoản Google. Vui lòng thử lại.");
                    request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
                }
                return;
            }
            
            // New user - register with Google
            User newUser = createUserFromGoogleInfo(googleUserInfo);
            
            if (daoUser.registerWithGoogle(newUser)) {
                // Registration successful - login the user
                loginUser(request, response, newUser);
            } else {
                request.setAttribute("registerError", "Không thể tạo tài khoản. Vui lòng thử lại.");
                request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
            }
            
        } catch (IOException e) {
            System.err.println("Error processing Google OAuth callback: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("loginError", "Lỗi xử lý thông tin Google. Vui lòng thử lại.");
            request.getRequestDispatcher("/jsp/landing.jsp").forward(request, response);
        }
    }
    
    /**
     * Create User object from Google user info
     */
    private User createUserFromGoogleInfo(GoogleUserInfo googleUserInfo) {
        User user = new User();
        user.setFullName(googleUserInfo.getName() != null ? googleUserInfo.getName() : "Google User");
        user.setEmail(googleUserInfo.getEmail());
        user.setGoogleId(googleUserInfo.getId());
        user.setRole(User.Role.PATIENT); // Default role
        return user;
    }
    
    /**
     * Login user and set session
     */
    private void loginUser(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userFullName", user.getFullName());
        
        // Redirect to homepage after successful login
        response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp");
    }
} 