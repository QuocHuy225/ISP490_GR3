package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;

public class Queue {

    private int id;
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private int slotId;
    private String queueType; // ENUM: 'main'
    private String status;    // ENUM: 'waiting', 'in_progress', 'completed', 'skipped', 'rejected'
    private int priority;     // 0: Trung bình, 1: Cao
    private String description;
    private boolean isDeleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Queue() {
    }

    // Full constructor
    public Queue(int id, int appointmentId, int patientId, int doctorId, int slotId,
                 String queueType, String status, int priority, String description,
                 boolean isDeleted, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.queueType = queueType;
        this.status = status;
        this.priority = priority;
        this.description = description;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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
}
