package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.MedicalSupply;
import com.mycompany.isp490_gr3.model.Medicine;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOWarehouse {
    
    // =====================================================
    // PHẦN QUẢN LÝ VẬT TƯ Y TẾ (MEDICAL SUPPLIES)
    // Bảng database: medical_supply
    // Model: MedicalSupply
    // =====================================================
    
    // Get all medical supplies
    public List<MedicalSupply> getAllSupplies() {
        List<MedicalSupply> supplies = new ArrayList<>();
        String sql = "SELECT supply_id, supply_group, supply_name, quantity, unit_price, stock_quantity, created_at, updated_at, isdeleted FROM medical_supply WHERE isdeleted = FALSE ORDER BY supply_group, supply_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                MedicalSupply supply = new MedicalSupply();
                supply.setSupplyId(rs.getInt("supply_id"));
                supply.setSupplyGroup(rs.getString("supply_group"));
                supply.setSupplyName(rs.getString("supply_name"));
                supply.setQuantity(rs.getObject("quantity", Integer.class));
                supply.setUnitPrice(rs.getBigDecimal("unit_price"));
                supply.setStockQuantity(rs.getInt("stock_quantity"));
                supply.setCreatedAt(rs.getTimestamp("created_at"));
                supply.setUpdatedAt(rs.getTimestamp("updated_at"));
                supply.setIsdeleted(rs.getBoolean("isdeleted"));
                supplies.add(supply);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all supplies: " + e.getMessage());
            e.printStackTrace();
        }
        
        return supplies;
    }
    
    // Search supplies by group or name
    public List<MedicalSupply> searchSupplies(String keyword) {
        List<MedicalSupply> supplies = new ArrayList<>();
        String sql = "SELECT supply_id, supply_group, supply_name, quantity, unit_price, stock_quantity, created_at, updated_at, isdeleted " +
                    "FROM medical_supply WHERE (supply_group LIKE ? OR supply_name LIKE ?) AND isdeleted = FALSE ORDER BY supply_group, supply_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchKeyword = "%" + keyword + "%";
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalSupply supply = new MedicalSupply();
                    supply.setSupplyId(rs.getInt("supply_id"));
                    supply.setSupplyGroup(rs.getString("supply_group"));
                    supply.setSupplyName(rs.getString("supply_name"));
                    supply.setQuantity(rs.getObject("quantity", Integer.class));
                    supply.setUnitPrice(rs.getBigDecimal("unit_price"));
                    supply.setStockQuantity(rs.getInt("stock_quantity"));
                    supply.setCreatedAt(rs.getTimestamp("created_at"));
                    supply.setUpdatedAt(rs.getTimestamp("updated_at"));
                    supply.setIsdeleted(rs.getBoolean("isdeleted"));
                    supplies.add(supply);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching supplies: " + e.getMessage());
            e.printStackTrace();
        }
        
        return supplies;
    }
    
    // Get supply by ID
    public MedicalSupply getSupplyById(int supplyId) {
        String sql = "SELECT supply_id, supply_group, supply_name, quantity, unit_price, stock_quantity, created_at, updated_at, isdeleted " +
                    "FROM medical_supply WHERE supply_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicalSupply supply = new MedicalSupply();
                    supply.setSupplyId(rs.getInt("supply_id"));
                    supply.setSupplyGroup(rs.getString("supply_group"));
                    supply.setSupplyName(rs.getString("supply_name"));
                    supply.setQuantity(rs.getObject("quantity", Integer.class));
                    supply.setUnitPrice(rs.getBigDecimal("unit_price"));
                    supply.setStockQuantity(rs.getInt("stock_quantity"));
                    supply.setCreatedAt(rs.getTimestamp("created_at"));
                    supply.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return supply;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting supply by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Check if supply exists by group and name
    public MedicalSupply findExistingSupply(String supplyGroup, String supplyName) {
        String sql = "SELECT supply_id, supply_group, supply_name, quantity, unit_price, stock_quantity, created_at, updated_at " +
                    "FROM medical_supply WHERE supply_group = ? AND supply_name = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supplyGroup);
            stmt.setString(2, supplyName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicalSupply supply = new MedicalSupply();
                    supply.setSupplyId(rs.getInt("supply_id"));
                    supply.setSupplyGroup(rs.getString("supply_group"));
                    supply.setSupplyName(rs.getString("supply_name"));
                    supply.setQuantity(rs.getObject("quantity", Integer.class));
                    supply.setUnitPrice(rs.getBigDecimal("unit_price"));
                    supply.setStockQuantity(rs.getInt("stock_quantity"));
                    supply.setCreatedAt(rs.getTimestamp("created_at"));
                    supply.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return supply;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding existing supply: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add new supply
    public boolean addSupply(MedicalSupply supply) {
        String sql = "INSERT INTO medical_supply (supply_group, supply_name, unit_price, stock_quantity) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supply.getSupplyGroup());
            stmt.setString(2, supply.getSupplyName());
            stmt.setBigDecimal(3, supply.getUnitPrice());
            stmt.setInt(4, supply.getStockQuantity());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding supply: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update stock quantity (for existing supplies)
    public boolean updateStockQuantity(int supplyId, int additionalQuantity) {
        String sql = "UPDATE medical_supply SET stock_quantity = stock_quantity + ?, updated_at = CURRENT_TIMESTAMP WHERE supply_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, additionalQuantity);
            stmt.setInt(2, supplyId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating stock quantity: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update supply information
    public boolean updateSupply(MedicalSupply supply) {
        String sql = "UPDATE medical_supply SET supply_group = ?, supply_name = ?, unit_price = ?, stock_quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE supply_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supply.getSupplyGroup());
            stmt.setString(2, supply.getSupplyName());
            stmt.setBigDecimal(3, supply.getUnitPrice());
            stmt.setInt(4, supply.getStockQuantity());
            stmt.setInt(5, supply.getSupplyId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating supply: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Soft delete supply
    public boolean deleteSupply(int supplyId) {
        String sql = "UPDATE medical_supply SET isdeleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE supply_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplyId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting supply: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Get all distinct supply groups
    public List<String> getAllSupplyGroups() {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT DISTINCT supply_group FROM medical_supply WHERE isdeleted = FALSE ORDER BY supply_group";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                groups.add(rs.getString("supply_group"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting supply groups: " + e.getMessage());
            e.printStackTrace();
        }
        
        return groups;
    }
    
    // =====================================================
    // PHẦN QUẢN LÝ KHO THUỐC (MEDICINES)
    // Bảng database: examination_medicines
    // Model: Medicine
    // =====================================================
    
    // Get all medicines
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT exam_medicine_id, medicine_name, quantity, unit_of_measure, unit_price, stock_quantity, created_at, updated_at, isdeleted FROM examination_medicines WHERE isdeleted = FALSE ORDER BY medicine_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Medicine medicine = new Medicine();
                medicine.setExamMedicineId(rs.getInt("exam_medicine_id"));
                medicine.setMedicineName(rs.getString("medicine_name"));
                medicine.setQuantity(rs.getObject("quantity", Integer.class));
                medicine.setUnitOfMeasure(rs.getString("unit_of_measure"));
                medicine.setUnitPrice(rs.getBigDecimal("unit_price"));
                medicine.setStockQuantity(rs.getInt("stock_quantity"));
                medicine.setCreatedAt(rs.getTimestamp("created_at"));
                medicine.setUpdatedAt(rs.getTimestamp("updated_at"));
                medicine.setIsdeleted(rs.getBoolean("isdeleted"));
                medicines.add(medicine);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all medicines: " + e.getMessage());
            e.printStackTrace();
        }
        
        return medicines;
    }
    
    // Search medicines by name
    public List<Medicine> searchMedicines(String keyword) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT exam_medicine_id, medicine_name, quantity, unit_of_measure, unit_price, stock_quantity, created_at, updated_at, isdeleted " +
                    "FROM examination_medicines WHERE medicine_name LIKE ? AND isdeleted = FALSE ORDER BY medicine_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchKeyword = "%" + keyword + "%";
            stmt.setString(1, searchKeyword);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medicine medicine = new Medicine();
                    medicine.setExamMedicineId(rs.getInt("exam_medicine_id"));
                    medicine.setMedicineName(rs.getString("medicine_name"));
                    medicine.setQuantity(rs.getObject("quantity", Integer.class));
                    medicine.setUnitOfMeasure(rs.getString("unit_of_measure"));
                    medicine.setUnitPrice(rs.getBigDecimal("unit_price"));
                    medicine.setStockQuantity(rs.getInt("stock_quantity"));
                    medicine.setCreatedAt(rs.getTimestamp("created_at"));
                    medicine.setUpdatedAt(rs.getTimestamp("updated_at"));
                    medicine.setIsdeleted(rs.getBoolean("isdeleted"));
                    medicines.add(medicine);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching medicines: " + e.getMessage());
            e.printStackTrace();
        }
        
        return medicines;
    }
    
    // Get medicine by ID
    public Medicine getMedicineById(int medicineId) {
        String sql = "SELECT exam_medicine_id, medicine_name, quantity, unit_of_measure, unit_price, stock_quantity, created_at, updated_at, isdeleted " +
                    "FROM examination_medicines WHERE exam_medicine_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, medicineId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Medicine medicine = new Medicine();
                    medicine.setExamMedicineId(rs.getInt("exam_medicine_id"));
                    medicine.setMedicineName(rs.getString("medicine_name"));
                    medicine.setQuantity(rs.getObject("quantity", Integer.class));
                    medicine.setUnitOfMeasure(rs.getString("unit_of_measure"));
                    medicine.setUnitPrice(rs.getBigDecimal("unit_price"));
                    medicine.setStockQuantity(rs.getInt("stock_quantity"));
                    medicine.setCreatedAt(rs.getTimestamp("created_at"));
                    medicine.setUpdatedAt(rs.getTimestamp("updated_at"));
                    medicine.setIsdeleted(rs.getBoolean("isdeleted"));
                    return medicine;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting medicine by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Check if medicine exists by name and unit
    public Medicine findExistingMedicine(String medicineName, String unitOfMeasure) {
        String sql = "SELECT exam_medicine_id, medicine_name, quantity, unit_of_measure, unit_price, stock_quantity, created_at, updated_at, isdeleted " +
                    "FROM examination_medicines WHERE medicine_name = ? AND unit_of_measure = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medicineName);
            stmt.setString(2, unitOfMeasure);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Medicine medicine = new Medicine();
                    medicine.setExamMedicineId(rs.getInt("exam_medicine_id"));
                    medicine.setMedicineName(rs.getString("medicine_name"));
                    medicine.setQuantity(rs.getObject("quantity", Integer.class));
                    medicine.setUnitOfMeasure(rs.getString("unit_of_measure"));
                    medicine.setUnitPrice(rs.getBigDecimal("unit_price"));
                    medicine.setStockQuantity(rs.getInt("stock_quantity"));
                    medicine.setCreatedAt(rs.getTimestamp("created_at"));
                    medicine.setUpdatedAt(rs.getTimestamp("updated_at"));
                    medicine.setIsdeleted(rs.getBoolean("isdeleted"));
                    return medicine;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding existing medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add new medicine
    public boolean addMedicine(Medicine medicine) {
        String sql = "INSERT INTO examination_medicines (medicine_name, unit_of_measure, unit_price, stock_quantity) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medicine.getMedicineName());
            stmt.setString(2, medicine.getUnitOfMeasure());
            stmt.setBigDecimal(3, medicine.getUnitPrice());
            stmt.setInt(4, medicine.getStockQuantity());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update medicine stock quantity
    public boolean updateMedicineStockQuantity(int medicineId, int additionalQuantity) {
        String sql = "UPDATE examination_medicines SET stock_quantity = stock_quantity + ?, updated_at = CURRENT_TIMESTAMP WHERE exam_medicine_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, additionalQuantity);
            stmt.setInt(2, medicineId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating medicine stock quantity: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Update medicine information
    public boolean updateMedicine(Medicine medicine) {
        String sql = "UPDATE examination_medicines SET medicine_name = ?, unit_of_measure = ?, unit_price = ?, stock_quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE exam_medicine_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medicine.getMedicineName());
            stmt.setString(2, medicine.getUnitOfMeasure());
            stmt.setBigDecimal(3, medicine.getUnitPrice());
            stmt.setInt(4, medicine.getStockQuantity());
            stmt.setInt(5, medicine.getExamMedicineId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Soft delete medicine
    public boolean deleteMedicine(int medicineId) {
        String sql = "UPDATE examination_medicines SET isdeleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE exam_medicine_id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, medicineId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Get all distinct medicine units
    public List<String> getAllMedicineUnits() {
        List<String> units = new ArrayList<>();
        String sql = "SELECT DISTINCT unit_of_measure FROM examination_medicines WHERE isdeleted = FALSE ORDER BY unit_of_measure";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                units.add(rs.getString("unit_of_measure"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting medicine units: " + e.getMessage());
            e.printStackTrace();
        }
        
        return units;
    }

} 