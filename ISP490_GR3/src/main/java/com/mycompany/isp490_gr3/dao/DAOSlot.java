package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.dto.SlotViewDTO;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOSlot {

    private static final Logger LOGGER = Logger.getLogger(DAOSlot.class.getName());

    //getter

    public List<SlotViewDTO> getTodaySlotViewDTOs(int offset, int limit) {
        String sql = "SELECT s.id, s.slot_date, s.start_time, s.end_time, s.max_patients, d.full_name AS doctor_name, " +
                     "(SELECT COUNT(*) FROM appointment a WHERE a.slot_id = s.id AND a.status != 'cancelled') AS booked_patients " +
                     "FROM slot s JOIN doctors d ON s.doctor_id = d.id " +
                     "WHERE s.is_deleted = FALSE AND s.slot_date = CURRENT_DATE " +
                     "ORDER BY s.start_time DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            return extractSlotViewDTOs(ps.executeQuery());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy slot hôm nay: " + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public int countTodaySlotViewDTOs() {
        String sql = "SELECT COUNT(*) FROM slot WHERE is_deleted = FALSE AND slot_date = CURRENT_DATE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm slot hôm nay: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<SlotViewDTO> searchSlotViewDTOs(Integer doctorId, LocalDate slotDate, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT s.id, s.slot_date, s.start_time, s.end_time, s.max_patients, d.full_name AS doctor_name, ");
        sql.append("(SELECT COUNT(*) FROM appointment a WHERE a.slot_id = s.id AND a.status != 'cancelled') AS booked_patients ");
        sql.append("FROM slot s JOIN doctors d ON s.doctor_id = d.id WHERE s.is_deleted = FALSE");
        if (doctorId != null) sql.append(" AND s.doctor_id = ?");
        if (slotDate != null) sql.append(" AND s.slot_date = ?");
        sql.append(" ORDER BY s.slot_date DESC, s.start_time DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (doctorId != null) ps.setInt(idx++, doctorId);
            if (slotDate != null) ps.setDate(idx++, Date.valueOf(slotDate));
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            return extractSlotViewDTOs(ps.executeQuery());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm slot: " + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public int countFilteredSlotViewDTOs(Integer doctorId, LocalDate slotDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM slot WHERE is_deleted = FALSE");
        if (doctorId != null) sql.append(" AND doctor_id = ?");
        if (slotDate != null) sql.append(" AND slot_date = ?");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (doctorId != null) ps.setInt(idx++, doctorId);
            if (slotDate != null) ps.setDate(idx++, Date.valueOf(slotDate));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm slot tìm kiếm: " + e.getMessage(), e);
        }
        return 0;
    }

    // insert

    public boolean insertSlotWithValidation(int doctorId, LocalDate date, LocalTime start, int duration, int maxPatients) throws SQLException {
        LocalTime end = start.plusMinutes(duration);
        try (Connection conn = DBContext.getConnection()) {
            return !isSlotConflict(conn, doctorId, date, start, end) && insertSlot(conn, doctorId, date, start, end, maxPatients);
        }
    }

    public int insertRangeSlots(int doctorId, LocalDate date, String startStr, String endStr, int duration, int maxPatients) throws SQLException {
        LocalTime startTime = LocalTime.parse(startStr);
        LocalTime endTime = LocalTime.parse(endStr);
        int inserted = 0;

        try (Connection conn = DBContext.getConnection()) {
            LocalTime current = startTime;
            while (!current.plusMinutes(duration).isAfter(endTime)) {
                LocalTime next = current.plusMinutes(duration);
                if (!isSlotConflict(conn, doctorId, date, current, next)) {
                    if (insertSlot(conn, doctorId, date, current, next, maxPatients)) inserted++;
                }
                current = next;
            }
        }
        return inserted;
    }

    //Private

    private boolean insertSlot(Connection conn, int doctorId, LocalDate date, LocalTime start, LocalTime end, int maxPatients) throws SQLException {
        String sql = "INSERT INTO slot (doctor_id, slot_date, start_time, end_time, max_patients, is_deleted, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, FALSE, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(start));
            ps.setTime(4, Time.valueOf(end));
            ps.setInt(5, maxPatients);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean isSlotConflict(Connection conn, int doctorId, LocalDate date, LocalTime start, LocalTime end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM slot WHERE doctor_id = ? AND slot_date = ? AND is_deleted = FALSE " +
                     "AND ((start_time < ? AND end_time > ?) OR (start_time < ? AND end_time > ?) OR (start_time >= ? AND end_time <= ?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(end));
            ps.setTime(4, Time.valueOf(end));
            ps.setTime(5, Time.valueOf(start));
            ps.setTime(6, Time.valueOf(start));
            ps.setTime(7, Time.valueOf(start));
            ps.setTime(8, Time.valueOf(end));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private List<SlotViewDTO> extractSlotViewDTOs(ResultSet rs) throws SQLException {
        List<SlotViewDTO> slots = new ArrayList<>();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        while (rs.next()) {
            SlotViewDTO dto = new SlotViewDTO();
            dto.setId(rs.getInt("id"));
            dto.setSlotDate(rs.getDate("slot_date").toLocalDate().format(dateFmt));
            dto.setStartTime(rs.getTime("start_time").toLocalTime().format(timeFmt));
            dto.setEndTime(rs.getTime("end_time").toLocalTime().format(timeFmt));
            dto.setMaxPatients(rs.getInt("max_patients"));
            dto.setBookedPatients(rs.getInt("booked_patients"));
            dto.setDoctorName(rs.getString("doctor_name"));
            dto.setCheckinRange(dto.getStartTime() + " - " + dto.getEndTime());
            dto.setBookingStatus(dto.getBookedPatients() + " / " + dto.getMaxPatients());
            slots.add(dto);
        }

        return slots;
    }
    
    
}