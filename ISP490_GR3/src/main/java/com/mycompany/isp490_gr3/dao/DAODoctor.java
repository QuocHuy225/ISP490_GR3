/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAODoctor {

    public DAODoctor() {
        // Constructor, DBConnection handles driver loading
    }

    // Maps a ResultSet row to a Doctor object
    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setAccountId(rs.getString("account_id"));
        doctor.setFullName(rs.getString("full_name"));
        doctor.setGender(rs.getInt("gender"));
        doctor.setPhoneNumber(rs.getString("phone")); // From doctors table
        doctor.setDepartmentId(rs.getInt("department_id"));
        doctor.setIsDeleted(rs.getBoolean("is_deleted"));
        doctor.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        doctor.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);

        // Fields from JOINs
        doctor.setSpecializationName(rs.getString("specialization_name")); // From department.name
        
        // 'degree', 'clinic_address', 'image_url' fields have been removed as requested.
        // These fields are no longer mapped from the ResultSet.

        return doctor;
    }

    // Fetches a list of doctors based on search query, limit, and offset for pagination
    public List<Doctor> getAllDoctors(String searchQuery, int limit, int offset) {
        List<Doctor> doctors = new ArrayList<>();
        // Updated SQL to JOIN with 'department' and 'user' tables.
        // Removed 'degree', 'clinic_address', 'image_url' from SELECT statement as requested.
        StringBuilder sql = new StringBuilder("SELECT " +
                     "d.id, d.account_id, d.full_name, d.gender, d.phone, d.department_id, d.is_deleted, d.created_at, d.updated_at, " +
                     "dep.name AS specialization_name " + // From department table
                     "FROM doctors d " +
                     "LEFT JOIN department dep ON d.department_id = dep.id " +
                     "LEFT JOIN user u ON d.account_id = u.id " + // Join with user table (still needed for potential future user-related filters)
                     "WHERE d.is_deleted = FALSE"); // Exclude soft-deleted doctors
        
        List<Object> params = new ArrayList<>();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append(" AND (LOWER(d.full_name) LIKE LOWER(?) OR LOWER(dep.name) LIKE LOWER(?))");
            params.add("%" + searchQuery.trim() + "%");
            params.add("%" + searchQuery.trim() + "%");
        }

        sql.append(" ORDER BY d.full_name");
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    // Gets the total count of doctors based on search query for pagination
    public int getTotalDoctors(String searchQuery) {
        int total = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM doctors d " +
                                              "LEFT JOIN department dep ON d.department_id = dep.id " + // Need join for filtering by specialization
                                              "WHERE d.is_deleted = FALSE"); // Exclude soft-deleted doctors
        List<Object> params = new ArrayList<>();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append(" AND (LOWER(d.full_name) LIKE LOWER(?) OR LOWER(dep.name) LIKE LOWER(?))");
            params.add("%" + searchQuery.trim() + "%");
            params.add("%" + searchQuery.trim() + "%");
        }

        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}