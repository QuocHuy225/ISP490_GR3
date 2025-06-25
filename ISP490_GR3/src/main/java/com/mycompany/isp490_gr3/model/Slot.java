package com.mycompany.isp490_gr3.model;

public class Slot {
    private int id;
    private String slotCode;
    private String slotDate;
    private String slotTime;
    private int doctorId;
    private String doctorName;
    private String status;

    public Slot() {}

    public Slot(int id, String slotCode, String slotDate, String slotTime, int doctorId, String doctorName, String status) {
        this.id = id;
        this.slotCode = slotCode;
        this.slotDate = slotDate;
        this.slotTime = slotTime;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.status = status;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSlotCode() { return slotCode; }
    public void setSlotCode(String slotCode) { this.slotCode = slotCode; }
    public String getSlotDate() { return slotDate; }
    public void setSlotDate(String slotDate) { this.slotDate = slotDate; }
    public String getSlotTime() { return slotTime; }
    public void setSlotTime(String slotTime) { this.slotTime = slotTime; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}