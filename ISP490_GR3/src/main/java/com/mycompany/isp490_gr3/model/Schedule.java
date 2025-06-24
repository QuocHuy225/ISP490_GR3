package com.mycompany.isp490_gr3.model;

import com.mycompany.isp490_gr3.dto.SlotDetailDTO; // Import DTO mới
import java.time.LocalDate;
import java.time.LocalTime; // Vẫn giữ LocalTime nếu bạn có nơi nào khác sử dụng List<LocalTime>
import java.util.List;
import java.util.ArrayList; // Thêm import này

public class Schedule {
    private int scheduleId; // Có thể có hoặc không, tùy vào cấu trúc DB
    private int doctorId;
    private LocalDate workingDate;
    // Đã thay thế List<LocalTime> bằng List<SlotDetailDTO>
    private List<SlotDetailDTO> availableSlotDetails; 

    // Constructors
    public Schedule() {
        this.availableSlotDetails = new ArrayList<>(); // Khởi tạo danh sách trống
    }

    public Schedule(int scheduleId, int doctorId, LocalDate workingDate, List<SlotDetailDTO> availableSlotDetails) {
        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.workingDate = workingDate;
        this.availableSlotDetails = availableSlotDetails;
    }

    // Constructor mới để phù hợp với việc tạo Schedule theo ngày và SlotDetailDTO
    public Schedule(LocalDate workingDate, List<SlotDetailDTO> availableSlotDetails) {
        this.workingDate = workingDate;
        this.availableSlotDetails = availableSlotDetails;
    }

    // Getters and Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

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

    // Getter mới cho List<SlotDetailDTO>
    public List<SlotDetailDTO> getAvailableSlotDetails() {
        return availableSlotDetails;
    }

    // Setter mới cho List<SlotDetailDTO>
    public void setAvailableSlotDetails(List<SlotDetailDTO> availableSlotDetails) {
        this.availableSlotDetails = availableSlotDetails;
    }
}
