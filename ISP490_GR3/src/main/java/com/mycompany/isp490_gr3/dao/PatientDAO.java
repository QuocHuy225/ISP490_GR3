package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for handling Patient-related database operations.
 */
public class PatientDAO {

    private static final Logger LOGGER = Logger.getLogger(PatientDAO.class.getName());

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE is_deleted = 0 ORDER BY created_at DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patients.add(extractPatient(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all patients: {0}", e.getMessage());
        }

        return patients;
    }

    public List<Patient> searchPatients(String code, String name, String phone) {
        List<Patient> patients = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM patients WHERE is_deleted = 0");
        List<Object> params = new ArrayList<>();

        if (code != null && !code.trim().isEmpty()) {
            sql.append(" AND patient_code LIKE ?");
            params.add("%" + code.trim().toUpperCase() + "%");
        }
        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            params.add("%" + name.trim() + "%");
        }
        if (phone != null && !phone.trim().isEmpty()) {
            sql.append(" AND phone LIKE ?");
            params.add("%" + phone.trim() + "%");
        }

        LOGGER.log(Level.FINE, "SQL Query: {0}, Parameters: {1}", new Object[]{sql.toString(), params});

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    patients.add(extractPatient(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching patients: {0}", e.getMessage());
        }

        return patients;
    }

    public void addPatient(Patient patient) throws SQLException {
        if (patient == null || patient.getFullName() == null || patient.getDob() == null) {
            throw new IllegalArgumentException("Patient data is incomplete.");
        }

        String sql = "INSERT INTO patients (full_name, gender, dob, phone, cccd, address, created_by, updated_by, created_at, updated_at, is_deleted) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            try {
                ps.setString(1, patient.getFullName());
                ps.setInt(2, patient.getGender());
                ps.setDate(3, patient.getDob());
                ps.setString(4, patient.getPhone());
                ps.setString(5, patient.getCccd());
                ps.setString(6, patient.getAddress());
                ps.setString(7, patient.getCreatedBy());
                ps.setString(8, patient.getUpdatedBy());
                ps.setTimestamp(9, patient.getCreatedAt());
                ps.setTimestamp(10, patient.getUpdatedAt());
                ps.setBoolean(11, patient.isDeleted());

                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error adding patient: {0}", e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updatePatient(Patient patient) throws SQLException {
        if (patient == null || patient.getId() <= 0 || patient.getFullName() == null || patient.getDob() == null) {
            throw new IllegalArgumentException("Patient data is incomplete or invalid ID.");
        }

        String sql = "UPDATE patients SET full_name = ?, gender = ?, dob = ?, phone = ?, cccd = ?, address = ?, updated_by = ?, updated_at = ? " +
                     "WHERE id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            try {
                ps.setString(1, patient.getFullName());
                ps.setInt(2, patient.getGender());
                ps.setDate(3, patient.getDob());
                ps.setString(4, patient.getPhone());
                ps.setString(5, patient.getCccd());
                ps.setString(6, patient.getAddress());
                ps.setString(7, patient.getUpdatedBy());
                ps.setTimestamp(8, patient.getUpdatedAt());
                ps.setInt(9, patient.getId());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("Patient not found with ID: " + patient.getId());
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error updating patient: {0}", e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private Patient extractPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("id"));
        p.setPatientCode(rs.getString("patient_code"));
        p.setAccountId(rs.getString("account_id"));
        p.setFullName(rs.getString("full_name"));
        p.setGender(rs.getInt("gender"));
        p.setDob(rs.getDate("dob"));
        p.setPhone(rs.getString("phone"));
        p.setCccd(rs.getString("cccd"));
        p.setAddress(rs.getString("address"));
        p.setCreatedBy(rs.getString("created_by"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedBy(rs.getString("updated_by"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        p.setDeleted(rs.getBoolean("is_deleted"));
        return p;
    }
}