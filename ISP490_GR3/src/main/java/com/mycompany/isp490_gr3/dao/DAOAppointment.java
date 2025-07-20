package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.dto.AppointmentViewDTO;
import com.mycompany.isp490_gr3.model.Appointment;
import com.mycompany.isp490_gr3.model.Appointment.AppointmentStatus;
import com.mycompany.isp490_gr3.model.Appointment.PaymentStatus;
import com.mycompany.isp490_gr3.model.Slot;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOAppointment {

    private static final Logger LOGGER = Logger.getLogger(DAOAppointment.class.getName());

    // Lấy tất cả lịch hẹn của ngày hiện tại (đã sửa để loại bỏ thời gian khi so sánh)
    public List<AppointmentViewDTO> getTodayAppointmentViewDTOs(int offset, int limit) {
        String sql = "SELECT "
                + "a.id, a.appointment_code, "
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, "
                + "d.full_name AS doctor_name, "
                + "ms.service_name, "
                + "a.status, a.payment_status, "
                + "s.max_patients, "
                + "(SELECT COUNT(*) FROM appointment ap WHERE ap.slot_id = s.id AND ap.patient_id IS NOT NULL AND ap.status != 'cancelled') AS booked_patients "
                + "FROM appointment a "
                + "LEFT JOIN patients p ON a.patient_id = p.id "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "LEFT JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.is_deleted = FALSE AND s.is_deleted = FALSE AND DATE(s.slot_date) = CURDATE() "
                + "ORDER BY s.slot_date DESC, s.start_time DESC "
                + "LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return extractAppointmentViewDTOs(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách lịch hẹn hôm nay: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public int countTodayAppointmentViewDTOs() {
        String sql = "SELECT COUNT(*) FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE a.is_deleted = FALSE AND s.is_deleted = FALSE AND DATE(s.slot_date) = CURDATE()";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm lịch hẹn hôm nay: " + e.getMessage(), e);
        }
        return 0;
    }

    // Chuyển ResultSet thành danh sách AppointmentViewDTO
    private List<AppointmentViewDTO> extractAppointmentViewDTOs(ResultSet rs) throws SQLException {
        List<AppointmentViewDTO> list = new ArrayList<>();
        while (rs.next()) {
            AppointmentViewDTO dto = new AppointmentViewDTO();
            dto.setId(rs.getInt("id"));
            dto.setAppointmentCode(rs.getString("appointment_code"));
            dto.setSlotDate(rs.getString("slot_date"));
            dto.setSlotTimeRange(rs.getString("slot_time_range"));
            dto.setPatientCode(rs.getString("patient_code") != null ? rs.getString("patient_code") : "");
            dto.setPatientName(rs.getString("patient_name") != null ? rs.getString("patient_name") : "");
            dto.setPatientPhone(rs.getString("patient_phone") != null ? rs.getString("patient_phone") : "");
            dto.setDoctorName(rs.getString("doctor_name"));
            dto.setServiceName(rs.getString("service_name") != null ? rs.getString("service_name") : "");
            dto.setStatus(rs.getString("status"));
            dto.setPaymentStatus(rs.getString("payment_status"));
            dto.setBookedPatients(rs.getInt("booked_patients"));
            dto.setMaxPatients(rs.getInt("max_patients"));
            dto.setBookingStatus(dto.getBookedPatients() + " / " + dto.getMaxPatients());
            list.add(dto);
        }
        return list;
    }

    // Tìm kiếm lịch hẹn với các bộ lọc
    public List<AppointmentViewDTO> searchAppointmentViewDTOs(String appointmentCode, String patientCode,
            Integer doctorId, Integer servicesId, String status, LocalDate slotDate, int offset, int limit) {
        List<AppointmentViewDTO> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.id, a.appointment_code, "
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, "
                + "d.full_name AS doctor_name, "
                + "ms.service_name, a.status, a.payment_status, "
                + "s.max_patients, "
                + "(SELECT COUNT(*) FROM appointment ap WHERE ap.slot_id = s.id AND ap.patient_id IS NOT NULL AND ap.status != 'cancelled') AS booked_patients "
                + "FROM appointment a "
                + "LEFT JOIN patients p ON a.patient_id = p.id "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "LEFT JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.is_deleted = FALSE AND s.is_deleted = FALSE ");

        List<Object> params = new ArrayList<>();
        if (slotDate != null) {
            sql.append("AND DATE(s.slot_date) = ? ");
            params.add(Date.valueOf(slotDate));
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

        sql.append("ORDER BY s.slot_date DESC, s.start_time DESC ");
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

    // Đếm số lượng kết quả tìm kiếm
    public int countSearchAppointmentViewDTOs(String appointmentCode, String patientCode,
            Integer doctorId, Integer servicesId, String status, LocalDate slotDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM appointment a "
                + "LEFT JOIN patients p ON a.patient_id = p.id "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "LEFT JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.is_deleted = FALSE AND s.is_deleted = FALSE ");
        List<Object> params = new ArrayList<>();
        if (slotDate != null) {
            sql.append("AND DATE(s.slot_date) = ? ");
            params.add(Date.valueOf(slotDate));
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

    // Trong file DAOAppointment.java
    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        // Câu lệnh SQL này JOIN các bảng để lấy tất cả thông tin cần thiết
        String sql = "SELECT "
                + "    a.id, a.status, a.services_id, "
                + "    s.slot_date, s.start_time, s.end_time, "
                + "    d.full_name AS doctor_name, "
                + "    ms.service_name "
                + "FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "LEFT JOIN medical_services ms ON a.services_id = ms.services_id " // Dùng LEFT JOIN phòng trường hợp service bị xóa
                + "WHERE a.patient_id = ? "
                + "ORDER BY s.slot_date DESC, s.start_time DESC"; // Sắp xếp

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment();

                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(patientId);
                appointment.setServicesId(rs.getInt("services_id"));
                appointment.setStatus(Appointment.AppointmentStatus.fromString(rs.getString("status")));
                appointment.setDoctorFullName(rs.getString("doctor_name"));
                appointment.setServiceName(rs.getString("service_name"));
                appointment.setAppointmentDate(rs.getDate("slot_date").toString());

                // Tạo khoảng thời gian "HH:mm - HH:mm"
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                String timeRange = startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);
                appointment.setAppointmentTime(timeRange);

                appointments.add(appointment);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách lịch hẹn cho patientId " + patientId, e);
        }
        return appointments;
    }

    /**
     * Creates a new appointment. This method assumes you have a way to
     * determine the 'slot_id' based on the requested date, time, and doctor.
     * This might require a lookup in the 'slot' table.
     *
     * @param appointment The Appointment object containing details for the new
     * appointment.
     * @return The created Appointment object with its generated ID, or null if
     * creation fails.
     */
    public Appointment createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointment (patient_id, slot_id, services_id, status, payment_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getSlotId()); // Ensure slotId is set before calling this method
            pstmt.setInt(3, appointment.getServicesId());
            pstmt.setString(4, appointment.getStatus().name().toLowerCase()); // Convert Enum to String (lowercase for DB ENUM)
            pstmt.setString(5, appointment.getPaymentStatus().name().toLowerCase()); // Convert Enum to String (lowercase for DB ENUM)

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getInt(1)); // Set the generated ID back to the object
                        return appointment;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error
        }
        return null;
    }

    public boolean cancelAppointment(int appointmentId) {
        // Only allow cancellation if status is 'pending' or 'confirmed'
        String sql = "UPDATE appointment SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND is_deleted = FALSE AND status IN ('pending', 'confirmed')";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, AppointmentStatus.CANCELLED.name().toLowerCase()); // Set status to 'cancelled'
            pstmt.setInt(2, appointmentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error
        }
        return false;
    }

    public Integer findAvailableSlot(int doctorId, String date, String time) {
        String dateTimeStr = date + " " + time + ":00"; // Format for DATETIME comparison
        // Query to find an available slot that is not deleted, has capacity,
        // and matches the doctor, date, and time.
        // It also checks if the slot is not already fully booked by 'pending' or 'confirmed' appointments.
        String sql = "SELECT s.id FROM slot s "
                + "WHERE s.doctor_id = ? AND s.start_time = ? AND s.is_available = TRUE AND s.is_deleted = FALSE "
                + "AND s.max_patients > (SELECT COUNT(*) FROM appointment a WHERE a.slot_id = s.id AND a.status IN ('pending', 'confirmed'))";

        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, dateTimeStr);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error
        }
        return null; // No available slot found
    }

    public Integer getPatientIdByAccountId(String userAccountId) {
        String sql = "SELECT id FROM patients WHERE account_id = ? AND is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userAccountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean assignPatient(int appointmentId, int patientId, int servicesId) throws SQLException {
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);

            // Cập nhật appointment
            String updateSql = "UPDATE appointment SET patient_id = ?, services_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND is_deleted = FALSE";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, patientId);
                ps.setInt(2, servicesId);
                ps.setInt(3, appointmentId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Lấy slot_id và doctor_id
            String query = "SELECT s.id AS slot_id, s.doctor_id FROM appointment a JOIN slot s ON a.slot_id = s.id WHERE a.id = ?";
            int slotId, doctorId;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, appointmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    slotId = rs.getInt("slot_id");
                    doctorId = rs.getInt("doctor_id");
                }
            }

            // Thêm vào hàng đợi (queue)
            String insertQueue = "INSERT INTO queue (appointment_id, patient_id, doctor_id, slot_id, queue_type, priority, created_at) "
                    + "VALUES (?, ?, ?, ?, 'main', 0, CURRENT_TIMESTAMP)";
            try (PreparedStatement ps = conn.prepareStatement(insertQueue)) {
                ps.setInt(1, appointmentId);
                ps.setInt(2, patientId);
                ps.setInt(3, doctorId);
                ps.setInt(4, slotId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gán bệnh nhân: " + e.getMessage(), e);
            throw e;
        }
    }

    public boolean exists(int appointmentId) throws SQLException {
        String sql = "SELECT 1 FROM appointment WHERE id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isAssigned(int appointmentId) throws SQLException {
        String sql = "SELECT patient_id FROM appointment WHERE id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject("patient_id") != null;
                }
                return false;
            }
        }
    }

    private boolean isValidService(int servicesId) throws SQLException {
        String sql = "SELECT 1 FROM medical_services WHERE services_id = ? AND isdeleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, servicesId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }

    }

    public Slot getSlotByAppointmentId(int appointmentId) {
        String sql = "SELECT s.* FROM slot s "
                + "JOIN appointment a ON s.id = a.slot_id "
                + "WHERE a.id = ? AND a.is_deleted = FALSE AND s.is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Slot slot = new Slot();
                    slot.setId(rs.getInt("id"));
                    slot.setDoctorId(rs.getInt("doctor_id"));
                    slot.setSlotDate(rs.getDate("slot_date").toLocalDate());
                    slot.setStartTime(rs.getTime("start_time").toLocalTime());  // sửa tại đây
                    slot.setEndTime(rs.getTime("end_time").toLocalTime());      // sửa tại đây
                    slot.setMaxPatients(rs.getInt("max_patients"));
                    slot.setAvailable(rs.getBoolean("is_available"));
                    slot.setDeleted(rs.getBoolean("is_deleted"));
                    return slot;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy slot theo appointmentId: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean hasServiceInSameDay(int patientId, int servicesId, LocalDate slotDate) {
        String sql = "SELECT COUNT(*) FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE a.patient_id = ? AND a.services_id = ? AND DATE(s.slot_date) = ? "
                + "AND a.status != 'cancelled' AND a.is_deleted = FALSE AND s.is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, servicesId);
            ps.setDate(3, Date.valueOf(slotDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra trùng dịch vụ trong cùng ngày: " + e.getMessage(), e);
        }
        return false;
    }

    public int countAppointmentsInDateByPatient(int patientId, LocalDate slotDate) {
        String sql = "SELECT COUNT(*) FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE a.patient_id = ? AND DATE(s.slot_date) = ? "
                + "AND a.status != 'cancelled' AND a.is_deleted = FALSE AND s.is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setDate(2, Date.valueOf(slotDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm số lịch hẹn trong ngày theo bệnh nhân: " + e.getMessage(), e);
        }
        return 0;
    }

    public boolean unassignPatient(int appointmentId) throws SQLException {
        String updateAppointmentSql = "UPDATE appointment SET patient_id = NULL, services_id = NULL, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND is_deleted = FALSE";
        String softDeleteQueueSql = "UPDATE queue SET is_deleted = TRUE WHERE appointment_id = ? AND is_deleted = FALSE";

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);

            // Gỡ bệnh nhân khỏi lịch hẹn
            try (PreparedStatement ps = conn.prepareStatement(updateAppointmentSql)) {
                ps.setInt(1, appointmentId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }
            // Xóa mềm hàng đợi tương ứng
            try (PreparedStatement ps = conn.prepareStatement(softDeleteQueueSql)) {
                ps.setInt(1, appointmentId);
                ps.executeUpdate(); // Không rollback nếu không có dòng nào (có thể không có hàng đợi)
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gỡ bệnh nhân khỏi lịch hẹn (xóa mềm queue): " + e.getMessage(), e);
            throw e;
        }
    }

    //Checkin
    public List<AppointmentViewDTO> getTodayCheckinAppointments(int offset, int limit) {
        String sql = "SELECT "
                + "a.id, a.appointment_code, "
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, "
                + "d.full_name AS doctor_name, "
                + "ms.service_name, "
                + "a.status, a.payment_status, "
                + "s.max_patients, "
                + "(SELECT COUNT(*) FROM appointment ap WHERE ap.slot_id = s.id AND ap.patient_id IS NOT NULL AND ap.status != 'cancelled') AS booked_patients, "
                + "q.priority "
                + "FROM appointment a "
                + "LEFT JOIN patients p ON a.patient_id = p.id "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "LEFT JOIN medical_services ms ON a.services_id = ms.services_id "
                + "LEFT JOIN queue q ON q.appointment_id = a.id AND q.is_deleted = FALSE "
                + "WHERE a.is_deleted = FALSE "
                + "AND s.is_deleted = FALSE "
                + "AND DATE(s.slot_date) = CURDATE() "
                + "AND a.patient_id IS NOT NULL "
                + "AND a.status = 'pending' "
                + "ORDER BY s.slot_date DESC, s.start_time ASC "
                + "LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                return extractAppointmentViewDTOs(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách check-in hôm nay: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public int countTodayCheckinAppointments() {
        String sql = "SELECT COUNT(*) FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE a.is_deleted = FALSE "
                + "AND s.is_deleted = FALSE "
                + "AND DATE(s.slot_date) = CURDATE() "
                + "AND a.patient_id IS NOT NULL "
                + "AND a.status = 'pending'";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm check-in hôm nay: " + e.getMessage(), e);
        }
        return 0;
    }

    //Search checkin
    public List<AppointmentViewDTO> searchCheckinAppointments(String appointmentCode, String patientCode,
            Integer doctorId, Integer servicesId, int offset, int limit) {

        List<AppointmentViewDTO> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.id, a.appointment_code, "
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, "
                + "d.full_name AS doctor_name, "
                + "ms.service_name, a.status, a.payment_status, "
                + "s.max_patients, "
                + "(SELECT COUNT(*) FROM appointment ap WHERE ap.slot_id = s.id AND ap.patient_id IS NOT NULL AND ap.status != 'cancelled') AS booked_patients, "
                + "q.priority "
                + "FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "LEFT JOIN patients p ON a.patient_id = p.id "
                + "LEFT JOIN medical_services ms ON a.services_id = ms.services_id "
                + "LEFT JOIN queue q ON q.appointment_id = a.id AND q.is_deleted = FALSE "
                + "WHERE a.is_deleted = FALSE AND s.is_deleted = FALSE "
                + "AND a.patient_id IS NOT NULL "
                + "AND a.status = 'pending' "
                + "AND DATE(s.slot_date) = CURDATE() ");

        List<Object> params = new ArrayList<>();

        if (appointmentCode != null && !appointmentCode.isBlank()) {
            sql.append("AND a.appointment_code LIKE ? ");
            params.add("%" + appointmentCode.trim() + "%");
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

        sql.append("ORDER BY s.start_time ASC ");
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
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm lịch check-in: " + e.getMessage(), e);
            return result;
        }
    }

    public int countCheckinAppointments(String appointmentCode, String patientCode,
            Integer doctorId, Integer servicesId) {

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "LEFT JOIN patients p ON a.patient_id = p.id "
                + "LEFT JOIN medical_services ms ON a.services_id = ms.services_id "
                + "LEFT JOIN queue q ON q.appointment_id = a.id AND q.is_deleted = FALSE "
                + "WHERE a.is_deleted = FALSE AND s.is_deleted = FALSE "
                + "AND a.patient_id IS NOT NULL "
                + "AND a.status = 'pending' "
                + "AND DATE(s.slot_date) = CURDATE() ");

        List<Object> params = new ArrayList<>();

        if (appointmentCode != null && !appointmentCode.isBlank()) {
            sql.append("AND a.appointment_code LIKE ? ");
            params.add("%" + appointmentCode.trim() + "%");
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
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm lịch check-in: " + e.getMessage(), e);
        }

        return 0;
    }

    public boolean checkinAppointment(int appointmentId, int priority, String description) {
        if (priority != 0 && priority != 1) {
            throw new IllegalArgumentException("Priority must be 0 (Trung bình) hoặc 1 (Cao).");
        }

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);

            // Update appointment
            try (PreparedStatement stmt1 = conn.prepareStatement(
                    "UPDATE appointment SET checkin_time = CURRENT_TIMESTAMP, status = 'confirmed' WHERE id = ?")) {
                stmt1.setInt(1, appointmentId);
                stmt1.executeUpdate();
            }

            // Update queue
            try (PreparedStatement stmt2 = conn.prepareStatement(
                    "UPDATE queue SET priority = ?, description = ?, status = 'waiting', updated_at = CURRENT_TIMESTAMP "
                    + "WHERE appointment_id = ? AND is_deleted = FALSE")) {
                stmt2.setInt(1, priority);
                stmt2.setString(2, description);
                stmt2.setInt(3, appointmentId);
                stmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateNoShowAppointments() {
        String updateAppointmentSql = "UPDATE appointment "
                + "SET status = 'no_show' "
                + "WHERE status = 'pending' "
                + "AND checkin_time IS NULL "
                + "AND slot_id IN ( "
                + "    SELECT id FROM slot "
                + "    WHERE (slot_date < CURDATE()) "
                + "       OR (slot_date = CURDATE() AND end_time < CURTIME()) "
                + ")";

        String updateQueueSql = "UPDATE queue "
                + "SET status = 'skipped' "
                + "WHERE status = 'waiting' "
                + "AND is_deleted = FALSE "
                + "AND appointment_id IN ( "
                + "    SELECT id FROM appointment WHERE status = 'no_show' "
                + ")";

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement ps1 = conn.prepareStatement(updateAppointmentSql); PreparedStatement ps2 = conn.prepareStatement(updateQueueSql)) {
                int updatedAppointments = ps1.executeUpdate();
                int updatedQueues = ps2.executeUpdate();
                conn.commit();

                System.out.println("Đã cập nhật " + updatedAppointments + " appointment → no_show");
                System.out.println("Đã cập nhật " + updatedQueues + " queue → skipped");

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi khi cập nhật trạng thái no_show/skipped:");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
// Thêm phương thức này vào file DAOAppointment.java

    // Trong file DAOAppointment.java
    // Trong file DAOAppointment.java
    public Appointment createAppointmentWithSlotId(int patientId, int serviceId, int slotId) {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // BƯỚC 1: Tìm xem có lịch hẹn nào của bệnh nhân này cho slot này không
            String findExistingSql = "SELECT id, status FROM appointment WHERE patient_id = ? AND slot_id = ? FOR UPDATE";
            Integer existingAppointmentId = null;
            String existingStatus = null;

            try (PreparedStatement psFind = conn.prepareStatement(findExistingSql)) {
                psFind.setInt(1, patientId);
                psFind.setInt(2, slotId);
                ResultSet rs = psFind.executeQuery();
                if (rs.next()) {
                    existingAppointmentId = rs.getInt("id");
                    existingStatus = rs.getString("status");
                }
            }

            // BƯỚC 2: Xử lý các trường hợp
            if (existingAppointmentId != null) {
                // TRƯỜNG HỢP 1: ĐÃ TỒN TẠI LỊCH HẸN
                if ("cancelled".equalsIgnoreCase(existingStatus)) {
                    // Nếu nó đã bị hủy -> Kích hoạt lại (UPDATE)
                    String updateSql = "UPDATE appointment SET status = 'confirmed', services_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setInt(1, serviceId);
                        psUpdate.setInt(2, existingAppointmentId);
                        psUpdate.executeUpdate();
                    }
                } else {
                    // Nếu nó đang hoạt động (pending, confirmed) -> Đây là đặt trùng, báo lỗi
                    conn.rollback();
                    LOGGER.warning("Bệnh nhân " + patientId + " đã cố gắng đặt lại slot " + slotId + " mà họ đang có lịch hẹn đang hoạt động.");
                    return null;
                }
            } else {
                // TRƯỜNG HỢP 2: CHƯA CÓ LỊCH HẸN NÀO -> TẠO MỚI (INSERT)
                // Kiểm tra xem slot có còn chỗ không
                String checkSql = "SELECT s.max_patients, COUNT(a.id) as booked_count FROM slot s LEFT JOIN appointment a ON s.id = a.slot_id AND a.status != 'cancelled' WHERE s.id = ? GROUP BY s.id, s.max_patients";
                try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                    psCheck.setInt(1, slotId);
                    ResultSet rsCheck = psCheck.executeQuery();
                    if (rsCheck.next()) {
                        if (rsCheck.getInt("booked_count") >= rsCheck.getInt("max_patients")) {
                            conn.rollback();
                            return null; // Hết chỗ
                        }
                    } else {
                        conn.rollback();
                        return null; // Slot không tồn tại
                    }
                }
                // Nếu còn chỗ, insert
                String insertSql = "INSERT INTO appointment (patient_id, services_id, slot_id, status) VALUES (?, ?, ?, 'confirmed')";
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setInt(1, patientId);
                    psInsert.setInt(2, serviceId);
                    psInsert.setInt(3, slotId);
                    psInsert.executeUpdate();
                }
            }

            conn.commit(); // Lưu tất cả thay đổi nếu không có lỗi
            return getLatestAppointmentByPatientAndSlot(patientId, slotId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tạo lịch hẹn cho slotId: " + slotId, e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi rollback", ex);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi đóng connection", ex);
                }
            }
        }
        return null;
    }
