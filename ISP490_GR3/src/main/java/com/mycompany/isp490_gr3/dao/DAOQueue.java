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
                + "d.full_name AS doctor_name, "
                + "p.id AS patient_id "
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
                    dto.setPatientId(rs.getInt("patient_id"));
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
                + "d.full_name AS doctor_name, "
                + "p.id AS patient_id "
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
                    dto.setPatientId(rs.getInt("patient_id"));
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

    // hàm của huy
    public boolean updateQueueStatus(int queueId, String newStatus) {
        // SQL query để cập nhật cột 'status' và 'updated_at'
        String sql = "UPDATE queue SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBContext.getConnection(); // Lấy kết nối từ DBContext của bạn
                 PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus); // Đặt giá trị trạng thái mới vào tham số thứ nhất
            ps.setInt(2, queueId);      // Đặt ID hàng đợi vào tham số thứ hai

            int rowsAffected = ps.executeUpdate(); // Thực thi lệnh cập nhật

            if (rowsAffected > 0) {
                LOGGER.info("Queue ID " + queueId + " status updated to " + newStatus);
                return true; // Cập nhật thành công
            } else {
                LOGGER.warning("No rows affected when updating status for Queue ID " + queueId + ". Status might be the same or ID not found.");
                return false; // Không có hàng nào bị ảnh hưởng (có thể ID không tồn tại hoặc trạng thái đã giống)
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái hàng đợi cho ID " + queueId + " thành " + newStatus + ": " + e.getMessage(), e);
            return false; // Xảy ra lỗi SQL
        }
    }

    public QueueViewDTO getQueueViewDTOById(int queueId) {
        QueueViewDTO queue = null;
        String sql = "SELECT "
                + "q.id AS queueId, "
                + "a.appointment_code AS appointmentCode, "
                + "s.slot_date AS slotDate, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slotTimeRange, "
                + "p.patient_code AS patientCode, "
                + "p.full_name AS patientName, "
                + "p.phone AS patientPhone, "
                + "ms.service_name AS serviceName, "
                + "q.priority, "
                + "a.checkin_time AS checkinTime, "
                + "d.full_name AS doctorName, "
                + "q.status, " // KHÔNG CÓ 'q.description' ở đây
                + "p.id AS patient_id "
                + "FROM queue q "
                + "JOIN appointment a ON q.appointment_id = a.id "
                + "JOIN slot s ON q.slot_id = s.id "
                + "JOIN patients p ON q.patient_id = p.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "LEFT JOIN doctors d ON q.doctor_id = d.id "
                + "WHERE q.id = ?";

        try (Connection conn = DBContext.getConnection(); // Lấy kết nối từ DBContext của bạn
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, queueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    queue = new QueueViewDTO();
                    queue.setQueueId(rs.getInt("queueId"));
                    queue.setAppointmentCode(rs.getString("appointmentCode"));
                    queue.setSlotDate(rs.getString("slotDate"));
                    queue.setSlotTimeRange(rs.getString("slotTimeRange"));
                    queue.setPatientCode(rs.getString("patientCode"));
                    queue.setPatientName(rs.getString("patientName"));
                    queue.setPatientPhone(rs.getString("patientPhone"));
                    queue.setServiceName(rs.getString("serviceName"));
                    queue.setPriority(rs.getInt("priority"));
                    queue.setCheckinTime(rs.getTimestamp("checkinTime") != null ? rs.getTimestamp("checkinTime").toLocalDateTime().toString() : null);
                    queue.setDoctorName(rs.getString("doctorName"));
                    queue.setStatus(rs.getString("status"));
                    queue.setPatientId(rs.getInt("patient_id"));
                    // KHÔNG CÓ dòng queue.setDescription(rs.getString("description")); ở đây
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy chi tiết hàng đợi theo ID: " + queueId + ": " + e.getMessage(), e);
        }
        return queue;
    }

    public boolean removeFromQueue(String appointmentCode) {
        if (appointmentCode == null || appointmentCode.trim().isEmpty()) {
            LOGGER.warning("Mã lịch hẹn không hợp lệ: " + appointmentCode);
            return false;
        }

        String sqlQueue = "UPDATE queue q "
                + "INNER JOIN appointment a ON q.appointment_id = a.id "
                + "SET q.status = 'rejected', q.updated_at = CURRENT_TIMESTAMP "
                + "WHERE a.appointment_code = ? "
                + "AND q.status IN ('waiting', 'in_progress') "
                + "AND q.is_deleted = FALSE "
                + "AND a.is_deleted = FALSE";
        String sqlAppointment = "UPDATE appointment a "
                + "SET a.status = 'cancelled', a.updated_at = CURRENT_TIMESTAMP "
                + "WHERE a.appointment_code = ? "
                + "AND a.status IN ('pending', 'confirmed') "
                + "AND a.is_deleted = FALSE";

        Connection conn = null;
        PreparedStatement stmtQueue = null;
        PreparedStatement stmtAppointment = null;
        boolean success = false;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Cập nhật bảng queue
            stmtQueue = conn.prepareStatement(sqlQueue);
            stmtQueue.setString(1, appointmentCode);
            int rowsAffectedQueue = stmtQueue.executeUpdate();
            LOGGER.info("Cập nhật queue cho appointmentCode " + appointmentCode + ": " + rowsAffectedQueue + " hàng bị ảnh hưởng");

            // Cập nhật bảng appointment
            stmtAppointment = conn.prepareStatement(sqlAppointment);
            stmtAppointment.setString(1, appointmentCode);
            int rowsAffectedAppointment = stmtAppointment.executeUpdate();
            LOGGER.info("Cập nhật appointment cho appointmentCode " + appointmentCode + ": " + rowsAffectedAppointment + " hàng bị ảnh hưởng");

            // Chỉ commit nếu cả hai cập nhật đều thành công
            if (rowsAffectedQueue > 0 && rowsAffectedAppointment > 0) {
                conn.commit();
                success = true;
                LOGGER.info("Đã gỡ thành công khỏi queue và cập nhật appointment thành 'cancelled' cho appointmentCode: " + appointmentCode);
            } else {
                conn.rollback();
                LOGGER.warning("Không tìm thấy bản ghi queue hoặc appointment hợp lệ cho appointmentCode: " + appointmentCode
                        + " (queue rows: " + rowsAffectedQueue + ", appointment rows: " + rowsAffectedAppointment + ")");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gỡ khỏi queue và cập nhật appointment cho appointmentCode: " + appointmentCode, e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi rollback giao dịch cho appointmentCode: " + appointmentCode, rollbackEx);
                }
            }
        } finally {
            try {
                if (stmtQueue != null) {
                    stmtQueue.close();
                }
                if (stmtAppointment != null) {
                    stmtAppointment.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi đóng tài nguyên cho appointmentCode: " + appointmentCode, e);
            }
        }
        return success;
    }

    
}
