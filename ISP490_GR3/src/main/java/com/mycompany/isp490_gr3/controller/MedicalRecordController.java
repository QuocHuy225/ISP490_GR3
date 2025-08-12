package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOMedicalRecord;
import com.mycompany.isp490_gr3.dao.DAOPatient;
import com.mycompany.isp490_gr3.dao.DAOMedicalExamTemplate;
import com.mycompany.isp490_gr3.model.MedicalRecord;
import com.mycompany.isp490_gr3.model.Patient;
import com.mycompany.isp490_gr3.model.MedicalExamTemplate;
import com.mycompany.isp490_gr3.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import com.google.gson.Gson;

/**
 * Controller for managing medical records
 * Allows both ADMIN and DOCTOR access
 */
@WebServlet(name = "MedicalRecordController", urlPatterns = {"/doctor/medical-records", "/doctor/medical-records/*"})
public class MedicalRecordController extends HttpServlet {
    
    private DAOMedicalRecord medicalRecordDAO = new DAOMedicalRecord();
    private DAOPatient patientDAO = new DAOPatient();
    private DAOMedicalExamTemplate templateDAO = new DAOMedicalExamTemplate();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        // Check doctor access (Admin and Doctor allowed)
        if (!checkDoctorAccess(request, response)) {
            return;
        }
        
        String action = request.getParameter("action");
        String patientIdStr = request.getParameter("patientId");
        String recordId = request.getParameter("recordId");
        
        if ("list".equals(action) && patientIdStr != null) {
            listMedicalRecords(request, response, Integer.parseInt(patientIdStr));
        } else if ("get".equals(action) && recordId != null) {
            getMedicalRecordById(request, response, recordId);
        } else if ("new".equals(action) && patientIdStr != null) {
            showNewMedicalRecordForm(request, response, Integer.parseInt(patientIdStr));
        } else if ("edit".equals(action) && recordId != null) {
            showEditMedicalRecordForm(request, response, recordId);
        } else if ("view".equals(action) && recordId != null) {
            showViewMedicalRecord(request, response, recordId);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action or missing parameters");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        // Check doctor access (Admin and Doctor allowed)
        if (!checkDoctorAccess(request, response)) {
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            addMedicalRecord(request, response);
        } else if ("update".equals(action)) {
            updateMedicalRecord(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
    
    private void listMedicalRecords(HttpServletRequest request, HttpServletResponse response, int patientId)
            throws ServletException, IOException {
        
        // Get patient information
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            // Redirect to not-found page with patient context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=patient&id=" + patientId);
            return;
        }
        
        // Get medical records for this patient
        List<MedicalRecord> medicalRecords = medicalRecordDAO.getMedicalRecordsByPatientId(patientId);
        
        request.setAttribute("patient", patient);
        request.setAttribute("medicalRecords", medicalRecords);
        request.getRequestDispatcher("/jsp/medical-record-list.jsp").forward(request, response);
    }
    
    private void getMedicalRecordById(HttpServletRequest request, HttpServletResponse response, String recordId)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        MedicalRecord record = medicalRecordDAO.getMedicalRecordById(recordId);
        if (record != null) {
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(record));
            out.flush();
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showNewMedicalRecordForm(HttpServletRequest request, HttpServletResponse response, int patientId)
            throws ServletException, IOException {
        
        // Get patient information
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            // Redirect to not-found page with patient context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=patient&id=" + patientId);
            return;
        }
        
        // Get medical exam templates
        List<MedicalExamTemplate> templates = templateDAO.getAllTemplates();
        
        request.setAttribute("patient", patient);
        request.setAttribute("templates", templates);
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/jsp/medical-record-form.jsp").forward(request, response);
    }
    
    private void showEditMedicalRecordForm(HttpServletRequest request, HttpServletResponse response, String recordId)
            throws ServletException, IOException {
        
        // Get medical record
        MedicalRecord record = medicalRecordDAO.getMedicalRecordById(recordId);
        if (record == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Medical record not found");
            return;
        }
        
        // Get patient information
        Patient patient = patientDAO.getPatientById(record.getPatientId());
        if (patient == null) {
            // Redirect to not-found page with patient context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=patient&id=" + record.getPatientId());
            return;
        }
        
        // Get medical exam templates
        List<MedicalExamTemplate> templates = templateDAO.getAllTemplates();
        
        request.setAttribute("patient", patient);
        request.setAttribute("medicalRecord", record);
        request.setAttribute("templates", templates);
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/jsp/medical-record-form.jsp").forward(request, response);
    }
    
    private void showViewMedicalRecord(HttpServletRequest request, HttpServletResponse response, String recordId)
            throws ServletException, IOException {
        
        // Get medical record
        MedicalRecord record = medicalRecordDAO.getMedicalRecordById(recordId);
        if (record == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Medical record not found");
            return;
        }
        
        // Get patient information
        Patient patient = patientDAO.getPatientById(record.getPatientId());
        if (patient == null) {
            // Redirect to not-found page with patient context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=patient&id=" + record.getPatientId());
            return;
        }
        
        request.setAttribute("patient", patient);
        request.setAttribute("medicalRecord", record);
        request.getRequestDispatcher("/jsp/medical-record-view.jsp").forward(request, response);
    }
    
    private void addMedicalRecord(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            MedicalRecord record = extractMedicalRecordFromRequest(request);
            
            // Validate vital signs for both draft and completed records
            String vitalSignsError = validateVitalSigns(record);
            if (vitalSignsError != null) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=new&patientId=" + 
                                    record.getPatientId() + "&error=" + vitalSignsError);
                return;
            }
            
