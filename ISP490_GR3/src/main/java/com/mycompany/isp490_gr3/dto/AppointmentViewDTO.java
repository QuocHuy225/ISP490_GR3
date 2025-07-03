/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.dto;

/**
 *
 * @author FPT SHOP
 */
public class AppointmentViewDTO {

    private int id;
    private String appointmentCode;
    private String slotDate;
    private String slotTimeRange;
    private String patientCode;
    private String patientName;
    private String patientPhone;
    private String doctorName;
    private String serviceName;
    private String status;
    private String paymentStatus;
    private int bookedPatients;
    private int maxPatients;
    private String bookingStatus;

    public AppointmentViewDTO() {
    }

    public AppointmentViewDTO(int id, String appointmentCode, String slotDate, String slotTimeRange, String patientCode, String patientName, String patientPhone, String doctorName, String serviceName, String status, String paymentStatus, int bookedPatients, int maxPatients, String bookingStatus) {
        this.id = id;
        this.appointmentCode = appointmentCode;
        this.slotDate = slotDate;
        this.slotTimeRange = slotTimeRange;
        this.patientCode = patientCode;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.doctorName = doctorName;
        this.serviceName = serviceName;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.bookedPatients = bookedPatients;
        this.maxPatients = maxPatients;
        this.bookingStatus = bookingStatus;
    }

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

    public String getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(String slotDate) {
        this.slotDate = slotDate;
    }

    public String getSlotTimeRange() {
        return slotTimeRange;
    }

    public void setSlotTimeRange(String slotTimeRange) {
        this.slotTimeRange = slotTimeRange;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getBookedPatients() {
        return bookedPatients;
    }

    public void setBookedPatients(int bookedPatients) {
        this.bookedPatients = bookedPatients;
    }

    public int getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(int maxPatients) {
        this.maxPatients = maxPatients;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

}
