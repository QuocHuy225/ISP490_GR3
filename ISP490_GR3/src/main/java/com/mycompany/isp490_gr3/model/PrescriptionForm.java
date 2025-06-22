package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * PrescriptionForm model tương ứng với bảng prescription_form trong database
 */
public class PrescriptionForm {
    
    private int prescriptionFormId;
    private String formName;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isDeleted;
    
    // Danh sách thuốc trong đơn mẫu này
    private List<PrescriptionMedicine> medicines;
    
    // Default constructor
    public PrescriptionForm() {
        this.isDeleted = false;
    }
    
    // Constructor with essential fields
    public PrescriptionForm(String formName, String notes) {
        this();
        this.formName = formName;
        this.notes = notes;
    }
    
    // Full constructor
    public PrescriptionForm(int prescriptionFormId, String formName, String notes) {
        this();
        this.prescriptionFormId = prescriptionFormId;
        this.formName = formName;
        this.notes = notes;
    }
    
    // Getters and Setters
    public int getPrescriptionFormId() {
        return prescriptionFormId;
    }
    
    public void setPrescriptionFormId(int prescriptionFormId) {
        this.prescriptionFormId = prescriptionFormId;
    }
    
    public String getFormName() {
        return formName;
    }
    
    public void setFormName(String formName) {
        this.formName = formName;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    
    public List<PrescriptionMedicine> getMedicines() {
        return medicines;
    }
    
    public void setMedicines(List<PrescriptionMedicine> medicines) {
        this.medicines = medicines;
    }
    
    @Override
    public String toString() {
        return "PrescriptionForm{" +
                "prescriptionFormId=" + prescriptionFormId +
                ", formName='" + formName + '\'' +
                ", notes='" + notes + '\'' +
                ", medicinesCount=" + (medicines != null ? medicines.size() : 0) +
                '}';
    }
} 