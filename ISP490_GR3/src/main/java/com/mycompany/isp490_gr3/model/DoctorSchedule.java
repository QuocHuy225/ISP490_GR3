package com.mycompany.isp490_gr3.model;

import java.sql.Date; // Use java.sql.Date for database interaction
import java.sql.Timestamp;

public class DoctorSchedule {
    private String id; // Changed to String to match frontend dummy data for easier mapping
    private int doctorId;
    private Date workDate; // Use java.sql.Date for database DATE type
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Additional fields for frontend display (not directly from DB table, but useful)
    private String doctorName;
    private String name; // This maps to 'eventName' from frontend

    // Constructors
    public DoctorSchedule() {}

    public DoctorSchedule(String id, int doctorId, Date workDate, boolean isActive, Timestamp createdAt, Timestamp updatedAt, String doctorName, String name) {
        this.id = id;
        this.doctorId = doctorId;
        this.workDate = workDate;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.doctorName = doctorName;
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
