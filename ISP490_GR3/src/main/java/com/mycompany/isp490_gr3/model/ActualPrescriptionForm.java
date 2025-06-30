package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Model đại diện cho bảng actual_prescription_form (đơn thuốc thực tế gắn với hồ sơ bệnh án).
 */
public class ActualPrescriptionForm {

    private String actualPrescriptionFormId;
    private String medicalRecordId;
    private int patientId;
    private Integer doctorId;
    private String formName;
    private Timestamp prescriptionDate;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean deleted;

    // Quan hệ
    private List<ActualPrescriptionMedicine> medicines;

    public ActualPrescriptionForm() {
        this.deleted = false;
    }

    public String getActualPrescriptionFormId() {
        return actualPrescriptionFormId;
    }

    public void setActualPrescriptionFormId(String actualPrescriptionFormId) {
        this.actualPrescriptionFormId = actualPrescriptionFormId;
    }

    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Timestamp getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(Timestamp prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<ActualPrescriptionMedicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<ActualPrescriptionMedicine> medicines) {
        this.medicines = medicines;
    }
} 