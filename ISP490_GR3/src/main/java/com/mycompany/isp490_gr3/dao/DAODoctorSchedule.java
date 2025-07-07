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
     * Finds doctor schedules for a specific month and year. Joins with the
     * doctors table to get doctor's full name. IMPORTANT: This method now
     * explicitly filters for active schedules and active doctors.
     *
     * @param year The year to search for.
     * @param month The month to search for (1-12).
     * @return A list of DoctorSchedule objects.
     */
    public List<DoctorSchedule> findSchedulesByMonth(int year, int month) {
        List<DoctorSchedule> schedules = new ArrayList<>();
        // Ensure this query only fetches ACTIVE schedules and ACTIVE doctors
        String sql = "SELECT ds.id, ds.doctor_id, ds.work_date, ds.is_active, d.full_name "
                + "FROM doctor_schedule ds "
                + "JOIN doctors d ON ds.doctor_id = d.id "
                + "WHERE YEAR(ds.work_date) = ? AND MONTH(ds.work_date) = ? AND ds.is_active = TRUE AND d.is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DoctorSchedule schedule = new DoctorSchedule();
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
     * Checks if an ACTIVE doctor schedule already exists for a given doctor and
     * date. This method will only return true if an active schedule is found.
     *
     * @param doctorId The ID of the doctor.
     * @param workDate The date of the schedule.
     * @return true if an ACTIVE schedule exists, false otherwise.
     */
    public boolean isScheduleExists(int doctorId, Date workDate) {
        // This method's logic is fine as it already checks for is_active = TRUE
        String sql = "SELECT COUNT(*) FROM doctor_schedule WHERE doctor_id = ? AND work_date = ? AND is_active = TRUE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, workDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if active schedule exists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Saves a new doctor schedule to the database.
     *
     * @param schedule The DoctorSchedule object to save.
     * @return true if the schedule was saved successfully, false otherwise.
     */
    public boolean saveSchedule(DoctorSchedule schedule) {
        String sql = "INSERT INTO doctor_schedule (doctor_id, work_date, is_active) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
     *
     * @param schedule The DoctorSchedule object with updated information.
     * @return true if the schedule was updated successfully, false otherwise.
     */
    public boolean updateSchedule(DoctorSchedule schedule) {
        String sql = "UPDATE doctor_schedule SET doctor_id = ?, work_date = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
     *
     * @param scheduleId The ID of the schedule to delete.
     * @return true if the schedule was deleted successfully, false otherwise.
     */
    public boolean deleteSchedule(String scheduleId) {
        // Soft delete: set is_active to FALSE
        String sql = "UPDATE doctor_schedule SET is_active = FALSE WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
     * Finds a doctor schedule by its ID. Note: This method does NOT filter by
     * 'is_active'.
     *
     * @param scheduleId The ID of the schedule.
     * @return A DoctorSchedule object if found, null otherwise.
     */
    public DoctorSchedule findScheduleById(String scheduleId) {
        // This query finds a schedule by ID regardless of its active status.
        // It's used when we want to load details for editing/deleting, even if it's inactive.
        String sql = "SELECT ds.id, ds.doctor_id, ds.work_date, ds.is_active, d.full_name "
                + "FROM doctor_schedule ds "
                + "JOIN doctors d ON ds.doctor_id = d.id "
                + "WHERE ds.id = ? AND d.is_deleted = FALSE"; // Still filter out deleted doctors
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    public List<String> getWorkingDatesByDoctorId(int doctorId) {
        List<String> dates = new ArrayList<>();
        // This query already correctly filters by is_active = TRUE
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT DISTINCT DATE_FORMAT(work_date, '%Y-%m-%d') AS work_date "
                + "FROM doctor_schedule WHERE doctor_id = ? AND work_date >= CURDATE() AND is_active = TRUE "
                + "ORDER BY work_date")) {
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

    /**
     * Finds a doctor schedule for a given doctor and date, regardless of its
     * 'is_active' status. This method is used by the service layer to check for
     * any existing schedule (active or soft-deleted) before creating a new one.
     *
     * @param doctorId The ID of the doctor.
     * @param workDate The specific work date.
     * @return DoctorSchedule object if found (active or inactive), null
     * otherwise.
     */
    public DoctorSchedule findScheduleByDoctorIdAndWorkDateIncludingInactive(int doctorId, Date workDate) {
        // This query does NOT filter by is_active, so it will find both active and inactive schedules.
        String sql = "SELECT id, doctor_id, work_date, is_active FROM doctor_schedule WHERE doctor_id = ? AND work_date = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, workDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DoctorSchedule schedule = new DoctorSchedule();
                    schedule.setId(String.valueOf(rs.getInt("id")));
                    schedule.setDoctorId(rs.getInt("doctor_id"));
                    schedule.setWorkDate(rs.getDate("work_date"));
                    schedule.setActive(rs.getBoolean("is_active"));
                    // doctorName and name fields are not populated here as this is for internal check.
                    return schedule;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding schedule by doctor and date (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds an ACTIVE doctor schedule for a given doctor and date. This method
     * is specifically for checking conflicts against *active* schedules.
     *
     * @param doctorId The ID of the doctor.
     * @param workDate The specific work date.
     * @return DoctorSchedule object if an active schedule is found, null
     * otherwise.
     */
    public DoctorSchedule findActiveScheduleByDoctorAndDate(int doctorId, Date workDate) {
        // This query explicitly filters by is_active = TRUE
        String sql = "SELECT id, doctor_id, work_date, is_active FROM doctor_schedule WHERE doctor_id = ? AND work_date = ? AND is_active = TRUE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, workDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DoctorSchedule schedule = new DoctorSchedule();
                    schedule.setId(String.valueOf(rs.getInt("id")));
                    schedule.setDoctorId(rs.getInt("doctor_id"));
                    schedule.setWorkDate(rs.getDate("work_date"));
                    schedule.setActive(rs.getBoolean("is_active"));
                    // doctorName and name fields are not populated here as this is for internal check.
                    return schedule;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding active schedule by doctor and date: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
