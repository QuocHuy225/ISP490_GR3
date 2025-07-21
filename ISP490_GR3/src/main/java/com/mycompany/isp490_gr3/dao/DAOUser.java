package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

/**
 * =====================================================
 * DAOUser - QUẢN LÝ NGƯỜI DÙNG HỆ THỐNG
 * 
 * Chức năng: Xử lý tất cả các thao tác liên quan đến người dùng
 * Bảng database: user
 * Model tương ứng: User
 * URL liên quan: /auth/*, /admin/authorization, /user/profile
 * 
 * Các chức năng chính:
 * - Đăng nhập/đăng ký
 * - Quản lý thông tin cá nhân
 * - Phân quyền người dùng (Admin, Doctor, Receptionist, Patient)
 * - Đổi mật khẩu
 * - Xóa/khôi phục người dùng (soft delete)
 * - Tìm kiếm và lọc người dùng
 * =====================================================
 */
public class DAOUser {
    
    /**
     * Authenticate user login
     * @param email User's email
     * @param password User's password (plain text)
     * @return User object if login successful, null otherwise
     */
    public User login(String email, String password) {
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM user WHERE email = ? AND password = ? AND isdeleted = FALSE AND (is_email_verified = TRUE OR is_email_verified IS NULL)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Register a new user
     * @param user User object to register
     * @return true if registration successful, false otherwise
     */
    public boolean register(User user) {
        // Check if email already exists
        if (isEmailExists(user.getEmail())) {
            System.out.println("Email already exists: " + user.getEmail());
            return false;
        }
        
        String sql = "INSERT INTO user (id, fullname, email, password, other_contact, role, created_At) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Generate unique ID
            String userId = generateUserId();
            
            ps.setString(1, userId);
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashPassword(user.getPassword()));
            ps.setString(5, user.getOtherContact());
            ps.setString(6, user.getRole().getValue());
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Register a new user with email verification
     * @param user User object to register
     * @param verificationToken Verification token for email confirmation
     * @return true if registration successful, false otherwise
     */
    public boolean registerWithVerification(User user, String verificationToken) {
        // Check if email already exists
        if (isEmailExists(user.getEmail())) {
            System.out.println("Email already exists: " + user.getEmail());
            return false;
        }
        
        String sql = "INSERT INTO user (id, fullname, email, password, other_contact, role, created_At, verification_token, is_email_verified, verification_expiry_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Generate unique ID
            String userId = generateUserId();
            
            // Set verification expiry time (15 minutes from now)
            Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + (15 * 60 * 1000));
            
            ps.setString(1, userId);
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashPassword(user.getPassword()));
            ps.setString(5, user.getOtherContact());
            ps.setString(6, user.getRole().getValue());
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            ps.setString(8, verificationToken);
            ps.setBoolean(9, false); // Not verified initially
            ps.setTimestamp(10, expiryTime);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error during registration with verification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Create a new user with pre-verified email (for admin creation)
     * @param user User object to create
     * @param createdBy ID of admin who created the user
     * @return true if creation successful, false otherwise
     */
    public boolean createVerifiedUser(User user, String createdBy) {
        // Check if email already exists
        if (isEmailExists(user.getEmail())) {
            System.out.println("Email already exists: " + user.getEmail());
            return false;
        }
        
        String sql = "INSERT INTO user (id, fullname, email, password, other_contact, role, created_At, is_email_verified, updated_By) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Generate unique ID
            String userId = generateUserId();
            
            ps.setString(1, userId);
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashPassword(user.getPassword()));
            ps.setString(5, user.getOtherContact());
            ps.setString(6, user.getRole().getValue());
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            ps.setBoolean(8, true); // Pre-verified
            ps.setString(9, createdBy);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating verified user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Verify user email with verification code
     * @param email User's email
     * @param verificationCode Verification code entered by user
     * @return true if verification successful, false otherwise
     */
    public boolean verifyEmail(String email, String verificationCode) {
        String sql = "SELECT * FROM user WHERE email = ? AND verification_token = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, verificationCode);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Check if verification token is expired
                Timestamp expiryTime = rs.getTimestamp("verification_expiry_time");
                if (expiryTime != null && expiryTime.before(new Timestamp(System.currentTimeMillis()))) {
                    System.out.println("Verification token expired for email: " + email);
                    return false;
                }
                
                // Update user as verified
                String updateSql = "UPDATE user SET is_email_verified = TRUE, verification_token = NULL, verification_expiry_time = NULL, updated_At = ? WHERE email = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    updatePs.setString(2, email);
                    
                    int result = updatePs.executeUpdate();
                    return result > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error during email verification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Resend verification code (update token and expiry time)
     * @param email User's email
     * @param newVerificationToken New verification token
     * @return true if update successful, false otherwise
     */
    public boolean resendVerificationCode(String email, String newVerificationToken) {
        String sql = "UPDATE user SET verification_token = ?, verification_expiry_time = ?, updated_At = ? WHERE email = ? AND is_email_verified = FALSE AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Set new expiry time (15 minutes from now)
            Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + (15 * 60 * 1000));
            
            ps.setString(1, newVerificationToken);
            ps.setTimestamp(2, expiryTime);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, email);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error resending verification code: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if email already exists in database
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(String userId) {
        String sql = "SELECT * FROM user WHERE id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get user by email
     * @param email User email
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Update user information
     * @param user User object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE user SET FullName = ?, Other_Contact = ?, Updated_At = ? WHERE id = ? AND IsDeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getOtherContact());
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, user.getId());
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Change user password
     * @param userId User ID
     * @param newPassword New password (plain text)
     * @return true if password change successful, false otherwise
     */
    public boolean changePassword(String userId, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        String sql = "UPDATE user SET Password = ?, Updated_At = ? WHERE id = ? AND IsDeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hashedPassword);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, userId);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update reset password token for a user
     * @param email User's email
     * @param token Reset password token
     * @param expiry Token expiry time
     * @return true if update successful, false otherwise
     */
    public boolean updateResetPasswordToken(String email, String token, Timestamp expiry) {
        String sql = "UPDATE user SET reset_password_token = ?, reset_password_expiry = ?, updated_At = ? WHERE email = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            ps.setTimestamp(2, expiry);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, email);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating reset password token: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get user by reset password token
     * @param token Reset password token
     * @return User object if found and token is valid, null otherwise
     */
    public User getUserByResetToken(String token) {
        String sql = "SELECT * FROM user WHERE reset_password_token = ? AND reset_password_expiry > ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // Check token not expired
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by reset token: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Reset user password using token
     * @param token Reset password token
     * @param newPassword New password (plain text)
     * @return true if password reset successful, false otherwise
     */
    public boolean resetPassword(String token, String newPassword) {
        // First check if token is valid
        User user = getUserByResetToken(token);
        if (user == null) {
            System.out.println("Invalid or expired reset token");
            return false;
        }
        
        String hashedPassword = hashPassword(newPassword);
        String sql = "UPDATE user SET password = ?, reset_password_token = NULL, reset_password_expiry = NULL, updated_At = ? WHERE reset_password_token = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hashedPassword);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, token);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Generate reset password token
     * @return Random token string
     */
    public String generateResetPasswordToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
    

    
    /**
     * Hash password using SHA-256
     * @param password Plain text password
     * @return Hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            e.printStackTrace();
            return password; // Return original password if hashing fails
        }
    }
    
    /**
     * Generate unique user ID
     * @return Generated user ID
     */
    private String generateUserId() {
        return "USR" + String.format("%03d", (int)(Math.random() * 1000));
    }
    
    /**
     * Map ResultSet to User object
     * @param rs ResultSet from database query
     * @return User object
     * @throws SQLException if error occurs during mapping
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setFullName(rs.getString("fullname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setOtherContact(rs.getString("other_contact"));
        user.setRole(User.Role.fromString(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_At"));
        user.setUpdatedBy(rs.getString("updated_By"));
        user.setUpdatedAt(rs.getTimestamp("updated_At"));
        user.setDeleted(rs.getBoolean("isdeleted"));
        
        // Map verification fields (may be null for existing users)
        try {
            user.setVerificationToken(rs.getString("verification_token"));
            user.setEmailVerified(rs.getBoolean("is_email_verified"));
            user.setVerificationExpiryTime(rs.getTimestamp("verification_expiry_time"));
        } catch (SQLException e) {
            // These columns might not exist for old database schema
            user.setEmailVerified(true); // Default to verified for existing users
        }
        
        // Map reset password fields (may be null for existing users)
        try {
            user.setResetPasswordToken(rs.getString("reset_password_token"));
            user.setResetPasswordExpiry(rs.getTimestamp("reset_password_expiry"));
        } catch (SQLException e) {
            // These columns might not exist for old database schema
            // Just leave them as null
        }
        
        return user;
    }
    
    /**
     * Get all users from database (excluding deleted users)
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE IsDeleted = FALSE ORDER BY Created_At DESC";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    

    
    /**
     * Get users by specific role
     * @param role Role to filter by
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(User.Role role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE Role = ? AND IsDeleted = FALSE ORDER BY Created_At DESC";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, role.getValue());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting users by role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get total count of users by role
     * @param role Role to count
     * @return Number of users with the specified role
     */
    public int getUserCountByRole(User.Role role) {
        String sql = "SELECT COUNT(*) FROM user WHERE Role = ? AND IsDeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, role.getValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user count by role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Soft delete user (set IsDeleted to TRUE)
     * @param userId User ID to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteUser(String userId) {
        String sql = "UPDATE user SET IsDeleted = TRUE, Updated_At = ? WHERE id = ? AND IsDeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, userId);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Restore deleted user (set IsDeleted to FALSE)
     * @param userId User ID to restore
     * @return true if restoration successful, false otherwise
     */
    public boolean restoreUser(String userId) {
        String sql = "UPDATE user SET IsDeleted = FALSE, Updated_At = ? WHERE id = ? AND IsDeleted = TRUE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, userId);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error restoring user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all deleted users
     * @return List of deleted users
     */
    public List<User> getDeletedUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE IsDeleted = TRUE ORDER BY Updated_At DESC";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting deleted users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;  
    }
    
    /**
     * Get all active users with filters
     * @param roleFilter Role to filter by (can be null)
     * @param sortOrder Sort order: "newest" or "oldest" (can be null)
     * @param emailSearch Email to search for (can be null)
     * @return List of filtered users
     */
    public List<User> getAllUsersWithFilters(String roleFilter, String sortOrder, String emailSearch) {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM user WHERE IsDeleted = FALSE");
        List<Object> parameters = new ArrayList<>();
        
        // Add role filter
        if (roleFilter != null && !roleFilter.trim().isEmpty() && !roleFilter.equals("all")) {
            sql.append(" AND Role = ?");
            parameters.add(roleFilter);
        }
        
        // Add email search
        if (emailSearch != null && !emailSearch.trim().isEmpty()) {
            sql.append(" AND (Email LIKE ? OR FullName LIKE ?)");
            String searchPattern = "%" + emailSearch.trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        
        // Add sort order
        if ("oldest".equals(sortOrder)) {
            sql.append(" ORDER BY Created_At ASC");
        } else {
            sql.append(" ORDER BY Created_At DESC"); // Default to newest first
        }
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting filtered users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get deleted users with filters
     * @param roleFilter Role to filter by (can be null)
     * @param sortOrder Sort order: "newest" or "oldest" (can be null)
     * @param emailSearch Email to search for (can be null)
     * @return List of filtered deleted users
     */
    public List<User> getDeletedUsersWithFilters(String roleFilter, String sortOrder, String emailSearch) {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM user WHERE IsDeleted = TRUE");
        List<Object> parameters = new ArrayList<>();
        
        // Add role filter
        if (roleFilter != null && !roleFilter.trim().isEmpty() && !roleFilter.equals("all")) {
            sql.append(" AND Role = ?");
            parameters.add(roleFilter);
        }
        
        // Add email search
        if (emailSearch != null && !emailSearch.trim().isEmpty()) {
            sql.append(" AND (Email LIKE ? OR FullName LIKE ?)");
            String searchPattern = "%" + emailSearch.trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        
        // Add sort order (use Updated_At for deleted users)
        if ("oldest".equals(sortOrder)) {
            sql.append(" ORDER BY Updated_At ASC");
        } else {
            sql.append(" ORDER BY Updated_At DESC"); // Default to newest first
        }
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting filtered deleted users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
} 