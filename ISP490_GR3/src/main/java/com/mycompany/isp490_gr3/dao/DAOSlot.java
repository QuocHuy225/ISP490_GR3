package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.dto.SlotViewDTO;
import com.mycompany.isp490_gr3.model.Slot;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

public class DAOSlot {

    private static final Logger LOGGER = Logger.getLogger(DAOSlot.class.getName());

    // Lấy danh sách slot của ngày hiện tại với phân trang
    public List<SlotViewDTO> getTodaySlotViewDTOs(int offset, int limit) {
        String sql = "SELECT s.id, s.slot_date, s.start_time, s.end_time, s.max_patients, d.full_name AS doctor_name, "
                + "(SELECT COUNT(*) FROM appointment a WHERE a.slot_id = s.id AND a.patient_id IS NOT NULL AND a.status != 'cancelled') AS booked_patients "
                + "FROM slot s JOIN doctors d ON s.doctor_id = d.id "
                + "WHERE s.is_deleted = FALSE AND s.slot_date = CURRENT_DATE "
                + "ORDER BY s.start_time DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            return extractSlotViewDTOs(ps.executeQuery());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy slot hôm nay: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // Đếm số slot của ngày hiện tại
    public int countTodaySlotViewDTOs() {
        String sql = "SELECT COUNT(*) FROM slot WHERE is_deleted = FALSE AND slot_date = CURRENT_DATE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm slot hôm nay: " + e.getMessage(), e);
            return 0;
        }
    }

    // Tìm kiếm slot theo doctorId và/hoặc slotDate với phân trang
    public List<SlotViewDTO> searchSlotViewDTOs(Integer doctorId, LocalDate slotDate, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT s.id, s.slot_date, s.start_time, s.end_time, s.max_patients, d.full_name AS doctor_name, ");
        sql.append("(SELECT COUNT(*) FROM appointment a WHERE a.slot_id = s.id AND a.patient_id IS NOT NULL AND a.status != 'cancelled') AS booked_patients ");
        sql.append("FROM slot s JOIN doctors d ON s.doctor_id = d.id WHERE s.is_deleted = FALSE");
        if (doctorId != null) {
            sql.append(" AND s.doctor_id = ?");
        }
        if (slotDate != null) {
            sql.append(" AND s.slot_date = ?");
        }
        sql.append(" ORDER BY s.slot_date DESC, s.start_time DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (doctorId != null) {
                ps.setInt(idx++, doctorId);
            }
            if (slotDate != null) {
                ps.setDate(idx++, Date.valueOf(slotDate));
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            return extractSlotViewDTOs(ps.executeQuery());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm slot: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // Đếm số slot theo bộ lọc
    public int countFilteredSlotViewDTOs(Integer doctorId, LocalDate slotDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM slot WHERE is_deleted = FALSE");
        if (doctorId != null) {
            sql.append(" AND doctor_id = ?");
        }
        if (slotDate != null) {
            sql.append(" AND slot_date = ?");
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (doctorId != null) {
                ps.setInt(idx++, doctorId);
            }
            if (slotDate != null) {
                ps.setDate(idx++, Date.valueOf(slotDate));
            }
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm slot tìm kiếm: " + e.getMessage(), e);
            return 0;
        }
    }

    // Chèn một slot đơn, kiểm tra không trùng giờ, và tạo maxPatients bản ghi appointment
    public boolean insertSlotWithValidation(int doctorId, LocalDate date, LocalTime start, int duration, int maxPatients) throws SQLException {
        LocalTime end = start.plusMinutes(duration);
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean success = !isSlotConflict(conn, doctorId, date, start, end) && insertSlot(conn, doctorId, date, start, end, maxPatients);
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                return success;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Chèn nhiều slot trong khoảng thời gian, kiểm tra không trùng giờ
    public int insertRangeSlots(int doctorId, LocalDate date, String startStr, String endStr, int duration, int maxPatients) throws SQLException {
        LocalTime startTime = LocalTime.parse(startStr);
        LocalTime endTime = LocalTime.parse(endStr);
        int inserted = 0;

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                LocalTime current = startTime;
                while (!current.plusMinutes(duration).isAfter(endTime)) {
                    LocalTime next = current.plusMinutes(duration);
                    if (!isSlotConflict(conn, doctorId, date, current, next)) {
                        if (insertSlot(conn, doctorId, date, current, next, maxPatients)) {
                            inserted++;
                        }
                    }
                    current = next;
                }
                conn.commit();
                return inserted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Chèn slot và maxPatients bản ghi appointment với appointment_code ngắn
    private boolean insertSlot(Connection conn, int doctorId, LocalDate date, LocalTime start, LocalTime end, int maxPatients) throws SQLException {
        if (maxPatients > 100) {
            throw new IllegalArgumentException("maxPatients quá lớn: " + maxPatients);
        }
        String slotSql = "INSERT INTO slot (doctor_id, slot_date, start_time, end_time, max_patients, is_deleted, created_at) "
                + "VALUES (?, ?, ?, ?, ?, FALSE, CURRENT_TIMESTAMP)";

        try (PreparedStatement ps = conn.prepareStatement(slotSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(start));
            ps.setTime(4, Time.valueOf(end));
            ps.setInt(5, maxPatients);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Kiểm tra trùng giờ
    private boolean isSlotConflict(Connection conn, int doctorId, LocalDate date, LocalTime start, LocalTime end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM slot WHERE doctor_id = ? AND slot_date = ? AND is_deleted = FALSE "
                + "AND ((start_time < ? AND end_time > ?) OR (start_time < ? AND end_time > ?) OR (start_time >= ? AND end_time <= ?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(end));
            ps.setTime(4, Time.valueOf(end));
            ps.setTime(5, Time.valueOf(start));
            ps.setTime(6, Time.valueOf(start));
            ps.setTime(7, Time.valueOf(start));
            ps.setTime(8, Time.valueOf(end));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Chuyển ResultSet thành danh sách SlotViewDTO
    private List<SlotViewDTO> extractSlotViewDTOs(ResultSet rs) throws SQLException {
        List<SlotViewDTO> slots = new ArrayList<>();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        while (rs.next()) {
            SlotViewDTO dto = new SlotViewDTO();
            dto.setId(rs.getInt("id"));
            dto.setSlotDate(rs.getDate("slot_date").toLocalDate().format(dateFmt));
            dto.setStartTime(rs.getTime("start_time").toLocalTime().format(timeFmt));
            dto.setEndTime(rs.getTime("end_time").toLocalTime().format(timeFmt));
            dto.setMaxPatients(rs.getInt("max_patients"));
            dto.setBookedPatients(rs.getInt("booked_patients"));
            dto.setDoctorName(rs.getString("doctor_name"));
            dto.setCheckinRange(dto.getStartTime() + " - " + dto.getEndTime());
            dto.setBookingStatus(dto.getBookedPatients() + " / " + dto.getMaxPatients());
            slots.add(dto);
        }
        return slots;
    }

    // Xóa mềm slot và cập nhật status của appointment
    public void deleteSlot(String slotId) throws SQLException {
        try {
            int id = Integer.parseInt(slotId);
            try (Connection conn = DBContext.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    if (!slotExists(slotId)) {
                        throw new SQLException("Slot không tồn tại hoặc đã bị xóa: " + slotId);
                    }
                    if (isSlotBooked(slotId)) {
                        throw new SQLException("Không thể xóa slot vì đã có lịch hẹn của bệnh nhân.");
                    }
                    String updateAppointmentSql = "UPDATE appointment SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " + "WHERE slot_id = ? AND is_deleted = FALSE";
                    try (PreparedStatement ps = conn.prepareStatement(updateAppointmentSql)) {
                        ps.setInt(1, id);
                        int appointmentsUpdated = ps.executeUpdate();
                        LOGGER.info("Đã xóa ");
                    }
                    String updateSlotSql = "UPDATE slot SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " + "WHERE id = ? AND is_deleted = FALSE";
                    try (PreparedStatement ps = conn.prepareStatement(updateSlotSql)) {
                        ps.setInt(1, id);
                        int rowsAffected = ps.executeUpdate();
                        if (rowsAffected == 0) {
                            throw new SQLException("Không thể xóa slot: " + slotId);
                        }
                    }
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        } catch (NumberFormatException e) {
            throw new SQLException("ID slot không hợp lệ: " + slotId, e);
        }
    }

    // Xóa mềm nhiều slot
    public int deleteMultipleSlots(String[] slotIds) throws SQLException {
        if (slotIds == null || slotIds.length == 0) {
            return 0;
        }
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int updatedSlots = 0;
                for (String slotId : slotIds) {
                    if (!slotExists(slotId)) {
                        LOGGER.warning("Slot không tồn tại hoặc đã bị xóa: " + slotId);
                        continue;
                    }
                    if (isSlotBooked(slotId)) {
                        LOGGER.warning("Không thể xóa slot vì đã có lịch hẹn của bệnh nhân: " + slotId);
                        continue;
                    }
                    String updateAppointmentSql = "UPDATE appointment SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " + "WHERE slot_id = ? AND is_deleted = FALSE";
                    try (PreparedStatement ps = conn.prepareStatement(updateAppointmentSql)) {
                        ps.setInt(1, Integer.parseInt(slotId));
                        int appointmentsUpdated = ps.executeUpdate();
                        LOGGER.info("Đã cập nhật trạng thái 'cancelled' cho " + appointmentsUpdated + " lịch hẹn của slot " + slotId);
                    }
                    String updateSlotSql = "UPDATE slot SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " + "WHERE id = ? AND is_deleted = FALSE";
                    try (PreparedStatement ps = conn.prepareStatement(updateSlotSql)) {
                        ps.setInt(1, Integer.parseInt(slotId));
                        updatedSlots += ps.executeUpdate();
                    }
                }
                conn.commit();
                return updatedSlots;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Kiểm tra slot có lịch hẹn của bệnh nhân
    public boolean isSlotBooked(String slotId) throws SQLException {
        try {
            int id = Integer.parseInt(slotId);
            String sql = "SELECT COUNT(*) FROM appointment WHERE slot_id = ? AND patient_id IS NOT NULL AND status != 'cancelled'";
            try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    LOGGER.info("Slot " + slotId + " có " + count + " lịch hẹn của bệnh nhân.");
                    return count > 0;
                }
                return false;
            }
        } catch (NumberFormatException e) {
            throw new SQLException("ID slot không hợp lệ: " + slotId, e);
        }
    }

    // Kiểm tra slot tồn tại
    public boolean slotExists(String slotId) throws SQLException {
        try {
            int id = Integer.parseInt(slotId);
            String sql = "SELECT COUNT(*) FROM slot WHERE id = ? AND is_deleted = FALSE";
            try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (NumberFormatException e) {
            throw new SQLException("ID slot không hợp lệ: " + slotId, e);
        }
    }

    // Kiểm tra tính duy nhất của appointment_code
    private boolean isAppointmentCodeUnique(Connection conn, String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE appointment_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    // Kiểm tra bác sĩ có slot không
    public boolean hasSlotsForDoctor(int doctorId, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM slot WHERE doctor_id = ? AND slot_date = ? AND is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Lấy danh sách các ngày mà một bác sĩ có slot trống (còn chỗ) trong tương
     * lai.
     *
     * @param doctorId ID của bác sĩ.
     * @return Danh sách các LocalDate có slot trống.
     */
    /**
     * Lấy danh sách các ngày mà một bác sĩ có slot trống (còn chỗ) trong tương
     * lai.
     *
     * @param doctorId ID của bác sĩ.
     * @return Danh sách các LocalDate có slot trống.
     */
    // Dán đoạn code này vào file DAOSlot.java, thay thế cho phương thức cũ.
    public List<LocalDate> getAvailableDatesForDoctor(int doctorId) {
        List<LocalDate> availableDates = new ArrayList<>();
        // Câu lệnh SQL đã được viết lại cho đơn giản và chính xác hơn.
        String sql = "SELECT DISTINCT s.slot_date "
                + "FROM slot s "
                + "WHERE s.doctor_id = ? "
                + "AND s.is_deleted = FALSE "
                + "AND s.slot_date >= CURDATE() "
                + "AND (SELECT COUNT(a.id) FROM appointment a WHERE a.slot_id = s.id) < s.max_patients "
                + "ORDER BY s.slot_date ASC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    availableDates.add(rs.getDate("slot_date").toLocalDate());
                }
            }
        } catch (SQLException e) {
            // Ghi log lỗi để dễ dàng debug nếu có sự cố trong tương lai
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách ngày trống cho bác sĩ ID " + doctorId, e);
            // Ném lại một RuntimeException để báo hiệu lỗi nghiêm trọng ở tầng DAO
            throw new RuntimeException("Lỗi truy vấn cơ sở dữ liệu khi lấy ngày làm việc.", e);
        }
        return availableDates;
    }

    // Lấy slot có sẵn (chưa có lịch hẹn của bệnh nhân)
    public List<Slot> getAvailableSlots(int doctorId, String slotDate) {
        List<Slot> availableSlots = new ArrayList<>();
        LocalDate date;
        try {
            date = LocalDate.parse(slotDate);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ngày không hợp lệ: " + slotDate, e);
            return availableSlots;
        }

        // Truy vấn lấy các slot CÒN TRỐNG (booked_count < max_patients)
        String sql = "SELECT s.id, s.slot_date, s.start_time, s.end_time, s.max_patients "
                + "FROM slot s "
                + "LEFT JOIN (SELECT slot_id, COUNT(id) AS booked_count FROM appointment WHERE status IN ('pending', 'confirmed', 'done') GROUP BY slot_id) AS a ON s.id = a.slot_id "
                + "WHERE s.doctor_id = ? AND s.slot_date = ? AND s.is_deleted = FALSE "
                + "AND s.is_available = TRUE " // Đảm bảo chỉ lấy slot đang hoạt động
                + "AND COALESCE(booked_count, 0) < s.max_patients " // Đảm bảo slot còn chỗ trống
                + "ORDER BY s.start_time ASC"; // Sắp xếp theo thời gian để dễ hiển thị

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(date)); // Sử dụng java.sql.Date.valueOf
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Slot slot = new Slot();
                    slot.setId(rs.getInt("id"));
                    slot.setSlotDate(rs.getDate("slot_date").toLocalDate());
                    slot.setStartTime(rs.getTime("start_time").toLocalTime());
                    slot.setEndTime(rs.getTime("end_time").toLocalTime());
                    slot.setMaxPatients(rs.getInt("max_patients"));
                    // Các thuộc tính khác của Slot nếu cần
                    availableSlots.add(slot);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy slot có sẵn cho bác sĩ " + doctorId + " vào ngày " + slotDate + ": " + e.getMessage(), e);
            e.printStackTrace();
        }
        return availableSlots;
    }

    // Test các chức năng
    public static void main(String[] args) {
        DAOSlot dao = new DAOSlot();
        try {
            // Test 0: Kiểm tra slot cho bác sĩ
            System.out.println("=== Test 0: hasSlotsForDoctor ===");
            int doctorId = 2; // Giả sử bác sĩ ID = 2 tồn tại
            LocalDate date = LocalDate.of(2025, 7, 3);
            try {
                if (dao.hasSlotsForDoctor(doctorId, date)) {
                    System.out.println("Bác sĩ " + doctorId + " đã có slot vào ngày " + date);
                } else {
                    System.out.println("Bác sĩ " + doctorId + " chưa có slot vào ngày " + date);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra slot của bác sĩ: " + e.getMessage(), e);
            }

            // Test 1: Chèn slot đơn và kiểm tra bookingStatus
            System.out.println("\n=== Test 1: insertSlotWithValidation ===");
            LocalTime start = LocalTime.of(10, 0);
            int duration = 30;
            int maxPatients = 4;
            try {
                boolean inserted = dao.insertSlotWithValidation(doctorId, date, start, duration, maxPatients);
                System.out.println("Chèn slot đơn: " + (inserted ? "Thành công" : "Thất bại (có thể do trùng giờ)"));
                printSlotAndAppointments(dao, doctorId, date);
                // Kiểm tra bookingStatus
                System.out.println("Kiểm tra bookingStatus sau khi chèn slot đơn:");
                List<SlotViewDTO> slots = dao.searchSlotViewDTOs(doctorId, date, 0, 10);
                for (SlotViewDTO slot : slots) {
                    System.out.println("Slot ID: " + slot.getId() + ", Booking Status: " + slot.getBookingStatus());
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi chèn slot đơn: " + e.getMessage(), e);
            }

            // Test 2: Chèn dải slot và kiểm tra bookingStatus
            System.out.println("\n=== Test 2: insertRangeSlots ===");
            String startStr = "14:00";
            String endStr = "16:00";
            try {
                int inserted = dao.insertRangeSlots(doctorId, date, startStr, endStr, duration, maxPatients);
                System.out.println("Đã chèn " + inserted + " slot trong khoảng " + startStr + "-" + endStr);
                printSlotAndAppointments(dao, doctorId, date);
                // Kiểm tra bookingStatus
                System.out.println("Kiểm tra bookingStatus sau khi chèn dải slot:");
                List<SlotViewDTO> slots = dao.searchSlotViewDTOs(doctorId, date, 0, 10);
                for (SlotViewDTO slot : slots) {
                    System.out.println("Slot ID: " + slot.getId() + ", Booking Status: " + slot.getBookingStatus());
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi chèn dải slot: " + e.getMessage(), e);
            }

            // Test 3: Xóa slot đơn
            System.out.println("\n=== Test 3: deleteSlot ===");
            String slotId = "1001"; // Thay bằng slotId thực tế
            try {
                if (dao.slotExists(slotId)) {
                    if (!dao.isSlotBooked(slotId)) {
                        dao.deleteSlot(slotId);
                        System.out.println("Xóa slot " + slotId + " thành công");
                        printSlotAndAppointments(dao, doctorId, date);
                    } else {
                        System.out.println("Không thể xóa slot " + slotId + " vì đã có lịch hẹn của bệnh nhân");
                    }
                } else {
                    System.out.println("Slot " + slotId + " không tồn tại");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi xóa slot " + slotId + ": " + e.getMessage(), e);
            }

            // Test 4: Xóa nhiều slot
            System.out.println("\n=== Test 4: deleteMultipleSlots ===");
            String[] slotIds = {"1002", "1003"}; // Thay bằng slotId thực tế
            try {
                int deletedCount = dao.deleteMultipleSlots(slotIds);
                System.out.println("Đã xóa " + deletedCount + " slot");
                printSlotAndAppointments(dao, doctorId, date);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi xóa nhiều slot: " + e.getMessage(), e);
            }

            // Test 5: Lấy slot có sẵn
            System.out.println("\n=== Test 5: getAvailableSlots ===");
            String slotDate = "2025-07-03";
            List<Slot> availableSlots = dao.getAvailableSlots(doctorId, slotDate);
            if (availableSlots.isEmpty()) {
                System.out.println("Không có slot có sẵn vào ngày " + slotDate + " cho bác sĩ " + doctorId);
            } else {
                System.out.println("Danh sách slot có sẵn:");
                for (Slot slot : availableSlots) {
                    System.out.println("Slot ID: " + slot.getId() + ", Thời gian: " + slot.getStartTime() + " - " + slot.getEndTime() + ", Max Patients: " + slot.getMaxPatients());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi trong main: " + e.getMessage(), e);
        }
    }

    // Phương thức phụ trợ để in slot và appointment
    private static void printSlotAndAppointments(DAOSlot dao, int doctorId, LocalDate date) {
        try (Connection conn = DBContext.getConnection()) {
            // In slot
            String slotSql = "SELECT id, slot_date, start_time, end_time, max_patients, is_deleted FROM slot WHERE doctor_id = ? AND slot_date = ? AND is_deleted = FALSE";
            try (PreparedStatement ps = conn.prepareStatement(slotSql)) {
                ps.setInt(1, doctorId);
                ps.setDate(2, Date.valueOf(date));
                ResultSet rs = ps.executeQuery();
                System.out.println("Slots trong bảng slot:");
                while (rs.next()) {
                    System.out.println("Slot ID: " + rs.getInt("id") + ", Date: " + rs.getDate("slot_date") + ", Time: "
                            + rs.getTime("start_time") + " - " + rs.getTime("end_time") + ", Max Patients: " + rs.getInt("max_patients")
                            + ", Is Deleted: " + rs.getBoolean("is_deleted"));
                }
            }

            // In appointment
            String apptSql = "SELECT id, slot_id, appointment_code, status, patient_id FROM appointment WHERE slot_id IN "
                    + "(SELECT id FROM slot WHERE doctor_id = ? AND slot_date = ? AND is_deleted = FALSE) AND is_deleted = FALSE";
            try (PreparedStatement ps = conn.prepareStatement(apptSql)) {
                ps.setInt(1, doctorId);
                ps.setDate(2, Date.valueOf(date));
                ResultSet rs = ps.executeQuery();
                System.out.println("Appointments trong bảng appointment:");
                while (rs.next()) {
                    System.out.println("Appointment ID: " + rs.getInt("id") + ", Slot ID: " + rs.getInt("slot_id")
                            + ", Code: " + rs.getString("appointment_code") + ", Status: " + rs.getString("status")
                            + ", Patient ID: " + (rs.getObject("patient_id") == null ? "NULL" : rs.getInt("patient_id")));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi in slot và appointment: " + e.getMessage(), e);
        }
    }

    // Lấy danh sách bệnh nhân theo slotId
    public List<PatientDTO> getPatientsBySlotId(int slotId) {
        List<PatientDTO> patients = new ArrayList<>();
        String sql = "SELECT p.patient_code, p.cccd, p.full_name, p.phone "
                + "FROM appointment a "
                + "JOIN patients p ON a.patient_id = p.id "
                + "WHERE a.slot_id = ? AND a.is_deleted = FALSE AND p.is_deleted = FALSE "
                + "AND a.status != 'cancelled'";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PatientDTO patient = new PatientDTO();
                patient.setPatientCode(rs.getString("patient_code"));
                patient.setCccd(rs.getString("cccd"));
                patient.setFullName(rs.getString("full_name"));
                patient.setPhone(rs.getString("phone"));
                patients.add(patient);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách bệnh nhân cho slotId " + slotId + ": " + e.getMessage(), e);
        }
        return patients;
    }

    // DTO nội bộ để ánh xạ thông tin bệnh nhân
    public static class PatientDTO {

        private String patientCode;
        private String cccd;
        private String fullName;
        private String phone;

        public String getPatientCode() {
            return patientCode;
        }

        public void setPatientCode(String patientCode) {
            this.patientCode = patientCode;
        }

        public String getCccd() {
            return cccd;
        }

        public void setCccd(String cccd) {
            this.cccd = cccd;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
