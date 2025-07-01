package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOActualPrescription;
import com.mycompany.isp490_gr3.dao.DAOMedicalRecord;
import com.mycompany.isp490_gr3.dao.DAOPatient;
import com.mycompany.isp490_gr3.dao.DAOPrescription;
import com.mycompany.isp490_gr3.model.ActualPrescriptionForm;
import com.mycompany.isp490_gr3.model.ActualPrescriptionMedicine;
import com.mycompany.isp490_gr3.model.MedicalRecord;
import com.mycompany.isp490_gr3.model.Patient;
import com.mycompany.isp490_gr3.model.PrescriptionMedicine;
import com.mycompany.isp490_gr3.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller quản lý đơn thuốc thực tế (ActualPrescription)
 */
@WebServlet(name = "ActualPrescriptionController", urlPatterns = {"/actual-prescriptions"})
public class ActualPrescriptionController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ActualPrescriptionController.class.getName());

    private DAOActualPrescription daoRx;
    private DAOPatient daoPatient;
    private DAOMedicalRecord daoMedicalRecord;
    private DAOPrescription daoPrescriptionTemplate;

    @Override
    public void init() throws ServletException {
        daoRx = new DAOActualPrescription();
        daoPatient = new DAOPatient();
        daoMedicalRecord = new DAOMedicalRecord();
        daoPrescriptionTemplate = new DAOPrescription();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "listByMedicalRecord":
                    handleListByMedicalRecord(request, response);
                    break;
                case "new":
                    handleNewForm(request, response);
                    break;
                case "edit":
                    handleEditForm(request, response);
                    break;
                case "view":
                    handleViewForm(request, response);
                    break;
                case "delete":
                    handleDeleteForm(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/homepage");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ActualPrescriptionController: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=system_error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "add";

        try {
            switch (action) {
                case "add":
                    handleAddForm(request, response);
                    break;
                case "update":
                    handleUpdateForm(request, response);
                    break;
                case "delete":
                    handleDeleteForm(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/actual-prescriptions");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ActualPrescriptionController POST: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=system_error");
        }
    }

    // ===== GET HANDLERS =====
    private void handleListByMedicalRecord(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String medicalRecordId = request.getParameter("medicalRecordId");
        if (medicalRecordId == null || medicalRecordId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(medicalRecordId);
        if (medicalRecord == null) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }
        Patient patient = daoPatient.getPatientById(medicalRecord.getPatientId());
        List<ActualPrescriptionForm> forms = daoRx.getFormsByMedicalRecord(medicalRecordId);

        request.setAttribute("medicalRecord", medicalRecord);
        request.setAttribute("patient", patient);
        request.setAttribute("forms", forms);
        request.getRequestDispatcher("/jsp/actual-prescription-list.jsp").forward(request, response);
    }

    private void handleNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String medicalRecordId = request.getParameter("medicalRecordId");
        if (medicalRecordId == null || medicalRecordId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=invalid_record");
            return;
        }
        MedicalRecord record = daoMedicalRecord.getMedicalRecordById(medicalRecordId);
        if (record == null) {
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=record_not_found");
            return;
        }
        Patient patient = daoPatient.getPatientById(record.getPatientId());
        List<PrescriptionMedicine> templateMeds = daoPrescriptionTemplate.getAllMedicines();

        request.setAttribute("medicalRecord", record);
        request.setAttribute("patient", patient);
        request.setAttribute("templateMeds", templateMeds);
        request.setAttribute("action", "add");
        request.getRequestDispatcher("/jsp/actual-prescription-form.jsp").forward(request, response);
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String formId = request.getParameter("formId");
        if (formId == null || formId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=invalid_form");
            return;
        }
        ActualPrescriptionForm form = daoRx.getFormById(formId);
        if (form == null) {
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=form_not_found");
            return;
        }
        MedicalRecord record = daoMedicalRecord.getMedicalRecordById(form.getMedicalRecordId());
        Patient patient = daoPatient.getPatientById(form.getPatientId());
        List<PrescriptionMedicine> templateMeds = daoPrescriptionTemplate.getAllMedicines();

        request.setAttribute("form", form);
        request.setAttribute("medicalRecord", record);
        request.setAttribute("patient", patient);
        request.setAttribute("templateMeds", templateMeds);
        request.setAttribute("action", "update");
        request.getRequestDispatcher("/jsp/actual-prescription-form.jsp").forward(request, response);
    }

    private void handleViewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String formId = request.getParameter("formId");
        if (formId == null || formId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }
        ActualPrescriptionForm form = daoRx.getFormById(formId);
        if (form == null) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }
        MedicalRecord record = daoMedicalRecord.getMedicalRecordById(form.getMedicalRecordId());
        Patient patient = daoPatient.getPatientById(form.getPatientId());

        request.setAttribute("form", form);
        request.setAttribute("medicalRecord", record);
        request.setAttribute("patient", patient);
        request.getRequestDispatcher("/jsp/actual-prescription-view.jsp").forward(request, response);
    }

    // ===== POST HANDLERS =====
    private void handleAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        String userId = currentUser != null ? currentUser.getId() : "system";

        try {
            String medicalRecordId = request.getParameter("medicalRecordId");
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            String doctorIdStr = request.getParameter("doctorId");
            Integer doctorId = (doctorIdStr != null && !doctorIdStr.isEmpty()) ? Integer.parseInt(doctorIdStr) : null;
            String formName = request.getParameter("formName");
            String notes = request.getParameter("notes");

            ActualPrescriptionForm form = new ActualPrescriptionForm();
            form.setMedicalRecordId(medicalRecordId);
            form.setPatientId(patientId);
            form.setDoctorId(doctorId);
            form.setFormName(formName);
            form.setNotes(notes);
            form.setCreatedBy(userId);
            form.setUpdatedBy(userId);

            List<ActualPrescriptionMedicine> medicines = parseMedicinesFromRequest(request);

            boolean success = daoRx.addForm(form, medicines);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/actual-prescriptions?action=listByMedicalRecord&medicalRecordId=" + medicalRecordId + "&success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/actual-prescriptions?action=listByMedicalRecord&medicalRecordId=" + medicalRecordId + "&error=add_failed");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding prescription form: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=system_error");
        }
    }

    private void handleUpdateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        String userId = currentUser != null ? currentUser.getId() : "system";

        try {
            String formId = request.getParameter("formId");
            String formName = request.getParameter("formName");
            String notes = request.getParameter("notes");

            ActualPrescriptionForm form = daoRx.getFormById(formId);
            if (form == null) {
                response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=form_not_found");
                return;
            }
            form.setFormName(formName);
            form.setNotes(notes);
            form.setUpdatedBy(userId);

            List<ActualPrescriptionMedicine> medicines = parseMedicinesFromRequest(request);
            boolean success = daoRx.updateForm(form, medicines);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/actual-prescriptions?action=listByMedicalRecord&medicalRecordId=" + form.getMedicalRecordId() + "&success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/actual-prescriptions?action=listByMedicalRecord&medicalRecordId=" + form.getMedicalRecordId() + "&error=update_failed");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating prescription form: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions?error=system_error");
        }
    }

    private void handleDeleteForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String formId = request.getParameter("formId");
        String medicalRecordId = request.getParameter("medicalRecordId");
        if (formId == null || formId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/actual-prescriptions");
            return;
        }
        boolean success = daoRx.deleteForm(formId);
        String redirectUrl = request.getContextPath() + "/actual-prescriptions";
        if (medicalRecordId != null) {
            redirectUrl += "?action=listByMedicalRecord&medicalRecordId=" + medicalRecordId;
        }
        if (success) {
            response.sendRedirect(redirectUrl + "&success=deleted");
        } else {
            response.sendRedirect(redirectUrl + "&error=delete_failed");
        }
    }

    // ===== UTILITY =====
    private List<ActualPrescriptionMedicine> parseMedicinesFromRequest(HttpServletRequest request) {
        List<ActualPrescriptionMedicine> meds = new ArrayList<>();
        String[] names = request.getParameterValues("medicineName");
        if (names == null) return meds;
        String[] days = request.getParameterValues("daysOfTreatment");
        String[] unitsPerDay = request.getParameterValues("unitsPerDay");
        String[] quantities = request.getParameterValues("totalQuantity");
        String[] units = request.getParameterValues("unitOfMeasure");
        String[] routes = request.getParameterValues("administrationRoute");
        String[] instructions = request.getParameterValues("usageInstructions");

        for (int i = 0; i < names.length; i++) {
            if (names[i] == null || names[i].trim().isEmpty()) continue;
            ActualPrescriptionMedicine med = new ActualPrescriptionMedicine();
            med.setMedicineName(names[i].trim());
            if (days != null && days.length > i && !days[i].isEmpty()) med.setDaysOfTreatment(Integer.parseInt(days[i]));
            if (unitsPerDay != null && unitsPerDay.length > i && !unitsPerDay[i].isEmpty()) med.setUnitsPerDay(Integer.parseInt(unitsPerDay[i]));
            if (quantities != null && quantities.length > i && !quantities[i].isEmpty()) med.setTotalQuantity(Integer.parseInt(quantities[i]));
            if (units != null && units.length > i) med.setUnitOfMeasure(units[i]);
            if (routes != null && routes.length > i) med.setAdministrationRoute(routes[i]);
            if (instructions != null && instructions.length > i) med.setUsageInstructions(instructions[i]);
            meds.add(med);
        }
        return meds;
    }
} 