package com.mycompany.isp490_gr3.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Slot {

    private int id;
    private int doctorId;
    private String doctorName;           // Dùng để hiển thị (join với bảng doctor)
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String duration;
    private int maxPatients;
    private int bookedPatients;          // Đã đặt (JOIN từ bảng appointment)
    private boolean isAvailable;
    private boolean isDeleted;
    private String slotCode;             // Mã định danh của slot (nếu có dùng)
    private String status;               // "available", "full", "closed", v.v.

    // Constructors
    public Slot() {
    }

    public Slot(int id, int doctorId, LocalDate slotDate, LocalTime startTime, LocalTime endTime,
            String duration, int maxPatients, boolean isAvailable, boolean isDeleted) {
        this.id = id;
        this.doctorId = doctorId;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.maxPatients = maxPatients;
        this.isAvailable = isAvailable;
        this.isDeleted = isDeleted;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(int maxPatients) {
        this.maxPatients = maxPatients;
    }

    public int getBookedPatients() {
        return bookedPatients;
    }

    public void setBookedPatients(int bookedPatients) {
        this.bookedPatients = bookedPatients;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Optional: override toString() for debug
    @Override
    public String toString() {
        return "Slot{"
                + "id=" + id
                + ", doctorId=" + doctorId
                + ", doctorName='" + doctorName + '\''
                + ", slotDate=" + slotDate
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", duration='" + duration + '\''
                + ", maxPatients=" + maxPatients
                + ", bookedPatients=" + bookedPatients
                + ", isAvailable=" + isAvailable
                + ", isDeleted=" + isDeleted
                + ", slotCode='" + slotCode + '\''
                + ", status='" + status + '\''
                + '}';
    }
}
