package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;
import java.math.BigDecimal;

/**
 * Model for Medical Record
 */
public class MedicalRecord {
    private String id;
    private int patientId;
    private Integer doctorId; // Nullable
    
    // Vital signs
    private Integer respirationRate;
    private BigDecimal temperature;
    private BigDecimal height;
    private Integer pulse;
    private BigDecimal bmi;
    private BigDecimal weight;
    private String bloodPressure;
    private BigDecimal spo2;
    
    // Medical information
    private String medicalHistory;
    private String currentDisease;
    private String physicalExam;
    private String clinicalInfo;
    private String finalDiagnosis;
    private String treatmentPlan;
    private String note;
    
    // Management
    private Timestamp createdAt;
    private String createdBy;
    private Timestamp updatedAt;
    private String updatedBy;
    private String status; // 'ongoing' or 'completed'
    
    // Constructors
    public MedicalRecord() {
        this.status = "ongoing";
    }
    
    public MedicalRecord(String id, int patientId) {
        this.id = id;
        this.patientId = patientId;
        this.status = "ongoing";
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public Integer getRespirationRate() {
        return respirationRate;
    }
    
    public void setRespirationRate(Integer respirationRate) {
        this.respirationRate = respirationRate;
    }
    
    public BigDecimal getTemperature() {
        return temperature;
    }
    
    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }
    
    public BigDecimal getHeight() {
        return height;
    }
    
    public void setHeight(BigDecimal height) {
        this.height = height;
    }
    
    public Integer getPulse() {
        return pulse;
    }
    
    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }
    
    public BigDecimal getBmi() {
        return bmi;
    }
    
    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public String getBloodPressure() {
        return bloodPressure;
    }
    
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
    
    public BigDecimal getSpo2() {
        return spo2;
    }
    
    public void setSpo2(BigDecimal spo2) {
        this.spo2 = spo2;
    }
    
    public String getMedicalHistory() {
        return medicalHistory;
    }
    
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public String getCurrentDisease() {
        return currentDisease;
    }
    
    public void setCurrentDisease(String currentDisease) {
        this.currentDisease = currentDisease;
    }
    
    public String getPhysicalExam() {
        return physicalExam;
    }
    
    public void setPhysicalExam(String physicalExam) {
        this.physicalExam = physicalExam;
    }
    
    public String getClinicalInfo() {
        return clinicalInfo;
    }
    
    public void setClinicalInfo(String clinicalInfo) {
        this.clinicalInfo = clinicalInfo;
    }
    
    public String getFinalDiagnosis() {
        return finalDiagnosis;
    }
    
    public void setFinalDiagnosis(String finalDiagnosis) {
        this.finalDiagnosis = finalDiagnosis;
    }
    
    public String getTreatmentPlan() {
        return treatmentPlan;
    }
    
    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Helper methods
    public boolean isOngoing() {
        return "ongoing".equalsIgnoreCase(status);
    }
    
    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }
    
    public String getStatusDisplay() {
        if (isCompleted()) {
            return "Completed";
        } else {
            return "Ongoing";
        }
    }
} 