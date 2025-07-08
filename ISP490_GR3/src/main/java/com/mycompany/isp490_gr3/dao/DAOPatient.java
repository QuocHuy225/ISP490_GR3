package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for handling Patient-related database operations.
 */
public class DAOPatient {

    private static final Logger LOGGER = Logger.getLogger(DAOPatient.class.getName());

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE is_deleted = 0 ORDER BY created_at DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patients.add(extractPatient(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all patients: {0}", e.getMessage());
        }

        return patients;
    }

    public List<Patient> searchPatients(String code, String name, String phone, String cccd) {
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
        if (cccd != null && !cccd.trim().isEmpty()) {
            sql.append(" AND cccd LIKE ?");
            params.add("%" + cccd.trim() + "%");
        }

        sql.append(" ORDER BY created_at DESC");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
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

    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPatient(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting patient by id: {0}", e.getMessage());
        }
        return null;
    }

    public boolean addPatient(Patient patient) {
        if (patient == null || patient.getFullName() == null || patient.getDob() == null) {
            throw new IllegalArgumentException("Patient data is incomplete.");
        }

        String sql = "INSERT INTO patients (patient_code, full_name, gender, dob, phone, cccd, address, created_at, updated_at, is_deleted) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            try {
                // Generate patient code
                String patientCode = generatePatientCode();

                ps.setString(1, patientCode);
                ps.setString(2, patient.getFullName());
                ps.setInt(3, patient.getGender());
                ps.setDate(4, patient.getDob());
                ps.setString(5, patient.getPhone());
                ps.setString(6, patient.getCccd());
                ps.setString(7, patient.getAddress());
                ps.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setBoolean(10, false);

                int result = ps.executeUpdate();
                if (result > 0) {
                    // Get generated ID
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            patient.setId(generatedKeys.getInt(1));
                            patient.setPatientCode(patientCode);
                        }
                    }
                }
                conn.commit();
                return result > 0;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error adding patient: {0}", e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding patient: {0}", e.getMessage());
            return false;
        }
    }

    public boolean updatePatient(Patient patient) {
        if (patient == null || patient.getId() <= 0 || patient.getFullName() == null || patient.getDob() == null) {
            throw new IllegalArgumentException("Patient data is incomplete or invalid ID.");
        }

        String sql = "UPDATE patients SET full_name = ?, gender = ?, dob = ?, phone = ?, cccd = ?, address = ?, updated_at = ? "
                + "WHERE id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            try {
                ps.setString(1, patient.getFullName());
                ps.setInt(2, patient.getGender());
                ps.setDate(3, patient.getDob());
                ps.setString(4, patient.getPhone());
                ps.setString(5, patient.getCccd());
                ps.setString(6, patient.getAddress());
                ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setInt(8, patient.getId());

                int rows = ps.executeUpdate();
                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error updating patient: {0}", e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating patient: {0}", e.getMessage());
            return false;
        }
    }

    public boolean deletePatient(int id) {
        String sql = "UPDATE patients SET is_deleted = 1, updated_at = ? WHERE id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting patient: {0}", e.getMessage());
            return false;
        }
    }

    private String generatePatientCode() {
        String sql = "SELECT MAX(CAST(SUBSTRING(patient_code, 3) AS UNSIGNED)) as max_num FROM patients WHERE patient_code LIKE 'BN%'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                return String.format("BN%06d", maxNum + 1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating patient code: {0}", e.getMessage());
        }
        return "BN000001";
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
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        p.setDeleted(rs.getBoolean("is_deleted"));
        return p;
    }

    public static void main(String[] args) {
        DAOPatient daoPatient = new DAOPatient();

        // Test case 1: Tìm kiếm với code = "bn"
        System.out.println("Test 1: Tìm kiếm với code = 'bn'");
        testSearch(daoPatient, "bn", null, null, null);

        // Test case 2: Tìm kiếm với code = "BN001" (giả định có bệnh nhân với mã này)
        System.out.println("\nTest 2: Tìm kiếm với code = 'BN001'");
        testSearch(daoPatient, "BN001", null, null, null);

        // Test case 3: Tìm kiếm với tên (thay "Nguyen" bằng tên có trong dữ liệu)
        System.out.println("\nTest 3: Tìm kiếm với name = 'Nguyen'");
        testSearch(daoPatient, null, "Nguyen", null, null);

        // Test case 4: Tìm kiếm với số điện thoại (thay "0123" bằng số có trong dữ liệu)
        System.out.println("\nTest 4: Tìm kiếm với phone = '09'");
        testSearch(daoPatient, null, null, "09", null);

        // Test case 5: Tìm kiếm với CCCD (thay "123456" bằng CCCD có trong dữ liệu)
        System.out.println("\nTest 5: Tìm kiếm với cccd = '123456'");
        testSearch(daoPatient, null, null, null, "123456");

        // Test case 6: Tìm kiếm không tham số
        System.out.println("\nTest 6: Tìm kiếm không tham số");
        testSearch(daoPatient, null, null, null, null);
    }

    private static void testSearch(DAOPatient dao, String code, String name, String phone, String cccd) {
        List<Patient> patients = dao.searchPatients(code, name, phone, cccd);
        System.out.println("Số bệnh nhân tìm thấy: " + patients.size());
        if (patients.isEmpty()) {
            System.out.println("Không tìm thấy bệnh nhân nào.");
        } else {
            for (Patient patient : patients) {
                System.out.println("ID: " + patient.getId());
                System.out.println("Patient Code: " + patient.getPatientCode());
                System.out.println("Full Name: " + patient.getFullName());
                System.out.println("CCCD: " + patient.getCccd());
                System.out.println("Phone: " + patient.getPhone());
                System.out.println("-------------------");
            }
        }
    }

}
