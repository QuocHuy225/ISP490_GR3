package com.mycompany.isp490_gr3.dto; // Đảm bảo gói này phù hợp với cấu trúc dự án của bạn

import java.time.LocalTime;

public class SlotDetailDTO {
    private int slotId; // ID của slot từ bảng 'slot'
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxPatients;
    private int bookedPatients;

    public SlotDetailDTO(int slotId, LocalTime startTime, LocalTime endTime, int maxPatients, int bookedPatients) {
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxPatients = maxPatients;
        this.bookedPatients = bookedPatients;
    }

    // Getters
    public int getSlotId() {
        return slotId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getMaxPatients() {
        return maxPatients;
    }

    public int getBookedPatients() {
        return bookedPatients;
    }

    // Setters (tùy chọn, chỉ thêm nếu bạn cần chỉnh sửa các giá trị này sau khi tạo DTO)
    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setMaxPatients(int maxPatients) {
        this.maxPatients = maxPatients;
    }

    public void setBookedPatients(int bookedPatients) {
        this.bookedPatients = bookedPatients;
    }

    @Override
    public String toString() {
        return "SlotDetailDTO{" +
               "slotId=" + slotId +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", maxPatients=" + maxPatients +
               ", bookedPatients=" + bookedPatients +
               '}';
    }
}
