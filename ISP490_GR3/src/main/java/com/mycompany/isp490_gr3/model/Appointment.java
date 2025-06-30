package com.mycompany.isp490_gr3.model;

import java.time.LocalDateTime;

public class Appointment {

    private int id;
    private String appointmentCode;

    private int patientId;
    private int slotId;
    private int servicesId;

    private AppointmentStatus status;         // pending, confirmed, done, ...
    private PaymentStatus paymentStatus;      // unpaid, paid

    private LocalDateTime checkinTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean isDeleted;

    // ENUM định nghĩa trạng thái
    public enum AppointmentStatus {
        PENDING, CONFIRMED, DONE, CANCELLED, NO_SHOW, ABANDONED
    }

    public enum PaymentStatus {
        UNPAID, PAID
    }

    // Constructors
    public Appointment() {
    }

    public Appointment(int id, String appointmentCode, int patientId, int slotId, int servicesId,
            AppointmentStatus status, PaymentStatus paymentStatus,
            LocalDateTime checkinTime, LocalDateTime createdAt, LocalDateTime updatedAt,
            boolean isDeleted) {
        this.id = id;
        this.appointmentCode = appointmentCode;
        this.patientId = patientId;
        this.slotId = slotId;
        this.servicesId = servicesId;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.checkinTime = checkinTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppointmentCode() {
        return appointmentCode;
    }

    public void setAppointmentCode(String appointmentCode) {
        this.appointmentCode = appointmentCode;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getServicesId() {
        return servicesId;
    }

    public void setServicesId(int servicesId) {
        this.servicesId = servicesId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
