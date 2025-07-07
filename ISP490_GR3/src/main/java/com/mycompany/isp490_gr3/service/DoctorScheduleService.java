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
        // IMPORTANT: Ensure findSchedulesByMonth in DAODoctorSchedule only returns ACTIVE schedules
        // If it currently returns all (active and inactive), you need to modify that DAO method.
        return scheduleDAO.findSchedulesByMonth(year, month);
    }

    /**
     * Checks if a given date falls within the allowed scheduling period. Rule:
     * - If today is Monday-Thursday, Saturday, Sunday: allow scheduling from
     * today until the end of the *current* week (Sunday). - If today is Friday:
     * allow scheduling from today until the end of the *next* week (Sunday).
     *
     * @param checkDate The date to validate.
     * @return true if the date is within the allowed period, false otherwise.
     */
    private boolean isWithinAllowedSchedulingPeriod(LocalDate checkDate) {
        LocalDate today = LocalDate.now();
        LocalDate limitDate;

        // Determine the end of the current week (Sunday of this week)
        LocalDate endOfCurrentWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        if (today.getDayOfWeek() == DayOfWeek.FRIDAY) {
            // From Friday, allow until the end of the *next* week
            limitDate = endOfCurrentWeek.plusWeeks(1);
        } else {
            // For Mon, Tue, Wed, Thu, Sat, Sun: only allow until the end of the *current* week.
            limitDate = endOfCurrentWeek;
        }

        // The date must not be in the past and must be within the calculated limit.
        return !checkDate.isBefore(today) && !checkDate.isAfter(limitDate);
    }

    /**
     * Creates a new doctor schedule or reactivates a soft-deleted one. This
     * method is modified to prioritize reactivating soft-deleted schedules
     * before creating new ones for the same doctor and date.
     *
     * @param doctorId The ID of the doctor.
     * @param workDate The work date (YYYY-MM-DD string).
     * @param isActive Whether the schedule is active (for the new/reactivated
     * schedule, should usually be true).
     * @param eventName The display name for the event.
     * @return A string indicating success or an error message.
     */
    public String createSchedule(int doctorId, String workDate, boolean isActive, String eventName) {
        Date sqlWorkDate = Date.valueOf(workDate);
        LocalDate localWorkDate = LocalDate.parse(workDate);

        if (!isWithinAllowedSchedulingPeriod(localWorkDate)) {
            return "Ngày làm việc " + workDate + " nằm ngoài khoảng thời gian cho phép. (Chỉ được tạo lịch từ hôm nay đến hết Chủ Nhật của tuần này, hoặc đến hết Chủ Nhật tuần sau nếu là Thứ Sáu)";
        }

        // MODIFIED LOGIC:
        // First, check for any existing schedule for this doctor and date, regardless of its 'active' status.
        // This requires a new method in your DAODoctorSchedule.
        DoctorSchedule existingSchedule = scheduleDAO.findScheduleByDoctorIdAndWorkDateIncludingInactive(doctorId, sqlWorkDate);

        if (existingSchedule != null) {
            // If a schedule exists for this doctor on this date
            if (existingSchedule.isActive()) {
                // If it's already active, it's a conflict.
                System.out.println("Schedule already exists and is active for doctor " + doctorId + " on " + workDate);
                return "Lịch làm việc cho bác sĩ này vào ngày " + workDate + " đã tồn tại và đang hoạt động.";
            } else {
                // If it's inactive (soft-deleted), reactivate it.
                System.out.println("Reactivating soft-deleted schedule for doctor " + doctorId + " on " + workDate);
                existingSchedule.setActive(true); // Set to active
                existingSchedule.setName(eventName); // Update event name if needed
                // You might also want to update other fields if they were meant to be modified
                if (scheduleDAO.updateSchedule(existingSchedule)) { // Use update method for reactivation
                    return "success";
                } else {
                    return "Có lỗi xảy ra khi khôi phục lịch làm việc.";
                }
            }
        } else {
            // No existing schedule (active or inactive) found, create a brand new one.
            DoctorSchedule newSchedule = new DoctorSchedule();
            newSchedule.setDoctorId(doctorId);
            newSchedule.setWorkDate(sqlWorkDate);
            newSchedule.setActive(true); // New schedules are always active
            newSchedule.setName(eventName);

            if (scheduleDAO.saveSchedule(newSchedule)) {
                return "success";
            } else {
                return "Có lỗi xảy ra khi lưu lịch làm việc mới.";
            }
        }
    }

    /**
     * Updates an existing doctor schedule. This method will now explicitly
     * check against other *active* schedules to prevent conflicts when updating
     * doctorId or workDate.
     *
     * @param scheduleId The ID of the schedule to update.
     * @param doctorId The updated doctor ID.
     * @param workDate The updated work date (YYYY-MM-DD string).
     * @param isActive The updated active status.
     * @param eventName The updated display name.
     * @return A string indicating success or an error message.
     */
    public String updateSchedule(String scheduleId, int doctorId, String workDate, boolean isActive, String eventName) {
        Date sqlWorkDate = Date.valueOf(workDate);
        LocalDate localWorkDate = LocalDate.parse(workDate);

        if (!isWithinAllowedSchedulingPeriod(localWorkDate)) {
            return "Ngày làm việc " + workDate + " nằm ngoài khoảng thời gian cho phép. (Chỉ được tạo lịch từ hôm nay đến hết Chủ Nhật của tuần này, hoặc đến hết Chủ Nhật tuần sau nếu là Thứ Sáu)";
        }

        DoctorSchedule existingSchedule = scheduleDAO.findScheduleById(scheduleId);
        if (existingSchedule == null) {
            System.out.println("Schedule with ID " + scheduleId + " not found for update.");
            return "Lịch làm việc không tìm thấy.";
        }

        // MODIFIED LOGIC FOR UPDATE:
        // Check for conflicts if doctor or date is changed AND the conflicting schedule is ACTIVE
        if (existingSchedule.getDoctorId() != doctorId || !existingSchedule.getWorkDate().equals(sqlWorkDate)) {
            // Check if an ACTIVE schedule already exists for the NEW doctorId and NEW workDate
            // and it's not the same schedule we are trying to update
            DoctorSchedule conflictingActiveSchedule = scheduleDAO.findActiveScheduleByDoctorAndDate(doctorId, sqlWorkDate);

            if (conflictingActiveSchedule != null && !conflictingActiveSchedule.getId().equals(scheduleId)) {
                System.out.println("Update failed: Conflicting active schedule already exists for doctor " + doctorId + " on " + workDate);
                return "Cập nhật thất bại: Lịch làm việc cho bác sĩ này vào ngày " + workDate + " đã tồn tại và đang hoạt động.";
            }
        }

        existingSchedule.setDoctorId(doctorId);
        existingSchedule.setWorkDate(sqlWorkDate);
        existingSchedule.setActive(isActive); // Allow updating active status
        existingSchedule.setName(eventName);

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