// Trong file DAOAppointment.java

 
    public Appointment getLatestAppointmentByPatientAndSlot(int patientId, int slotId) {
        String sql = "SELECT "
                + "    a.id AS appointment_id, a.status, a.services_id, "
                + "    s.slot_date, s.start_time, s.end_time, " // Lấy cả end_time
                + "    d.full_name AS doctor_name, "
                + "    ms.service_name "
                + "FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.patient_id = ? AND a.slot_id = ? "
                + "ORDER BY a.id DESC "
                + "LIMIT 1";


        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, slotId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Appointment appointment = new Appointment();

                    appointment.setId(rs.getInt("appointment_id"));
                    appointment.setPatientId(patientId);
                    appointment.setSlotId(slotId);
                    appointment.setServicesId(rs.getInt("services_id"));
                    appointment.setStatus(Appointment.AppointmentStatus.fromString(rs.getString("status")));
                    appointment.setDoctorFullName(rs.getString("doctor_name"));
                    appointment.setServiceName(rs.getString("service_name"));
                    appointment.setAppointmentDate(rs.getDate("slot_date").toString());

                    // === THAY ĐỔI ĐỂ TẠO KHOẢNG THỜI GIAN ===
                    LocalTime startTime = rs.getTime("start_time").toLocalTime();
                    LocalTime endTime = rs.getTime("end_time").toLocalTime();

                    // Định dạng lại cho đẹp (ví dụ: 10:00 - 10:30)
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    String timeRange = startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);

                    // Gán khoảng thời gian vào trường appointmentTime
                    appointment.setAppointmentTime(timeRange);
                    // === KẾT THÚC THAY ĐỔI ===

                    return appointment;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy chi tiết lịch hẹn cho patientId " + patientId + " và slotId " + slotId, e);
        }
        return null;
    }
    
    
    public boolean isCheckedIn(int appointmentId) throws SQLException {
        String sql = "SELECT checkin_time FROM appointment WHERE id = ? AND checkin_time IS NOT NULL";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Trả về true nếu có checkin_time
        }
    }
   
}
