package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.PrescriptionMedicine;
import com.mycompany.isp490_gr3.model.PrescriptionForm;
import com.mycompany.isp490_gr3.model.PrescriptionFormMedicine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================
 * DAOPrescription - QUẢN LÝ ĐƠN THUỐC
 * 
 * Chức năng: Xử lý tất cả các thao tác liên quan đến đơn thuốc
 * Bảng database: prescription_medicine, prescription_form, prescription_form_medicine
 * Model tương ứng: PrescriptionMedicine, PrescriptionForm, PrescriptionFormMedicine
 * 
 * Các chức năng chính:
 * - Quản lý thuốc trên thị trường (CRUD + search)
 * - Quản lý đơn thuốc mẫu cho các bệnh (CRUD + search)
 * - Quản lý thuốc trong đơn mẫu
 * =====================================================
 */
public class DAOPrescription {
    
    // ===============================
    // PRESCRIPTION MEDICINE METHODS
    // ===============================
    
    /**
     * Lấy tất cả thuốc trong hệ thống
     */
    public List<PrescriptionMedicine> getAllMedicines() {
        List<PrescriptionMedicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM prescription_medicine WHERE is_deleted = FALSE ORDER BY medicine_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all medicines: " + e.getMessage());
            e.printStackTrace();
        }
        
