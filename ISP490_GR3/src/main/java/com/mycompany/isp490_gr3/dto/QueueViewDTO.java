package com.mycompany.isp490_gr3.dto;

public class QueueViewDTO {

    private int queueId;
    private int appointmentId;
    private String appointmentCode;
    private String slotDate;
    private String slotTimeRange;
    private String patientCode;
    private String patientName;
    private String patientPhone;
    private String serviceName;
    private String status;
    private int priority;
    private String checkinTime;
    private String doctorName;
    private boolean isBeforeCurrentTime;

    public QueueViewDTO() {
    }

    public QueueViewDTO(int queueId, int appointmentId, String appointmentCode, String slotDate, String slotTimeRange, String patientCode, String patientName, String patientPhone, String serviceName, String status, int priority, String checkinTime, String doctorName) {
        this.queueId = queueId;
        this.appointmentId = appointmentId;
        this.appointmentCode = appointmentCode;
        this.slotDate = slotDate;
        this.slotTimeRange = slotTimeRange;
        this.patientCode = patientCode;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.serviceName = serviceName;
        this.status = status;
        this.priority = priority;
        this.checkinTime = checkinTime;
        this.doctorName = doctorName;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

   
    public boolean isBeforeCurrentTime() {
        return isBeforeCurrentTime;
    }

    public void setBeforeCurrentTime(boolean isBeforeCurrentTime) {
        this.isBeforeCurrentTime = isBeforeCurrentTime;
    }

}
