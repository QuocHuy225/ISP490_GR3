package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Partner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================
 * DAOPartner - QUẢN LÝ ĐỐI TÁC
 * 
 * Chức năng: Xử lý tất cả các thao tác liên quan đến đối tác
 * Bảng database: partners
 * Model tương ứng: Partner
 * URL liên quan: /admin/partners
 * 
 * Các chức năng chính:
 * - Lấy danh sách đối tác
 * - Tìm kiếm đối tác theo tên/số điện thoại
 * - Thêm/sửa/xóa đối tác
 * - Quản lý thông tin đối tác
 * =====================================================
 */

public class DAOPartner {
    
    // Get all partners
    public List<Partner> getAllPartners() {
        List<Partner> partners = new ArrayList<>();
        String sql = "SELECT partner_id, name, phone, address, description, created_at, updated_at, isdeleted FROM partners WHERE isdeleted = FALSE ORDER BY name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Partner partner = new Partner();
                partner.setPartnerId(rs.getInt("partner_id"));
                partner.setName(rs.getString("name"));
                partner.setPhone(rs.getString("phone"));
                partner.setAddress(rs.getString("address"));
                partner.setDescription(rs.getString("description"));
                partner.setCreatedAt(rs.getTimestamp("created_at"));
                partner.setUpdatedAt(rs.getTimestamp("updated_at"));
                partner.setIsdeleted(rs.getBoolean("isdeleted"));
                partners.add(partner);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all partners: " + e.getMessage());
            e.printStackTrace();
        }
        
        return partners;
    }
    
    // Search partners by name or phone
    public List<Partner> searchPartners(String keyword) {
        List<Partner> partners = new ArrayList<>();
        String sql = "SELECT partner_id, name, phone, address, description, created_at, updated_at, isdeleted " +
                    "FROM partners WHERE (name LIKE ? OR phone LIKE ?) AND isdeleted = FALSE ORDER BY name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchKeyword = "%" + keyword + "%";
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Partner partner = new Partner();
                    partner.setPartnerId(rs.getInt("partner_id"));
                    partner.setName(rs.getString("name"));
                    partner.setPhone(rs.getString("phone"));
                    partner.setAddress(rs.getString("address"));
                    partner.setDescription(rs.getString("description"));
                    partner.setCreatedAt(rs.getTimestamp("created_at"));
                    partner.setUpdatedAt(rs.getTimestamp("updated_at"));
                    partner.setIsdeleted(rs.getBoolean("isdeleted"));
                    partners.add(partner);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching partners: " + e.getMessage());
            e.printStackTrace();
        }
        
        return partners;
    }
    
    // Get partner by ID
    public Partner getPartnerById(int partnerId) {
        String sql = "SELECT partner_id, name, phone, address, description, created_at, updated_at, isdeleted " +
                    "FROM partners WHERE partner_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, partnerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Partner partner = new Partner();
                    partner.setPartnerId(rs.getInt("partner_id"));
                    partner.setName(rs.getString("name"));
                    partner.setPhone(rs.getString("phone"));
                    partner.setAddress(rs.getString("address"));
                    partner.setDescription(rs.getString("description"));
                    partner.setCreatedAt(rs.getTimestamp("created_at"));
                    partner.setUpdatedAt(rs.getTimestamp("updated_at"));
                    partner.setIsdeleted(rs.getBoolean("isdeleted"));
                    return partner;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting partner by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Check if partner exists by name
    public Partner findExistingPartner(String name) {
        String sql = "SELECT partner_id, name, phone, address, description, created_at, updated_at, isdeleted " +
                    "FROM partners WHERE name = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Partner partner = new Partner();
                    partner.setPartnerId(rs.getInt("partner_id"));
                    partner.setName(rs.getString("name"));
                    partner.setPhone(rs.getString("phone"));
                    partner.setAddress(rs.getString("address"));
                    partner.setDescription(rs.getString("description"));
                    partner.setCreatedAt(rs.getTimestamp("created_at"));
                    partner.setUpdatedAt(rs.getTimestamp("updated_at"));
                    partner.setIsdeleted(rs.getBoolean("isdeleted"));
                    return partner;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding existing partner: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add new partner
    public boolean addPartner(Partner partner) {
        String sql = "INSERT INTO partners (name, phone, address, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, partner.getName());
            stmt.setString(2, partner.getPhone());
            stmt.setString(3, partner.getAddress());
            stmt.setString(4, partner.getDescription());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding partner: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update partner information
    public boolean updatePartner(Partner partner) {
        String sql = "UPDATE partners SET name = ?, phone = ?, address = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE partner_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, partner.getName());
            stmt.setString(2, partner.getPhone());
            stmt.setString(3, partner.getAddress());
            stmt.setString(4, partner.getDescription());
            stmt.setInt(5, partner.getPartnerId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating partner: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Soft delete partner
    public boolean deletePartner(int partnerId) {
        String sql = "UPDATE partners SET isdeleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE partner_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, partnerId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting partner: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    

    
    public static void main(String[] args) {
        DAOPartner dao = new DAOPartner();
        List<Partner> partners = dao.getAllPartners();

        System.out.println("Danh sách đối tác:");
        for (Partner partner : partners) {
            System.out.printf("ID: %d | Tên: %s | SĐT: %s | Địa chỉ: %s | Mô tả: %s | Tạo lúc: %s | Cập nhật: %s | Đã xóa: %s%n",
                    partner.getPartnerId(),
                    partner.getName(),
                    partner.getPhone(),
                    partner.getAddress(),
                    partner.getDescription(),
                    partner.getCreatedAt(),
                    partner.getUpdatedAt(),
                    partner.isIsdeleted());
        }
    }
} 