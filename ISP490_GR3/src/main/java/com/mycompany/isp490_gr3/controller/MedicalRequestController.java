package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOMedicalRequest;
import com.mycompany.isp490_gr3.dao.DAOPatient;
import com.mycompany.isp490_gr3.dao.DAOMedicalRecord;
import com.mycompany.isp490_gr3.dao.DAOPartner;
import com.mycompany.isp490_gr3.model.MedicalRequest;
import com.mycompany.isp490_gr3.model.Patient;
import com.mycompany.isp490_gr3.model.MedicalRecord;
import com.mycompany.isp490_gr3.model.Partner;
import com.mycompany.isp490_gr3.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing medical requests
 * Allows both ADMIN and DOCTOR access
 */
@WebServlet(name = "MedicalRequestController", urlPatterns = {"/doctor/medical-requests"})
public class MedicalRequestController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(MedicalRequestController.class.getName());
    private DAOMedicalRequest daoMedicalRequest;
    private DAOPatient daoPatient;
    private DAOMedicalRecord daoMedicalRecord;
    private DAOPartner daoPartner;
    
    @Override
    public void init() throws ServletException {
        super.init();
        daoMedicalRequest = new DAOMedicalRequest();
        daoPatient = new DAOPatient();
        daoMedicalRecord = new DAOMedicalRecord();
        daoPartner = new DAOPartner();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkDoctorAccess(request, response)) {
            return;
        }
        
        String action = request.getParameter("action");
        String medicalRecordId = request.getParameter("medicalRecordId");
        
        // If no action but has medicalRecordId, check if medical request exists
        if (action == null && medicalRecordId != null) {
            handleMedicalRecordRequest(request, response);
            return;
        }
        
        // Otherwise process specific actions
        try {
            switch (action) {
                case "new":
                    handleNewRequest(request, response);
                    break;
                case "edit":
                    handleEditRequest(request, response);
                    break;
                case "view":
                    handleViewRequest(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/homepage");
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in MedicalRequestController: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=system_error");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkDoctorAccess(request, response)) {
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "add";
        
        try {
            switch (action) {
                case "add":
                    handleAddRequest(request, response);
                    break;
                case "update":
                    handleUpdateRequest(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/doctor/medical-requests");
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in MedicalRequestController POST: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=system_error");
        }
    }
    
    // ===== GET HANDLERS =====
    
    private void handleMedicalRecordRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String medicalRecordId = request.getParameter("medicalRecordId");
        if (medicalRecordId == null || medicalRecordId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=invalid_record");
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(medicalRecordId);
        if (medicalRecord == null) {
            // Redirect to not-found page with medical record context
            response.sendRedirect(request.getContextPath() + "/jsp/not-found.jsp?type=medical-record&id=" + medicalRecordId);
            return;
        }
        
        // Get the single medical request for this medical record
        List<MedicalRequest> requests = daoMedicalRequest.getRequestsByMedicalRecord(medicalRecordId);
        
        // If request exists, show it. If not, create new one
        if (!requests.isEmpty()) {
            MedicalRequest medicalRequest = requests.get(0);
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?action=view&requestId=" + medicalRequest.getId());
        } else {
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?action=new&medicalRecordId=" + medicalRecordId);
        }
    }
    
    private void handleNewRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String medicalRecordId = request.getParameter("medicalRecordId");
        if (medicalRecordId == null || medicalRecordId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=invalid_record");
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(medicalRecordId);
        if (medicalRecord == null) {
            // Redirect to not-found page with medical record context
            response.sendRedirect(request.getContextPath() + "/jsp/not-found.jsp?type=medical-record&id=" + medicalRecordId);
            return;
        }
        
        // Check if medical record is completed - if so, don't allow creating new medical requests
        if (medicalRecord.isCompleted()) {
            response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + medicalRecordId + "&error=medical_record_completed");
            return;
        }
        
        Patient patient = daoPatient.getPatientById(medicalRecord.getPatientId());
        
        // Get all partners for the clinic selection
        List<Partner> partners = daoPartner.getAllPartners();
        
        request.setAttribute("medicalRecord", medicalRecord);
        request.setAttribute("patient", patient);
        request.setAttribute("partners", partners);
        request.setAttribute("action", "add");
        
        request.getRequestDispatcher("/jsp/medical-request-form.jsp").forward(request, response);
    }
    
    private void handleEditRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestIdStr = request.getParameter("requestId");
        if (requestIdStr == null || requestIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=invalid_request");
            return;
        }
        
        int requestId = Integer.parseInt(requestIdStr);
        MedicalRequest medicalRequest = daoMedicalRequest.getRequestById(requestId);
        if (medicalRequest == null) {
            // Redirect to not-found page with medical request context
            response.sendRedirect(request.getContextPath() + "/jsp/not-found.jsp?type=medical-request&id=" + requestId);
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(medicalRequest.getMedicalRecordId());
        Patient patient = daoPatient.getPatientById(medicalRequest.getPatientId());
        
        // Get all partners for the clinic selection
        List<Partner> partners = daoPartner.getAllPartners();
        
        // Check if medical record is completed - if so, redirect to view mode
        if (medicalRecord != null && medicalRecord.isCompleted()) {
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?action=view&requestId=" + requestId + "&error=medical_record_completed");
            return;
        }
        
        request.setAttribute("medicalRequest", medicalRequest);
        request.setAttribute("medicalRecord", medicalRecord);
        request.setAttribute("patient", patient);
        request.setAttribute("partners", partners);
        request.setAttribute("action", "update");
        
        request.getRequestDispatcher("/jsp/medical-request-form.jsp").forward(request, response);
    }
    
    private void handleViewRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestIdStr = request.getParameter("requestId");
        if (requestIdStr == null || requestIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=invalid_request");
            return;
        }
        
        int requestId = Integer.parseInt(requestIdStr);
        MedicalRequest medicalRequest = daoMedicalRequest.getRequestById(requestId);
        if (medicalRequest == null) {
            // Redirect to not-found page with medical request context
            response.sendRedirect(request.getContextPath() + "/jsp/not-found.jsp?type=medical-request&id=" + requestId);
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(medicalRequest.getMedicalRecordId());
        Patient patient = daoPatient.getPatientById(medicalRequest.getPatientId());
        
        request.setAttribute("medicalRequest", medicalRequest);
        request.setAttribute("medicalRecord", medicalRecord);
        request.setAttribute("patient", patient);
        
        request.getRequestDispatcher("/jsp/medical-request-view.jsp").forward(request, response);
    }
    
    // ===== POST HANDLERS =====
    
    private void handleAddRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        String userId = currentUser != null ? currentUser.getId() : "system";
        
        try {
            String medicalRecordId = request.getParameter("medicalRecordId");
            String patientIdStr = request.getParameter("patientId");
            String clinicName = request.getParameter("clinicName");
            String clinicPhone = request.getParameter("clinicPhone");
            String clinicAddress = request.getParameter("clinicAddress");
            String instructionContent = request.getParameter("instructionContent");
            String instructionRequirements = request.getParameter("instructionRequirements");
            String notes = request.getParameter("notes");
            
            // Validate required fields
            if (medicalRecordId == null || patientIdStr == null || clinicName == null || clinicName.trim().isEmpty() || clinicPhone == null || clinicPhone.trim().isEmpty() || clinicAddress == null || clinicAddress.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=missing_fields");
                return;
            }
            
            // Check if medical record is completed - if so, don't allow adding new medical requests
            MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(medicalRecordId);
            if (medicalRecord != null && medicalRecord.isCompleted()) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-records?action=edit&recordId=" + medicalRecordId + "&error=medical_record_completed");
                return;
            }
            
            int patientId = Integer.parseInt(patientIdStr);
            
            // Create medical request
            MedicalRequest medicalRequest = new MedicalRequest();
            medicalRequest.setMedicalRecordId(medicalRecordId);
            medicalRequest.setPatientId(patientId);
            medicalRequest.setClinicName(clinicName);
            medicalRequest.setClinicPhone(clinicPhone);
            medicalRequest.setClinicAddress(clinicAddress);
            medicalRequest.setInstructionContent(instructionContent);
            medicalRequest.setInstructionRequirements(instructionRequirements);
            medicalRequest.setNotes(notes);
            medicalRequest.setCreatedBy(userId);
            
            // Save medical request
            boolean success = daoMedicalRequest.addRequest(medicalRequest);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?medicalRecordId=" + medicalRecordId + "&success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?medicalRecordId=" + medicalRecordId + "&error=add_failed");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in add medical request: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=invalid_data");
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("already exists")) {
                // Medical request already exists for this medical record
                String redirectMedicalRecordId = request.getParameter("medicalRecordId");
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?medicalRecordId=" + redirectMedicalRecordId + "&error=request_exists");
            } else {
                LOGGER.log(Level.WARNING, "State error: {0}", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=state_error");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding medical request: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=system_error");
        }
    }
    
    private void handleUpdateRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String requestIdStr = request.getParameter("requestId");
            String clinicName = request.getParameter("clinicName");
            String clinicPhone = request.getParameter("clinicPhone");
            String clinicAddress = request.getParameter("clinicAddress");
            String instructionContent = request.getParameter("instructionContent");
            String instructionRequirements = request.getParameter("instructionRequirements");
            String notes = request.getParameter("notes");
            
            if (requestIdStr == null || requestIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=invalid_request");
                return;
            }
            
            int requestId = Integer.parseInt(requestIdStr);
            MedicalRequest existingRequest = daoMedicalRequest.getRequestById(requestId);
            if (existingRequest == null) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=request_not_found");
                return;
            }
            
            // Validate required fields
            if (clinicName == null || clinicName.trim().isEmpty() || clinicPhone == null || clinicPhone.trim().isEmpty() || clinicAddress == null || clinicAddress.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?action=edit&requestId=" + requestId + "&error=missing_fields");
                return;
            }
            
            // Check if medical record is completed - if so, don't allow updates
            MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(existingRequest.getMedicalRecordId());
            if (medicalRecord != null && medicalRecord.isCompleted()) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?action=view&requestId=" + requestId + "&error=medical_record_completed");
                return;
            }
            
            // Update medical request
            existingRequest.setClinicName(clinicName);
            existingRequest.setClinicPhone(clinicPhone);
            existingRequest.setClinicAddress(clinicAddress);
            existingRequest.setInstructionContent(instructionContent);
            existingRequest.setInstructionRequirements(instructionRequirements);
            existingRequest.setNotes(notes);
            
            // Save medical request
            boolean success = daoMedicalRequest.updateRequest(existingRequest);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?action=view&requestId=" + existingRequest.getId() + "&success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?action=edit&requestId=" + existingRequest.getId() + "&error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in update medical request: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=invalid_data");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating medical request: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/medical-requests?error=system_error");
        }
    }
    
    // ===== UTILITY METHODS =====
    
    /**
     * Check doctor access - allows DOCTOR
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
        
        // Allow Doctor to access
        if (currentUser.getRole() != User.Role.DOCTOR) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }
        
        return true;
    }
} 