package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.ReportData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DAOReport extends DBContext {

    public ReportData getSummaryData() {
        ReportData data = new ReportData();
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM patients WHERE is_deleted = FALSE) as total_patients, "
                + "(SELECT COUNT(*) FROM doctors WHERE is_deleted = FALSE) as total_doctors, "
                + "(SELECT COUNT(*) FROM appointment WHERE is_deleted = FALSE) as total_appointments, "
                + "(SELECT COUNT(*) FROM medical_record) as total_medical_records, "
                + "(SELECT COUNT(*) FROM invoices WHERE isdeleted = FALSE) as total_invoices, "
                + "(SELECT COALESCE(SUM(final_amount), 0) FROM invoices WHERE isdeleted = FALSE) as total_revenue";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                data.setTotalPatients(rs.getInt("total_patients"));
                data.setTotalDoctors(rs.getInt("total_doctors"));
                data.setTotalAppointments(rs.getInt("total_appointments"));
                data.setTotalMedicalRecords(rs.getInt("total_medical_records"));
                data.setTotalInvoices(rs.getInt("total_invoices"));
                data.setTotalRevenue(rs.getDouble("total_revenue"));
            }
        } catch (SQLException e) {
            System.out.println("Error in getSummaryData: " + e.getMessage());
        }
        return data;
    }

    public Map<String, Object> getPatientStatistics() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT "
                + "COUNT(*) as total_patients, "
                + "SUM(CASE WHEN gender = 1 THEN 1 ELSE 0 END) as male_count, "
                + "SUM(CASE WHEN gender = 0 THEN 1 ELSE 0 END) as female_count, "
                + "(SELECT COUNT(*) FROM appointment WHERE is_deleted = FALSE) as total_visits, "
                + "(SELECT COUNT(DISTINCT patient_id) FROM appointment WHERE is_deleted = FALSE) as patients_with_appointments "
                + "FROM patients "
                + "WHERE is_deleted = FALSE";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.put("totalPatients", rs.getInt("total_patients"));
                stats.put("maleCount", rs.getInt("male_count"));
                stats.put("femaleCount", rs.getInt("female_count"));
                stats.put("totalVisits", rs.getInt("total_visits"));
                stats.put("patientsWithAppointments", rs.getInt("patients_with_appointments"));
            }
        } catch (SQLException e) {
            System.out.println("Error in getPatientStatistics: " + e.getMessage());
        }
        return stats;
    }

    public Map<String, Object> getMedicalRecordStatistics() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT "
                + "COUNT(*) as total_records, "
                + "COUNT(DISTINCT patient_id) as unique_patients, "
                + "COUNT(DISTINCT doctor_id) as unique_doctors, "
                + "COUNT(CASE WHEN status = 'completed' THEN 1 END) as completed_records, "
                + "COUNT(CASE WHEN status = 'ongoing' THEN 1 END) as ongoing_records "
                + "FROM medical_record";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.put("totalRecords", rs.getInt("total_records"));
                stats.put("uniquePatients", rs.getInt("unique_patients"));
                stats.put("uniqueDoctors", rs.getInt("unique_doctors"));
                stats.put("completedRecords", rs.getInt("completed_records"));
                stats.put("ongoingRecords", rs.getInt("ongoing_records"));
            }
        } catch (SQLException e) {
            System.out.println("Error in getMedicalRecordStatistics: " + e.getMessage());
        }
        return stats;
    }

    public Map<String, Object> getInvoiceStatistics() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT "
                + "COUNT(*) as total_invoices, "
                + "SUM(final_amount) as total_revenue, "
                + "AVG(final_amount) as average_invoice_amount, "
                + "COUNT(DISTINCT patient_id) as unique_patients, "
                + "SUM(total_service_amount) as total_service_amount, "
                + "SUM(total_supply_amount) as total_supply_amount, "
                + "SUM(discount_amount) as total_discount_amount "
                + "FROM invoices "
                + "WHERE isdeleted = FALSE";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.put("totalInvoices", rs.getInt("total_invoices"));
                stats.put("totalRevenue", rs.getDouble("total_revenue"));
                stats.put("averageInvoiceAmount", rs.getDouble("average_invoice_amount"));
                stats.put("uniquePatients", rs.getInt("unique_patients"));
                stats.put("totalServiceAmount", rs.getDouble("total_service_amount"));
                stats.put("totalSupplyAmount", rs.getDouble("total_supply_amount"));
                stats.put("totalDiscountAmount", rs.getDouble("total_discount_amount"));
            }
        } catch (SQLException e) {
            System.out.println("Error in getInvoiceStatistics: " + e.getMessage());
        }
        return stats;
    }

    public Map<String, Object> getAppointmentStatistics() {
        Map<String, Object> stats = new HashMap<>();

        String sqlTotal = "SELECT COUNT(*) AS total FROM appointment WHERE is_deleted = FALSE";
        String sqlDone = "SELECT COUNT(*) AS done FROM appointment WHERE status = 'done' AND is_deleted = FALSE";
        String sqlCancelled = "SELECT COUNT(*) AS cancelled FROM appointment WHERE status = 'cancelled' AND is_deleted = FALSE";
        String sqlToday = "SELECT COUNT(*) AS today "
                + "FROM appointment a JOIN slot s ON a.slot_id = s.id "
                + "WHERE s.slot_date = CURRENT_DATE AND a.is_deleted = FALSE";

        String sqlByDate = "SELECT s.slot_date, COUNT(*) AS total "
                + "FROM appointment a JOIN slot s ON a.slot_id = s.id "
                + "WHERE a.is_deleted = FALSE GROUP BY s.slot_date ORDER BY s.slot_date";

        String sqlByDoctor = "SELECT d.full_name AS doctor_name, COUNT(*) AS total "
                + "FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN doctors d ON s.doctor_id = d.id "
                + "WHERE a.is_deleted = FALSE AND d.is_deleted = FALSE "
                + "GROUP BY d.full_name ORDER BY total DESC";

        String sqlByService = "SELECT sv.service_name AS service_name, COUNT(*) AS total "
                + "FROM appointment a "
                + "JOIN medical_services sv ON a.services_id = sv.services_id "
                + "WHERE a.is_deleted = FALSE AND sv.isdeleted = FALSE "
                + "GROUP BY sv.service_name ORDER BY total DESC";

        String sqlByStatus = "SELECT a.status AS status, COUNT(*) AS total "
                + "FROM appointment a "
                + "WHERE a.is_deleted = FALSE "
                + "GROUP BY a.status";

        try (Connection conn = getConnection()) {
            // Tổng số lịch hẹn
            try (PreparedStatement ps = conn.prepareStatement(sqlTotal); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalAppointments", rs.getInt("total"));
                }
            }

            // Lịch hẹn đã hoàn tất
            try (PreparedStatement ps = conn.prepareStatement(sqlDone); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("doneAppointments", rs.getInt("done"));
                }
            }

            // Lịch hẹn đã hủy
            try (PreparedStatement ps = conn.prepareStatement(sqlCancelled); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("cancelledAppointments", rs.getInt("cancelled"));
                }
            }

            // Lịch hẹn hôm nay
            try (PreparedStatement ps = conn.prepareStatement(sqlToday); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("todayAppointments", rs.getInt("today"));
                }
            }

            // Thống kê theo ngày
            List<Map<String, Object>> appointmentsByDate = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlByDate); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("date", rs.getDate("slot_date").toString());
                    entry.put("count", rs.getInt("total"));
                    appointmentsByDate.add(entry);
                }
            }
            stats.put("appointmentsByDate", appointmentsByDate);

            // Thống kê theo bác sĩ
            List<Map<String, Object>> appointmentsByDoctor = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlByDoctor); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("doctor", rs.getString("doctor_name"));
                    entry.put("count", rs.getInt("total"));
                    appointmentsByDoctor.add(entry);
                }
            }
            stats.put("appointmentsByDoctor", appointmentsByDoctor);

            // Thống kê theo dịch vụ
            List<Map<String, Object>> appointmentsByService = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlByService); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("service", rs.getString("service_name"));
                    entry.put("count", rs.getInt("total"));
                    appointmentsByService.add(entry);
                }
            }
            stats.put("appointmentsByService", appointmentsByService);

            // Thống kê theo trạng thái
            List<Map<String, Object>> appointmentsByStatus = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlByStatus); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("status", rs.getString("status"));
                    entry.put("count", rs.getInt("total"));
                    appointmentsByStatus.add(entry);
                }
            }
            stats.put("appointmentsByStatus", appointmentsByStatus);

        } catch (SQLException e) {
            System.out.println("Error in getAppointmentStatistics: " + e.getMessage());
        }

        return stats;
    }

    public Map<String, Object> getDoctorStatistics(int doctorId) {
        Map<String, Object> stats = new HashMap<>();

        String sqlTotalAppointments = "SELECT COUNT(*) AS total FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE s.doctor_id = ? AND a.is_deleted = FALSE";

        String sqlUniquePatients = "SELECT COUNT(DISTINCT a.patient_id) AS total FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE s.doctor_id = ? AND a.is_deleted = FALSE";

        String sqlAppointmentsByService = "SELECT ms.service_name, COUNT(*) AS total FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "JOIN medical_services ms ON a.services_id = ms.services_id "
                + "WHERE s.doctor_id = ? AND a.is_deleted = FALSE AND ms.isdeleted = FALSE "
                + "GROUP BY ms.service_name ORDER BY total DESC";

        String sqlAppointmentsByStatus = "SELECT a.status, COUNT(*) AS total FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE s.doctor_id = ? AND a.is_deleted = FALSE "
                + "GROUP BY a.status";

        try (Connection conn = getConnection()) {
            // Tổng lịch hẹn
            try (PreparedStatement ps = conn.prepareStatement(sqlTotalAppointments)) {
                ps.setInt(1, doctorId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        stats.put("totalAppointments", rs.getInt("total"));
                    }
                }
            }

            // Tổng bệnh nhân đã khám
            try (PreparedStatement ps = conn.prepareStatement(sqlUniquePatients)) {
                ps.setInt(1, doctorId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        stats.put("uniquePatients", rs.getInt("total"));
                    }
                }
            }

            // Lịch hẹn theo dịch vụ
            List<Map<String, Object>> serviceStats = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlAppointmentsByService)) {
                ps.setInt(1, doctorId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("service", rs.getString("service_name"));
                        row.put("count", rs.getInt("total"));
                        serviceStats.add(row);
                    }
                }
            }
            stats.put("appointmentsByService", serviceStats);

            // Lịch hẹn theo trạng thái
            List<Map<String, Object>> statusStats = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlAppointmentsByStatus)) {
                ps.setInt(1, doctorId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("status", rs.getString("status"));
                        row.put("count", rs.getInt("total"));
                        statusStats.add(row);
                    }
                }
            }
            stats.put("appointmentsByStatus", statusStats);

        } catch (SQLException e) {
            System.out.println("Error in getDoctorStatistics: " + e.getMessage());
        }

        return stats;
    }

    public int getDoctorIdByAccountId(String accountId) {
        int doctorId = -1;
        String sql = "SELECT id FROM doctors WHERE account_id = ? AND is_deleted = FALSE";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    doctorId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getDoctorIdByAccountId: " + e.getMessage());
        }

        return doctorId;
    }

    public Map<String, Object> getReceptionistStatistics() {
        Map<String, Object> stats = new HashMap<>();

        String sqlAppointmentsToday = "SELECT COUNT(*) AS total_today FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE s.slot_date = CURRENT_DATE AND a.is_deleted = FALSE";

        String sqlPatientsToday = "SELECT COUNT(DISTINCT a.patient_id) AS total_patients FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE s.slot_date = CURRENT_DATE AND a.is_deleted = FALSE";

        String sqlDoctorsToday = "SELECT COUNT(DISTINCT s.doctor_id) AS total_doctors FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE s.slot_date = CURRENT_DATE AND a.is_deleted = FALSE";

        String sqlByStatusToday = "SELECT a.status, COUNT(*) AS total FROM appointment a "
                + "JOIN slot s ON a.slot_id = s.id "
                + "WHERE s.slot_date = CURRENT_DATE AND a.is_deleted = FALSE "
                + "GROUP BY a.status";

        try (Connection conn = getConnection()) {
            // Tổng số lịch hẹn hôm nay
            try (PreparedStatement ps = conn.prepareStatement(sqlAppointmentsToday); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("appointmentsToday", rs.getInt("total_today"));
                }
            }

            // Tổng số bệnh nhân hôm nay
            try (PreparedStatement ps = conn.prepareStatement(sqlPatientsToday); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("patientsToday", rs.getInt("total_patients"));
                }
            }

            // Tổng số bác sĩ hôm nay
            try (PreparedStatement ps = conn.prepareStatement(sqlDoctorsToday); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("doctorsToday", rs.getInt("total_doctors"));
                }
            }

            // Thống kê lịch hẹn theo trạng thái hôm nay
            List<Map<String, Object>> statusList = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlByStatusToday); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("status", rs.getString("status"));
                    entry.put("count", rs.getInt("total"));
                    statusList.add(entry);
                }
            }
            stats.put("appointmentsByStatusToday", statusList);

        } catch (SQLException e) {
            System.out.println("Error in getReceptionistStatistics: " + e.getMessage());
        }

        return stats;
    }

    public List<Map<String, Object>> getNewPatientsByMonth() {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS total "
                + "FROM patients "
                + "WHERE is_deleted = FALSE "
                + "GROUP BY month "
                + "ORDER BY month";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("month", rs.getString("month"));
                entry.put("count", rs.getInt("total"));
                data.add(entry);
            }
        } catch (SQLException e) {
            System.out.println("Error in getNewPatientsByMonth: " + e.getMessage());
        }

        return data;
    }

    public int getNewPatientsThisWeek() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM patients WHERE created_at >= ? AND created_at <= ? AND is_deleted = FALSE";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // Lấy ngày hiện tại
            LocalDate today = LocalDate.now();

            // Xác định ngày đầu và cuối tuần dựa trên Locale mặc định
            // Ví dụ: Locale.US tuần bắt đầu từ Chủ Nhật (SUNDAY = 1), Locale.FRANCE tuần bắt đầu từ Thứ Hai (MONDAY = 1)
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Ngày đầu tiên của tuần
            LocalDate endOfWeek = startOfWeek.plusDays(6); // Ngày cuối cùng của tuần

            // Đặt các tham số ngày vào PreparedStatement
            ps.setDate(1, java.sql.Date.valueOf(startOfWeek));
            ps.setDate(2, java.sql.Date.valueOf(endOfWeek));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getNewPatientsThisWeek: " + e.getMessage());
        }
        return count;
    }

}
