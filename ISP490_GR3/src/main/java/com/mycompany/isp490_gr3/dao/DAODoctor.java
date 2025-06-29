package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAODoctor {

    private static final Logger LOGGER = Logger.getLogger(DAODoctor.class.getName());
    private Connection connection;

    public DAODoctor() {
        try {
            connection = DBContext.getConnection();
            if (connection == null) {
                LOGGER.severe("Kết nối database trả về null.");
            } else {
                LOGGER.info("Kết nối database thành công trong DAODoctor.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi khởi tạo kết nối database trong DAODoctor: " + e.getMessage(), e);
        }
    }

    public List<Doctor> getAllDoctors(String search, int limit, int offset) {
        List<Doctor> doctors = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return doctors;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT id, account_id, full_name, gender, phone, is_deleted "
                + "FROM doctors WHERE is_deleted = FALSE "
        );
        if (search != null && !search.isEmpty()) {
            sql.append("AND full_name LIKE ? ");
        }
        sql.append("ORDER BY full_name LIMIT ? OFFSET ?");

        LOGGER.info("Thực thi truy vấn: " + sql.toString());

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setAccountId(rs.getString("account_id"));
                    doctor.setFullName(rs.getString("full_name"));
                    doctor.setGender(rs.getInt("gender"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setDeleted(rs.getBoolean("is_deleted"));
                    doctors.add(doctor);
                    LOGGER.info(String.format("Tìm thấy bác sĩ: ID=%d, FullName=%s", doctor.getId(), doctor.getFullName()));
                }
            }
            LOGGER.info(String.format("getAllDoctors (có search, limit, offset): Tìm thấy %d bác sĩ.", doctors.size()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy danh sách bác sĩ (có search, limit, offset): " + e.getMessage(), e);
        }
        return doctors;
    }

    public int getTotalDoctors(String search) {
        int total = 0;
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return total;
        }

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM doctors WHERE is_deleted = FALSE ");
        if (search != null && !search.isEmpty()) {
            sql.append("AND full_name LIKE ? ");
        }

        LOGGER.info("Thực thi truy vấn: " + sql.toString());

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (search != null && !search.isEmpty()) {
                ps.setString(1, "%" + search + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
            LOGGER.info(String.format("getTotalDoctors: Tổng số bác sĩ: %d", total));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy tổng số bác sĩ: " + e.getMessage(), e);
        }
        return total;
    }

    public Doctor getDoctorById(int id) {
        Doctor doctor = null;
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return null;
        }

        String sql = "SELECT id, account_id, full_name, gender, phone, is_deleted "
                + "FROM doctors WHERE id = ? AND is_deleted = FALSE";

        LOGGER.info("Thực thi truy vấn: " + sql + " với ID: " + id);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setAccountId(rs.getString("account_id"));
                    doctor.setFullName(rs.getString("full_name"));
                    doctor.setGender(rs.getInt("gender"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setDeleted(rs.getBoolean("is_deleted"));
                    LOGGER.info(String.format("Tìm thấy bác sĩ: ID=%d, FullName=%s", doctor.getId(), doctor.getFullName()));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy bác sĩ theo ID: " + e.getMessage(), e);
        }
        return doctor;
    }

    public List<Doctor> getFeaturedDoctors(int limit, int offset) {
        List<Doctor> doctors = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return doctors;
        }

        String sql = "SELECT id, account_id, full_name, gender, phone, is_deleted "
                + "FROM doctors WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT ? OFFSET ?";

        LOGGER.info("Thực thi truy vấn: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setAccountId(rs.getString("account_id"));
                    doctor.setFullName(rs.getString("full_name"));
                    doctor.setGender(rs.getInt("gender"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setDeleted(rs.getBoolean("is_deleted"));
                    doctors.add(doctor);
                    LOGGER.info(String.format("Tìm thấy bác sĩ nổi bật: ID=%d, FullName=%s", doctor.getId(), doctor.getFullName()));
                }
            }
            LOGGER.info(String.format("getFeaturedDoctors: Tìm thấy %d bác sĩ.", doctors.size()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy danh sách bác sĩ nổi bật: " + e.getMessage(), e);
        }
        return doctors;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Đóng kết nối database thành công.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi đóng kết nối: " + e.getMessage(), e);
            }
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return doctors;
        }

        String sql = "SELECT id, account_id, full_name, gender, phone, is_deleted "
                + "FROM doctors WHERE is_deleted = FALSE ORDER BY full_name";

        LOGGER.info("Thực thi truy vấn: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setAccountId(rs.getString("account_id"));
                doctor.setFullName(rs.getString("full_name"));
                doctor.setGender(rs.getInt("gender"));
                doctor.setPhone(rs.getString("phone"));
                doctor.setDeleted(rs.getBoolean("is_deleted"));
                doctors.add(doctor);
                LOGGER.info(String.format("Tìm thấy bác sĩ: ID=%d, FullName=%s", doctor.getId(), doctor.getFullName()));
            }
            LOGGER.info(String.format("getAllDoctors (không tham số): Tìm thấy %d bác sĩ.", doctors.size()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy danh sách bác sĩ (không tham số): " + e.getMessage(), e);
        }
        return doctors;
    }

    /**
     * Lấy ID nội bộ của bác sĩ dựa trên account_id (từ bảng user). Đây là
     * phương thức quan trọng để ánh xạ từ account_id của người dùng sang
     * doctor_id nội bộ.
     *
     * @param accountId ID tài khoản của bác sĩ (từ bảng user).
     * @return ID nội bộ của bác sĩ (từ bảng doctors) nếu tìm thấy, ngược lại
     * trả về null.
     * @throws SQLException Nếu có lỗi khi truy vấn database.
     */
    public Integer getDoctorIdByAccountId(String accountId) throws SQLException {
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return null;
        }
        String sql = "SELECT id FROM doctors WHERE account_id = ? AND is_deleted = FALSE";
        LOGGER.info("Thực thi truy vấn: " + sql + " với accountId: " + accountId);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int doctorId = rs.getInt("id");
                    LOGGER.info(String.format("Tìm thấy Doctor ID %d cho Account ID %s", doctorId, accountId));
                    return doctorId;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy ID bác sĩ theo account_id: " + e.getMessage(), e);
            throw e;
        }
        LOGGER.info("Không tìm thấy Doctor ID cho Account ID: " + accountId);
        return null;
    }

    /**
     * Lấy cấu hình lịch làm việc chi tiết (dưới dạng JSON string) của một bác
     * sĩ từ cột detailed_schedule_config.
     *
     * @param doctorId ID nội bộ của bác sĩ.
     * @return Chuỗi JSON chứa cấu hình lịch chi tiết, hoặc null nếu không tìm
     * thấy.
     * @throws SQLException nếu có lỗi truy vấn database.
     */
    public String getDoctorDetailedScheduleConfigJson(int doctorId) throws SQLException {
        String configJson = null;
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return null;
        }
        String query = "SELECT detailed_schedule_config FROM doctors WHERE id = ?";
        LOGGER.info("Thực thi truy vấn: " + query + " với doctorId: " + doctorId);
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    configJson = rs.getString("detailed_schedule_config");
                    LOGGER.info(String.format("Tìm thấy cấu hình lịch chi tiết cho doctorId %d: %s", doctorId, configJson));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy cấu hình lịch chi tiết: " + e.getMessage(), e);
            throw e;
        }
        return configJson;
    }

    /**
     * Cập nhật cấu hình lịch làm việc chi tiết (dưới dạng JSON string) cho một
     * bác sĩ vào cột detailed_schedule_config.
     *
     * @param doctorId ID nội bộ của bác sĩ.
     * @param jsonConfig Chuỗi JSON của cấu hình lịch.
     * @throws SQLException nếu có lỗi truy vấn database.
     */
    public boolean updateDoctorDetailedScheduleConfig(int doctorId, String jsonConfig) throws SQLException {
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return false;
        }
        String query = "UPDATE doctors SET detailed_schedule_config = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        LOGGER.info("Thực thi truy vấn: " + query + " với doctorId: " + doctorId + ", configJson: " + jsonConfig);
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, jsonConfig);
            pstmt.setInt(2, doctorId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info(String.format("Cập nhật cấu hình lịch chi tiết thành công cho doctorId %d.", doctorId));
                return true;
            } else {
                LOGGER.warning(String.format("Không tìm thấy doctorId %d để cập nhật cấu hình lịch chi tiết.", doctorId));
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi cập nhật cấu hình lịch chi tiết: " + e.getMessage(), e);
            throw e;
        }
    }

    public static void main(String[] args) {
        // Main method cho mục đích test DAODoctor (không liên quan trực tiếp đến schedule)
        DAODoctor daoDoctor = new DAODoctor();
        try {
            // Test getAllDoctors
            List<Doctor> doctors = daoDoctor.getAllDoctors();
            if (doctors.isEmpty()) {
                LOGGER.warning("Không tìm thấy bác sĩ nào.");
            } else {
                LOGGER.info(String.format("Tìm thấy %d bác sĩ:", doctors.size()));
                for (Doctor doctor : doctors) {
                    LOGGER.info(doctor.toString());
                }
            }

            // Thử nghiệm getDoctorIdByAccountId
            String testAccountId = "user_doctor_001"; // Thay bằng một account_id có thật trong DB của bạn
            Integer doctorId = daoDoctor.getDoctorIdByAccountId(testAccountId);
            if (doctorId != null) {
                LOGGER.info("Doctor ID cho " + testAccountId + " là: " + doctorId);

                // --- Thử nghiệm các phương thức mới cho detailed_schedule_config ---
                // 1. Cập nhật cấu hình
                String sampleConfig = "{\"appointment_duration\":\"30\",\"schedule_period\":\"future\",\"weekly_schedule\":{\"monday\":[{\"start\":\"09:00\",\"end\":\"12:00\"}],\"tuesday\":[]}}";
                boolean updated = daoDoctor.updateDoctorDetailedScheduleConfig(doctorId, sampleConfig);
                LOGGER.info("Đã cập nhật cấu hình mẫu cho doctorId: " + doctorId + ", Status: " + updated);

                // 2. Lấy cấu hình
                String fetchedConfig = daoDoctor.getDoctorDetailedScheduleConfigJson(doctorId);
                LOGGER.info("Cấu hình đã lấy cho doctorId " + doctorId + ": " + fetchedConfig);
                // --- Kết thúc thử nghiệm các phương thức mới ---

            } else {
                LOGGER.warning("Không tìm thấy bác sĩ với account_id: " + testAccountId);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi test DAODoctor: " + e.getMessage(), e);
        } finally {
            daoDoctor.closeConnection();
        }
    }
}
