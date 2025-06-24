package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.Schedule;
import com.mycompany.isp490_gr3.dto.SlotDetailDTO; // Import DTO mới
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

public class DAODoctor {
    private Connection connection;

    public DAODoctor() {
        try {
            connection = new DBContext().getConnection();
            if (connection == null) {
                System.err.println("DAODoctor: Lỗi kết nối database - Kết nối trả về null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("DAODoctor: Lỗi khi khởi tạo kết nối database: " + e.getMessage());
        }
    }

    // Các phương thức khác như getFeaturedDoctors, getAllDoctors, getTotalDoctors, getDoctorById
    // (Giữ nguyên như code hiện tại của bạn)
    public List<Doctor> getFeaturedDoctors(int limit, int offset) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.id, d.full_name, d.gender, d.phone, d.department_id, dpt.name as specializationName " +
                     "FROM doctors d JOIN department dpt ON d.department_id = dpt.id " +
                     "WHERE d.is_featured = TRUE AND d.is_deleted = FALSE " +
                     "LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setFullName(rs.getString("full_name"));
                    doctor.setGender(rs.getInt("gender"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setDepartmentId(rs.getInt("department_id"));
                    doctor.setSpecializationName(rs.getString("specializationName"));
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DAODoctor: Lỗi khi lấy danh sách bác sĩ nổi bật: " + e.getMessage());
        }
        return doctors;
    }

    public List<Doctor> getAllDoctors(String search, int limit, int offset) {
        List<Doctor> doctors = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT d.id, d.full_name, d.gender, d.phone, d.department_id, dpt.name as specializationName ");
        sql.append("FROM doctors d JOIN department dpt ON d.department_id = dpt.id ");
        sql.append("WHERE d.is_deleted = FALSE ");
        if (search != null && !search.isEmpty()) {
            sql.append("AND (d.full_name LIKE ? OR dpt.name LIKE ?) ");
        }
        sql.append("ORDER BY d.full_name ");
        sql.append("LIMIT ? OFFSET ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setFullName(rs.getString("full_name"));
                    doctor.setGender(rs.getInt("gender"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setDepartmentId(rs.getInt("department_id"));
                    doctor.setSpecializationName(rs.getString("specializationName"));
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DAODoctor: Lỗi khi lấy tất cả bác sĩ: " + e.getMessage());
        }
        return doctors;
    }

    public int getTotalDoctors(String search) {
        int total = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM doctors d JOIN department dpt ON d.department_id = dpt.id ");
        sql.append("WHERE d.is_deleted = FALSE ");
        if (search != null && !search.isEmpty()) {
            sql.append("AND (d.full_name LIKE ? OR dpt.name LIKE ?) ");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (search != null && !search.isEmpty()) {
                ps.setString(1, "%" + search + "%");
                ps.setString(2, "%" + search + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DAODoctor: Lỗi khi lấy tổng số bác sĩ: " + e.getMessage());
        }
        return total;
    }

    public Doctor getDoctorById(int id) {
        Doctor doctor = null;
        String sql = "SELECT d.id, d.full_name, d.gender, d.phone, d.department_id, " +
                     "dpt.name as specializationName " +
                     "FROM doctors d JOIN department dpt ON d.department_id = dpt.id " +
                     "WHERE d.id = ? AND d.is_deleted = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setFullName(rs.getString("full_name"));
                    doctor.setGender(rs.getInt("gender"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setDepartmentId(rs.getInt("department_id"));
                    doctor.setSpecializationName(rs.getString("specializationName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DAODoctor: Lỗi khi lấy bác sĩ theo ID: " + e.getMessage());
        }
        return doctor;
    }

    public List<Schedule> getDoctorSchedules(int doctorId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Schedule> schedulesMap = new TreeMap<>(); 
        
        // Khởi tạo các đối tượng Schedule rỗng cho tất cả các ngày trong phạm vi
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            Schedule emptySchedule = new Schedule();
            emptySchedule.setDoctorId(doctorId);
            emptySchedule.setWorkingDate(d);
            emptySchedule.setAvailableSlotDetails(new ArrayList<>()); // Đảm bảo danh sách slot rỗng
            schedulesMap.put(d, emptySchedule);
        }

        // SQL truy vấn từ bảng 'slot' và đếm số lượng lịch hẹn cho mỗi slot
        String sql = "SELECT " +
                     "s.id AS slot_id, " +
                     "DATE(s.start_time) AS working_date, " +
                     "TIME(s.start_time) AS start_time, " +
                     "TIME(s.end_time) AS end_time, " +
                     "s.max_patients, " + // Lấy max_patients từ bảng slot
                     "COUNT(a.id) AS booked_patients " + // Đếm số lượng đặt lịch
                     "FROM slot s " +
                     "LEFT JOIN appointment a ON s.id = a.slot_id AND a.status IN ('pending', 'confirmed') " + // Chỉ đếm các lịch hẹn đang chờ/đã xác nhận
                     "WHERE s.doctor_id = ? AND DATE(s.start_time) BETWEEN ? AND ? " +
                     "AND s.is_deleted = FALSE " + // Chỉ lấy các slot chưa bị xóa
                     "GROUP BY s.id, s.start_time, s.end_time, s.max_patients " + // Group by các cột để COUNT đúng
                     "ORDER BY DATE(s.start_time), TIME(s.start_time)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setObject(2, startDate);
            ps.setObject(3, endDate);

            System.out.println("DAODoctor.getDoctorSchedules: Thực thi SQL: " + sql);
            System.out.println("DAODoctor.getDoctorSchedules: Parameters: doctorId=" + doctorId + ", startDate=" + startDate + ", endDate=" + endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int slotId = rs.getInt("slot_id");
                    LocalDate workingDate = rs.getObject("working_date", LocalDate.class);
                    LocalTime startTime = rs.getObject("start_time", LocalTime.class);
                    LocalTime endTime = rs.getObject("end_time", LocalTime.class);
                    int maxPatients = rs.getInt("max_patients");
                    int bookedPatients = rs.getInt("booked_patients"); // Lấy số lượng đã đặt

                    // Tạo đối tượng SlotDetailDTO
                    SlotDetailDTO slotDetail = new SlotDetailDTO(slotId, startTime, endTime, maxPatients, bookedPatients);

                    // Thêm SlotDetailDTO vào Schedule của ngày tương ứng
                    if (schedulesMap.containsKey(workingDate)) {
                        schedulesMap.get(workingDate).getAvailableSlotDetails().add(slotDetail);
                        System.out.println("  => Thêm SlotDetailDTO: " + slotDetail + " vào schedule cho ngày: " + workingDate);
                    } else {
                        System.err.println("Cảnh báo: Ngày " + workingDate + " không có trong schedulesMap. Slot bị bỏ qua.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("DAODoctor: Lỗi khi lấy lịch trình bác sĩ: " + e.getMessage());
        }
        
        List<Schedule> finalSchedules = new ArrayList<>(schedulesMap.values());
        System.out.println("DAODoctor.getDoctorSchedules: Tổng số Schedules (có slot) trả về: " + finalSchedules.size());
        return finalSchedules;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("DAODoctor: Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
}
