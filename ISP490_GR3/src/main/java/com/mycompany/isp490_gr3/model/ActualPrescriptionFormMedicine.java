package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;

/**
 * Model đại diện cho bảng actual_prescription_form_medicine (bảng phụ nối).
 */
public class ActualPrescriptionFormMedicine {

    private Integer id;
    private String actualPrescriptionFormId;
    private Integer actualPreMedicineId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean deleted;

    public ActualPrescriptionFormMedicine() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActualPrescriptionFormId() {
        return actualPrescriptionFormId;
    }

    public void setActualPrescriptionFormId(String actualPrescriptionFormId) {
        this.actualPrescriptionFormId = actualPrescriptionFormId;
    }

    public Integer getActualPreMedicineId() {
        return actualPreMedicineId;
    }

    public void setActualPreMedicineId(Integer actualPreMedicineId) {
        this.actualPreMedicineId = actualPreMedicineId;
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
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
} 