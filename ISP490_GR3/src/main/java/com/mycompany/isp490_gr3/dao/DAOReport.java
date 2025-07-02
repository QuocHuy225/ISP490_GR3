package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.ReportData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DAOReport extends DBContext {

    public ReportData getSummaryData() {
        ReportData data = new ReportData();
        String sql = "SELECT " +
                    "(SELECT COUNT(*) FROM patients WHERE is_deleted = FALSE) as total_patients, " +
                    "(SELECT COUNT(*) FROM doctors WHERE is_deleted = FALSE) as total_doctors, " +
                    "(SELECT COUNT(*) FROM appointment WHERE is_deleted = FALSE) as total_appointments, " +
                    "(SELECT COUNT(*) FROM medical_record) as total_medical_records, " +
                    "(SELECT COUNT(*) FROM invoices WHERE isdeleted = FALSE) as total_invoices, " +
                    "(SELECT COALESCE(SUM(final_amount), 0) FROM invoices WHERE isdeleted = FALSE) as total_revenue";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
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
        String sql = "SELECT " +
                    "COUNT(*) as total_patients, " +
                    "SUM(CASE WHEN gender = 1 THEN 1 ELSE 0 END) as male_count, " +
                    "SUM(CASE WHEN gender = 0 THEN 1 ELSE 0 END) as female_count, " +
                    "(SELECT COUNT(*) FROM appointment WHERE is_deleted = FALSE) as total_visits, " +
                    "(SELECT COUNT(DISTINCT patient_id) FROM appointment WHERE is_deleted = FALSE) as patients_with_appointments " +
                    "FROM patients " +
                    "WHERE is_deleted = FALSE";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
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
        String sql = "SELECT " +
                    "COUNT(*) as total_records, " +
                    "COUNT(DISTINCT patient_id) as unique_patients, " +
                    "COUNT(DISTINCT doctor_id) as unique_doctors, " +
                    "COUNT(CASE WHEN status = 'completed' THEN 1 END) as completed_records, " +
                    "COUNT(CASE WHEN status = 'ongoing' THEN 1 END) as ongoing_records " +
                    "FROM medical_record";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
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
        String sql = "SELECT " +
                    "COUNT(*) as total_invoices, " +
                    "SUM(final_amount) as total_revenue, " +
                    "AVG(final_amount) as average_invoice_amount, " +
                    "COUNT(DISTINCT patient_id) as unique_patients, " +
                    "SUM(total_service_amount) as total_service_amount, " +
                    "SUM(total_supply_amount) as total_supply_amount, " +
                    "SUM(discount_amount) as total_discount_amount " +
                    "FROM invoices " +
                    "WHERE isdeleted = FALSE";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
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
} 