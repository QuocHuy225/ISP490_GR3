package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;

/**
 * MedicalExamTemplate model tương ứng với bảng medical_exam_templates trong database
 */
public class MedicalExamTemplate {
    
    private int id;
    private String name;
    private String physicalExam;     // Khám lâm sàng
    private String clinicalInfo;     // Thông tin lâm sàng  
    private String finalDiagnosis;   // Chẩn đoán cuối cùng
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isDeleted;
    
    // Default constructor
    public MedicalExamTemplate() {
        this.isDeleted = false;
    }
    
    // Constructor with essential fields
    public MedicalExamTemplate(String name, String physicalExam, String clinicalInfo, String finalDiagnosis) {
        this();
        this.name = name;
        this.physicalExam = physicalExam;
        this.clinicalInfo = clinicalInfo;
        this.finalDiagnosis = finalDiagnosis;
    }
    
    // Full constructor
    public MedicalExamTemplate(int id, String name, String physicalExam, String clinicalInfo, 
                             String finalDiagnosis, Timestamp createdAt, Timestamp updatedAt, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.physicalExam = physicalExam;
        this.clinicalInfo = clinicalInfo;
        this.finalDiagnosis = finalDiagnosis;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
        return "MedicalExamTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", physicalExam='" + physicalExam + '\'' +
                ", clinicalInfo='" + clinicalInfo + '\'' +
                ", finalDiagnosis='" + finalDiagnosis + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }
} 