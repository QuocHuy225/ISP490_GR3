package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.DoctorSchedule; 
import com.mycompany.isp490_gr3.model.Schedule; // Import Schedule model
import com.mycompany.isp490_gr3.dto.SlotDetailDTO; // Import SlotDetailDTO
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime; // Import LocalTime
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap; 
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DAODoctorSchedule {
    private static final Logger LOGGER = Logger.getLogger(DAODoctorSchedule.class.getName()); 
    private Connection connection;

    public DAODoctorSchedule() {
        try {
            connection = DBContext.getConnection();
            if (connection == null) {
                LOGGER.severe("Kết nối database trả về null trong DAODoctorSchedule."); 
            } else {
                LOGGER.info("Kết nối database thành công trong DAODoctorSchedule."); 
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi khởi tạo kết nối database trong DAODoctorSchedule: " + e.getMessage(), e); 
        }
    }

    /**
     * Lấy các ngày làm việc đã được ghi nhận cho một bác sĩ.
     * Chỉ lấy các ngày làm việc từ ngày hiện tại trở đi và đang active.
     * @param doctorId ID nội bộ của bác sĩ.
     * @return Danh sách các chuỗi ngày làm việc (YYYY-MM-DD).
     */
    public List<String> getWorkingDatesByDoctorId(int doctorId) {
        List<String> dates = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query for working dates.");
            return dates;
        }
        try (PreparedStatement stmt = connection.prepareStatement(
                     "SELECT DISTINCT DATE_FORMAT(work_date, '%Y-%m-%d') AS work_date " +
                     "FROM doctor_schedule WHERE doctor_id = ? AND work_date >= CURDATE() AND is_active = TRUE " +
                     "ORDER BY work_date")) {
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dates.add(rs.getString("work_date"));
            }
            LOGGER.info(String.format("Tìm thấy %d ngày làm việc cho bác sĩ ID %d.", dates.size(), doctorId));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi truy vấn ngày làm việc: " + e.getMessage(), e);
        }
        return dates;
    }
    
    /**
     * Lấy tất cả các bản ghi lịch làm việc (entries) cho một bác sĩ cụ thể, chỉ bao gồm các bản ghi active.
     * @param doctorId ID nội bộ của bác sĩ.
     * @return Danh sách các đối tượng DoctorSchedule.
     * @throws SQLException Nếu có lỗi khi truy vấn database.
     */
    public List<DoctorSchedule> getDoctorScheduleEntries(int doctorId) throws SQLException {
        List<DoctorSchedule> schedule = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute query for DoctorSchedule entries.");
            return schedule;
        }
        String sql = "SELECT id, doctor_id, work_date, is_active FROM doctor_schedule WHERE doctor_id = ? AND is_active = TRUE";
        LOGGER.info("Thực thi truy vấn: " + sql + " với doctorId: " + doctorId);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DoctorSchedule entry = new DoctorSchedule(
                        rs.getInt("id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("work_date"), 
                        rs.getBoolean("is_active")
                    );
                    schedule.add(entry);
                    LOGGER.info(String.format("Tìm thấy lịch làm việc: ID=%d, Ngày=%s", entry.getId(), entry.getWorkDate()));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi lấy lịch làm việc bác sĩ (entries): " + e.getMessage(), e);
            throw e; 
        }
        LOGGER.info(String.format("getDoctorScheduleEntries: Tìm thấy %d lịch làm việc cho doctorId %d.", schedule.size(), doctorId));
        return schedule;
    }

    /**
     * Thêm hoặc cập nhật các ngày làm việc cho bác sĩ vào bảng doctor_schedule.
     * Phương thức này sẽ kiểm tra từng ngày: nếu ngày đó đã tồn tại trong doctor_schedule cho bác sĩ này,
     * nó sẽ cập nhật is_active thành TRUE (và updated_at). Nếu chưa tồn tại, nó sẽ thêm mới.
     *
     * @param doctorId ID nội bộ của bác sĩ.
     * @param workDates Danh sách các ngày (LocalDate) để thêm hoặc cập nhật.
     * @return Một Map chứa số lượng các bản ghi đã được thêm ('added_dates') và đã được cập nhật ('updated_dates').
     * @throws SQLException Nếu có lỗi database.
     */
    public Map<String, Integer> addOrUpdateDoctorSchedule(int doctorId, List<LocalDate> workDates) throws SQLException {
        int addedCount = 0;
        int updatedCount = 0;

        String checkExistingSql = "SELECT id FROM doctor_schedule WHERE doctor_id = ? AND work_date = ?";
        String updateSql = "UPDATE doctor_schedule SET is_active = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        String insertSql = "INSERT INTO doctor_schedule (doctor_id, work_date, is_active) VALUES (?, ?, TRUE)";

        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute update for doctor schedule.");
            return Map.of("added_dates", 0, "updated_dates", 0);
        }

        try {
            connection.setAutoCommit(false); 

            for (LocalDate workDate : workDates) {
                try (PreparedStatement checkStmt = connection.prepareStatement(checkExistingSql)) {
                    checkStmt.setInt(1, doctorId);
                    checkStmt.setDate(2, Date.valueOf(workDate)); 
                    LOGGER.info(String.format("Kiểm tra lịch làm việc: DoctorID=%d, Ngày=%s", doctorId, workDate));
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                                updateStmt.setInt(1, rs.getInt("id"));
                                updateStmt.executeUpdate();
                            }
                            updatedCount++;
                            LOGGER.info(String.format("Cập nhật lịch làm việc: DoctorID=%d, Ngày=%s", doctorId, workDate));
                        } else {
                            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                                insertStmt.setInt(1, doctorId);
                                insertStmt.setDate(2, Date.valueOf(workDate)); 
                                insertStmt.executeUpdate();
                            }
                            addedCount++;
                            LOGGER.info(String.format("Thêm mới lịch làm việc: DoctorID=%d, Ngày=%s", doctorId, workDate));
                        }
                    }
                }
            }
            connection.commit(); 
            LOGGER.info(String.format("Transaction hoàn tất. Đã thêm %d, đã cập nhật %d lịch.", addedCount, updatedCount));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLException khi thêm/cập nhật lịch làm việc: " + e.getMessage(), e);
            if (connection != null) {
                try {
                    connection.rollback(); 
                    LOGGER.info("Transaction đã được rollback.");
                } catch (SQLException rollbackErr) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi rollback transaction: " + rollbackErr.getMessage(), rollbackErr);
                }
            }
            throw e; 
        } finally {
            try {
                if (connection != null && !connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                    LOGGER.info("Auto-commit đã được đặt lại về TRUE.");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi reset auto-commit: " + e.getMessage(), e);
            }
        }

        return Map.of("added_dates", addedCount, "updated_dates", updatedCount);
    }

    /**
     * Lấy lịch trình chi tiết của bác sĩ (bao gồm thông tin slot và số lượng bệnh nhân đã đặt).
     * Phương thức này được dùng cho trang chi tiết bác sĩ/đặt lịch của bệnh nhân.
     *
     * @param doctorId ID nội bộ của bác sĩ.
     * @param startDate Ngày bắt đầu của phạm vi lịch.
     * @param endDate Ngày kết thúc của phạm vi lịch.
     * @return Danh sách các đối tượng Schedule, mỗi Schedule đại diện cho lịch trình một ngày.
     */
    public List<Schedule> getDoctorSchedules(int doctorId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Schedule> schedulesMap = new TreeMap<>();      
        
        // Initialize empty Schedule objects for all dates in the range
        // Khởi tạo các đối tượng Schedule rỗng cho tất cả các ngày trong phạm vi
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            Schedule emptySchedule = new Schedule();
            emptySchedule.setDoctorId(doctorId);
            emptySchedule.setWorkingDate(d);
            emptySchedule.setAvailableSlotDetails(new ArrayList<>()); // Ensure empty slot list
            schedulesMap.put(d, emptySchedule);
        }

        // SQL query to retrieve slots from 'slot' table and count appointments for each slot
        // SQL truy vấn từ bảng 'slot' và đếm số lượng lịch hẹn cho mỗi slot
        String sql = "SELECT " +
                     "s.id AS slot_id, " +
                     "DATE(s.start_time) AS working_date, " +
                     "TIME(s.start_time) AS start_time, " +
                     "TIME(s.end_time) AS end_time, " +
                     "s.max_patients, " + 
                     "COUNT(a.id) AS booked_patients " + 
                     "FROM slot s " +
                     "LEFT JOIN appointment a ON s.id = a.slot_id AND a.status IN ('pending', 'confirmed') " + 
                     "WHERE s.doctor_id = ? AND DATE(s.start_time) BETWEEN ? AND ? " +
                     "AND s.is_deleted = FALSE " + 
                     "GROUP BY s.id, s.start_time, s.end_time, s.max_patients " + 
                     "ORDER BY DATE(s.start_time), TIME(s.start_time)";

        if (connection == null) {
            LOGGER.severe("Connection is null, cannot execute getDoctorSchedules query.");
            return new ArrayList<>(schedulesMap.values());
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setObject(2, startDate);
            ps.setObject(3, endDate);

            LOGGER.info("DAODoctorSchedule.getDoctorSchedules: Thực thi SQL: " + sql);
            LOGGER.info("DAODoctorSchedule.getDoctorSchedules: Parameters: doctorId=" + doctorId + ", startDate=" + startDate + ", endDate=" + endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int slotId = rs.getInt("slot_id");
                    LocalDate workingDate = rs.getObject("working_date", LocalDate.class);
                    LocalTime startTime = rs.getObject("start_time", LocalTime.class);
                    LocalTime endTime = rs.getObject("end_time", LocalTime.class);
                    int maxPatients = rs.getInt("max_patients");
                    int bookedPatients = rs.getInt("booked_patients"); 

                    // Create SlotDetailDTO object
                    SlotDetailDTO slotDetail = new SlotDetailDTO(slotId, startTime, endTime, maxPatients, bookedPatients);

                    // Add SlotDetailDTO to the corresponding day's Schedule
                    if (schedulesMap.containsKey(workingDate)) {
                        schedulesMap.get(workingDate).getAvailableSlotDetails().add(slotDetail);
                        LOGGER.info(" => Thêm SlotDetailDTO: " + slotDetail + " vào schedule cho ngày: " + workingDate);
                    } else {
                        LOGGER.warning("Cảnh báo: Ngày " + workingDate + " không có trong schedulesMap. Slot bị bỏ qua.");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DAODoctorSchedule: Lỗi khi lấy lịch trình bác sĩ: " + e.getMessage(), e);
        }
        
        List<Schedule> finalSchedules = new ArrayList<>(schedulesMap.values());
        LOGGER.info("DAODoctorSchedule.getDoctorSchedules: Tổng số Schedules (có slot) trả về: " + finalSchedules.size());
        return finalSchedules;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Đóng kết nối database thành công trong DAODoctorSchedule.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi đóng kết nối trong DAODoctorSchedule: " + e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) {
        // Main method cho mục đích test DAODoctorSchedule
        DAODoctorSchedule daoSchedule = new DAODoctorSchedule();

        int doctorIdToTest = 1; // Thay bằng ID bác sĩ bạn muốn test
        try {
            // Test getWorkingDatesByDoctorId
            List<String> workingDates = daoSchedule.getWorkingDatesByDoctorId(doctorIdToTest);
            if (workingDates.isEmpty()) {
                LOGGER.info("Không có ngày làm việc nào được tìm thấy cho bác sĩ ID: " + doctorIdToTest);
            } else {
                LOGGER.info("Các ngày làm việc của bác sĩ ID " + doctorIdToTest + ":");
                for (String date : workingDates) {
                    LOGGER.info("- " + date);
                }
            }

            // Test getDoctorScheduleEntries (để lấy DoctorSchedule objects)
            List<DoctorSchedule> scheduleEntries = daoSchedule.getDoctorScheduleEntries(doctorIdToTest);
            LOGGER.info("DoctorSchedule entries cho bác sĩ ID " + doctorIdToTest + ": " + scheduleEntries);

            // Test addOrUpdateDoctorSchedule
            List<LocalDate> datesToAdd = new ArrayList<>();
            datesToAdd.add(LocalDate.now().plusDays(7)); 
            datesToAdd.add(LocalDate.now().plusDays(8)); 
            datesToAdd.add(LocalDate.parse("2025-07-01")); // Example specific date
            
            Map<String, Integer> updateResult = daoSchedule.addOrUpdateDoctorSchedule(doctorIdToTest, datesToAdd);
            LOGGER.info("Kết quả thêm/cập nhật lịch: " + updateResult);

            // Lấy lại lịch DoctorSchedule entries để xác nhận
            scheduleEntries = daoSchedule.getDoctorScheduleEntries(doctorIdToTest);
            LOGGER.info("Lịch làm việc mới (entries) cho bác sĩ ID " + doctorIdToTest + ": " + scheduleEntries);

            // Test getDoctorSchedules (để lấy List<Schedule> với SlotDetailDTOs)
            LocalDate testStartDate = LocalDate.now();
            LocalDate testEndDate = testStartDate.plusDays(30);
            List<Schedule> fullSchedules = daoSchedule.getDoctorSchedules(doctorIdToTest, testStartDate, testEndDate);
            LOGGER.info("Lịch trình đầy đủ (Schedule objects) cho bác sĩ ID " + doctorIdToTest + " từ " + testStartDate + " đến " + testEndDate + ": " + fullSchedules);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi test DAODoctorSchedule: " + e.getMessage(), e);
        } finally {
            daoSchedule.closeConnection();
        }
    }
}
