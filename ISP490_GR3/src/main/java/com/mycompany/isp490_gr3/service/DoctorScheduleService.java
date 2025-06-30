package com.mycompany.isp490_gr3.service;

import com.mycompany.isp490_gr3.dao.DAODoctorSchedule;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.DoctorSchedule;

import java.util.List;
import java.sql.Date; // For java.sql.Date

public class DoctorScheduleService {

    private DAODoctorSchedule scheduleDAO;
    private DAODoctor doctorDAO;

    public DoctorScheduleService() {
        this.scheduleDAO = new DAODoctorSchedule();
        this.doctorDAO = new DAODoctor();
    }

    /**
     * Retrieves all active doctors.
     * @return A list of Doctor objects.
     */
    public List<Doctor> getAllDoctors() {
        return doctorDAO.findAllDoctors();
    }

    /**
     * Retrieves doctor schedules for a given month and year.
     * @param year The year.
     * @param month The month (1-12).
     * @return A list of DoctorSchedule objects.
     */
    public List<DoctorSchedule> getSchedulesByMonth(int year, int month) {
        // You can add business logic here, e.g., filtering based on user roles
        return scheduleDAO.findSchedulesByMonth(year, month);
    }

    /**
     * Creates a new doctor schedule.
     * @param doctorId The ID of the doctor.
     * @param workDate The work date.
     * @param isActive Whether the schedule is active.
     * @param eventName The display name for the event (optional, can be constructed).
     * @return true if creation was successful, false otherwise (e.g., if schedule already exists).
     */
    public boolean createSchedule(int doctorId, String workDate, boolean isActive, String eventName) {
        // Convert String workDate to java.sql.Date
        Date sqlWorkDate = Date.valueOf(workDate);

        // Business logic: Check if schedule already exists for this doctor on this date
        if (scheduleDAO.isScheduleExists(doctorId, sqlWorkDate)) {
            System.out.println("Schedule already exists for doctor " + doctorId + " on " + workDate);
            return false; // Schedule already exists
        }

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctorId(doctorId);
        schedule.setWorkDate(sqlWorkDate);
        schedule.setActive(isActive);
        schedule.setName(eventName); // Set the display name

        return scheduleDAO.saveSchedule(schedule);
    }

    /**
     * Updates an existing doctor schedule.
     * @param scheduleId The ID of the schedule to update.
     * @param doctorId The updated doctor ID.
     * @param workDate The updated work date.
     * @param isActive The updated active status.
     * @param eventName The updated display name.
     * @return true if update was successful, false otherwise.
     */
    public boolean updateSchedule(String scheduleId, int doctorId, String workDate, boolean isActive, String eventName) {
        // Convert String workDate to java.sql.Date
        Date sqlWorkDate = Date.valueOf(workDate);

        DoctorSchedule existingSchedule = scheduleDAO.findScheduleById(scheduleId);
        if (existingSchedule == null) {
            System.out.println("Schedule with ID " + scheduleId + " not found for update.");
            return false; // Schedule not found
        }

        // Business logic: Check for conflicts if doctor or date is changed
        if (existingSchedule.getDoctorId() != doctorId || !existingSchedule.getWorkDate().equals(sqlWorkDate)) {
            if (scheduleDAO.isScheduleExists(doctorId, sqlWorkDate)) {
                System.out.println("Update failed: Conflicting schedule already exists for doctor " + doctorId + " on " + workDate);
                return false; // Conflicting schedule exists
            }
        }

        existingSchedule.setDoctorId(doctorId);
        existingSchedule.setWorkDate(sqlWorkDate);
        existingSchedule.setActive(isActive);
        existingSchedule.setName(eventName); // Update the display name

        return scheduleDAO.updateSchedule(existingSchedule);
    }

    /**
     * Deletes (soft delete) a doctor schedule.
     * @param scheduleId The ID of the schedule to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteSchedule(String scheduleId) {
        // Business logic: e.g., check if there are associated appointments before deleting
        return scheduleDAO.deleteSchedule(scheduleId);
    }
    public DoctorSchedule findScheduleById(String scheduleId) {
        return scheduleDAO.findScheduleById(scheduleId);
    }
}
