package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.ActualPrescriptionForm;
import com.mycompany.isp490_gr3.model.ActualPrescriptionMedicine;
import com.mycompany.isp490_gr3.model.ActualPrescriptionFormMedicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAOActualPrescription - Xử lý CRUD cho đơn thuốc thực tế và thuốc thực tế
 */
public class DAOActualPrescription {

    private static final Logger LOGGER = Logger.getLogger(DAOActualPrescription.class.getName());

    // ===== FORM OPERATIONS =====
    public List<ActualPrescriptionForm> getFormsByMedicalRecord(String medicalRecordId) {
        List<ActualPrescriptionForm> forms = new ArrayList<>();
        String sql = "SELECT * FROM actual_prescription_form WHERE medical_record_id = ? AND is_deleted = FALSE ORDER BY prescription_date DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicalRecordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    forms.add(mapResultSetToForm(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting prescription forms: {0}", e.getMessage());
        }
        return forms;
    }

    public ActualPrescriptionForm getFormById(String formId) {
        String sql = "SELECT * FROM actual_prescription_form WHERE actual_prescription_form_id = ? AND is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, formId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ActualPrescriptionForm form = mapResultSetToForm(rs);
                    form.setMedicines(getMedicinesByForm(formId));
                    return form;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting prescription form: {0}", e.getMessage());
        }
        return null;
    }

    public boolean addForm(ActualPrescriptionForm form, List<ActualPrescriptionMedicine> medicines) {
        if (form == null || form.getMedicalRecordId() == null) {
            throw new IllegalArgumentException("Form data is incomplete");
        }

        // Check if a prescription already exists for this medical record
        List<ActualPrescriptionForm> existingForms = getFormsByMedicalRecord(form.getMedicalRecordId());
        if (!existingForms.isEmpty()) {
            throw new IllegalStateException("A prescription already exists for this medical record.");
        }

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Generate form id
            String formId = generateFormId();
            form.setActualPrescriptionFormId(formId);

            // Insert form
            String formSql = "INSERT INTO actual_prescription_form (actual_prescription_form_id, medical_record_id, patient_id, doctor_id, form_name, prescription_date, notes, created_by, updated_by, is_deleted) "
                    + "VALUES (?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?,FALSE)";
            try (PreparedStatement ps = conn.prepareStatement(formSql)) {
                ps.setString(1, formId);
                ps.setString(2, form.getMedicalRecordId());
                ps.setInt(3, form.getPatientId());
                ps.setObject(4, form.getDoctorId());
                ps.setString(5, form.getFormName());
                ps.setString(6, form.getNotes());
                ps.setString(7, form.getCreatedBy());
                ps.setString(8, form.getUpdatedBy());
                ps.executeUpdate();
            }

            // Insert medicines & mapping
            if (medicines != null && !medicines.isEmpty()) {
                addMedicinesAndMappings(conn, formId, medicines);
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Error adding prescription form: {0}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    /* ignore */ }
            }
        }
    }

    public boolean updateForm(ActualPrescriptionForm form, List<ActualPrescriptionMedicine> medicines) {
        if (form == null || form.getActualPrescriptionFormId() == null) {
            throw new IllegalArgumentException("Form data is incomplete");
        }

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Update form
            String formSql = "UPDATE actual_prescription_form SET form_name = ?, notes = ?, updated_at = CURRENT_TIMESTAMP, updated_by = ? WHERE actual_prescription_form_id = ? AND is_deleted = FALSE";
            try (PreparedStatement ps = conn.prepareStatement(formSql)) {
                ps.setString(1, form.getFormName());
                ps.setString(2, form.getNotes());
                ps.setString(3, form.getUpdatedBy());
                ps.setString(4, form.getActualPrescriptionFormId());
                ps.executeUpdate();
            }

            // Delete existing mappings (soft delete medicines optional?)
            deleteMappings(conn, form.getActualPrescriptionFormId());

            // Add medicines again
            if (medicines != null && !medicines.isEmpty()) {
                addMedicinesAndMappings(conn, form.getActualPrescriptionFormId(), medicines);
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Error updating prescription form: {0}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    // ===== MEDICINE OPERATIONS =====
    private List<ActualPrescriptionMedicine> getMedicinesByForm(String formId) {
        List<ActualPrescriptionMedicine> medicines = new ArrayList<>();
        String sql = "SELECT m.* FROM actual_prescription_medicine m "
                + "JOIN actual_prescription_form_medicine fm ON m.actual_pre_medicine_id = fm.actual_pre_medicine_id "
                + "WHERE fm.actual_prescription_form_id = ? AND m.is_deleted = FALSE ORDER BY m.medicine_name";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, formId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicines.add(mapResultSetToMedicine(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting medicines of form: {0}", e.getMessage());
        }
        return medicines;
    }

    // ===== INTERNAL HELPERS =====
    private void addMedicinesAndMappings(Connection conn, String formId, List<ActualPrescriptionMedicine> medicines) throws SQLException {
        // Câu lệnh SQL để chèn thuốc mới (nếu chưa tồn tại)
        String medicineSql = "INSERT INTO actual_prescription_medicine (medicine_name, days_of_treatment, units_per_day, total_quantity, unit_of_measure, administration_route, usage_instructions) "
                + "VALUES (?,?,?,?,?,?,?)";
        // Câu lệnh SQL để chèn liên kết
        String mappingSql = "INSERT INTO actual_prescription_form_medicine (actual_prescription_form_id, actual_pre_medicine_id) VALUES (?,?)";

        try (PreparedStatement medPs = conn.prepareStatement(medicineSql, Statement.RETURN_GENERATED_KEYS); PreparedStatement mapPs = conn.prepareStatement(mappingSql)) {

            for (ActualPrescriptionMedicine med : medicines) {
                int medicineId = 0;
                // Bước 1: Kiểm tra xem thuốc đã tồn tại chưa
                ActualPrescriptionMedicine existingMed = findMedicineByName(med.getMedicineName());

                if (existingMed != null) {
                    // Thuốc đã tồn tại, sử dụng ID của bản ghi đã có
                    medicineId = existingMed.getActualPreMedicineId();
                } else {
                    // Thuốc chưa tồn tại, thêm mới và lấy ID
                    medPs.setString(1, med.getMedicineName());
                    medPs.setObject(2, med.getDaysOfTreatment());
                    medPs.setObject(3, med.getUnitsPerDay());
                    medPs.setObject(4, med.getTotalQuantity());
                    medPs.setString(5, med.getUnitOfMeasure());
                    medPs.setString(6, med.getAdministrationRoute());
                    medPs.setString(7, med.getUsageInstructions());
                    medPs.executeUpdate();

                    try (ResultSet generated = medPs.getGeneratedKeys()) {
                        if (generated.next()) {
                            medicineId = generated.getInt(1);
                        }
                    }
                }

                // Bước 2: Chèn liên kết
                if (medicineId > 0) {
                    mapPs.setString(1, formId);
                    mapPs.setInt(2, medicineId);
                    mapPs.addBatch();
                }
            }
            mapPs.executeBatch();
        }
    }

    public ActualPrescriptionMedicine findMedicineByName(String medicineName) {
        String sql = "SELECT * FROM actual_prescription_medicine WHERE medicine_name = ? AND is_deleted = FALSE";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicineName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicine(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding medicine by name: {0}", e.getMessage());
        }
        return null;
    }

    private void deleteMappings(Connection conn, String formId) throws SQLException {
        // We keep medicine records for history, only delete mappings
        String sql = "DELETE FROM actual_prescription_form_medicine WHERE actual_prescription_form_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, formId);
            ps.executeUpdate();
        }
    }

    private String generateFormId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(actual_prescription_form_id, 4) AS UNSIGNED)) as max_num FROM actual_prescription_form WHERE actual_prescription_form_id LIKE 'PRX%'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                return String.format("PRX%06d", maxNum + 1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating prescription form id: {0}", e.getMessage());
        }
        return "PRX000001";
    }

    private ActualPrescriptionForm mapResultSetToForm(ResultSet rs) throws SQLException {
        ActualPrescriptionForm form = new ActualPrescriptionForm();
        form.setActualPrescriptionFormId(rs.getString("actual_prescription_form_id"));
        form.setMedicalRecordId(rs.getString("medical_record_id"));
        form.setPatientId(rs.getInt("patient_id"));
        form.setDoctorId((Integer) rs.getObject("doctor_id"));
        form.setFormName(rs.getString("form_name"));
        form.setPrescriptionDate(rs.getTimestamp("prescription_date"));
        form.setNotes(rs.getString("notes"));
        form.setCreatedAt(rs.getTimestamp("created_at"));
        form.setUpdatedAt(rs.getTimestamp("updated_at"));
        form.setCreatedBy(rs.getString("created_by"));
        form.setUpdatedBy(rs.getString("updated_by"));
        form.setDeleted(rs.getBoolean("is_deleted"));
        return form;
    }

    private ActualPrescriptionMedicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        ActualPrescriptionMedicine medicine = new ActualPrescriptionMedicine();
        medicine.setActualPreMedicineId(rs.getInt("actual_pre_medicine_id"));
        medicine.setMedicineName(rs.getString("medicine_name"));
        medicine.setDaysOfTreatment((Integer) rs.getObject("days_of_treatment"));
        medicine.setUnitsPerDay((Integer) rs.getObject("units_per_day"));
        medicine.setTotalQuantity((Integer) rs.getObject("total_quantity"));
        medicine.setUnitOfMeasure(rs.getString("unit_of_measure"));
        medicine.setAdministrationRoute(rs.getString("administration_route"));
        medicine.setUsageInstructions(rs.getString("usage_instructions"));
        medicine.setCreatedAt(rs.getTimestamp("created_at"));
        medicine.setUpdatedAt(rs.getTimestamp("updated_at"));
        medicine.setDeleted(rs.getBoolean("is_deleted"));
        return medicine;
    }
}
