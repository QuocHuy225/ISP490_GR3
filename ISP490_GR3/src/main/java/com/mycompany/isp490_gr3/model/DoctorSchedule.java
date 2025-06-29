/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.model;

import java.sql.Date;

/**
 *
 * @author FPT SHOP
 */
public class DoctorSchedule {

    private int id;
    private int doctorId;
    private Date workDate;
    private boolean isActive;

    public DoctorSchedule() {
    }

    public DoctorSchedule(int id, int doctorId, Date workDate, boolean isActive) {
        this.id = id;
        this.doctorId = doctorId;
        this.workDate = workDate;
        this.isActive = isActive;
    }

    // Getters và setters
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
}
