package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Appointment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime; // Cần import LocalTime cho LocalTime.from()
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger; // Để có thể log lỗi nếu cần

public class AppointmentDAO {
    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());
    private Connection connection;

    public AppointmentDAO(Connection connection) {
        this.connection = connection;
    }

    // Hàm ánh xạ ResultSet sang đối tượng Appointment (ĐÃ CẬP NHẬT để ánh xạ các trường JOIN)
    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {
        Appointment app = new Appointment();
        app.setId(rs.getInt("id"));
        app.setAppointmentCode(rs.getString("appointment_code"));
        app.setPatientId(rs.getInt("patient_id"));

        // Xử lý các trường có thể NULL (doctor_id, slot_id)
        if (rs.getObject("doctor_id") != null) {
            app.setDoctorId(rs.getInt("doctor_id"));
        } else {
            app.setDoctorId(null);
        }
        if (rs.getObject("slot_id") != null) {
            app.setSlotId(rs.getInt("slot_id"));
        } else {
            app.setSlotId(null);
        }

        app.setStatus(rs.getString("status"));
        app.setCreatedBy(rs.getString("created_by"));
        
        // Chuyển đổi Timestamp từ DB sang LocalDateTime
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            app.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        app.setUpdatedBy(rs.getString("updated_by"));
        Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
        if (updatedAtTimestamp != null) {
            app.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
        }
        app.setIsDeleted(rs.getBoolean("is_deleted"));

        // --- ÁNH XẠ CÁC TRƯỜNG BỔ SUNG TỪ KẾT QUẢ JOIN ---
        // Sử dụng khối try-catch để an toàn hơn nếu một cột không tồn tại trong ResultSet
        // (ví dụ: nếu bạn quên join bảng hoặc tên cột sai)
        try {
            app.setPatientName(rs.getString("patient_full_name"));
        } catch (SQLException e) { LOGGER.log(Level.FINE, "Cột 'patient_full_name' không tìm thấy.", e); }
        try {
            app.setPatientPhoneNumber(rs.getString("patient_phone"));
        } catch (SQLException e) { LOGGER.log(Level.FINE, "Cột 'patient_phone' không tìm thấy.", e); }
        try {
            app.setPatientAddress(rs.getString("patient_address"));
        } catch (SQLException e) { LOGGER.log(Level.FINE, "Cột 'patient_address' không tìm thấy.", e); }
        
        try {
            app.setDoctorName(rs.getString("doctor_full_name"));
        } catch (SQLException e) { LOGGER.log(Level.FINE, "Cột 'doctor_full_name' không tìm thấy.", e); }

        // service_name: Cần đảm bảo cột này được trả về từ truy vấn JOIN nếu bạn sử dụng nó.
        // Hiện tại, schema không rõ nguồn gốc của service_name.
        try {
            app.setServiceName(rs.getString("service_name")); // Ví dụ: lấy từ một bảng 'services' được join
        } catch (SQLException e) { LOGGER.log(Level.FINE, "Cột 'service_name' không tìm thấy (cần JOIN bảng services).", e); }
        
        // appointmentDate và appointmentTime: Lấy từ slot_start_date
        try {
            Timestamp slotStartTimestamp = rs.getTimestamp("slot_start_date");
            if (slotStartTimestamp != null) {
                LocalDateTime slotStartDateTime = slotStartTimestamp.toLocalDateTime();
                app.setAppointmentDate(slotStartDateTime); // Gán toàn bộ LocalDateTime cho ngày và giờ
                app.setAppointmentTime(LocalTime.from(slotStartDateTime)); // Lấy chỉ phần giờ
            }
        } catch (SQLException e) { LOGGER.log(Level.FINE, "Cột 'slot_start_date' không tìm thấy.", e); }
        
        return app;
    }

    // ✅ Hàm thêm lịch hẹn mới
    public boolean addAppointment(Appointment app) throws SQLException {
        String sql = "INSERT INTO appointments " +
                      "(appointment_code, patient_id, doctor_id, slot_id, status, created_by, updated_by, is_deleted) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, app.getAppointmentCode());
            ps.setInt(2, app.getPatientId());
            
            if (app.getDoctorId() != null) {
                ps.setInt(3, app.getDoctorId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            if (app.getSlotId() != null) {
                ps.setInt(4, app.getSlotId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setString(5, app.getStatus());
            ps.setString(6, app.getCreatedBy());
            ps.setString(7, app.getUpdatedBy());
            ps.setBoolean(8, app.isIsDeleted());

            return ps.executeUpdate() > 0;
        }
    }
    
    // ✅ Hàm cập nhật lịch hẹn
    public boolean updateAppointment(Appointment app) throws SQLException {
        String sql = "UPDATE appointments SET " +
                      "appointment_code = ?, patient_id = ?, doctor_id = ?, slot_id = ?, status = ?, " +
                      "updated_by = ?, updated_at = CURRENT_TIMESTAMP, is_deleted = ? " +
                      "WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, app.getAppointmentCode());
            ps.setInt(2, app.getPatientId());
            
            if (app.getDoctorId() != null) {
                ps.setInt(3, app.getDoctorId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            if (app.getSlotId() != null) {
                ps.setInt(4, app.getSlotId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setString(5, app.getStatus());
            ps.setString(6, app.getUpdatedBy());
            ps.setBoolean(7, app.isIsDeleted());
            ps.setInt(8, app.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ✅ Hàm xóa mềm (soft delete) lịch hẹn
    public boolean softDeleteAppointment(int appointmentId, String updatedBy) throws SQLException {
        String sql = "UPDATE appointments SET is_deleted = TRUE, updated_by = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, updatedBy);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }
    
    // ✅ Hàm xóa cứng (hard delete) lịch hẹn - nên cẩn thận khi sử dụng
    public boolean hardDeleteAppointment(int appointmentId) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }
    
    // ✅ Hàm lấy lịch hẹn theo ID (Cũng cần JOIN để lấy thông tin chi tiết)
    public Appointment getAppointmentById(int id) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        sqlBuilder.append("a.id, a.appointment_code, a.patient_id, a.doctor_id, a.slot_id, a.status, ");
        sqlBuilder.append("a.created_by, a.created_at, a.updated_by, a.updated_at, a.is_deleted, ");
        sqlBuilder.append("p.full_name AS patient_full_name, p.phone AS patient_phone, p.address AS patient_address, ");
        sqlBuilder.append("d.full_name AS doctor_full_name, ");
        sqlBuilder.append("s.start_date AS slot_start_date ");
        // Nếu có bảng services và muốn lấy service_name:
        // sqlBuilder.append(", svc.service_name AS service_name "); 
        
        sqlBuilder.append("FROM appointments a ");
        sqlBuilder.append("JOIN patients p ON a.patient_id = p.id ");
        sqlBuilder.append("LEFT JOIN doctors d ON a.doctor_id = d.id ");
        sqlBuilder.append("LEFT JOIN slots s ON a.slot_id = s.id ");
        // Nếu bạn có bảng services và slots có service_id:
        // sqlBuilder.append("LEFT JOIN services svc ON s.service_id = svc.id ");

        sqlBuilder.append("WHERE a.id = ?");

        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppointment(rs);
                }
            }
        }
        return null;
    }

    // ✅ Hàm lấy tổng số lịch hẹn (không bao gồm is_deleted = TRUE nếu bạn muốn)
    // Hàm này không cần JOIN vì chỉ cần COUNT(*) từ bảng appointments
    public int getTotalAppointmentCount(boolean includeDeleted) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments";
        if (!includeDeleted) {
            sql += " WHERE is_deleted = FALSE";
        }
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ✅ Hàm lấy tổng số lịch hẹn theo tiêu chí lọc (Cũng không cần JOIN nếu chỉ COUNT)
    public int getTotalFilteredAppointmentCount(String appointmentCode, Integer patientId, Integer doctorId,
                                                String status, Boolean isDeleted) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM appointments a WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (appointmentCode != null && !appointmentCode.isEmpty()) {
            sqlBuilder.append(" AND a.appointment_code LIKE ?");
            params.add("%" + appointmentCode + "%");
        }
        // Thêm JOIN vào đây nếu bạn muốn lọc theo tên bệnh nhân/bác sĩ thay vì ID
        // Nếu bạn muốn lọc theo patient_id/doctor_id, không cần join
        if (patientId != null) {
            sqlBuilder.append(" AND a.patient_id = ?");
            params.add(patientId);
        }
        if (doctorId != null) {
            sqlBuilder.append(" AND a.doctor_id = ?");
            params.add(doctorId);
        }
        if (status != null && !status.isEmpty()) {
            sqlBuilder.append(" AND a.status = ?");
            params.add(status);
        }
        if (isDeleted != null) {
            sqlBuilder.append(" AND a.is_deleted = ?");
            params.add(isDeleted);
        } else {
            sqlBuilder.append(" AND a.is_deleted = FALSE");
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // ✅ Hàm lấy danh sách lịch hẹn theo phân trang và tiêu chí lọc (ĐÃ CẬP NHẬT VỚI JOIN)
    public List<Appointment> getFilteredAppointmentsByPage(String appointmentCode, Integer patientId, Integer doctorId,
                                                           String status, Boolean isDeleted, int offset, int limit) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        sqlBuilder.append("a.id, a.appointment_code, a.patient_id, a.doctor_id, a.slot_id, a.status, ");
        sqlBuilder.append("a.created_by, a.created_at, a.updated_by, a.updated_at, a.is_deleted, ");
        
        // Thêm các cột từ bảng patients, doctors, slots, và services (nếu có)
        // Dùng ALIAS (AS) để tên cột dễ đọc và tránh xung đột
        sqlBuilder.append("p.full_name AS patient_full_name, p.phone AS patient_phone, p.address AS patient_address, ");
        sqlBuilder.append("d.full_name AS doctor_full_name, ");
        sqlBuilder.append("s.start_date AS slot_start_date ");
        // Nếu có bảng services và muốn lấy service_name (thay thế hoặc thêm vào nếu cần):
        // sqlBuilder.append(", svc.service_name AS service_name "); 
        
        sqlBuilder.append("FROM appointments a ");
        sqlBuilder.append("JOIN patients p ON a.patient_id = p.id "); // INNER JOIN vì mọi appointment cần có patient
        sqlBuilder.append("LEFT JOIN doctors d ON a.doctor_id = d.id "); // LEFT JOIN vì doctor_id có thể NULL
        sqlBuilder.append("LEFT JOIN slots s ON a.slot_id = s.id "); // LEFT JOIN vì slot_id có thể NULL
        // Nếu bạn có bảng services và slots có service_id:
        // sqlBuilder.append("LEFT JOIN services svc ON s.service_id = svc.id ");

        sqlBuilder.append("WHERE 1=1"); // Bắt đầu điều kiện WHERE

        List<Object> params = new ArrayList<>();

        if (appointmentCode != null && !appointmentCode.isEmpty()) {
            sqlBuilder.append(" AND a.appointment_code LIKE ?");
            params.add("%" + appointmentCode + "%");
        }
        if (patientId != null) {
            sqlBuilder.append(" AND a.patient_id = ?");
            params.add(patientId);
        }
        if (doctorId != null) {
            sqlBuilder.append(" AND a.doctor_id = ?");
            params.add(doctorId);
        }
        if (status != null && !status.isEmpty()) {
            sqlBuilder.append(" AND a.status = ?");
            params.add(status);
        }
        if (isDeleted != null) {
            sqlBuilder.append(" AND a.is_deleted = ?");
            params.add(isDeleted);
        } else {
            sqlBuilder.append(" AND a.is_deleted = FALSE");
        }

        sqlBuilder.append(" ORDER BY a.created_at DESC LIMIT ? OFFSET ?"); // Sắp xếp theo created_at mới nhất

        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            int paramIndex = 1;
            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    // ✅ Hàm xóa nhiều lịch hẹn (soft delete)
    public boolean softDeleteMultipleAppointments(List<Integer> appointmentIds, String updatedBy) throws SQLException {
        if (appointmentIds == null || appointmentIds.isEmpty()) {
            return false;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(appointmentIds.size(), "?"));
        String sql = "UPDATE appointments SET is_deleted = TRUE, updated_by = ?, updated_at = CURRENT_TIMESTAMP WHERE id IN (" + placeholders + ")";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, updatedBy);
            for (int i = 0; i < appointmentIds.size(); i++) {
                ps.setInt(i + 2, appointmentIds.get(i));
            }
            return ps.executeUpdate() > 0;
        }
    }
}