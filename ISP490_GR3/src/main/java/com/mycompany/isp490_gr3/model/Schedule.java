package com.mycompany.isp490_gr3.model;

import com.mycompany.isp490_gr3.dto.SlotDetailDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList; // Đảm bảo ArrayList được import

/**
 * Model class representing a doctor's schedule for a specific day.
 * Lớp model đại diện cho lịch trình làm việc của bác sĩ trong một ngày cụ thể.
 * Chứa thông tin về ngày làm việc và các khung giờ khám bệnh có sẵn (slots).
 */
public class Schedule {
    private int doctorId;
    private LocalDate workingDate;
    private List<SlotDetailDTO> availableSlotDetails; // Danh sách các khung giờ chi tiết

    public Schedule() {
        this.availableSlotDetails = new ArrayList<>(); // Khởi tạo để tránh NullPointerException
    }

    public Schedule(int doctorId, LocalDate workingDate, List<SlotDetailDTO> availableSlotDetails) {
        this.doctorId = doctorId;
        this.workingDate = workingDate;
        this.availableSlotDetails = availableSlotDetails != null ? availableSlotDetails : new ArrayList<>();
    }

    // Getters and Setters
    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(LocalDate workingDate) {
        this.workingDate = workingDate;
    }

    public List<SlotDetailDTO> getAvailableSlotDetails() {
        return availableSlotDetails;
    }

    public void setAvailableSlotDetails(List<SlotDetailDTO> availableSlotDetails) {
        this.availableSlotDetails = availableSlotDetails;
    }

    @Override
    public String toString() {
        return "Schedule{" +
               "doctorId=" + doctorId +
               ", workingDate=" + workingDate +
               ", availableSlotDetails=" + availableSlotDetails +
               '}';
    }
}
