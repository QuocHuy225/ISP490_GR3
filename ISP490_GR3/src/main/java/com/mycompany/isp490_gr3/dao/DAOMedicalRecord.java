package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.MedicalRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigDecimal;

/**
 * DAO for handling MedicalRecord-related database operations.
 */
public class DAOMedicalRecord {
    
    private static final Logger LOGGER = Logger.getLogger(DAOMedicalRecord.class.getName());
    
    public List<MedicalRecord> getMedicalRecordsByPatientId(int patientId) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM medical_record WHERE patient_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(extractMedicalRecord(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting medical records for patient " + patientId + ": " + e.getMessage());
        }
        
        return records;
    }
    
    public MedicalRecord getMedicalRecordById(String id) {
        String sql = "SELECT * FROM medical_record WHERE id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractMedicalRecord(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting medical record by id " + id + ": " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean addMedicalRecord(MedicalRecord record) {
        String sql = "INSERT INTO medical_record (id, patient_id, doctor_id, " +
                    "respiration_rate, temperature, height, pulse, bmi, weight, blood_pressure, spo2, " +
                    "medical_history, current_disease, physical_exam, clinical_info, final_diagnosis, treatment_plan, note, " +
                    "created_at, created_by, updated_at, updated_by, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            try {
                // Generate ID if not provided
                if (record.getId() == null || record.getId().isEmpty()) {
                    record.setId(generateMedicalRecordId());
                }
                
                ps.setString(1, record.getId());
                ps.setInt(2, record.getPatientId());
                ps.setObject(3, record.getDoctorId());
                
                // Vital signs
                ps.setObject(4, record.getRespirationRate());
                ps.setObject(5, record.getTemperature());
                ps.setObject(6, record.getHeight());
                ps.setObject(7, record.getPulse());
                ps.setObject(8, record.getBmi());
                ps.setObject(9, record.getWeight());
                ps.setString(10, record.getBloodPressure());
                ps.setObject(11, record.getSpo2());
                
                // Medical information
                ps.setString(12, record.getMedicalHistory());
                ps.setString(13, record.getCurrentDisease());
                ps.setString(14, record.getPhysicalExam());
                ps.setString(15, record.getClinicalInfo());
                ps.setString(16, record.getFinalDiagnosis());
                ps.setString(17, record.getTreatmentPlan());
                ps.setString(18, record.getNote());
                
                // Management
                ps.setTimestamp(19, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setString(20, record.getCreatedBy());
                ps.setTimestamp(21, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setString(22, record.getUpdatedBy());
                ps.setString(23, record.getStatus());
                
                int result = ps.executeUpdate();
                conn.commit();
                return result > 0;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error adding medical record: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding medical record: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateMedicalRecord(MedicalRecord record) {
        String sql = "UPDATE medical_record SET doctor_id = ?, " +
                    "respiration_rate = ?, temperature = ?, height = ?, pulse = ?, bmi = ?, weight = ?, blood_pressure = ?, spo2 = ?, " +
                    "medical_history = ?, current_disease = ?, physical_exam = ?, clinical_info = ?, final_diagnosis = ?, treatment_plan = ?, note = ?, " +
                    "updated_at = ?, updated_by = ?, status = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            try {
                ps.setObject(1, record.getDoctorId());
                
                // Vital signs
                ps.setObject(2, record.getRespirationRate());
                ps.setObject(3, record.getTemperature());
                ps.setObject(4, record.getHeight());
                ps.setObject(5, record.getPulse());
                ps.setObject(6, record.getBmi());
                ps.setObject(7, record.getWeight());
                ps.setString(8, record.getBloodPressure());
                ps.setObject(9, record.getSpo2());
                
                // Medical information
                ps.setString(10, record.getMedicalHistory());
                ps.setString(11, record.getCurrentDisease());
                ps.setString(12, record.getPhysicalExam());
                ps.setString(13, record.getClinicalInfo());
                ps.setString(14, record.getFinalDiagnosis());
                ps.setString(15, record.getTreatmentPlan());
                ps.setString(16, record.getNote());
                
                // Management
                ps.setTimestamp(17, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setString(18, record.getUpdatedBy());
                ps.setString(19, record.getStatus());
                ps.setString(20, record.getId());
                
                int result = ps.executeUpdate();
                conn.commit();
                return result > 0;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error updating medical record: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating medical record: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateMedicalRecordStatus(String id, String status, String updatedBy) {
        String sql = "UPDATE medical_record SET status = ?, updated_at = ?, updated_by = ? WHERE id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setString(3, updatedBy);
            ps.setString(4, id);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating medical record status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateMedicalRecordNote(String id, String note, String updatedBy) {
        String sql = "UPDATE medical_record SET note = ?, updated_at = ?, updated_by = ? WHERE id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, note);
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setString(3, updatedBy);
            ps.setString(4, id);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating medical record note: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateMedicalRecordStatusAndNote(String id, String status, String note, String updatedBy) {
        String sql = "UPDATE medical_record SET status = ?, note = ?, updated_at = ?, updated_by = ? WHERE id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setString(2, note);
            ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setString(4, updatedBy);
            ps.setString(5, id);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating medical record status and note: " + e.getMessage());
            return false;
        }
    }
    
    private String generateMedicalRecordId() {
        String sql = "SELECT COUNT(*) + 1 as next_num FROM medical_record";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int nextNum = rs.getInt("next_num");
                return String.format("MR%06d", nextNum);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating medical record ID: " + e.getMessage());
        }
        return "MR000001";
    }
    
    private MedicalRecord extractMedicalRecord(ResultSet rs) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        
        record.setId(rs.getString("id"));
        record.setPatientId(rs.getInt("patient_id"));
        record.setDoctorId((Integer) rs.getObject("doctor_id"));
        
        // Vital signs
        record.setRespirationRate((Integer) rs.getObject("respiration_rate"));
        record.setTemperature(rs.getBigDecimal("temperature"));
        record.setHeight(rs.getBigDecimal("height"));
        record.setPulse((Integer) rs.getObject("pulse"));
        record.setBmi(rs.getBigDecimal("bmi"));
        record.setWeight(rs.getBigDecimal("weight"));
        record.setBloodPressure(rs.getString("blood_pressure"));
        record.setSpo2(rs.getBigDecimal("spo2"));
        
        // Medical information
        record.setMedicalHistory(rs.getString("medical_history"));
        record.setCurrentDisease(rs.getString("current_disease"));
        record.setPhysicalExam(rs.getString("physical_exam"));
        record.setClinicalInfo(rs.getString("clinical_info"));
        record.setFinalDiagnosis(rs.getString("final_diagnosis"));
        record.setTreatmentPlan(rs.getString("treatment_plan"));
        record.setNote(rs.getString("note"));
        
        // Management
        record.setCreatedAt(rs.getTimestamp("created_at"));
        record.setCreatedBy(rs.getString("created_by"));
        record.setUpdatedAt(rs.getTimestamp("updated_at"));
        record.setUpdatedBy(rs.getString("updated_by"));
        record.setStatus(rs.getString("status"));
        
        return record;
    }
} 