            // Validate final diagnosis only if status is completed
            if ("completed".equals(record.getStatus())) {
                if (record.getFinalDiagnosis() == null || record.getFinalDiagnosis().trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=new&patientId=" + 
                                        record.getPatientId() + "&error=final_diagnosis_required");
                    return;
                }
            }
            
            // Get current user for created_by
            Object userObj = request.getSession().getAttribute("user");
            if (userObj instanceof User) {
                User user = (User) userObj;
                record.setCreatedBy(user.getId());
                record.setUpdatedBy(user.getId());
            }
            
            boolean success = medicalRecordDAO.addMedicalRecord(record);
            
            if (success) {
                if ("completed".equals(record.getStatus())) {
                    // Nếu tạo mới và hoàn thành, chuyển về list luôn
                    response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=list&patientId=" + 
                        record.getPatientId() + "&success=status_completed");
                } else {
                    response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + 
                        record.getId() + "&success=added");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=new&patientId=" + 
                                    record.getPatientId() + "&error=add_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String patientId = request.getParameter("patientId");
            response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=new&patientId=" + 
                                patientId + "&error=system_error");
        }
    }
    
    private void updateMedicalRecord(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String recordId = request.getParameter("recordId");
            
            // Get current record to check status
            MedicalRecord currentRecord = medicalRecordDAO.getMedicalRecordById(recordId);
            if (currentRecord == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Medical record not found");
                return;
            }
            
            // Get current user for updated_by
            Object userObj = request.getSession().getAttribute("user");
            String updatedBy = null;
            if (userObj instanceof User) {
                User user = (User) userObj;
                updatedBy = user.getId();
            }
            
            boolean success = false;
            String newStatus = request.getParameter("status");
            
            // If record is currently completed, only allow note updates
            if (currentRecord.isCompleted()) {
                // Check if trying to change status from completed to ongoing
                if ("ongoing".equals(newStatus)) {
                    response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + 
                                        recordId + "&error=status_change_not_allowed");
                    return;
                }
                String note = request.getParameter("note");
                success = medicalRecordDAO.updateMedicalRecordNote(recordId, note, updatedBy);
            } else {
                // For ongoing records, allow full update regardless of status change
                MedicalRecord record = extractMedicalRecordFromRequest(request);
                record.setId(recordId);
                record.setUpdatedBy(updatedBy);
                
                // Validate vital signs for both draft and completed records
                String vitalSignsError = validateVitalSigns(record);
                if (vitalSignsError != null) {
                    response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + 
                                        recordId + "&error=" + vitalSignsError);
                    return;
                }
                
                // Validate final diagnosis only if status is completed
                if ("completed".equals(record.getStatus())) {
                    if (record.getFinalDiagnosis() == null || record.getFinalDiagnosis().trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + 
                                            recordId + "&error=final_diagnosis_required");
                        return;
                    }
                }
                
                success = medicalRecordDAO.updateMedicalRecord(record);
            }
            
            if (success) {
                String successMessage = "updated";
                // Check if this was a status change from ongoing to completed
                if (currentRecord.isOngoing() && "completed".equals(newStatus)) {
                    successMessage = "status_completed";
                    // Redirect to list after completion
                    response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=list&patientId=" + currentRecord.getPatientId() + "&success=" + successMessage);
                    return;
                }
                response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + 
                                    recordId + "&success=" + successMessage);
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + 
                                    recordId + "&error=update_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String recordIdParam = request.getParameter("recordId");
            if (recordIdParam != null) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + 
                                    recordIdParam + "&error=system_error");
            } else {
                String patientId = request.getParameter("patientId");
                response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=new&patientId=" + 
                                    patientId + "&error=system_error");
            }
        }
    }
    
    private MedicalRecord extractMedicalRecordFromRequest(HttpServletRequest request) {
        MedicalRecord record = new MedicalRecord();
        
        // Basic info
        String patientIdStr = request.getParameter("patientId");
        if (patientIdStr != null && !patientIdStr.isEmpty()) {
            record.setPatientId(Integer.parseInt(patientIdStr));
        }
        
        String doctorIdStr = request.getParameter("doctorId");
        if (doctorIdStr != null && !doctorIdStr.isEmpty()) {
            record.setDoctorId(Integer.parseInt(doctorIdStr));
        }
        
        // Vital signs
        String respirationRateStr = request.getParameter("respirationRate");
        if (respirationRateStr != null && !respirationRateStr.isEmpty()) {
            record.setRespirationRate(Integer.parseInt(respirationRateStr));
        }
        
        String temperatureStr = request.getParameter("temperature");
        if (temperatureStr != null && !temperatureStr.isEmpty()) {
            record.setTemperature(new BigDecimal(temperatureStr));
        }
        
        String heightStr = request.getParameter("height");
        if (heightStr != null && !heightStr.isEmpty()) {
            record.setHeight(new BigDecimal(heightStr));
        }
        
        String pulseStr = request.getParameter("pulse");
        if (pulseStr != null && !pulseStr.isEmpty()) {
            record.setPulse(Integer.parseInt(pulseStr));
        }
        
        String bmiStr = request.getParameter("bmi");
        if (bmiStr != null && !bmiStr.isEmpty()) {
            record.setBmi(new BigDecimal(bmiStr));
        }
        
        String weightStr = request.getParameter("weight");
        if (weightStr != null && !weightStr.isEmpty()) {
            record.setWeight(new BigDecimal(weightStr));
        }
        
        record.setBloodPressure(request.getParameter("bloodPressure"));
        
        String spo2Str = request.getParameter("spo2");
        if (spo2Str != null && !spo2Str.isEmpty()) {
            record.setSpo2(new BigDecimal(spo2Str));
        }
        
        // Medical information
        record.setMedicalHistory(request.getParameter("medicalHistory"));
        record.setCurrentDisease(request.getParameter("currentDisease"));
        record.setPhysicalExam(request.getParameter("physicalExam"));
        record.setClinicalInfo(request.getParameter("clinicalInfo"));
        record.setFinalDiagnosis(request.getParameter("finalDiagnosis"));
        record.setTreatmentPlan(request.getParameter("treatmentPlan"));
        record.setNote(request.getParameter("note"));
        
        // Status - validate to prevent changing from completed to ongoing
        String status = request.getParameter("status");
        String recordId = request.getParameter("recordId");
        
        if (status != null && !status.isEmpty()) {
            // If this is an update operation and trying to change from completed to ongoing
            if (recordId != null && !recordId.isEmpty() && "ongoing".equals(status)) {
                MedicalRecord currentRecord = medicalRecordDAO.getMedicalRecordById(recordId);
                if (currentRecord != null && currentRecord.isCompleted()) {
                    // Keep the status as completed, don't allow change to ongoing
                    record.setStatus("completed");
                } else {
                    record.setStatus(status);
                }
            } else {
                record.setStatus(status);
            }
        } else {
            record.setStatus("ongoing");
        }
        
        return record;
    }
    
    /**
     * Validate vital signs when completing a medical record
     * @param record MedicalRecord to validate
     * @return error code if validation fails, null if validation passes
     */
    private String validateVitalSigns(MedicalRecord record) {
        // Validate respiration rate (8-40)
        if (record.getRespirationRate() != null) {
            int respirationRate = record.getRespirationRate();
            if (respirationRate < 8 || respirationRate > 40) {
                return "vital_signs_respiration_invalid";
            }
        }
        
        // Validate temperature (32-45°C)
        if (record.getTemperature() != null) {
            double temperature = record.getTemperature().doubleValue();
            if (temperature < 32.0 || temperature > 45.0) {
                return "vital_signs_temperature_invalid";
            }
        }
        
        // Validate pulse (30-200)
        if (record.getPulse() != null) {
            int pulse = record.getPulse();
            if (pulse < 30 || pulse > 200) {
                return "vital_signs_pulse_invalid";
            }
        }
        
        // Validate blood pressure format (systolic/diastolic)
        if (record.getBloodPressure() != null && !record.getBloodPressure().trim().isEmpty()) {
            String bloodPressure = record.getBloodPressure().trim();
            if (!bloodPressure.matches("^\\d{2,3}/\\d{2,3}$")) {
                return "vital_signs_blood_pressure_invalid";
            }
            
            // Additional validation for blood pressure values
            String[] parts = bloodPressure.split("/");
            try {
                int systolic = Integer.parseInt(parts[0]);
                int diastolic = Integer.parseInt(parts[1]);
                
                // Validate systolic (70-300) and diastolic (40-200)
                if (systolic < 70 || systolic > 300 || diastolic < 40 || diastolic > 200) {
                    return "vital_signs_blood_pressure_range_invalid";
                }
                
                // Systolic should be higher than diastolic
                if (systolic <= diastolic) {
                    return "vital_signs_blood_pressure_logic_invalid";
                }
            } catch (NumberFormatException e) {
                return "vital_signs_blood_pressure_format_invalid";
            }
        }
        
        // Validate height (50-250cm)
        if (record.getHeight() != null) {
            double height = record.getHeight().doubleValue();
            if (height < 50.0 || height > 250.0) {
                return "vital_signs_height_invalid";
            }
        }
        
        // Validate weight (5-300kg)
        if (record.getWeight() != null) {
            double weight = record.getWeight().doubleValue();
            if (weight < 5.0 || weight > 300.0) {
                return "vital_signs_weight_invalid";
            }
        }
        
        // Validate SpO2 (70-100%)
        if (record.getSpo2() != null) {
            double spo2 = record.getSpo2().doubleValue();
            if (spo2 < 70.0 || spo2 > 100.0) {
                return "vital_signs_spo2_invalid";
            }
        }
        
        // All validations passed
        return null;
    }
    
    /**
     * Check doctor access - allows both ADMIN and DOCTOR
     */
    private boolean checkDoctorAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }
        
        // Get current user
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }
        
        // Allow both Admin and Doctor to access
        if (currentUser.getRole() != User.Role.DOCTOR) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getServletInfo() {
        return "Medical Record Management Controller";
    }
} 