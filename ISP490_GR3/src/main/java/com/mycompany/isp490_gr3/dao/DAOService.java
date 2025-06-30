package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.MedicalService;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * =====================================================
 * DAOService - QUẢN LÝ DỊCH VỤ Y TẾ
 * 
 * Chức năng: Xử lý tất cả các thao tác liên quan đến dịch vụ y tế
 * Bảng database: medical_services
 * Model tương ứng: MedicalService
 * URL liên quan: /admin/services
 * 
 * Các chức năng chính:
 * - Lấy danh sách dịch vụ y tế
 * - Tìm kiếm dịch vụ theo nhóm/tên
 * - Thêm/sửa/xóa dịch vụ
 * - Quản lý nhóm dịch vụ
 * =====================================================
 */

public class DAOService {
    
    // Get all medical services
    public List<MedicalService> getAllServices() {
        List<MedicalService> services = new ArrayList<>();
        String sql = "SELECT services_id, service_group, service_name, price, created_at, updated_at, isdeleted FROM medical_services WHERE isdeleted = FALSE ORDER BY service_group, service_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                MedicalService service = new MedicalService();
                service.setServicesId(rs.getInt("services_id"));
                service.setServiceGroup(rs.getString("service_group"));
                service.setServiceName(rs.getString("service_name"));
                service.setPrice(rs.getBigDecimal("price"));
                service.setCreatedAt(rs.getTimestamp("created_at"));
                service.setUpdatedAt(rs.getTimestamp("updated_at"));
                service.setIsdeleted(rs.getBoolean("isdeleted"));
                services.add(service);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all services: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    
    // Search services by group or name
    public List<MedicalService> searchServices(String keyword) {
        List<MedicalService> services = new ArrayList<>();
        String sql = "SELECT services_id, service_group, service_name, price, created_at, updated_at, isdeleted " +
                    "FROM medical_services WHERE (service_group LIKE ? OR service_name LIKE ?) AND isdeleted = FALSE ORDER BY service_group, service_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchKeyword = "%" + keyword + "%";
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalService service = new MedicalService();
                    service.setServicesId(rs.getInt("services_id"));
                    service.setServiceGroup(rs.getString("service_group"));
                    service.setServiceName(rs.getString("service_name"));
                    service.setPrice(rs.getBigDecimal("price"));
                    service.setCreatedAt(rs.getTimestamp("created_at"));
                    service.setUpdatedAt(rs.getTimestamp("updated_at"));
                    service.setIsdeleted(rs.getBoolean("isdeleted"));
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching services: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    
    // Get service by ID
    public MedicalService getServiceById(int servicesId) {
        String sql = "SELECT services_id, service_group, service_name, price, created_at, updated_at, isdeleted " +
                    "FROM medical_services WHERE services_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, servicesId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicalService service = new MedicalService();
                    service.setServicesId(rs.getInt("services_id"));
                    service.setServiceGroup(rs.getString("service_group"));
                    service.setServiceName(rs.getString("service_name"));
                    service.setPrice(rs.getBigDecimal("price"));
                    service.setCreatedAt(rs.getTimestamp("created_at"));
                    service.setUpdatedAt(rs.getTimestamp("updated_at"));
                    service.setIsdeleted(rs.getBoolean("isdeleted"));
                    return service;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting service by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Check if service exists by group and name
    public MedicalService findExistingService(String serviceGroup, String serviceName) {
        String sql = "SELECT services_id, service_group, service_name, price, created_at, updated_at, isdeleted " +
                    "FROM medical_services WHERE service_group = ? AND service_name = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, serviceGroup);
            stmt.setString(2, serviceName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicalService service = new MedicalService();
                    service.setServicesId(rs.getInt("services_id"));
                    service.setServiceGroup(rs.getString("service_group"));
                    service.setServiceName(rs.getString("service_name"));
                    service.setPrice(rs.getBigDecimal("price"));
                    service.setCreatedAt(rs.getTimestamp("created_at"));
                    service.setUpdatedAt(rs.getTimestamp("updated_at"));
                    service.setIsdeleted(rs.getBoolean("isdeleted"));
                    return service;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding existing service: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add new service
    public boolean addService(MedicalService service) {
        String sql = "INSERT INTO medical_services (service_group, service_name, price) VALUES (?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, service.getServiceGroup());
            stmt.setString(2, service.getServiceName());
            stmt.setBigDecimal(3, service.getPrice());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding service: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update service information
    public boolean updateService(MedicalService service) {
        String sql = "UPDATE medical_services SET service_group = ?, service_name = ?, price = ?, updated_at = CURRENT_TIMESTAMP WHERE services_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, service.getServiceGroup());
            stmt.setString(2, service.getServiceName());
            stmt.setBigDecimal(3, service.getPrice());
            stmt.setInt(4, service.getServicesId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating service: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Soft delete service
    public boolean deleteService(int servicesId) {
        String sql = "UPDATE medical_services SET isdeleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE services_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, servicesId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting service: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Get all distinct service groups
    public List<String> getAllServiceGroups() {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT DISTINCT service_group FROM medical_services WHERE isdeleted = FALSE ORDER BY service_group";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                groups.add(rs.getString("service_group"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting service groups: " + e.getMessage());
            e.printStackTrace();
        }
        
        return groups;
    }
    
    // Filter services by group
    public List<MedicalService> filterServicesByGroup(String serviceGroup) {
        List<MedicalService> services = new ArrayList<>();
        String sql = "SELECT services_id, service_group, service_name, price, created_at, updated_at, isdeleted " +
                    "FROM medical_services WHERE service_group = ? AND isdeleted = FALSE ORDER BY service_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, serviceGroup);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalService service = new MedicalService();
                    service.setServicesId(rs.getInt("services_id"));
                    service.setServiceGroup(rs.getString("service_group"));
                    service.setServiceName(rs.getString("service_name"));
                    service.setPrice(rs.getBigDecimal("price"));
                    service.setCreatedAt(rs.getTimestamp("created_at"));
                    service.setUpdatedAt(rs.getTimestamp("updated_at"));
                    service.setIsdeleted(rs.getBoolean("isdeleted"));
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering services by group: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    
    public static void main(String[] args) {
    DAOService dao = new DAOService();
    List<MedicalService> services = dao.getAllServices();

    System.out.println("Danh sách dịch vụ y tế:");
    for (MedicalService service : services) {
        System.out.printf("ID: %d | Nhóm: %s | Tên: %s | Giá: %s | Tạo lúc: %s | Cập nhật: %s | Đã xóa: %s%n",
                service.getServicesId(),
                service.getServiceGroup(),
                service.getServiceName(),
                service.getPrice(),
                service.getCreatedAt(),
                service.getUpdatedAt(),
                service.isIsdeleted());
    }
}


} 
