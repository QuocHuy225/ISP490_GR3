package com.mycompany.isp490_gr3.model;

import java.time.LocalDateTime;
import java.sql.Timestamp; // Nếu bạn dùng java.sql.Timestamp cho created_at/updated_at
import java.time.LocalTime; // Nếu bạn muốn dùng LocalTime cho duration

public class Appointment {

    private int id;
    private String appointmentCode;
    private int patientId;
    private Integer doctorId; 
    private Integer slotId;    
    private String status; 
    private String createdBy;
    private LocalDateTime createdAt; 
    private String updatedBy;
    private LocalDateTime updatedAt; 
    private boolean isDeleted;

    private String patientName;
    private String patientPhoneNumber;
    private String patientAddress;
    private String doctorName;
    private String serviceName; 
    private LocalDateTime appointmentDate; 
    private LocalTime appointmentTime;   

    // Constructors

    public Appointment() {
    }

    // Constructor đầy đủ (có thể cần điều chỉnh nếu số lượng tham số quá nhiều)
    public Appointment(int id, String appointmentCode, int patientId, Integer doctorId, Integer slotId, String status,
                       String createdBy, LocalDateTime createdAt, String updatedBy, LocalDateTime updatedAt, boolean isDeleted,
                       String patientName, String patientPhoneNumber, String patientAddress, String doctorName,
                       String serviceName, LocalDateTime appointmentDate, LocalTime appointmentTime) {
        this.id = id;
        this.appointmentCode = appointmentCode;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
        this.patientName = patientName;
        this.patientPhoneNumber = patientPhoneNumber;
        this.patientAddress = patientAddress;
        this.doctorName = doctorName;
        this.serviceName = serviceName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }
    

    public Appointment(String appointmentCode, int patientId, Integer doctorId, Integer slotId, String status) {
        this.appointmentCode = appointmentCode;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.status = status;
        this.isDeleted = false; 
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public Integer getSlotId() { return slotId; }
    public void setSlotId(Integer slotId) { this.slotId = slotId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isIsDeleted() { return isDeleted; } // Getter cho boolean thường là is<PropertyName>
    public void setIsDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }

   
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientPhoneNumber() { return patientPhoneNumber; }
    public void setPatientPhoneNumber(String patientPhoneNumber) { this.patientPhoneNumber = patientPhoneNumber; }
    public String getPatientAddress() { return patientAddress; }
    public void setPatientAddress(String patientAddress) { this.patientAddress = patientAddress; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", appointmentCode='" + appointmentCode + '\'' +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", slotId=" + slotId +
                ", status='" + status + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                ", patientName='" + patientName + '\'' +
                ", patientPhoneNumber='" + patientPhoneNumber + '\'' +
                ", patientAddress='" + patientAddress + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                '}';
    }
}