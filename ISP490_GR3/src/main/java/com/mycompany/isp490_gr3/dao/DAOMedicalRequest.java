package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.MedicalRequest;
import com.mycompany.isp490_gr3.model.Patient;
import com.mycompany.isp490_gr3.model.MedicalRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for handling Medical Request-related database operations.
 */
public class DAOMedicalRequest {
    
    private static final Logger LOGGER = Logger.getLogger(DAOMedicalRequest.class.getName());
    
    // ===== MEDICAL REQUEST OPERATIONS =====
    
    /**
     * Get medical requests by medical record ID
     */
    public List<MedicalRequest> getRequestsByMedicalRecord(String medicalRecordId) {
        List<MedicalRequest> requests = new ArrayList<>();
        String sql = "SELECT mr.*, p.full_name as patient_name, med.id as medical_record_id " +
                     "FROM medical_requests mr " +
                     "LEFT JOIN patients p ON mr.patient_id = p.id " +
                     "LEFT JOIN medical_record med ON mr.medical_record_id = med.id " +
                     "WHERE mr.medical_record_id = ? ORDER BY mr.created_at DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicalRecordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(extractMedicalRequest(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting medical requests by medical record: {0}", e.getMessage());
        }

        return requests;
    }

    /**
     * Get medical request by ID
     */
    public MedicalRequest getRequestById(int id) {
        String sql = "SELECT mr.*, p.full_name as patient_name, med.id as medical_record_id " +
                     "FROM medical_requests mr " +
                     "LEFT JOIN patients p ON mr.patient_id = p.id " +
                     "LEFT JOIN medical_record med ON mr.medical_record_id = med.id " +
                     "WHERE mr.id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractMedicalRequest(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting medical request by id: {0}", e.getMessage());
        }
        return null;
    }

    /**
     * Add new medical request
     */
    public boolean addRequest(MedicalRequest request) {
        if (request == null || request.getMedicalRecordId() == null) {
            throw new IllegalArgumentException("Medical request data is incomplete.");
        }

        // Check if a medical request already exists for this medical record
        List<MedicalRequest> existingRequests = getRequestsByMedicalRecord(request.getMedicalRecordId());
        if (!existingRequests.isEmpty()) {
            throw new IllegalStateException("A medical request already exists for this medical record.");
        }

        String sql = "INSERT INTO medical_requests (clinic_name, clinic_phone, clinic_address, " +
                     "instruction_content, instruction_requirements, notes, created_by, patient_id, medical_record_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, request.getClinicName());
            ps.setString(2, request.getClinicPhone());
            ps.setString(3, request.getClinicAddress());
            ps.setString(4, request.getInstructionContent());
            ps.setString(5, request.getInstructionRequirements());
            ps.setString(6, request.getNotes());
            ps.setString(7, request.getCreatedBy());
            ps.setInt(8, request.getPatientId());
            ps.setString(9, request.getMedicalRecordId());

            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding medical request: {0}", e.getMessage());
            return false;
        }
        
        return false;
    }

    /**
     * Update medical request
     */
    public boolean updateRequest(MedicalRequest request) {
        if (request == null || request.getId() <= 0) {
            throw new IllegalArgumentException("Medical request data is incomplete.");
        }

        String sql = "UPDATE medical_requests SET " +
                     "clinic_name = ?, clinic_phone = ?, clinic_address = ?, " +
                     "instruction_content = ?, instruction_requirements = ?, notes = ? " +
                     "WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, request.getClinicName());
            ps.setString(2, request.getClinicPhone());
            ps.setString(3, request.getClinicAddress());
            ps.setString(4, request.getInstructionContent());
            ps.setString(5, request.getInstructionRequirements());
            ps.setString(6, request.getNotes());
            ps.setInt(7, request.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating medical request: {0}", e.getMessage());
            return false;
        }
    }

    /**
     * Delete medical request by ID
     */
    public boolean deleteRequest(int id) {
        String sql = "DELETE FROM medical_requests WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting medical request: {0}", e.getMessage());
            return false;
        }
    }

    // ===== UTILITY METHODS =====
    
    /**
     * Extract MedicalRequest from ResultSet
     */
    private MedicalRequest extractMedicalRequest(ResultSet rs) throws SQLException {
        MedicalRequest request = new MedicalRequest();
        request.setId(rs.getInt("id"));
        request.setClinicName(rs.getString("clinic_name"));
        request.setClinicPhone(rs.getString("clinic_phone"));
        request.setClinicAddress(rs.getString("clinic_address"));
        request.setInstructionContent(rs.getString("instruction_content"));
        request.setInstructionRequirements(rs.getString("instruction_requirements"));
        request.setNotes(rs.getString("notes"));
        request.setCreatedBy(rs.getString("created_by"));
        request.setCreatedAt(rs.getTimestamp("created_at"));
        request.setPatientId(rs.getInt("patient_id"));
        request.setMedicalRecordId(rs.getString("medical_record_id"));
        
        return request;
    }
} 