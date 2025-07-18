package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAODoctor {

    /**
     * Retrieves a list of all active doctors from the database.
     *
     * @return A list of Doctor objects.
     */
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
            System.err.println("Error finding all doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    /**
     * Retrieves a doctor by their ID.
     *
     * @param doctorId The ID of the doctor.
     * @return A Doctor object if found, null otherwise.
     */
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
            System.err.println("Error finding doctor by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Doctor> getDoctorsWithoutSchedule() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.id, d.full_name "
                + "FROM doctors d "
                + "WHERE d.is_deleted = FALSE AND NOT EXISTS ("
                + "    SELECT 1 FROM doctor_schedule ds WHERE ds.doctor_id = d.id"
                + ")";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setFullName(rs.getString("full_name"));
                list.add(doctor);
            }
        }
        return list;
    }

}
