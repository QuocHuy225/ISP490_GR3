package com.mycompany.isp490_gr3.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.isp490_gr3.model.DoctorSchedule;
import com.mycompany.isp490_gr3.model.Schedule;
import com.mycompany.isp490_gr3.dto.SlotDetailDTO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DAODoctorSchedule {
    private static final Logger LOGGER = Logger.getLogger(DAODoctorSchedule.class.getName());
    private Connection connection; // Giữ nguyên cách quản lý connection của bạn

    public DAODoctorSchedule() {
        try {
            // Giả định DBContext.getConnection() trả về một connection đã được khởi tạo
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
     * Tạo hoặc cập nhật lịch trình chi tiết (các ngày làm việc và các slot) cho một bác sĩ
     * dựa trên cấu hình JSON được lưu trữ.
     * Phương thức này sẽ:
     * 1. Lấy cấu hình JSON.
     * 2. Phân tích cấu hình để xác định appointment_duration, weekly_schedule và schedule_period.
     * 3. Xác định các ngày làm việc sẽ được tạo/cập nhật.
     * 4. Thêm/cập nhật các bản ghi trong doctor_schedule (is_active = TRUE).
     * 5. Tạo/cập nhật các slot trong bảng 'slot' cho các ngày đó.
     *
     * @param doctorId ID nội bộ của bác sĩ.
     * @param detailedScheduleConfigJson Chuỗi JSON chứa cấu hình lịch trình chi tiết của bác sĩ.
     * @throws SQLException Nếu có lỗi khi tương tác với database.
     * @throws IOException Nếu có lỗi khi phân tích cú pháp JSON.
     */
    public void generateDetailedDoctorScheduleAndSlots(int doctorId, String detailedScheduleConfigJson) throws SQLException, IOException {
        LOGGER.info(String.format("Bắt đầu tạo lịch và slot cho bác sĩ ID %d với cấu hình: %s", doctorId, detailedScheduleConfigJson));

        if (detailedScheduleConfigJson == null || detailedScheduleConfigJson.trim().isEmpty()) {
            LOGGER.warning(String.format("Không có cấu hình lịch chi tiết cho bác sĩ ID %d. Bỏ qua việc tạo lịch và slot.", doctorId));
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode configNode = objectMapper.readTree(detailedScheduleConfigJson);

        int appointmentDurationMinutes = configNode.has("appointment_duration") ?
                Integer.parseInt(configNode.get("appointment_duration").asText()) : 30; // Mặc định 30 phút
        // String schedulePeriod = configNode.has("schedule_period") ? // Tạm thời không sử dụng trực tiếp ở đây
        //         configNode.get("schedule_period").asText() : "future";

        JsonNode weeklyScheduleNode = configNode.get("weekly_schedule");

        if (weeklyScheduleNode == null || !weeklyScheduleNode.isObject()) {
            LOGGER.warning(String.format("Cấu hình weekly_schedule không hợp lệ hoặc thiếu cho bác sĩ ID %d.", doctorId));
            return;
        }

        LocalDate today = LocalDate.now();
        List<LocalDate> datesToGenerate = new ArrayList<>();
        int numberOfWeeks = 4; // Ví dụ: tạo lịch cho 4 tuần tới

        // Logic để xác định các ngày cần tạo lịch
        for (int i = 0; i < numberOfWeeks; i++) {
            LocalDate currentWeekStart = today.plusWeeks(i).with(DayOfWeek.MONDAY); // Bắt đầu từ thứ Hai của tuần hiện tại/tương lai
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                String dayName = dayOfWeek.toString().toLowerCase();
                if (weeklyScheduleNode.has(dayName)) {
                    LocalDate targetDate = currentWeekStart.with(dayOfWeek);
                    // Chỉ thêm ngày nếu nó là ngày hiện tại hoặc trong tương lai (sau hoặc bằng today)
                    if (!targetDate.isBefore(today)) {
                         datesToGenerate.add(targetDate);
                    }
                }
            }
        }
        
        // Sắp xếp lại danh sách các ngày để xử lý tuần tự và loại bỏ trùng lặp
        datesToGenerate = datesToGenerate.stream().distinct().sorted().collect(Collectors.toList());
        LOGGER.info(String.format("Xác định được %d ngày cần tạo/cập nhật lịch và slot cho bác sĩ ID %d.", datesToGenerate.size(), doctorId));

        connection.setAutoCommit(false); // Bắt đầu transaction

        try {
            // Vô hiệu hóa (deactivate) tất cả các lịch trình và slot hiện có từ ngày hôm nay trở đi 
            // nếu chúng không nằm trong các ngày mới được tạo hoặc không có trong cấu hình mới.
            // Điều này đảm bảo rằng lịch trình luôn phản ánh cấu hình hiện tại.

            // 1. Deactivate DoctorSchedule entries
            StringBuilder deactivateScheduleSql = new StringBuilder("UPDATE doctor_schedule SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP ")
                                                .append("WHERE doctor_id = ? AND work_date >= ? AND is_active = TRUE");
            if (!datesToGenerate.isEmpty()) {
                 deactivateScheduleSql.append(" AND work_date NOT IN (");
                 for(int i=0; i<datesToGenerate.size(); i++) {
                     deactivateScheduleSql.append("?");
                     if (i < datesToGenerate.size() - 1) deactivateScheduleSql.append(", ");
                 }
                 deactivateScheduleSql.append(")");
            }

            try (PreparedStatement psDeactivateSchedule = connection.prepareStatement(deactivateScheduleSql.toString())) {
                int paramIndex = 1;
                psDeactivateSchedule.setInt(paramIndex++, doctorId);
                psDeactivateSchedule.setDate(paramIndex++, Date.valueOf(today));
                for (LocalDate date : datesToGenerate) {
                    psDeactivateSchedule.setDate(paramIndex++, Date.valueOf(date));
                }
                int deactivatedCount = psDeactivateSchedule.executeUpdate();
                LOGGER.info(String.format("Đã vô hiệu hóa %d bản ghi doctor_schedule cũ không nằm trong lịch mới cho bác sĩ ID %d.", deactivatedCount, doctorId));
            }

            // 2. Deactivate Slots
            StringBuilder deactivateSlotsSql = new StringBuilder("UPDATE slot SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP ")
                                               .append("WHERE doctor_id = ? AND DATE(start_time) >= ? AND is_deleted = FALSE");
            if (!datesToGenerate.isEmpty()) {
                 deactivateSlotsSql.append(" AND DATE(start_time) NOT IN (");
                 for(int i=0; i<datesToGenerate.size(); i++) {
                     deactivateSlotsSql.append("?");
                     if (i < datesToGenerate.size() - 1) deactivateSlotsSql.append(", ");
                 }
                 deactivateSlotsSql.append(")");
            }

            try (PreparedStatement psDeactivateSlots = connection.prepareStatement(deactivateSlotsSql.toString())) {
                int paramIndex = 1;
                psDeactivateSlots.setInt(paramIndex++, doctorId);
                psDeactivateSlots.setDate(paramIndex++, Date.valueOf(today));
                for (LocalDate date : datesToGenerate) {
                    psDeactivateSlots.setDate(paramIndex++, Date.valueOf(date));
                }
                int deactivatedSlotCount = psDeactivateSlots.executeUpdate();
                LOGGER.info(String.format("Đã vô hiệu hóa %d slot cũ không nằm trong lịch mới cho bác sĩ ID %d.", deactivatedSlotCount, doctorId));
            }


            // Bước 3: Thêm/cập nhật doctor_schedule và tạo/cập nhật slot
            String upsertDoctorScheduleSql = "INSERT INTO doctor_schedule (doctor_id, work_date, is_active) VALUES (?, ?, TRUE) "
                                           + "ON DUPLICATE KEY UPDATE is_active = TRUE, updated_at = CURRENT_TIMESTAMP";
            
            // Cần cập nhật các slot hiện có hoặc thêm mới.
            // Logic ở đây sẽ deactivate tất cả slot của ngày đó và tạo lại các slot mới.
            String deleteExistingSlotsForDaySql = "UPDATE slot SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE doctor_id = ? AND DATE(start_time) = ?";
            String insertSlotSql = "INSERT INTO slot (doctor_id, start_time, end_time, max_patients, is_deleted) VALUES (?, ?, ?, ?, FALSE)";

            try (PreparedStatement psUpsertSchedule = connection.prepareStatement(upsertDoctorScheduleSql);
                 PreparedStatement psDeleteSlotsForDay = connection.prepareStatement(deleteExistingSlotsForDaySql);
                 PreparedStatement psInsertSlot = connection.prepareStatement(insertSlotSql)) {

                for (LocalDate date : datesToGenerate) {
                    // Cập nhật/thêm bản ghi vào doctor_schedule
                    psUpsertSchedule.setInt(1, doctorId);
                    psUpsertSchedule.setDate(2, Date.valueOf(date));
                    psUpsertSchedule.addBatch();

                    // Deactivate (logically delete) các slot cũ cho ngày này trước khi tạo mới
                    psDeleteSlotsForDay.setInt(1, doctorId);
                    psDeleteSlotsForDay.setDate(2, Date.valueOf(date));
                    psDeleteSlotsForDay.addBatch();

                    // Lấy lịch làm việc của ngày cụ thể từ weeklyScheduleNode
                    String dayName = date.getDayOfWeek().toString().toLowerCase();
                    JsonNode dayScheduleNode = weeklyScheduleNode.get(dayName);

                    if (dayScheduleNode != null && dayScheduleNode.isArray()) {
                        for (JsonNode periodNode : dayScheduleNode) {
                            String startTimeStr = periodNode.get("start").asText();
                            String endTimeStr = periodNode.get("end").asText();
                            int maxPatientsPerSlot = periodNode.has("maxPatients") ? periodNode.get("maxPatients").asInt() : 1;

                            LocalTime periodStartTime = LocalTime.parse(startTimeStr);
                            LocalTime periodEndTime = LocalTime.parse(endTimeStr);

                            // Tạo các slot nhỏ hơn dựa trên appointment_duration
                            LocalTime currentSlotStartTime = periodStartTime;
                            while (currentSlotStartTime.isBefore(periodEndTime)) {
                                LocalTime currentSlotEndTime = currentSlotStartTime.plusMinutes(appointmentDurationMinutes);
                                if (currentSlotEndTime.isAfter(periodEndTime)) {
                                    currentSlotEndTime = periodEndTime; // Tránh slot vượt quá thời gian kết thúc của giai đoạn
                                }
                                
                                if (currentSlotStartTime.isBefore(currentSlotEndTime)) { // Đảm bảo slot có độ dài dương
                                    psInsertSlot.setInt(1, doctorId);
                                    psInsertSlot.setTimestamp(2, java.sql.Timestamp.valueOf(date.atTime(currentSlotStartTime)));
                                    psInsertSlot.setTimestamp(3, java.sql.Timestamp.valueOf(date.atTime(currentSlotEndTime)));
                                    psInsertSlot.setInt(4, maxPatientsPerSlot);
                                    psInsertSlot.addBatch();
                                }

                                currentSlotStartTime = currentSlotEndTime;
                            }
                        }
                    } else {
                        LOGGER.warning(String.format("Không có cấu hình slot cho ngày %s (thứ %s) cho bác sĩ ID %d.", date, dayName, doctorId));
                    }
                }
                
                // Thực thi tất cả các batch
                int[] scheduleUpdates = psUpsertSchedule.executeBatch();
                int[] slotDeletions = psDeleteSlotsForDay.executeBatch();
                int[] slotInserts = psInsertSlot.executeBatch();

                LOGGER.info(String.format("Batch upsert doctor_schedule: %d, Batch deactivate slots for day: %d, Batch insert slots: %d",
                        scheduleUpdates.length, slotDeletions.length, slotInserts.length));
            }

            connection.commit();
            LOGGER.info(String.format("Đã tạo/cập nhật lịch và slot thành công cho bác sĩ ID %d.", doctorId));

        } catch (SQLException e) {
            connection.rollback();
            LOGGER.log(Level.SEVERE, String.format("Lỗi khi tạo lịch và slot cho bác sĩ ID %d: %s", doctorId, e.getMessage()), e);
            throw e;
        } finally {
            try {
                if (connection != null && !connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi reset auto-commit: " + e.getMessage(), e);
            }
        }
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
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            Schedule emptySchedule = new Schedule();
            emptySchedule.setDoctorId(doctorId);
            emptySchedule.setWorkingDate(d);
            emptySchedule.setAvailableSlotDetails(new ArrayList<>()); // Ensure empty slot list
            schedulesMap.put(d, emptySchedule);
        }

        // SQL query to retrieve slots from 'slot' table and count appointments for each slot
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

            // --- THÊM TEST CHO generateDetailedDoctorScheduleAndSlots TẠI ĐÂY ---
            // Bạn cần có một cấu hình JSON mẫu
            String sampleJsonConfig = "{\"appointment_duration\": \"30\", \"schedule_period\": \"future\", \"weekly_schedule\": {\"monday\": [{\"start\": \"09:00\", \"end\": \"12:00\", \"maxPatients\": 2}, {\"start\": \"14:00\", \"end\": \"17:00\", \"maxPatients\": 2}], \"wednesday\": [{\"start\": \"10:00\", \"end\": \"13:00\", \"maxPatients\": 3}], \"friday\": [{\"start\": \"08:00\", \"end\": \"11:00\", \"maxPatients\": 1}]}}";
            LOGGER.info("\n--- Đang test generateDetailedDoctorScheduleAndSlots ---");
            daoSchedule.generateDetailedDoctorScheduleAndSlots(doctorIdToTest, sampleJsonConfig);
            LOGGER.info("--- Test generateDetailedDoctorScheduleAndSlots hoàn tất ---");

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