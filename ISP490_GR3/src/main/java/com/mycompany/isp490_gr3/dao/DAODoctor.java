package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Doctor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAODoctor {

    private static final Logger LOGGER = Logger.getLogger(DAODoctor.class.getName());

    public List<Doctor> findAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT id, full_name FROM doctors WHERE is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setFullName(rs.getString("full_name"));
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DAO: Error finding all doctors: " + e.getMessage(), e);
        }
        return doctors;
    }

    public Doctor findDoctorById(int doctorId) {
        String sql = "SELECT id, full_name FROM doctors WHERE id = ? AND is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setFullName(rs.getString("full_name"));
                    return doctor;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DAO: Error finding doctor by ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Doctor> getDoctorsWithoutScheduleInPeriod(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.id, d.full_name "
                + "FROM doctors d "
                + "WHERE d.is_deleted = FALSE AND NOT EXISTS ("
                + "    SELECT 1 FROM doctor_schedule ds "
                + "    WHERE ds.doctor_id = d.id "
                + "    AND ds.is_active = TRUE "
                + "    AND ds.work_date BETWEEN ? AND ?"
                + ")";

        LOGGER.log(Level.INFO, "DAO_DEBUG: getDoctorsWithoutScheduleInPeriod called with startDate={0} and endDate={1}", new Object[]{startDate, endDate});
        LOGGER.log(Level.INFO, "DAO_DEBUG: SQL query being executed: {0}", sql);

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            LOGGER.log(Level.INFO, "DAO_DEBUG: Setting PreparedStatement param 1 (startDate): {0}", Date.valueOf(startDate));
            LOGGER.log(Level.INFO, "DAO_DEBUG: Setting PreparedStatement param 2 (endDate): {0}", Date.valueOf(endDate));

            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                int foundCount = 0;
                while (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    String fullNameFromDb = rs.getString("full_name");
                    doctor.setFullName(fullNameFromDb != null ? fullNameFromDb : "Tên bác sĩ không xác định");
                    list.add(doctor);
                    foundCount++;
                    LOGGER.log(Level.INFO, "DAO_DEBUG: Found doctor (ID: {0}, Name: {1}) matching criteria.", new Object[]{doctor.getId(), doctor.getFullName()});
                }
                LOGGER.log(Level.INFO, "DAO_DEBUG: Total doctors found by query result: {0}", foundCount);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DAO_ERROR: SQLException in getDoctorsWithoutScheduleInPeriod: " + e.getMessage(), e);
            throw e;
        }
        return list;
    }
       public Doctor getDoctorByUserId(String userId) { // Changed parameter type to String
        Doctor doctor = null;
        // SQL query now filters by 'account_id' which is a String
        String sql = "SELECT id, account_id, full_name, gender, phone, created_at, updated_at " +
                     "FROM doctors WHERE account_id = ? AND is_deleted = FALSE";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId); // Use setString for String parameter
            LOGGER.info("Executing SQL: " + sql + " with accountId (userId): " + userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setAccountId(rs.getString("account_id"));
                    doctor.setFullName(rs.getString("full_name"));
                    doctor.setGender(rs.getInt("gender"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setCreatedAt(rs.getTimestamp("created_at"));
                    doctor.setUpdatedAt(rs.getTimestamp("updated_at"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy thông tin bác sĩ theo User Account ID " + userId + ": " + e.getMessage(), e);
        }
        return doctor;
    }

    /**
     * Thêm mới bác sĩ vào bảng doctors
     * @param doctor Doctor object (cần accountId, fullName, phone, gender)
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (account_id, full_name, gender, phone, is_deleted, created_at, updated_at) VALUES (?, ?, ?, ?, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctor.getAccountId());
            ps.setString(2, doctor.getFullName());
            ps.setInt(3, doctor.getGender());
            ps.setString(4, doctor.getPhone());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm mới bác sĩ: " + e.getMessage(), e);
            return false;
        }
    }
}
