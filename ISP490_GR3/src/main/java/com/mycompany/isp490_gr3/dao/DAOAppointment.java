package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.dto.AppointmentViewDTO;
import com.mycompany.isp490_gr3.model.Appointment;
import com.mycompany.isp490_gr3.model.Appointment.PaymentStatus;
import com.mycompany.isp490_gr3.model.Slot;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID; // Import này cần thiết nếu bạn tạo mã ở đây, nhưng giờ tạo trong DAOSlot

public class DAOAppointment {

    private static final Logger LOGGER = Logger.getLogger(DAOAppointment.class.getName());
    private static final int MAX_APPOINTMENTS_PER_DAY = 2;

    // Lấy tất cả lịch hẹn của ngày hiện tại
    public List<AppointmentViewDTO> getTodayAppointmentViewDTOs(int offset, int limit) {
        String sql = "SELECT "
                + "a.id, a.appointment_code, " // Có trong DB
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, "
                + "d.full_name AS doctor_name, "
                + "ms.service_name, "
                + "a.status, a.payment_status, " // Có trong DB
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
            dto.setAppointmentCode(rs.getString("appointment_code")); // Lấy appointment_code
            dto.setSlotDate(rs.getString("slot_date"));
            dto.setSlotTimeRange(rs.getString("slot_time_range"));
            dto.setPatientCode(rs.getString("patient_code") != null ? rs.getString("patient_code") : "");
            dto.setPatientName(rs.getString("patient_name") != null ? rs.getString("patient_name") : "");
            dto.setPatientPhone(rs.getString("patient_phone") != null ? rs.getString("patient_phone") : "");
            dto.setDoctorName(rs.getString("doctor_name"));
            dto.setServiceName(rs.getString("service_name") != null ? rs.getString("service_name") : "");
            dto.setStatus(rs.getString("status"));
            dto.setPaymentStatus(rs.getString("payment_status")); // Lấy payment_status
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
                "SELECT a.id, a.appointment_code, " // ĐÃ SỬA: Thêm a.appointment_code
                + "DATE_FORMAT(s.slot_date, '%Y-%m-%d') AS slot_date, "
                + "CONCAT(TIME_FORMAT(s.start_time, '%H:%i'), ' - ', TIME_FORMAT(s.end_time, '%H:%i')) AS slot_time_range, "
                + "p.patient_code, p.full_name AS patient_name, p.phone AS patient_phone, "
                + "d.full_name AS doctor_name, "
                + "ms.service_name, a.status, a.payment_status, " // ĐÃ SỬA: Thêm a.payment_status
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
                + "         OR (slot_date = CURRENT_DATE AND end_time < CURRENT_TIME) " // SỬA: `end_time`
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
                .append("       OR (slot_date = CURRENT_DATE AND start_time > CURRENT_TIME) ") // ĐÃ SỬA: `start_time`
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
        // Bỏ điều kiện `a.is_deleted = FALSE` để lấy cả lịch hẹn đã hủy (đã được đánh dấu is_deleted=TRUE)
        String sql = "SELECT "
                + "    a.id, a.status, a.services_id, a.appointment_code, a.payment_status, "
                + "    s.slot_date, s.start_time, s.end_time, "
                + "    d.full_name AS doctor_name, "
                + "    ms.service_name "
                + "FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "LEFT JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.patient_id = ? " // Giữ nguyên patient_id
                + "ORDER BY s.slot_date DESC, s.start_time DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment();

                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(patientId);
                appointment.setServicesId(rs.getInt("services_id"));
                appointment.setAppointmentCode(rs.getString("appointment_code"));
                appointment.setStatus(Appointment.AppointmentStatus.fromString(rs.getString("status")));
                appointment.setPaymentStatus(PaymentStatus.fromString(rs.getString("payment_status")));
                appointment.setDoctorFullName(rs.getString("doctor_name"));
                appointment.setServiceName(rs.getString("service_name"));
                appointment.setAppointmentDate(rs.getDate("slot_date").toString());

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
    // Phương thức này KHÔNG CÒN ĐƯỢC DÙNG trong logic "pre-booking" mới khi bệnh nhân đặt lịch
    // Nó chỉ hữu ích nếu bạn có trường hợp tạo appointment không qua slot trống.
    // Nếu bạn không dùng, có thể xóa nó hoặc bỏ qua.
    public Appointment createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointment (patient_id, slot_id, services_id, status, payment_status, appointment_code) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getSlotId());
            pstmt.setInt(3, appointment.getServicesId());
            pstmt.setString(4, appointment.getStatus().name().toLowerCase());
            pstmt.setString(5, appointment.getPaymentStatus().name().toLowerCase());
            // Cần có appointment_code trong đối tượng Appointment nếu dùng phương thức này
            pstmt.setString(6, appointment.getAppointmentCode() != null ? appointment.getAppointmentCode() : UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getInt(1));
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

