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
 * - Đăng nhập/đăng ký (bao gồm Google OAuth)
 * - Quản lý thông tin cá nhân
 * - Phân quyền người dùng (Admin, Doctor, Receptionist, Patient)
 * - Đổi mật khẩu, liên kết tài khoản Google
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
        String sql = "SELECT * FROM user WHERE Email = ? AND Password = ? AND IsDeleted = FALSE";
        
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
        
        String sql = "INSERT INTO user (id, FullName, Email, Password, Phone, Role, Created_At) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Generate unique ID
            String userId = generateUserId();
            
            ps.setString(1, userId);
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashPassword(user.getPassword()));
            ps.setString(5, user.getPhone());
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
     * Check if email already exists in database
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE Email = ? AND IsDeleted = FALSE";
        
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
        String sql = "SELECT * FROM user WHERE id = ? AND IsDeleted = FALSE";
        
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
        String sql = "SELECT * FROM user WHERE Email = ? AND IsDeleted = FALSE";
        
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
        String sql = "UPDATE user SET FullName = ?, Phone = ?, Updated_At = ? WHERE id = ? AND IsDeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
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
     * Get user by Google ID
     * @param googleId Google OAuth ID
     * @return User object if found, null otherwise
     */
    public User getUserByGoogleId(String googleId) {
        String sql = "SELECT * FROM user WHERE GoogleId = ? AND IsDeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, googleId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by Google ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Register user with Google OAuth
     * @param user User object with Google information
     * @return true if registration successful, false otherwise
     */
    public boolean registerWithGoogle(User user) {
        // Check if email already exists
        if (isEmailExists(user.getEmail())) {
            System.out.println("Email already exists: " + user.getEmail());
            return false;
        }
        
        String sql = "INSERT INTO user (id, FullName, Email, GoogleId, Role, Created_At) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Generate unique ID
            String userId = generateUserId();
            
            ps.setString(1, userId);
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getGoogleId());
            ps.setString(5, user.getRole().getValue());
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            
            int result = ps.executeUpdate();
            if (result > 0) {
                user.setId(userId); // Set the generated ID back to the user object
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error during Google registration: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Link existing user account with Google ID
     * @param email User's email
     * @param googleId Google OAuth ID
     * @return true if linking successful, false otherwise
     */
    public boolean linkGoogleAccount(String email, String googleId) {
        String sql = "UPDATE user SET GoogleId = ?, Updated_At = ? WHERE Email = ? AND IsDeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, googleId);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, email);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error linking Google account: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
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
        user.setFullName(rs.getString("FullName"));
        user.setEmail(rs.getString("Email"));
        user.setPassword(rs.getString("Password"));
        user.setPhone(rs.getString("Phone"));
        user.setRole(User.Role.fromString(rs.getString("Role")));
        user.setGoogleId(rs.getString("GoogleId"));
        user.setCreatedAt(rs.getTimestamp("Created_At"));
        user.setUpdatedBy(rs.getString("Updated_By"));
        user.setUpdatedAt(rs.getTimestamp("Updated_At"));
        user.setDeleted(rs.getBoolean("IsDeleted"));
        
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
     * Update user role
     * @param userId User ID
     * @param newRole New role for the user
     * @param updatedBy Who is making the update
     * @return true if role update successful, false otherwise
     */
    public boolean updateUserRole(String userId, User.Role newRole, String updatedBy) {
        // Check if user exists and is not deleted
        User user = getUserById(userId);
        if (user == null) {
            System.out.println("User not found: " + userId);
            return false;
        }
        
        // Prevent updating admin role
        if (user.getRole() == User.Role.ADMIN) {
            System.out.println("Cannot update role for admin user: " + userId);
            return false;
        }
        
        // Prevent setting role to admin
        if (newRole == User.Role.ADMIN) {
            System.out.println("Cannot set role to admin: " + userId);
            return false;
        }
        
        String sql = "UPDATE user SET Role = ?, Updated_By = ?, Updated_At = ? WHERE id = ? AND IsDeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newRole.getValue());
            ps.setString(2, updatedBy);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, userId);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
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