package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.dto.AppointmentViewDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOAppointment {

    private static final Logger LOGGER = Logger.getLogger(DAOAppointment.class.getName());

    public List<AppointmentViewDTO> getAppointmentViewDTOs(int offset, int limit) {
        String sql = "SELECT "
                + "a.id, a.appointment_code, "
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, "
                + "d.full_name AS doctor_name, "
                + "ms.service_name, "
                + "a.status, a.payment_status "
                + "FROM appointment a "
                + "JOIN patients p ON a.patient_id = p.id "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.is_deleted = FALSE "
                + "ORDER BY s.slot_date DESC, s.start_time ASC "
                + "LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                return extractAppointmentViewDTOs(rs);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<AppointmentViewDTO> searchAppointmentViewDTOs(String appointmentCode, String slotDate, String patientCode,
            Integer doctorId, Integer servicesId, String status,
            int offset, int limit) {

        List<AppointmentViewDTO> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT a.id, a.appointment_code, "
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, "
                + "d.full_name AS doctor_name, "
                + "ms.service_name, a.status, a.payment_status "
                + "FROM appointment a "
                + "JOIN patients p ON a.patient_id = p.id "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.is_deleted = FALSE ");

        List<Object> params = new ArrayList<>();

        if (appointmentCode != null && !appointmentCode.isBlank()) {
            sql.append("AND a.appointment_code LIKE ? ");
            params.add("%" + appointmentCode.trim() + "%");
        }

        if (slotDate != null && !slotDate.isBlank()) {
            sql.append("AND s.slot_date = ? ");
            params.add(slotDate);
        }

        if (patientCode != null && !patientCode.isBlank()) {
            sql.append("AND p.patient_code LIKE ? ");
            params.add("%" + patientCode.trim() + "%");
        }

        if (doctorId != null) {
            sql.append("AND d.id = ? ");
            params.add(doctorId);
        }

        if (servicesId != null) {
            sql.append("AND ms.services_id = ? ");
            params.add(servicesId);
        }

        if (status != null && !status.isBlank()) {
            sql.append("AND a.status = ? ");
            params.add(status);
        }

        sql.append("ORDER BY s.slot_date DESC, s.start_time ASC ");
        sql.append("LIMIT ? OFFSET ? ");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                return extractAppointmentViewDTOs(rs);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm lịch hẹn: " + e.getMessage(), e);
            return result;
        }
    }

    private List<AppointmentViewDTO> extractAppointmentViewDTOs(ResultSet rs) throws SQLException {
        List<AppointmentViewDTO> list = new ArrayList<>();
        while (rs.next()) {
            AppointmentViewDTO dto = new AppointmentViewDTO();
            dto.setId(rs.getInt("id"));
            dto.setAppointmentCode(rs.getString("appointment_code"));
            dto.setSlotDate(rs.getString("slot_date"));
            dto.setSlotTimeRange(rs.getString("slot_time_range"));
            dto.setPatientCode(rs.getString("patient_code"));
            dto.setPatientName(rs.getString("patient_name"));
            dto.setPatientPhone(rs.getString("patient_phone"));
            dto.setDoctorName(rs.getString("doctor_name"));
            dto.setServiceName(rs.getString("service_name"));
            dto.setStatus(rs.getString("status"));
            dto.setPaymentStatus(rs.getString("payment_status"));
            list.add(dto);
        }
        return list;
    }

    //Count
    public int countAppointmentViewDTOs() {
        String sql = "SELECT COUNT(*) FROM appointment a WHERE a.is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm lịch hẹn: " + e.getMessage(), e);
        }
        return 0;
    }

    public int countSearchAppointmentViewDTOs(String appointmentCode, String slotDate, String patientCode,
            Integer doctorId, Integer servicesId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM appointment a "
                + "JOIN patients p ON a.patient_id = p.id "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.is_deleted = FALSE ");

        List<Object> params = new ArrayList<>();

        if (appointmentCode != null && !appointmentCode.isBlank()) {
            sql.append("AND a.appointment_code LIKE ? ");
            params.add("%" + appointmentCode.trim() + "%");
        }
        if (slotDate != null && !slotDate.isBlank()) {
            sql.append("AND s.slot_date = ? ");
            params.add(slotDate);
        }
        if (patientCode != null && !patientCode.isBlank()) {
            sql.append("AND p.patient_code LIKE ? ");
            params.add("%" + patientCode.trim() + "%");
        }
        if (doctorId != null) {
            sql.append("AND d.id = ? ");
            params.add(doctorId);
        }
        if (servicesId != null) {
            sql.append("AND ms.services_id = ? ");
            params.add(servicesId);
        }
        if (status != null && !status.isBlank()) {
            sql.append("AND a.status = ? ");
            params.add(status);
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm kết quả tìm kiếm lịch hẹn: " + e.getMessage(), e);
        }

        return 0;
    }

    //Delete
    public boolean deleteAppointmentById(int appointmentId) {
        String sql = "UPDATE appointment "
                + "SET is_deleted = 1, updated_at = CURRENT_TIMESTAMP "
                + "WHERE id = ? "
                + "  AND is_deleted = 0 "
                + "  AND status IN ('pending', 'confirmed') "
                + "  AND slot_id IN ( "
                + "      SELECT id FROM slot "
                + "      WHERE slot_date > CURRENT_DATE "
                + "         OR (slot_date = CURRENT_DATE AND slot_start_time > CURRENT_TIME) "
                + "  )";

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int deleteAppointmentsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("UPDATE appointment ")
                .append("SET is_deleted = 1, updated_at = CURRENT_TIMESTAMP ")
                .append("WHERE is_deleted = 0 ")
                .append("AND status IN ('pending', 'confirmed') ")
                .append("AND slot_id IN ( ")
                .append("    SELECT id FROM slot ")
                .append("    WHERE slot_date > CURRENT_DATE ")
                .append("       OR (slot_date = CURRENT_DATE AND slot_start_time > CURRENT_TIME) ")
                .append(") ")
                .append("AND id IN (");

        for (int i = 0; i < ids.size(); i++) {
            sqlBuilder.append("?");
            if (i < ids.size() - 1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(")");

        String sql = sqlBuilder.toString();

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }

            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    
 public static void main(String[] args) {
        DAOAppointment dao = new DAOAppointment();

        // Test xóa 1 lịch hẹn
        int singleId = 101; // Thay bằng ID tồn tại trong DB và hợp lệ
        boolean resultSingle = dao.deleteAppointmentById(singleId);
        System.out.println("Kết quả xóa 1 lịch hẹn (ID " + singleId + "): " + (resultSingle ? "Thành công" : "Thất bại"));

        // Test xóa nhiều lịch hẹn
        List<Integer> multipleIds = Arrays.asList(102, 103, 104); // Thay bằng ID hợp lệ
        int deletedCount = dao.deleteAppointmentsByIds(multipleIds);
        System.out.println("Đã xóa " + deletedCount + " lịch hẹn trong danh sách: " + multipleIds);
    }

}
