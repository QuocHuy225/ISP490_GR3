/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FPT SHOP
 */
public class DAODoctorSchedule {

    public List<String> getWorkingDatesByDoctorId(int doctorId) {
        List<String> dates = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT DISTINCT DATE_FORMAT(work_date, '%Y-%m-%d') AS work_date " +
                 "FROM doctor_schedule WHERE doctor_id = ? AND work_date >= CURDATE() AND is_active = TRUE " +
                 "ORDER BY work_date")) {
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dates.add(rs.getString("work_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public static void main(String[] args) {
        DAODoctorSchedule dao = new DAODoctorSchedule();

        int doctorIdToTest = 1; // Thay bằng ID bác sĩ bạn muốn test
        try {
            List<String> workingDates = dao.getWorkingDatesByDoctorId(doctorIdToTest);
            if (workingDates.isEmpty()) {
                System.out.println("Không có ngày làm việc nào được tìm thấy cho bác sĩ ID: " + doctorIdToTest);
            } else {
                System.out.println("Các ngày làm việc của bác sĩ ID " + doctorIdToTest + ":");
                for (String date : workingDates) {
                    System.out.println("- " + date);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi truy vấn ngày làm việc: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