        return medicines;
    }
    
    /**
     * Tìm kiếm thuốc theo tên
     */
    public List<PrescriptionMedicine> searchMedicines(String keyword) {
        List<PrescriptionMedicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM prescription_medicine WHERE medicine_name LIKE ? AND is_deleted = FALSE ORDER BY medicine_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching medicines: " + e.getMessage());
            e.printStackTrace();
        }
        
        return medicines;
    }
    
    /**
     * Lấy thuốc theo ID
     */
    public PrescriptionMedicine getMedicineById(int id) {
        String sql = "SELECT * FROM prescription_medicine WHERE pre_medicine_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMedicine(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting medicine by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Thêm thuốc mới
     */
    public boolean addMedicine(PrescriptionMedicine medicine) {
        String sql = "INSERT INTO prescription_medicine (medicine_name, days_of_treatment, units_per_day, " +
                    "total_quantity, unit_of_measure, administration_route, usage_instructions) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medicine.getMedicineName());
            ps.setObject(2, medicine.getDaysOfTreatment());
            ps.setObject(3, medicine.getUnitsPerDay());
            ps.setObject(4, medicine.getTotalQuantity());
            ps.setString(5, medicine.getUnitOfMeasure());
            ps.setString(6, medicine.getAdministrationRoute());
            ps.setString(7, medicine.getUsageInstructions());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cập nhật thông tin thuốc
     */
    public boolean updateMedicine(PrescriptionMedicine medicine) {
        String sql = "UPDATE prescription_medicine SET medicine_name = ?, days_of_treatment = ?, " +
                    "units_per_day = ?, total_quantity = ?, unit_of_measure = ?, " +
                    "administration_route = ?, usage_instructions = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE pre_medicine_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medicine.getMedicineName());
            ps.setObject(2, medicine.getDaysOfTreatment());
            ps.setObject(3, medicine.getUnitsPerDay());
            ps.setObject(4, medicine.getTotalQuantity());
            ps.setString(5, medicine.getUnitOfMeasure());
            ps.setString(6, medicine.getAdministrationRoute());
            ps.setString(7, medicine.getUsageInstructions());
            ps.setInt(8, medicine.getPreMedicineId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Xóa thuốc (soft delete)
     */
    public boolean deleteMedicine(int medicineId) {
        String sql = "UPDATE prescription_medicine SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE pre_medicine_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, medicineId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting medicine: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kiểm tra tên thuốc đã tồn tại chưa
     */
    public boolean isMedicineNameExists(String medicineName, int excludeId) {
        String sql = "SELECT COUNT(*) FROM prescription_medicine WHERE medicine_name = ? AND is_deleted = FALSE";
        if (excludeId > 0) {
            sql += " AND pre_medicine_id != ?";
        }
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medicineName);
            if (excludeId > 0) {
                ps.setInt(2, excludeId);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking medicine name existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ===============================
    // PRESCRIPTION FORM METHODS
    // ===============================
    
    /**
     * Lấy tất cả đơn thuốc mẫu
     */
    public List<PrescriptionForm> getAllPrescriptionForms() {
        List<PrescriptionForm> forms = new ArrayList<>();
        String sql = "SELECT * FROM prescription_form WHERE is_deleted = FALSE ORDER BY form_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PrescriptionForm form = mapResultSetToForm(rs);
                // Lấy danh sách thuốc cho mỗi form
                form.setMedicines(getMedicinesByFormId(form.getPrescriptionFormId()));
                forms.add(form);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all prescription forms: " + e.getMessage());
            e.printStackTrace();
        }
        
        return forms;
    }
    
    /**
     * Tìm kiếm đơn thuốc mẫu theo tên
     */
    public List<PrescriptionForm> searchPrescriptionForms(String keyword) {
        List<PrescriptionForm> forms = new ArrayList<>();
        String sql = "SELECT * FROM prescription_form WHERE form_name LIKE ? AND is_deleted = FALSE ORDER BY form_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                PrescriptionForm form = mapResultSetToForm(rs);
                form.setMedicines(getMedicinesByFormId(form.getPrescriptionFormId()));
                forms.add(form);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching prescription forms: " + e.getMessage());
            e.printStackTrace();
        }
        
        return forms;
    }
    
    /**
     * Lấy đơn thuốc mẫu theo ID
     */
    public PrescriptionForm getPrescriptionFormById(int id) {
        String sql = "SELECT * FROM prescription_form WHERE prescription_form_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                PrescriptionForm form = mapResultSetToForm(rs);
                form.setMedicines(getMedicinesByFormId(form.getPrescriptionFormId()));
                return form;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting prescription form by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Thêm đơn thuốc mẫu mới
     */
    public int addPrescriptionForm(PrescriptionForm form) {
        String sql = "INSERT INTO prescription_form (form_name, notes) VALUES (?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, form.getFormName());
            ps.setString(2, form.getNotes());
            
            int result = ps.executeUpdate();
            if (result > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding prescription form: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Cập nhật đơn thuốc mẫu
     */
    public boolean updatePrescriptionForm(PrescriptionForm form) {
        String sql = "UPDATE prescription_form SET form_name = ?, notes = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE prescription_form_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, form.getFormName());
            ps.setString(2, form.getNotes());
            ps.setInt(3, form.getPrescriptionFormId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating prescription form: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Xóa đơn thuốc mẫu (soft delete)
     */
    public boolean deletePrescriptionForm(int formId) {
        String sql = "UPDATE prescription_form SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE prescription_form_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, formId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting prescription form: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kiểm tra tên đơn thuốc mẫu đã tồn tại chưa
     */
    public boolean isFormNameExists(String formName, int excludeId) {
        String sql = "SELECT COUNT(*) FROM prescription_form WHERE form_name = ? AND is_deleted = FALSE";
        if (excludeId > 0) {
            sql += " AND prescription_form_id != ?";
        }
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, formName);
            if (excludeId > 0) {
                ps.setInt(2, excludeId);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking form name existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ===============================
    // PRESCRIPTION FORM MEDICINE METHODS
    // ===============================
    
    /**
     * Lấy danh sách thuốc theo form ID
     */
    public List<PrescriptionMedicine> getMedicinesByFormId(int formId) {
        List<PrescriptionMedicine> medicines = new ArrayList<>();
        String sql = "SELECT pm.* FROM prescription_medicine pm " +
                    "INNER JOIN prescription_form_medicine pfm ON pm.pre_medicine_id = pfm.pre_medicine_id " +
                    "WHERE pfm.prescription_form_id = ? AND pm.is_deleted = FALSE AND pfm.is_deleted = FALSE " +
                    "ORDER BY pm.medicine_name";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, formId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting medicines by form ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return medicines;
    }
    
    /**
     * Thêm thuốc vào đơn mẫu
     */
    public boolean addMedicineToForm(int formId, int medicineId) {
        // Kiểm tra xem thuốc đã có trong form chưa
        if (isMedicineInForm(formId, medicineId)) {
            return false;
        }
        
        String sql = "INSERT INTO prescription_form_medicine (prescription_form_id, pre_medicine_id) VALUES (?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, formId);
            ps.setInt(2, medicineId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding medicine to form: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Xóa thuốc khỏi đơn mẫu
     */
    public boolean removeMedicineFromForm(int formId, int medicineId) {
        String sql = "UPDATE prescription_form_medicine SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE prescription_form_id = ? AND pre_medicine_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, formId);
            ps.setInt(2, medicineId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing medicine from form: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cập nhật toàn bộ thuốc trong đơn mẫu
     */
    public boolean updateFormMedicines(int formId, List<Integer> medicineIds) {
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            
            // Xóa tất cả thuốc hiện tại trong form
            String deleteSql = "UPDATE prescription_form_medicine SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " +
                              "WHERE prescription_form_id = ?";
            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setInt(1, formId);
                deletePs.executeUpdate();
            }
            
            // Thêm lại thuốc mới
            if (medicineIds != null && !medicineIds.isEmpty()) {
                String insertSql = "INSERT INTO prescription_form_medicine (prescription_form_id, pre_medicine_id) VALUES (?, ?)";
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    for (Integer medicineId : medicineIds) {
                        insertPs.setInt(1, formId);
                        insertPs.setInt(2, medicineId);
                        insertPs.addBatch();
                    }
                    insertPs.executeBatch();
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error updating form medicines: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kiểm tra thuốc đã có trong đơn mẫu chưa
     */
    private boolean isMedicineInForm(int formId, int medicineId) {
        String sql = "SELECT COUNT(*) FROM prescription_form_medicine " +
                    "WHERE prescription_form_id = ? AND pre_medicine_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, formId);
            ps.setInt(2, medicineId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking medicine in form: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ===============================
    // HELPER METHODS
    // ===============================
    
    private PrescriptionMedicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        PrescriptionMedicine medicine = new PrescriptionMedicine();
        medicine.setPreMedicineId(rs.getInt("pre_medicine_id"));
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
    
    private PrescriptionForm mapResultSetToForm(ResultSet rs) throws SQLException {
        PrescriptionForm form = new PrescriptionForm();
        form.setPrescriptionFormId(rs.getInt("prescription_form_id"));
        form.setFormName(rs.getString("form_name"));
        form.setNotes(rs.getString("notes"));
        form.setCreatedAt(rs.getTimestamp("created_at"));
        form.setUpdatedAt(rs.getTimestamp("updated_at"));
        form.setDeleted(rs.getBoolean("is_deleted"));
        return form;
    }
} 