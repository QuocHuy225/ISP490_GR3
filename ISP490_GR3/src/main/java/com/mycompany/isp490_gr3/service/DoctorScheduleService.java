package com.mycompany.isp490_gr3.service;

import com.mycompany.isp490_gr3.dao.DAODoctorSchedule;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.DoctorSchedule;

import java.util.List;
import java.sql.Date; // For java.sql.Date
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class DoctorScheduleService {

    private DAODoctorSchedule scheduleDAO;
    private DAODoctor doctorDAO;

    public DoctorScheduleService() {
        this.scheduleDAO = new DAODoctorSchedule();
        this.doctorDAO = new DAODoctor();
    }

    /**
     * Retrieves all active doctors.
     *
     * @return A list of Doctor objects.
     */
    public List<Doctor> getAllDoctors() {
        return doctorDAO.findAllDoctors();
    }

    /**
     * Retrieves doctor schedules for a given month and year.
     *
     * @param year The year.
     * @param month The month (1-12).
     * @return A list of DoctorSchedule objects.
     */
    public List<DoctorSchedule> getSchedulesByMonth(int year, int month) {
        return scheduleDAO.findSchedulesByMonth(year, month);
    }

    /**
     * Retrieves doctor schedules for a given date range.
     *
     * @param startDate The start date (YYYY-MM-DD string).
     * @param endDate The end date (YYYY-MM-DD string).
     * @return A list of DoctorSchedule objects.
     */
    public List<DoctorSchedule> getSchedulesByDateRange(String startDate, String endDate) {
        Date sqlStartDate = Date.valueOf(startDate);
        Date sqlEndDate = Date.valueOf(endDate);
        return scheduleDAO.findSchedulesByDateRange(sqlStartDate, sqlEndDate);
    }

    /**
     * Checks if a given date falls within the allowed scheduling period. Rule:
     * - If today is Monday-Thursday: allow scheduling from today until the end of the *current* week (Sunday).
     * - If today is Friday, Saturday, Sunday: allow scheduling from today until the end of the *next* week (Sunday).
     *
     * @param checkDate The date to validate.
     * @return true if the date is within the allowed period, false otherwise.
     */
    private boolean isWithinAllowedSchedulingPeriod(LocalDate checkDate) {
        LocalDate today = LocalDate.now();
        LocalDate limitDate;

        // Determine the end of the current week (Sunday of this week)
        LocalDate endOfCurrentWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Modified Logic: If today is Friday, Saturday, or Sunday, allow scheduling until the end of the *next* week.
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        if (currentDayOfWeek == DayOfWeek.FRIDAY || currentDayOfWeek == DayOfWeek.SATURDAY || currentDayOfWeek == DayOfWeek.SUNDAY) {
            limitDate = endOfCurrentWeek.plusWeeks(1);
        } else {
            // For Mon, Tue, Wed, Thu: only allow until the end of the *current* week.
            limitDate = endOfCurrentWeek;
        }

        // The date must not be in the past and must be within the calculated limit.
        return !checkDate.isBefore(today) && !checkDate.isAfter(limitDate);
    }

    /**
     * Creates a new doctor schedule or reactivates a soft-deleted one.
     *
     * @param doctorId The ID of the doctor.
     * @param workDate The work date (YYYY-MM-DD string).
     * @param isActive Whether the schedule is active (for the new/reactivated schedule, should usually be true).
     * @return A string indicating success or an error message.
     */
    public String createSchedule(int doctorId, String workDate, boolean isActive) {
        Date sqlWorkDate = Date.valueOf(workDate);
        LocalDate localWorkDate = LocalDate.parse(workDate);

        if (!isWithinAllowedSchedulingPeriod(localWorkDate)) {
            return "Ngày làm việc " + workDate + " nằm ngoài khoảng thời gian cho phép. (Chỉ được tạo lịch từ hôm nay đến hết Chủ Nhật của tuần này, hoặc đến hết Chủ Nhật tuần sau nếu là Thứ Sáu)";
        }

        // --- LOGIC MỚI: KIỂM TRA MỘT NGÀY CHỈ CÓ MỘT BÁC SĨ ---
        // Kiểm tra xem đã có bác sĩ nào khác được xếp lịch vào ngày này chưa
        if (scheduleDAO.isAnyDoctorScheduledOnDate(sqlWorkDate)) {
            return "Ngày " + workDate + " đã có bác sĩ khác được xếp lịch. Mỗi ngày chỉ có 1 bác sĩ làm việc.";
        }
        // --- KẾT THÚC LOGIC MỚI ---

        DoctorSchedule existingSchedule = scheduleDAO.findScheduleByDoctorIdAndWorkDateIncludingInactive(doctorId, sqlWorkDate);

        if (existingSchedule != null) {
            if (existingSchedule.isActive()) {
                System.out.println("Lịch làm việc cho bác sĩ này vào ngày " + workDate + " đã tồn tại và đang hoạt động.");
                return "Lịch làm việc cho bác sĩ này vào ngày " + workDate + " đã tồn tại và đang hoạt động.";
            } else {
                System.out.println("Reactivating soft-deleted schedule for doctor " + doctorId + " on " + workDate);
                existingSchedule.setActive(true); // Set to active
                if (scheduleDAO.updateSchedule(existingSchedule)) { // Use update method for reactivation
                    return "success";
                } else {
                    return "Có lỗi xảy ra khi khôi phục lịch làm việc.";
                }
            }
        } else {
            DoctorSchedule newSchedule = new DoctorSchedule();
            newSchedule.setDoctorId(doctorId);
            newSchedule.setWorkDate(sqlWorkDate);
            newSchedule.setActive(true); // New schedules are always active

            if (scheduleDAO.saveSchedule(newSchedule)) {
                return "success";
            } else {
                return "Có lỗi xảy ra khi lưu lịch làm việc mới.";
            }
        }
    }

    /**
     * Updates an existing doctor schedule.
     *
     * @param scheduleId The ID of the schedule to update.
     * @param doctorId The updated doctor ID.
     * @param workDate The updated work date (YYYY-MM-DD string).
     * @param isActive The updated active status.
     * @return A string indicating success or an error message.
     */
    public String updateSchedule(String scheduleId, int doctorId, String workDate, boolean isActive) {
        Date sqlWorkDate = Date.valueOf(workDate);
        LocalDate localWorkDate = LocalDate.parse(workDate);

        if (!isWithinAllowedSchedulingPeriod(localWorkDate)) {
            return "Ngày làm việc " + workDate + " nằm ngoài khoảng thời gian cho phép. (Chỉ được tạo lịch từ hôm nay đến hết Chủ Nhật của tuần này, hoặc đến hết Chủ Nhật tuần sau nếu là Thứ Sáu)";
        }

        DoctorSchedule existingSchedule = scheduleDAO.findScheduleById(scheduleId);
        if (existingSchedule == null) {
            System.out.println("Lịch làm việc với ID " + scheduleId + " không tìm thấy.");
            return "Lịch làm việc không tìm thấy.";
        }

        // --- LOGIC MỚI: KIỂM TRA MỘT NGÀY CHỈ CÓ MỘT BÁC SĨ KHI CẬP NHẬT ---
        // Nếu ngày làm việc không thay đổi, hoặc bác sĩ không thay đổi, thì không cần kiểm tra lại quy tắc này
        // Nếu ngày làm việc thay đổi HOẶC bác sĩ thay đổi:
        if (!existingSchedule.getWorkDate().equals(sqlWorkDate) || existingSchedule.getDoctorId() != doctorId) {
            // Kiểm tra xem có bất kỳ lịch trình active nào khác (có ID khác với lịch trình đang cập nhật)
            // trên ngày được đề xuất không.
            List<DoctorSchedule> schedulesOnProposedDate = scheduleDAO.findSchedulesByDateRange(sqlWorkDate, sqlWorkDate);
            if (schedulesOnProposedDate.stream().anyMatch(s -> s.isActive() && !s.getId().equals(scheduleId))) {
                 return "Ngày " + workDate + " đã có bác sĩ khác được xếp lịch. Mỗi ngày chỉ có 1 bác sĩ làm việc.";
            }
        }
        // --- KẾT THÚC LOGIC MỚI ---

        // Check for conflicts if doctor or date is changed AND the conflicting schedule is ACTIVE
        // This original check specifically for THE SAME DOCTOR on THE SAME DATE.
        // The new check above is for ANY OTHER DOCTOR on THE SAME DATE.
        if (existingSchedule.getDoctorId() != doctorId || !existingSchedule.getWorkDate().equals(sqlWorkDate)) {
            DoctorSchedule conflictingActiveSchedule = scheduleDAO.findActiveScheduleByDoctorAndDate(doctorId, sqlWorkDate);

            if (conflictingActiveSchedule != null && !conflictingActiveSchedule.getId().equals(scheduleId)) {
                System.out.println("Cập nhật thất bại: Lịch làm việc cho bác sĩ này vào ngày " + workDate + " đã tồn tại và đang hoạt động.");
                return "Cập nhật thất bại: Lịch làm việc cho bác sĩ này vào ngày " + workDate + " đã tồn tại và đang hoạt động.";
            }
        }

        existingSchedule.setDoctorId(doctorId);
        existingSchedule.setWorkDate(sqlWorkDate);
        existingSchedule.setActive(isActive); // Allow updating active status

        if (scheduleDAO.updateSchedule(existingSchedule)) {
            return "success";
        } else {
            return "Có lỗi xảy ra khi cập nhật lịch làm việc.";
        }
    }

    /**
     * Deletes (soft delete) a doctor schedule.
     *
     * @param scheduleId The ID of the schedule to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteSchedule(String scheduleId) {
        return scheduleDAO.deleteSchedule(scheduleId);
    }

    public DoctorSchedule findScheduleById(String scheduleId) {
        return scheduleDAO.findScheduleById(scheduleId);
    }
}