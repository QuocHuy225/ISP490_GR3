/*
 * Click netbeans://netbeans/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click netbeans://netbeans/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.dto.QueueViewDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FPT SHOP
 */
public class DAOQueue {

    private static final Logger LOGGER = Logger.getLogger(DAOQueue.class.getName());

    // 1. Lấy danh sách hàng đợi hôm nay với phân trang
    public List<QueueViewDTO> getTodayQueueViewDTOs(int doctorId, LocalDate slotDate, int offset, int limit) {
        List<QueueViewDTO> list = new ArrayList<>();

        String sql = "SELECT "
                + "q.id AS queue_id, "
                + "a.id AS appointment_id, "
                + "a.appointment_code, "
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, "
                + "p.full_name AS patient_name, "
                + "p.phone AS patient_phone, "
                + "ms.service_name, "
                + "q.status, "
                + "q.priority, "
                + "a.checkin_time, "
                + "d.full_name AS doctor_name "
                + "FROM queue q "
                + "JOIN appointment a ON q.appointment_id = a.id "
                + "JOIN slot s ON q.slot_id = s.id "
                + "JOIN patients p ON q.patient_id = p.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "JOIN doctors d ON q.doctor_id = d.id "
                + "WHERE q.is_deleted = FALSE "
                + "AND a.is_deleted = FALSE "
                + "AND s.is_deleted = FALSE "
                + "AND a.checkin_time IS NOT NULL "
                + "AND q.status IN ('waiting', 'in_progress') ";

        if (doctorId > 0) {
            sql += "AND q.doctor_id = ? ";
        }
        if (slotDate != null) {
            sql += "AND DATE(s.slot_date) = ? ";
        } else {
            sql += "AND DATE(s.slot_date) = CURDATE() ";
        }
        sql += "ORDER BY q.priority DESC, s.start_time ASC, a.checkin_time ASC "
                + "LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (doctorId > 0) {
                ps.setInt(paramIndex++, doctorId);
            }
            if (slotDate != null) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(slotDate));
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            String debugSql = sql;
            if (doctorId > 0) {
                debugSql = debugSql.replaceFirst("\\?", String.valueOf(doctorId));
            }
            if (slotDate != null) {
                debugSql = debugSql.replaceFirst("\\?", slotDate.toString());
            }
            debugSql = debugSql.replaceFirst("\\?", String.valueOf(limit));
            debugSql = debugSql.replaceFirst("\\?", String.valueOf(offset));
            LOGGER.info("Executing SQL: " + debugSql);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QueueViewDTO dto = new QueueViewDTO();
                    dto.setQueueId(rs.getInt("queue_id"));
                    dto.setAppointmentId(rs.getInt("appointment_id"));
                    dto.setAppointmentCode(rs.getString("appointment_code"));
                    dto.setSlotDate(rs.getString("slot_date"));
                    dto.setSlotTimeRange(rs.getString("slot_time_range"));
                    dto.setPatientCode(rs.getString("patient_code"));
                    dto.setPatientName(rs.getString("patient_name"));
                    dto.setPatientPhone(rs.getString("patient_phone"));
                    dto.setServiceName(rs.getString("service_name"));
                    dto.setStatus(rs.getString("status"));
                    dto.setPriority(rs.getInt("priority"));
                    dto.setCheckinTime(rs.getTimestamp("checkin_time") != null
                            ? rs.getTimestamp("checkin_time").toLocalDateTime().toString() : "");
                    dto.setDoctorName(rs.getString("doctor_name"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách hàng đợi: " + e.getMessage(), e);
        }

        return list;
    }

    // 2. Đếm tổng số hàng đợi hôm nay
    public int countTodayQueueViewDTOs(int doctorId, LocalDate slotDate) {
        String sql = "SELECT COUNT(*) FROM queue q "
                + "JOIN appointment a ON q.appointment_id = a.id "
                + "JOIN slot s ON q.slot_id = s.id "
                + "WHERE q.is_deleted = FALSE "
                + "AND a.is_deleted = FALSE "
                + "AND s.is_deleted = FALSE "
                + "AND a.checkin_time IS NOT NULL "
                + "AND q.status IN ('waiting', 'in_progress') ";;

        if (doctorId > 0) {
            sql += "AND q.doctor_id = ? ";
        }
        if (slotDate != null) {
            sql += "AND DATE(s.slot_date) = ? ";
        } else {
            sql += "AND DATE(s.slot_date) = CURDATE() ";
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (doctorId > 0) {
                ps.setInt(paramIndex++, doctorId);
            }
            if (slotDate != null) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(slotDate));
            }
            String debugSql = sql;
            if (doctorId > 0) {
                debugSql = debugSql.replaceFirst("\\?", String.valueOf(doctorId));
            }
            if (slotDate != null) {
                debugSql = debugSql.replaceFirst("\\?", slotDate.toString());
            }
            LOGGER.info("Executing count SQL: " + debugSql);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm hàng đợi hôm nay: " + e.getMessage(), e);
        }
        return 0;
    }

    // 3. Tìm kiếm hàng đợi với các bộ lọc và phân trang
    public List<QueueViewDTO> searchQueueViewDTOs(String appointmentCode, String patientCode, Integer doctorId,
            LocalDate slotDate, int offset, int limit) {
        List<QueueViewDTO> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT "
                + "q.id AS queue_id, "
                + "a.id AS appointment_id, "
                + "a.appointment_code, "
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, "
                + "p.full_name AS patient_name, "
                + "p.phone AS patient_phone, "
                + "ms.service_name, "
                + "q.status, "
                + "q.priority, "
                + "a.checkin_time, "
                + "d.full_name AS doctor_name "
                + "FROM queue q "
                + "JOIN appointment a ON q.appointment_id = a.id "
                + "JOIN slot s ON q.slot_id = s.id "
                + "JOIN patients p ON q.patient_id = p.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "JOIN doctors d ON q.doctor_id = d.id "
                + "WHERE q.is_deleted = FALSE "
                + "AND a.is_deleted = FALSE "
                + "AND s.is_deleted = FALSE "
                + "AND a.checkin_time IS NOT NULL "
                + "AND q.status IN ('waiting', 'in_progress') ");

        List<Object> params = new ArrayList<>();
        if (slotDate != null) {
            sql.append("AND DATE(s.slot_date) = ? ");
            params.add(java.sql.Date.valueOf(slotDate));
        }
        if (appointmentCode != null && !appointmentCode.isBlank()) {
            sql.append("AND a.appointment_code LIKE ? ");
            params.add("%" + appointmentCode.trim() + "%");
        }
        if (patientCode != null && !patientCode.isBlank()) {
            sql.append("AND p.patient_code LIKE ? ");
            params.add("%" + patientCode.trim() + "%");
        }
        if (doctorId != null) {
            sql.append("AND q.doctor_id = ? ");
            params.add(doctorId);
        }

        sql.append("ORDER BY q.priority DESC, s.start_time ASC, a.checkin_time ASC ");
        sql.append("LIMIT ? OFFSET ? ");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            // Log câu truy vấn với giá trị thực tế
            StringBuilder debugSql = new StringBuilder(sql.toString());
            for (Object param : params) {
                debugSql = new StringBuilder(debugSql.toString().replaceFirst("\\?", param != null ? param.toString() : "NULL"));
            }
            LOGGER.info("Executing search SQL: " + debugSql.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QueueViewDTO dto = new QueueViewDTO();
                    dto.setQueueId(rs.getInt("queue_id"));
                    dto.setAppointmentId(rs.getInt("appointment_id"));
                    dto.setAppointmentCode(rs.getString("appointment_code"));
                    dto.setSlotDate(rs.getString("slot_date"));
                    dto.setSlotTimeRange(rs.getString("slot_time_range"));
                    dto.setPatientCode(rs.getString("patient_code"));
                    dto.setPatientName(rs.getString("patient_name"));
                    dto.setPatientPhone(rs.getString("patient_phone"));
                    dto.setServiceName(rs.getString("service_name"));
                    dto.setStatus(rs.getString("status"));
                    dto.setPriority(rs.getInt("priority"));
                    dto.setCheckinTime(rs.getTimestamp("checkin_time") != null
                            ? rs.getTimestamp("checkin_time").toLocalDateTime().toString() : "");
                    dto.setDoctorName(rs.getString("doctor_name"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm hàng đợi: " + e.getMessage(), e);
        }
        return result;
    }

    // 4. Đếm tổng số kết quả tìm kiếm
    public int countSearchQueueViewDTOs(String appointmentCode, String patientCode, Integer doctorId, LocalDate slotDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM queue q "
                + "JOIN appointment a ON q.appointment_id = a.id "
                + "JOIN slot s ON q.slot_id = s.id "
                + "JOIN patients p ON q.patient_id = p.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "JOIN doctors d ON q.doctor_id = d.id "
                + "WHERE q.is_deleted = FALSE "
                + "AND a.is_deleted = FALSE "
                + "AND s.is_deleted = FALSE "
                + "AND a.checkin_time IS NOT NULL "
                + "AND q.status IN ('waiting', 'in_progress') ");

        List<Object> params = new ArrayList<>();
        if (slotDate != null) {
            sql.append("AND DATE(s.slot_date) = ? ");
            params.add(java.sql.Date.valueOf(slotDate));
        }
        if (appointmentCode != null && !appointmentCode.isBlank()) {
            sql.append("AND a.appointment_code LIKE ? ");
            params.add("%" + appointmentCode.trim() + "%");
        }
        if (patientCode != null && !patientCode.isBlank()) {
            sql.append("AND p.patient_code LIKE ? ");
            params.add("%" + patientCode.trim() + "%");
        }
        if (doctorId != null) {
            sql.append("AND q.doctor_id = ? ");
            params.add(doctorId);
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            // Log câu truy vấn với giá trị thực tế
            StringBuilder debugSql = new StringBuilder(sql.toString());
            for (Object param : params) {
                debugSql = new StringBuilder(debugSql.toString().replaceFirst("\\?", param != null ? param.toString() : "NULL"));
            }
            LOGGER.info("Executing count search SQL: " + debugSql.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm kết quả tìm kiếm hàng đợi: " + e.getMessage(), e);
        }
        return 0;
    }

    public static void main(String[] args) {
        DAOQueue dao = new DAOQueue();

        // Tham số test
        int pageSize = 10;
        int currentPage = 1;
        int offset = (currentPage - 1) * pageSize;

        // Test 1: Danh sách hàng đợi mặc định
        System.out.println("=== Test 1: Danh sách hàng đợi mặc định ===");
        int doctorId1 = 0;
        LocalDate slotDate1 = null;
        List<QueueViewDTO> queueList1 = dao.getTodayQueueViewDTOs(doctorId1, slotDate1, offset, pageSize);
        int count1 = dao.countTodayQueueViewDTOs(doctorId1, slotDate1);
        if (queueList1.isEmpty()) {
            System.out.println("Không có bệnh nhân nào trong hàng đợi hôm nay.");
        } else {
            System.out.println("Tổng số bản ghi: " + count1);
            for (QueueViewDTO dto : queueList1) {
                System.out.println(dto.toString());
            }
        }

        // Test 2: Tìm kiếm với doctorId = 1 và patientCode = BN0002
        System.out.println("\n=== Test 2: Tìm kiếm hàng đợi cho bác sĩ ID = 1 và mã bệnh nhân BN0002 ===");
        int doctorId2 = 1;
        LocalDate slotDate2 = null;
        String appointmentCode2 = null;
        String patientCode2 = "BN0002";
        List<QueueViewDTO> queueList2 = dao.searchQueueViewDTOs(appointmentCode2, patientCode2, doctorId2, slotDate2, offset, pageSize);
        int count2 = dao.countSearchQueueViewDTOs(appointmentCode2, patientCode2, doctorId2, slotDate2);
        if (queueList2.isEmpty()) {
            System.out.println("Không có hàng đợi nào cho bác sĩ ID = " + doctorId2 + " với mã bệnh nhân " + patientCode2);
        } else {
            System.out.println("Tổng số bản ghi: " + count2);
            for (QueueViewDTO dto : queueList2) {
                System.out.println(dto.toString());
            }
        }

        // Test 3: Tìm kiếm với doctorId = 1 và slotDate = 15/07/2025
        System.out.println("\n=== Test 3: Tìm kiếm hàng đợi cho bác sĩ ID = 1 và ngày 15/07/2025 ===");
        int doctorId3 = 1;
        LocalDate slotDate3 = LocalDate.of(2025, 7, 15);
        String appointmentCode3 = null;
        String patientCode3 = null;
        List<QueueViewDTO> queueList3 = dao.searchQueueViewDTOs(appointmentCode3, patientCode3, doctorId3, slotDate3, offset, pageSize);
        int count3 = dao.countSearchQueueViewDTOs(appointmentCode3, patientCode3, doctorId3, slotDate3);
        if (queueList3.isEmpty()) {
            System.out.println("Không có hàng đợi nào cho bác sĩ ID = " + doctorId3 + " vào ngày " + slotDate3);
        } else {
            System.out.println("Tổng số bản ghi: " + count3);
            for (QueueViewDTO dto : queueList3) {
                System.out.println(dto.toString());
            }
        }
    }
}
