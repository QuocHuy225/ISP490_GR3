/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.dto.SlotViewDTO;
import com.mycompany.isp490_gr3.model.Slot;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FPT SHOP
 */
public class DAOSlot {

    private static final Logger LOGGER = Logger.getLogger(DAOSlot.class.getName());

    // Lấy danh sách slot có phân trang và lọc theo doctorId và ngày
    public List<SlotViewDTO> searchSlotViewDTOs(Integer doctorId, LocalDate slotDate, int offset, int limit) {
        List<SlotViewDTO> slots = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.id, s.slot_date, s.start_time, s.end_time, "
                + "s.max_patients, d.full_name AS doctor_name, "
                + "(SELECT COUNT(*) FROM appointment a WHERE a.slot_id = s.id AND a.status != 'cancelled') AS booked_patients "
                + // sửa tại đây
                "FROM slot s JOIN doctors d ON s.doctor_id = d.id "
                + "WHERE s.is_deleted = FALSE"
        );

        if (doctorId != null) {
            sql.append(" AND s.doctor_id = ?");
        }
        if (slotDate != null) {
            sql.append(" AND s.slot_date = ?");
        }

        sql.append(" ORDER BY s.slot_date DESC, s.start_time DESC LIMIT ? OFFSET ?");

        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (doctorId != null) {
                ps.setInt(paramIndex++, doctorId);
            }
            if (slotDate != null) {
                ps.setDate(paramIndex++, Date.valueOf(slotDate));
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            ResultSet rs = ps.executeQuery();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            while (rs.next()) {
                SlotViewDTO dto = new SlotViewDTO();
                dto.setId(rs.getInt("id"));
                dto.setSlotDate(rs.getDate("slot_date").toLocalDate().format(dateFormatter));
                dto.setStartTime(rs.getTime("start_time").toLocalTime().format(timeFormatter));
                dto.setEndTime(rs.getTime("end_time").toLocalTime().format(timeFormatter));
                dto.setMaxPatients(rs.getInt("max_patients"));
                dto.setBookedPatients(rs.getInt("booked_patients"));
                dto.setDoctorName(rs.getString("doctor_name"));
                dto.setCheckinRange(dto.getStartTime() + " - " + dto.getEndTime());
                dto.setBookingStatus(dto.getBookedPatients() + " / " + dto.getMaxPatients());

                slots.add(dto);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm slot: " + e.getMessage(), e);
        }
        return slots;
    }

    // Đếm tổng số slot sau khi lọc (phục vụ phân trang)
    public int countFilteredSlotViewDTOs(Integer doctorId, LocalDate slotDate) {
        int count = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM slot s WHERE s.is_deleted = FALSE");

        if (doctorId != null) {
            sql.append(" AND s.doctor_id = ?");
        }
        if (slotDate != null) {
            sql.append(" AND s.slot_date = ?");
        }

        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (doctorId != null) {
                ps.setInt(paramIndex++, doctorId);
            }
            if (slotDate != null) {
                ps.setDate(paramIndex++, Date.valueOf(slotDate));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm slot: " + e.getMessage(), e);
        }

        return count;
    }

   public static void main(String[] args) {
    DAOSlot dao = new DAOSlot();

    // Định dạng cho in bảng đẹp hơn
    String format = "%-3s | %-12s | %-20s | %-17s | %-13s | %-12s | %-12s%n";
    System.out.println("=== Test 1: Không lọc ===");
    List<SlotViewDTO> slots = dao.searchSlotViewDTOs(null, null, 0, 20);

    System.out.printf(format, "ID", "Ngày khám", "Bác sĩ", "Khoảng giờ", "Tối đa", "Đã đặt", "Trạng thái");
    System.out.println("------------------------------------------------------------------------------------------");

    for (SlotViewDTO slot : slots) {
        System.out.printf(format,
                slot.getId(),
                slot.getSlotDate(),
                slot.getDoctorName(),
                slot.getCheckinRange(),
                slot.getMaxPatients(),
                slot.getBookedPatients(),
                slot.getBookingStatus());
    }

    System.out.println("\n=== Tổng slot: " + slots.size());
}

}
