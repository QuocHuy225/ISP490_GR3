package com.mycompany.isp490_gr3.dto;

public class SlotViewDTO {

    private int id;
    private String slotDate;           // Ngày slot (yyyy-MM-dd)
    private String startTime;          // Giờ bắt đầu
    private String endTime;            // Giờ kết thúc
    private String doctorName;         // Tên bác sĩ
    private int maxPatients;           // Số bệnh nhân tối đa
    private int bookedPatients;        // Số bệnh nhân đã đặt

    private String checkinRange;       // Thời gian khám (computed)
    private String bookingStatus;      // Số đã đặt / tổng số (computed)

    // Constructors
    public SlotViewDTO() {
    }

    public SlotViewDTO(int id, String slotDate, String startTime, String endTime,
            String doctorName, int maxPatients, int bookedPatients) {
        this.id = id;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.doctorName = doctorName;
        this.maxPatients = maxPatients;
        this.bookedPatients = bookedPatients;
        this.checkinRange = startTime + " - " + endTime;
        this.bookingStatus = bookedPatients + "/" + maxPatients;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(String slotDate) {
        this.slotDate = slotDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        updateCheckinRange();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
        updateCheckinRange();
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public int getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(int maxPatients) {
        this.maxPatients = maxPatients;
        updateBookingStatus();
    }

    public int getBookedPatients() {
        return bookedPatients;
    }

    public void setBookedPatients(int bookedPatients) {
        this.bookedPatients = bookedPatients;
        updateBookingStatus();
    }

    public String getCheckinRange() {
        return checkinRange;
    }

    public void setCheckinRange(String checkinRange) {
        this.checkinRange = checkinRange;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

   
    private void updateCheckinRange() {
        if (startTime != null && endTime != null) {
            this.checkinRange = startTime + " - " + endTime;
        }
    }

    private void updateBookingStatus() {
        this.bookingStatus = bookedPatients + "/" + maxPatients;
    }
}
