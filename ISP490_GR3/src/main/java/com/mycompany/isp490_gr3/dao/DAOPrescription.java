package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.PrescriptionMedicine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================
 * DAOPrescription - QUẢN LÝ THUỐC
 * 
 * Chức năng: Xử lý tất cả các thao tác liên quan đến thuốc
 * Bảng database: prescription_medicine
 * Model tương ứng: PrescriptionMedicine
 * 
 * Các chức năng chính:
 * - Quản lý thuốc trên thị trường (CRUD + search)
 * =====================================================
 */
public class DAOPrescription {
    
    // ===============================
    // PRESCRIPTION MEDICINE METHODS
    // ===============================
    
    /**
     * Lấy tất cả thuốc trong hệ thống
     */
    public List<PrescriptionMedicine> getAllMedicines() {
        List<PrescriptionMedicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM prescription_medicine WHERE is_deleted = FALSE ORDER BY medicine_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all medicines: " + e.getMessage());
            e.printStackTrace();
        }
        
        return medicines;
    }
    
    /**
     * Tìm kiếm thuốc theo tên
     */
    public List<PrescriptionMedicine> searchMedicines(String keyword) {
        List<PrescriptionMedicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM prescription_medicine WHERE medicine_name LIKE ? AND is_deleted = FALSE ORDER BY medicine_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching medicines: " + e.getMessage());
            e.printStackTrace();
        }
        
        return medicines;
    }
    
    /**
     * Lấy thuốc theo ID
     */
    public PrescriptionMedicine getMedicineById(int id) {
        String sql = "SELECT * FROM prescription_medicine WHERE pre_medicine_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMedicine(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting medicine by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Thêm thuốc mới
     */
    public boolean addMedicine(PrescriptionMedicine medicine) {
        String sql = "INSERT INTO prescription_medicine (medicine_name, days_of_treatment, units_per_day, " +
                    "total_quantity, unit_of_measure, administration_route, usage_instructions) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medicine.getMedicineName());
            ps.setObject(2, medicine.getDaysOfTreatment());
            ps.setObject(3, medicine.getUnitsPerDay());
            ps.setObject(4, medicine.getTotalQuantity());
            ps.setString(5, medicine.getUnitOfMeasure());
            ps.setString(6, medicine.getAdministrationRoute());
            ps.setString(7, medicine.getUsageInstructions());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cập nhật thông tin thuốc
     */
    public boolean updateMedicine(PrescriptionMedicine medicine) {
        String sql = "UPDATE prescription_medicine SET medicine_name = ?, days_of_treatment = ?, " +
                    "units_per_day = ?, total_quantity = ?, unit_of_measure = ?, " +
                    "administration_route = ?, usage_instructions = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE pre_medicine_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medicine.getMedicineName());
            ps.setObject(2, medicine.getDaysOfTreatment());
            ps.setObject(3, medicine.getUnitsPerDay());
            ps.setObject(4, medicine.getTotalQuantity());
            ps.setString(5, medicine.getUnitOfMeasure());
            ps.setString(6, medicine.getAdministrationRoute());
            ps.setString(7, medicine.getUsageInstructions());
            ps.setInt(8, medicine.getPreMedicineId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Xóa thuốc (soft delete)
     */
    public boolean deleteMedicine(int medicineId) {
        String sql = "UPDATE prescription_medicine SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE pre_medicine_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, medicineId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kiểm tra tên thuốc đã tồn tại chưa
     */
    public boolean isMedicineNameExists(String medicineName, int excludeId) {
        String sql = "SELECT COUNT(*) FROM prescription_medicine WHERE medicine_name = ? AND is_deleted = FALSE";
        if (excludeId > 0) {
            sql += " AND pre_medicine_id != ?";
        }
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medicineName);
            if (excludeId > 0) {
                ps.setInt(2, excludeId);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking medicine name existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ===============================
    // HELPER METHODS
    // ===============================
    
    private PrescriptionMedicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        PrescriptionMedicine medicine = new PrescriptionMedicine();
        medicine.setPreMedicineId(rs.getInt("pre_medicine_id"));
        medicine.setMedicineName(rs.getString("medicine_name"));
        medicine.setDaysOfTreatment((Integer) rs.getObject("days_of_treatment"));
        medicine.setUnitsPerDay((Integer) rs.getObject("units_per_day"));
        medicine.setTotalQuantity((Integer) rs.getObject("total_quantity"));
        medicine.setUnitOfMeasure(rs.getString("unit_of_measure"));
        medicine.setAdministrationRoute(rs.getString("administration_route"));
        medicine.setUsageInstructions(rs.getString("usage_instructions"));
        medicine.setCreatedAt(rs.getTimestamp("created_at"));
        medicine.setUpdatedAt(rs.getTimestamp("updated_at"));
        medicine.setDeleted(rs.getBoolean("is_deleted"));
        return medicine;
    }
} 