    public boolean cancelAppointment(int appointmentId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Lấy thông tin chi tiết lịch hẹn gốc để xác nhận và lấy slot_id
            String getAppointmentDetailsSql = "SELECT patient_id, slot_id, status FROM appointment WHERE id = ? AND is_deleted = FALSE";
            Integer originalPatientId = null;
            int slotId = -1;
            String currentStatus = null;

            try (PreparedStatement psGet = conn.prepareStatement(getAppointmentDetailsSql)) {
                psGet.setInt(1, appointmentId);
                ResultSet rs = psGet.executeQuery();
                if (rs.next()) {
                    originalPatientId = rs.getInt("patient_id");
                    if (rs.wasNull()) { // Xử lý trường hợp patient_id là NULL
                        originalPatientId = null;
                    }
                    slotId = rs.getInt("slot_id");
                    currentStatus = rs.getString("status");
                } else {
                    conn.rollback();
                    LOGGER.warning("Không tìm thấy lịch hẹn hoặc đã bị xóa để hủy: " + appointmentId);
                    throw new SQLException("Lịch hẹn không tìm thấy hoặc không thể hủy.");
                }
            }

            // Đảm bảo đây là lịch hẹn hợp lệ để hủy (phải có bệnh nhân đặt, và đang pending/confirmed)
            if (originalPatientId == null || !("pending".equals(currentStatus) || "confirmed".equals(currentStatus))) {
                conn.rollback();
                LOGGER.warning("Lịch hẹn " + appointmentId + " không ở trạng thái hợp lệ để hủy (patient_id IS NULL hoặc status không phải pending/confirmed).");
                throw new SQLException("Không thể hủy lịch hẹn này vì nó không phải là lịch hẹn của bạn hoặc đã ở trạng thái không thể hủy.");
            }

            // 2. Cập nhật bản ghi lịch hẹn gốc: Đặt status = 'cancelled' và is_deleted = TRUE
            // Bản ghi này sẽ được giữ lại trong DB để hiển thị trong LỊCH SỬ của bệnh nhân đó.
            // (Nó sẽ không còn xuất hiện trong các truy vấn 'active' vì is_deleted=TRUE)
            String updateOriginalSql = "UPDATE appointment SET status = 'cancelled', is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateOriginalSql)) {
                psUpdate.setInt(1, appointmentId);
                int affectedRows = psUpdate.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    LOGGER.severe("Không thể cập nhật lịch hẹn gốc thành 'cancelled': " + appointmentId);
                    throw new SQLException("Không thể hủy lịch hẹn. Vui lòng thử lại.");
                }
            }

            // 3. Kiểm tra xem đã có một bản ghi slot trống (patient_id IS NULL, status='pending', is_deleted=FALSE)
            // cho slot_id này chưa. Điều này ngăn chặn việc tạo ra các bản ghi trống dư thừa.
            String checkEmptySlotSql = "SELECT COUNT(id) FROM appointment WHERE slot_id = ? AND patient_id IS NULL AND status = 'pending' AND is_deleted = FALSE";
            boolean emptySlotExists = false;
            try (PreparedStatement psCheckEmpty = conn.prepareStatement(checkEmptySlotSql)) {
                psCheckEmpty.setInt(1, slotId);
                ResultSet rsCheckEmpty = psCheckEmpty.executeQuery();
                if (rsCheckEmpty.next() && rsCheckEmpty.getInt(1) > 0) {
                    emptySlotExists = true;
                }
            }

            // 4. Nếu chưa có bản ghi slot trống, chèn một bản ghi lịch hẹn mới cho cùng slot_id.
            // Bản ghi này sẽ có patient_id=NULL và status='pending', làm cho slot có sẵn để người khác đặt.
            if (!emptySlotExists) {
                String appointmentCode = generateUniqueAppointmentCode(conn); // Tạo mã lịch hẹn duy nhất mới
                String insertEmptySql = "INSERT INTO appointment (slot_id, patient_id, services_id, status, payment_status, appointment_code, created_at, updated_at, is_deleted) VALUES (?, NULL, NULL, 'pending', 'UNPAID', ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE)";
                try (PreparedStatement psInsertEmpty = conn.prepareStatement(insertEmptySql)) {
                    psInsertEmpty.setInt(1, slotId);
                    psInsertEmpty.setString(2, appointmentCode); // Gán mã mới
                    psInsertEmpty.executeUpdate();
                }
            } else {
                LOGGER.info("Slot " + slotId + " đã có sẵn một lịch hẹn trống. Không cần tạo mới.");
            }

            // 5. Xóa mềm (soft delete) bản ghi liên quan trong bảng 'queue' (nếu có)
            String softDeleteQueueSql = "UPDATE queue SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE appointment_id = ? AND is_deleted = FALSE";
            try (PreparedStatement psDeleteQueue = conn.prepareStatement(softDeleteQueueSql)) {
                psDeleteQueue.setInt(1, appointmentId);
                psDeleteQueue.executeUpdate(); // Không cần kiểm tra affectedRows vì có thể không có hàng đợi
            }

            conn.commit(); // Hoàn tất transaction
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi hủy lịch hẹn: " + e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi rollback transaction", ex);
                }
            }
            throw e; // Ném lại ngoại lệ để controller xử lý
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Đặt lại auto-commit
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi đóng connection", ex);
                }
            }
        }
    }

    public Integer findAvailableSlot(int doctorId, String date, String time) {
        String dateTimeStr = date + " " + time + ":00"; // Format for DATETIME comparison
        // Query to find an available slot that is not deleted, has capacity,
        // and matches the doctor, date, and time.
        // It also checks if the slot is not already fully booked by 'pending' or 'confirmed' appointments.
        String sql = "SELECT s.id FROM slot s "
                + "WHERE s.doctor_id = ? AND s.start_time = ? AND s.is_available = TRUE AND s.is_deleted = FALSE "
                + "AND s.max_patients > (SELECT COUNT(*) FROM appointment a WHERE a.slot_id = s.id AND a.patient_id IS NOT NULL AND a.status IN ('pending', 'confirmed'))";

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
                    slot.setStartTime(rs.getTime("start_time").toLocalTime());
                    slot.setEndTime(rs.getTime("end_time").toLocalTime());
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
        // SQL update no_show cho appointment CÓ BỆNH NHÂN (patient_id IS NOT NULL), đang pending, và slot quá giờ
        String updateNoShowSql = "UPDATE appointment "
                + "SET status = 'no_show' "
                + "WHERE status IN ('pending') " // Chỉ update nếu đang pending
                + "AND checkin_time IS NULL " // Chưa check-in
                + "AND patient_id IS NOT NULL " // Phải có bệnh nhân (booked)
                + "AND slot_id IN ( " // Lấy slot_id từ slot quá giờ
                + "    SELECT id FROM slot "
                + "    WHERE (slot_date < CURDATE()) " // Slot ngày quá khứ
                + "       OR (slot_date = CURDATE() AND end_time < CURTIME()) " // Hoặc hôm nay nhưng end_time quá giờ hiện tại
                + ")";

        // SQL update expired cho appointment TRỐNG (patient_id IS NULL), đang pending, và slot quá giờ
        String updateExpiredSql = "UPDATE appointment "
                + "SET status = 'expired' "
                + "WHERE status = 'pending' " // Chỉ update nếu đang pending
                + "AND patient_id IS NULL " // Phải trống (no patient)
                + "AND slot_id IN ( " // Lấy slot_id từ slot quá giờ, giống trên
                + "    SELECT id FROM slot "
                + "    WHERE (slot_date < CURDATE()) "
                + "       OR (slot_date = CURDATE() AND end_time < CURTIME()) "
                + ")";

        // SQL update queue liên quan đến skipped nếu appointment no_show
        String updateQueueSql = "UPDATE queue "
                + "SET status = 'skipped' "
                + "WHERE status = 'waiting' "
                + "AND is_deleted = FALSE "
                + "AND appointment_id IN ( "
                + "    SELECT id FROM appointment WHERE status = 'no_show' " // Chỉ queue của appointment no_show
                + ")";

        // Mở connection từ DBContext
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);  // Bắt đầu transaction để đảm bảo atomic (nếu error, rollback hết)

            // Thực thi update no_show
            try (PreparedStatement psNoShow = conn.prepareStatement(updateNoShowSql)) {
                int updatedNoShow = psNoShow.executeUpdate();  // Thực thi SQL, trả về số row update
                LOGGER.info("Đã cập nhật " + updatedNoShow + " appointment có bệnh nhân → no_show");  // Log kết quả
            }

            // Thực thi update expired
            try (PreparedStatement psExpired = conn.prepareStatement(updateExpiredSql)) {
                int updatedExpired = psExpired.executeUpdate();  // Thực thi SQL
                LOGGER.info("Đã cập nhật " + updatedExpired + " appointment trống → expired");  // Log
            }

            // Thực thi update queue
            try (PreparedStatement psQueue = conn.prepareStatement(updateQueueSql)) {
                int updatedQueues = psQueue.executeUpdate();  // Thực thi SQL
                LOGGER.info("Đã cập nhật " + updatedQueues + " queue → skipped");  // Log
            }

            conn.commit();  // Commit transaction nếu tất cả ok

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái no_show/expired/skipped: " + e.getMessage(), e);  // Log error
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

            // BƯỚC 1: Tìm xem có lịch hẹn TRỐNG CÓ SẴN (patient_id IS NULL, status='pending', is_deleted=FALSE) cho slot này.
            // Sử dụng FOR UPDATE để khóa bản ghi tránh tranh chấp
            String findEmptyAppointmentSql = "SELECT id, appointment_code, status, payment_status FROM appointment WHERE slot_id = ? AND patient_id IS NULL AND status = 'pending' AND is_deleted = FALSE LIMIT 1 FOR UPDATE";

            Integer emptyAppointmentId = null;
            String foundAppointmentCode = null;
            String foundStatus = null;
            String foundPaymentStatus = null;

            try (PreparedStatement psFind = conn.prepareStatement(findEmptyAppointmentSql)) {
                psFind.setInt(1, slotId);
                ResultSet rs = psFind.executeQuery();
                if (rs.next()) {
                    emptyAppointmentId = rs.getInt("id");
                    foundAppointmentCode = rs.getString("appointment_code");
                    foundStatus = rs.getString("status");
                    foundPaymentStatus = rs.getString("payment_status");
                }
            }

            if (emptyAppointmentId == null) {
                conn.rollback();
                LOGGER.warning("Không tìm thấy lịch hẹn trống cho slotId: " + slotId + ". Slot có thể đã đầy hoặc không có lịch hẹn trống.");
                return null; // Không có lịch hẹn trống -> slot đã đầy hoặc không được setup đúng cách
            }

            // BƯỚC 2: Cập nhật lịch hẹn trống với thông tin bệnh nhân
            String newStatus = "pending"; // Trạng thái khi bệnh nhân đặt lịch
            String updateSql = "UPDATE appointment SET patient_id = ?, services_id = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, patientId);
                psUpdate.setInt(2, serviceId);
                psUpdate.setString(3, newStatus);
                psUpdate.setInt(4, emptyAppointmentId);
                int affectedRows = psUpdate.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    LOGGER.warning("Không thể cập nhật lịch hẹn ID: " + emptyAppointmentId + ". Có thể do tranh chấp.");
                    return null; // Không thể cập nhật (ví dụ: bị đặt bởi người khác trong tích tắc)
                }
            }

            conn.commit();
            // Sau khi cập nhật thành công, lấy thông tin chi tiết để trả về.
            return getLatestAppointmentByPatientAndSlot(patientId, slotId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đặt lịch hẹn vào slotId: " + slotId + ": " + e.getMessage(), e);
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
                + "    a.id AS appointment_id, a.status, a.services_id, a.appointment_code, a.payment_status, "
                + "    s.slot_date, s.start_time, s.end_time, "
                + "    d.full_name AS doctor_name, "
                + "    ms.service_name "
                + "FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE a.patient_id = ? AND a.slot_id = ? AND a.is_deleted = FALSE "
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
                    appointment.setAppointmentCode(rs.getString("appointment_code"));
                    appointment.setStatus(Appointment.AppointmentStatus.fromString(rs.getString("status")));
                    appointment.setPaymentStatus(PaymentStatus.fromString(rs.getString("payment_status")));
                    appointment.setDoctorFullName(rs.getString("doctor_name"));
                    appointment.setServiceName(rs.getString("service_name"));
                    appointment.setAppointmentDate(rs.getDate("slot_date").toString());

                    LocalTime startTime = rs.getTime("start_time").toLocalTime();
                    LocalTime endTime = rs.getTime("end_time").toLocalTime();

                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    String timeRange = startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);

                    appointment.setAppointmentTime(timeRange);

                    return appointment;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy chi tiết lịch hẹn cho patientId " + patientId + " và slotId " + slotId, e);
        }
        return null;
    }

    // Kiểm tra xem bệnh nhân đã check-in chưa
    public boolean isCheckedIn(int appointmentId) throws SQLException {
        String sql = "SELECT checkin_time FROM appointment WHERE id = ? AND checkin_time IS NOT NULL";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Trả về true nếu có checkin_time
        }
    }

    
    public Appointment updateEmptyAppointment(int patientId, int serviceId, int slotId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // CẬP NHẬT: Lấy thông tin slot để biết ngày của slot đó
            Slot slot = null;
            String getSlotSql = "SELECT slot_date FROM slot WHERE id = ? AND is_deleted = FALSE";
            try (PreparedStatement psGetSlot = conn.prepareStatement(getSlotSql)) {
                psGetSlot.setInt(1, slotId);
                ResultSet rsSlot = psGetSlot.executeQuery();
                if (rsSlot.next()) {
                    slot = new Slot();
                    slot.setSlotDate(rsSlot.getDate("slot_date").toLocalDate());
                } else {
                    conn.rollback();
                    throw new SQLException("Slot không tồn tại hoặc đã bị xóa.");
                }
            }

            // THAY ĐỔI CHÍNH: Kiểm tra giới hạn số lượng lịch hẹn TRONG CÙNG NGÀY
            // Sử dụng MAX_APPOINTMENTS_PER_DAY và countAppointmentsInDateByPatient
            int currentAppointmentsForThisDay = countAppointmentsInDateByPatient(patientId, slot.getSlotDate());
            if (currentAppointmentsForThisDay >= MAX_APPOINTMENTS_PER_DAY) {
                conn.rollback();
                LOGGER.warning("Bệnh nhân " + patientId + " đã đạt giới hạn " + MAX_APPOINTMENTS_PER_DAY + " lịch hẹn trong ngày " + slot.getSlotDate() + ". Không thể đặt thêm.");
                throw new SQLException("Bạn chỉ có thể đặt tối đa " + MAX_APPOINTMENTS_PER_DAY + " lịch hẹn trong một ngày. Vui lòng chọn ngày khác hoặc hủy lịch hẹn cũ.");
            }

            // ... (Các phần còn lại của phương thức updateEmptyAppointment vẫn giữ nguyên)
            // Cụ thể, phần tìm findEmptyAppointmentSql và cập nhật patient_id, services_id, status
            // cho bản ghi emptyAppointmentId vẫn sẽ được thực hiện như hiện tại.

            String findEmptyAppointmentSql = "SELECT id, appointment_code, status, payment_status FROM appointment WHERE slot_id = ? AND patient_id IS NULL AND status = 'pending' AND is_deleted = FALSE LIMIT 1 FOR UPDATE";
            Integer emptyAppointmentId = null;
            String foundAppointmentCode = null;
            String foundStatus = null;
            String foundPaymentStatus = null;

            try (PreparedStatement psFind = conn.prepareStatement(findEmptyAppointmentSql)) {
                psFind.setInt(1, slotId);
                ResultSet rs = psFind.executeQuery();
                if (rs.next()) {
                    emptyAppointmentId = rs.getInt("id");
                    foundAppointmentCode = rs.getString("appointment_code");
                    foundStatus = rs.getString("status");
                    foundPaymentStatus = rs.getString("payment_status");
                }
            }

            if (emptyAppointmentId == null) {
                conn.rollback();
                LOGGER.warning("Không tìm thấy lịch hẹn trống cho slotId: " + slotId + ". Slot có thể đã đầy hoặc không có lịch hẹn trống.");
                throw new SQLException("Không còn lịch hẹn trống phù hợp cho bạn trong khung giờ này.");
            }

            String newStatus = "pending";
            String updateSql = "UPDATE appointment SET patient_id = ?, services_id = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, patientId);
                psUpdate.setInt(2, serviceId);
                psUpdate.setString(3, newStatus);
                psUpdate.setInt(4, emptyAppointmentId);
                int affectedRows = psUpdate.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    LOGGER.warning("Không thể cập nhật lịch hẹn ID: " + emptyAppointmentId + ". Có thể do tranh chấp.");
                    throw new SQLException("Không thể hoàn tất việc đặt lịch. Vui lòng thử lại.");
                }
            }

            conn.commit();
            return getLatestAppointmentByPatientAndSlot(patientId, slotId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đặt lịch hẹn vào slotId: " + slotId + " và patientId: " + patientId + ": " + e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi rollback transaction", ex);
                }
            }
            throw e;
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
    }

    public int countUpcomingAppointmentsForPatient(int patientId) throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(a.id) "
                + "FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE a.patient_id = ? "
                + "AND a.is_deleted = FALSE " // Lịch hẹn không bị xóa mềm
                + "AND a.status IN ('pending', 'confirmed') " // Chỉ đếm các trạng thái chờ hoặc đã xác nhận
                + "AND ( "
                + "    s.slot_date > CURDATE() " // Ngày slot là trong tương lai
                + "    OR (s.slot_date = CURDATE() AND s.start_time >= CURTIME()) " // Hoặc là hôm nay VÀ giờ bắt đầu slot CHƯA TỚI HOẶC ĐANG TỚI
                + ")";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm số lịch hẹn sắp tới của bệnh nhân: " + e.getMessage(), e);
            throw e;
        }
        return count;
    }

    private String generateUniqueAppointmentCode(Connection conn) throws SQLException {
        String code;
        do {
            code = "APP" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (!isAppointmentCodeUnique(conn, code));
        return code;
    }

    // Phương thức kiểm tra tính duy nhất của mã lịch hẹn
    private boolean isAppointmentCodeUnique(Connection conn, String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE appointment_code = ?";
        // Sử dụng một kết nối riêng hoặc đảm bảo rằng kết nối được truyền vào là giao dịch an toàn.
        // Trong trường hợp này, vì generateUniqueAppointmentCode được gọi bên trong một giao dịch
        // lớn hơn, bạn nên đảm bảo conn này là một phần của giao dịch đó hoặc dùng conn truyền vào.
        // Đã sửa lại để sử dụng conn truyền vào, nếu không, tạo một conn mới độc lập (ít hiệu quả hơn).
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}
