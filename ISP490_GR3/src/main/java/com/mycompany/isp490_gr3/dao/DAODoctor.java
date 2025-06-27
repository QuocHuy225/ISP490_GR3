    package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.Schedule;
import com.mycompany.isp490_gr3.dto.SlotDetailDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
            LOGGER.log(Level.SEVERE, "Lỗi khi khởi tạo kết nối database: " + e.getMessage(), e);
        }
    }

    public List<Doctor> getAllDoctors(String search, int limit, int offset) {
        List<Doctor> doctors = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return doctors;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT id, account_id, full_name, gender, phone, is_deleted " +
            "FROM doctors WHERE is_deleted = FALSE "
        );
        if (search != null && !search.isEmpty()) {
            sql.append("AND full_name LIKE ? ");
        }
        sql.append("ORDER BY full_name LIMIT ? OFFSET ?");

        LOGGER.info("Thực thi truy vấn: " + sql);

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
            LOGGER.info(String.format("getAllDoctors: Tìm thấy %d bác sĩ.", doctors.size()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy danh sách bác sĩ: " + e.getMessage(), e);
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

        LOGGER.info("Thực thi truy vấn: " + sql);

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

        String sql = "SELECT id, account_id, full_name, gender, phone, is_deleted " +
                     "FROM doctors WHERE id = ? AND is_deleted = FALSE";

        LOGGER.info("Thực thi truy vấn: " + sql);

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

        String sql = "SELECT id, account_id, full_name, gender, phone, is_deleted " +
                     "FROM doctors WHERE is_deleted = FALSE LIMIT ? OFFSET ?";

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

    public List<Schedule> getDoctorSchedules(int doctorId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Schedule> schedulesMap = new TreeMap<>();

        // Khởi tạo các đối tượng Schedule rỗng cho tất cả các ngày trong phạm vi
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            Schedule emptySchedule = new Schedule();
            emptySchedule.setDoctorId(doctorId);
            emptySchedule.setWorkingDate(d);
            emptySchedule.setAvailableSlotDetails(new ArrayList<>());
            schedulesMap.put(d, emptySchedule);
        }

        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return new ArrayList<>(schedulesMap.values());
        }

        // SQL truy vấn từ bảng 'slot' và đếm số lượng lịch hẹn cho mỗi slot
        String sql = "SELECT s.id AS slot_id, s.slot_date AS working_date, s.start_time, s.end_time, " +
                     "s.max_patients, COUNT(a.id) AS booked_patients " +
                     "FROM slot s " +
                     "LEFT JOIN appointment a ON s.id = a.slot_id AND a.status IN ('pending', 'confirmed') " +
                     "WHERE s.doctor_id = ? AND s.slot_date BETWEEN ? AND ? AND s.is_deleted = FALSE " +
                     "GROUP BY s.id, s.slot_date, s.start_time, s.end_time, s.max_patients " +
                     "ORDER BY s.slot_date, s.start_time";

        LOGGER.info("Thực thi truy vấn: " + sql);
        LOGGER.info(String.format("Parameters: doctorId=%d, startDate=%s, endDate=%s", doctorId, startDate, endDate));

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setObject(2, startDate);
            ps.setObject(3, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int slotId = rs.getInt("slot_id");
                    LocalDate workingDate = rs.getObject("working_date", LocalDate.class);
                    LocalTime startTime = rs.getObject("start_time", LocalTime.class);
                    LocalTime endTime = rs.getObject("end_time", LocalTime.class);
                    int maxPatients = rs.getInt("max_patients");
                    int bookedPatients = rs.getInt("booked_patients");

                    // Tạo đối tượng SlotDetailDTO
                    SlotDetailDTO slotDetail = new SlotDetailDTO(slotId, startTime, endTime, maxPatients, bookedPatients);

                    // Thêm SlotDetailDTO vào Schedule của ngày tương ứng
                    if (schedulesMap.containsKey(workingDate)) {
                        schedulesMap.get(workingDate).getAvailableSlotDetails().add(slotDetail);
                        LOGGER.info(String.format("Thêm SlotDetailDTO: %s vào schedule cho ngày: %s", slotDetail, workingDate));
                    } else {
                        LOGGER.warning(String.format("Ngày %s không có trong schedulesMap. Slot bị bỏ qua.", workingDate));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy lịch trình bác sĩ: " + e.getMessage(), e);
        }

        List<Schedule> finalSchedules = new ArrayList<>(schedulesMap.values());
        LOGGER.info(String.format("getDoctorSchedules: Tổng số Schedules trả về: %d", finalSchedules.size()));
        return finalSchedules;
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

    // Hàm mới: Lấy tất cả bác sĩ không phân trang
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query.");
            return doctors;
        }

        String sql = "SELECT id, account_id, full_name, gender, phone, is_deleted " +
                     "FROM doctors WHERE is_deleted = FALSE ORDER BY full_name";

        LOGGER.info("Thực thi truy vấn: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
            LOGGER.info(String.format("getAllDoctors: Tìm thấy %d bác sĩ.", doctors.size()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy danh sách bác sĩ: " + e.getMessage(), e);
        }
        return doctors;
    }
    
    public static void main(String[] args) {
        DAODoctor daoDoctor = new DAODoctor();
        try {
            List<Doctor> doctors = daoDoctor.getAllDoctors();
            if (doctors.isEmpty()) {
                LOGGER.warning("Không tìm thấy bác sĩ nào.");
            } else {
                LOGGER.info(String.format("Tìm thấy %d bác sĩ:", doctors.size()));
                for (Doctor doctor : doctors) {
                    LOGGER.info(doctor.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi test getAllDoctors: " + e.getMessage(), e);
        } finally {
            daoDoctor.closeConnection();
        }
    }
}