package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.MedicalSupply;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOWarehouse {
    
    // Get all medical supplies
    public List<MedicalSupply> getAllSupplies() {
        List<MedicalSupply> supplies = new ArrayList<>();
        String sql = "SELECT supply_id, supply_group, supply_name, quantity, unit_price, stock_quantity, created_at, updated_at FROM medical_supply ORDER BY supply_group, supply_name";
        
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
        String sql = "SELECT supply_id, supply_group, supply_name, quantity, unit_price, stock_quantity, created_at, updated_at " +
                    "FROM medical_supply WHERE supply_group LIKE ? OR supply_name LIKE ? ORDER BY supply_group, supply_name";
        
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
        String sql = "SELECT supply_id, supply_group, supply_name, quantity, unit_price, stock_quantity, created_at, updated_at " +
                    "FROM medical_supply WHERE supply_id = ?";
        
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
    
    // Delete supply
    public boolean deleteSupply(int supplyId) {
        String sql = "DELETE FROM medical_supply WHERE supply_id = ?";
        
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
        String sql = "SELECT DISTINCT supply_group FROM medical_supply ORDER BY supply_group";
        
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
} 