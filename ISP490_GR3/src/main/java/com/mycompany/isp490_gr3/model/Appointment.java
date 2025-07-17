package com.mycompany.isp490_gr3.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Added for formatting LocalDateTime to String

public class Appointment {

    private int id;
    private String appointmentCode; 

    private int patientId;
    private int slotId;
    private int servicesId;

    private AppointmentStatus status;
    private PaymentStatus paymentStatus;

    private LocalDateTime checkinTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean isDeleted;

    // Additional fields for display in frontend (derived from joins) - RE-ADDED
    private String patientFullName;
    private String appointmentDate; // Derived from slot.start_time (YYYY-MM-DD)
    private String appointmentTime; // Derived from slot.start_time (HH:mm)
    private String doctorFullName;
    private String serviceName;
    private String notes; // This is a placeholder, needs to be fetched if available

    // ENUM định nghĩa trạng thái
    public enum AppointmentStatus {
        PENDING, CONFIRMED, DONE, CANCELLED, NO_SHOW, ABANDONED;

        // Helper to convert string from DB to enum
        public static AppointmentStatus fromString(String text) {
            if (text != null) {
                for (AppointmentStatus b : AppointmentStatus.values()) {
                    if (b.name().equalsIgnoreCase(text)) {
                        return b;
                    }
                }
            }
            // Log a warning or throw an exception if an unknown status is encountered
            System.err.println("Unknown AppointmentStatus: " + text + ". Defaulting to PENDING.");
            return PENDING; // Default or handle error
        }
    }

    public enum PaymentStatus {
        UNPAID, PAID;

        // Helper to convert string from DB to enum
        public static PaymentStatus fromString(String text) {
            if (text != null) {
                for (PaymentStatus b : PaymentStatus.values()) {
                    if (b.name().equalsIgnoreCase(text)) {
                        return b;
                    }
                }
            }
            // Log a warning or throw an exception if an unknown status is encountered
            System.err.println("Unknown PaymentStatus: " + text + ". Defaulting to UNPAID.");
            return UNPAID; // Default or handle error
        }
    }

    // Constructors
    public Appointment() {
    }

    // Constructor for creating a new appointment (from frontend input)
    // Notes field is included here for frontend consistency, but backend needs to handle its storage.
    public Appointment(int patientId, int slotId, int servicesId, String notes) {
        this.patientId = patientId;
        this.slotId = slotId;
        this.servicesId = servicesId;
        this.notes = notes;
        this.status = AppointmentStatus.PENDING; // Default status for new appointments
        this.paymentStatus = PaymentStatus.UNPAID; // Default payment status
    }

    // Full constructor for retrieving from DB (with derived fields)
    public Appointment(int id, String appointmentCode, int patientId, int slotId, int servicesId,
            AppointmentStatus status, PaymentStatus paymentStatus,
            LocalDateTime checkinTime, LocalDateTime createdAt, LocalDateTime updatedAt,
            boolean isDeleted,
            String patientFullName, String appointmentDate, String appointmentTime,
            String doctorFullName, String serviceName, String notes) {
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
        this.patientFullName = patientFullName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.doctorFullName = doctorFullName;
        this.serviceName = serviceName; // Corrected typo from serviceService
        this.notes = notes;
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
        // Automatically set appointmentCode from id if not explicitly set
        // This ensures appointmentCode is always available for frontend
        if (this.appointmentCode == null || this.appointmentCode.isEmpty()) {
            this.appointmentCode = String.format("APP%03d", id);
        }
    }

    public String getAppointmentCode() {
        // If appointmentCode is not explicitly set, derive it from id
        if (this.appointmentCode == null && this.id != 0) {
            return String.format("APP%03d", this.id);
        }
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

    // Getters and Setters for derived fields (for frontend display)
    public String getPatientFullName() {
        return patientFullName;
    }

    public void setPatientFullName(String patientFullName) {
        this.patientFullName = patientFullName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getDoctorFullName() {
        return doctorFullName;
    }

    public void setDoctorFullName(String doctorFullName) {
        this.doctorFullName = doctorFullName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
