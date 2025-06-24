package com.mycompany.isp490_gr3.dao;

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
import java.util.TreeMap; // Sử dụng TreeMap để đảm bảo thứ tự ngày

public class DAOSchedule {

    // Phương thức getSchedulesByDoctorIdForNext7Days - ĐÃ SỬA DÙNG BẢNG SLOT
    // Logic đã được cập nhật để trả về List<Schedule> chứa List<SlotDetailDTO>
    public List<Schedule> getSchedulesByDoctorIdForNext7Days(int doctorId, LocalDate startDate) {
        // Sử dụng TreeMap để đảm bảo các ngày được sắp xếp theo thứ tự
        Map<LocalDate, Schedule> schedulesMap = new TreeMap<>(); 

        // Khởi tạo các đối tượng Schedule rỗng cho tất cả các ngày trong phạm vi
        // Điều này đảm bảo rằng tất cả 60 ngày sẽ được hiển thị trên giao diện,
        // ngay cả khi không có slot nào cho ngày đó.
        LocalDate endDate = startDate.plusDays(59); // Phạm vi 60 ngày
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
                     "DATE(s.start_time) AS working_date, " + // Lấy ngày từ DATETIME
                     "TIME(s.start_time) AS start_time, " +   // Lấy giờ từ DATETIME
                     "TIME(s.end_time) AS end_time, " +       // Lấy giờ từ DATETIME
                     "s.max_patients, " + // Lấy max_patients từ bảng slot
                     "COUNT(a.id) AS booked_patients " + // Đếm số lượng đặt lịch
                     "FROM slot s " +
                     "LEFT JOIN appointment a ON s.id = a.slot_id AND a.status IN ('pending', 'confirmed') " + // Chỉ đếm các lịch hẹn đang chờ/đã xác nhận
                     "WHERE s.doctor_id = ? AND DATE(s.start_time) BETWEEN ? AND ? " +
                     "AND s.is_deleted = FALSE " + // Chỉ lấy các slot chưa bị xóa
                     "GROUP BY s.id, s.start_time, s.end_time, s.max_patients " + // Group by các cột để COUNT đúng
                     "ORDER BY DATE(s.start_time), TIME(s.start_time)";

        try (Connection conn = new DBContext().getConnection(); // Đảm bảo DBContext().getConnection() trả về kết nối hợp lệ
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(startDate));
            ps.setDate(3, java.sql.Date.valueOf(endDate)); 

            // DEBUGGING: In ra câu truy vấn SQL và các tham số để kiểm tra
            System.out.println("DAOSchedule.getSchedulesByDoctorIdForNext7Days: Thực thi SQL: " + sql);
            System.out.println("DAOSchedule.getSchedulesByDoctorIdForNext7Days: Parameters: doctorId=" + doctorId + ", startDate=" + startDate + ", endDate=" + endDate);

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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("DAOSchedule: Lỗi khi lấy lịch trình cho 60 ngày tới: " + e.getMessage());
        }

        // Chuyển đổi Map thành List<Schedule> cuối cùng
        List<Schedule> finalSchedules = new ArrayList<>(schedulesMap.values());
        System.out.println("DAOSchedule.getSchedulesByDoctorIdForNext7Days: Tổng số Schedules (có slot) trả về: " + finalSchedules.size());
        return finalSchedules;
    }
}
