package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;

public class MedicalRequest {
    private int id;
    private String clinicName;
    private String clinicPhone;
    private String clinicAddress;
    private String instructionContent;
    private String instructionRequirements;
    private String notes;
    private String createdBy;
    private Timestamp createdAt;
    
    // Foreign key references
    private int patientId;
    private String medicalRecordId;
    
    // Relations
    private Patient patient;
    private MedicalRecord medicalRecord;
    
    // Constructors
    public MedicalRequest() {
    }
    
    public MedicalRequest(int patientId, String medicalRecordId) {
        this.patientId = patientId;
        this.medicalRecordId = medicalRecordId;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getClinicName() {
        return clinicName;
    }
    
    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }
    
    public String getClinicPhone() {
        return clinicPhone;
    }
    
    public void setClinicPhone(String clinicPhone) {
        this.clinicPhone = clinicPhone;
    }
    
    public String getClinicAddress() {
        return clinicAddress;
    }
    
    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }
    
    public String getInstructionContent() {
        return instructionContent;
    }
    
    public void setInstructionContent(String instructionContent) {
        this.instructionContent = instructionContent;
    }
    
    public String getInstructionRequirements() {
        return instructionRequirements;
    }
    
    public void setInstructionRequirements(String instructionRequirements) {
        this.instructionRequirements = instructionRequirements;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public String getMedicalRecordId() {
        return medicalRecordId;
    }
    
    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }
    
    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }
} 