package com.mycompany.isp490_gr3.dao;
import com.mycompany.isp490_gr3.model.DoctorSchedule;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date; // For java.sql.Date

public class DAODoctorSchedule {

    /**
     * Finds doctor schedules for a specific month and year.
     * Joins with the doctors table to get doctor's full name.
     * @param year The year to search for.
     * @param month The month to search for (1-12).
     * @return A list of DoctorSchedule objects.
     */
    public List<DoctorSchedule> findSchedulesByMonth(int year, int month) {
        List<DoctorSchedule> schedules = new ArrayList<>();
        String sql = "SELECT ds.id, ds.doctor_id, ds.work_date, ds.is_active, d.full_name " +
                     "FROM doctor_schedule ds " +
                     "JOIN doctors d ON ds.doctor_id = d.id " +
                     "WHERE YEAR(ds.work_date) = ? AND MONTH(ds.work_date) = ? AND ds.is_active = TRUE AND d.is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DoctorSchedule schedule = new DoctorSchedule();
                    // Assuming 'id' in doctor_schedule table is INT AUTO_INCREMENT,
                    // but frontend uses String IDs like 'sch1'.
                    // For consistency, we'll convert the int ID to a String here.
                    // In a real app, you might use UUIDs for String IDs or adjust frontend.
                    schedule.setId(String.valueOf(rs.getInt("id")));
                    schedule.setDoctorId(rs.getInt("doctor_id"));
                    schedule.setWorkDate(rs.getDate("work_date"));
                    schedule.setActive(rs.getBoolean("is_active"));
                    schedule.setDoctorName(rs.getString("full_name"));
                    // For 'name' (eventName) in frontend, you can construct it
                    schedule.setName("Lịch BS " + rs.getString("full_name") + " (" + rs.getDate("work_date").toString() + ")");
                    schedules.add(schedule);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding schedules by month: " + e.getMessage());
            e.printStackTrace();
        }
        return schedules;
    }

    /**
     * Checks if a doctor schedule already exists for a given doctor and date.
     * @param doctorId The ID of the doctor.
     * @param workDate The date of the schedule.
     * @return true if a schedule exists, false otherwise.
     */
    public boolean isScheduleExists(int doctorId, Date workDate) {
        String sql = "SELECT COUNT(*) FROM doctor_schedule WHERE doctor_id = ? AND work_date = ? AND is_active = TRUE";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, workDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if schedule exists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Saves a new doctor schedule to the database.
     * @param schedule The DoctorSchedule object to save.
     * @return true if the schedule was saved successfully, false otherwise.
     */
    public boolean saveSchedule(DoctorSchedule schedule) {
        String sql = "INSERT INTO doctor_schedule (doctor_id, work_date, is_active) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, schedule.getDoctorId());
            pstmt.setDate(2, schedule.getWorkDate());
            pstmt.setBoolean(3, schedule.isActive());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated ID and set it to the schedule object
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        schedule.setId(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saving schedule: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing doctor schedule in the database.
     * @param schedule The DoctorSchedule object with updated information.
     * @return true if the schedule was updated successfully, false otherwise.
     */
    public boolean updateSchedule(DoctorSchedule schedule) {
        String sql = "UPDATE doctor_schedule SET doctor_id = ?, work_date = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, schedule.getDoctorId());
            pstmt.setDate(2, schedule.getWorkDate());
            pstmt.setBoolean(3, schedule.isActive());
            pstmt.setInt(4, Integer.parseInt(schedule.getId())); // Convert String ID back to int
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating schedule: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a doctor schedule (soft delete by setting is_active to FALSE).
     * @param scheduleId The ID of the schedule to delete.
     * @return true if the schedule was deleted successfully, false otherwise.
     */
    public boolean deleteSchedule(String scheduleId) {
        // Soft delete: set is_active to FALSE
        String sql = "UPDATE doctor_schedule SET is_active = FALSE WHERE id = ?";
        // Hard delete: String sql = "DELETE FROM doctor_schedule WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(scheduleId)); // Convert String ID back to int
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting schedule: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Finds a doctor schedule by its ID.
     * @param scheduleId The ID of the schedule.
     * @return A DoctorSchedule object if found, null otherwise.
     */
    public DoctorSchedule findScheduleById(String scheduleId) {
        String sql = "SELECT ds.id, ds.doctor_id, ds.work_date, ds.is_active, d.full_name " +
                     "FROM doctor_schedule ds " +
                     "JOIN doctors d ON ds.doctor_id = d.id " +
                     "WHERE ds.id = ? AND d.is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(scheduleId));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DoctorSchedule schedule = new DoctorSchedule();
                    schedule.setId(String.valueOf(rs.getInt("id")));
                    schedule.setDoctorId(rs.getInt("doctor_id"));
                    schedule.setWorkDate(rs.getDate("work_date"));
                    schedule.setActive(rs.getBoolean("is_active"));
                    schedule.setDoctorName(rs.getString("full_name"));
                    schedule.setName("Lịch BS " + rs.getString("full_name") + " (" + rs.getDate("work_date").toString() + ")");
                    return schedule;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding schedule by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
