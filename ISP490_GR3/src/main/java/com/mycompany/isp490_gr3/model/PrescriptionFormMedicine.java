package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;

/**
 * PrescriptionFormMedicine model tương ứng với bảng prescription_form_medicine trong database
 * Đây là bảng kết nối giữa prescription_form và prescription_medicine (many-to-many relationship)
 */
public class PrescriptionFormMedicine {
    
    private int id;
    private int prescriptionFormId;
    private int preMedicineId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isDeleted;
    
    // Default constructor
    public PrescriptionFormMedicine() {
        this.isDeleted = false;
    }
    
    // Constructor with essential fields
    public PrescriptionFormMedicine(int prescriptionFormId, int preMedicineId) {
        this();
        this.prescriptionFormId = prescriptionFormId;
        this.preMedicineId = preMedicineId;
    }
    
    // Full constructor
    public PrescriptionFormMedicine(int id, int prescriptionFormId, int preMedicineId) {
        this();
        this.id = id;
        this.prescriptionFormId = prescriptionFormId;
        this.preMedicineId = preMedicineId;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPrescriptionFormId() {
        return prescriptionFormId;
    }
    
    public void setPrescriptionFormId(int prescriptionFormId) {
        this.prescriptionFormId = prescriptionFormId;
    }
    
    public int getPreMedicineId() {
        return preMedicineId;
    }
    
    public void setPreMedicineId(int preMedicineId) {
        this.preMedicineId = preMedicineId;
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
    
    @Override
    public String toString() {
        return "PrescriptionFormMedicine{" +
                "id=" + id +
                ", prescriptionFormId=" + prescriptionFormId +
                ", preMedicineId=" + preMedicineId +
                '}';
    }
} 