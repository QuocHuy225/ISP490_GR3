package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOInvoice;
import com.mycompany.isp490_gr3.dao.DAOPatient;
import com.mycompany.isp490_gr3.dao.DAOMedicalRecord;
import com.mycompany.isp490_gr3.model.Invoice;
import com.mycompany.isp490_gr3.model.InvoiceItem;
import com.mycompany.isp490_gr3.model.Patient;
import com.mycompany.isp490_gr3.model.MedicalRecord;
import com.mycompany.isp490_gr3.model.MedicalService;
import com.mycompany.isp490_gr3.model.MedicalSupply;
import com.mycompany.isp490_gr3.model.Medicine;
import com.mycompany.isp490_gr3.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing invoices
 * Allows both ADMIN and DOCTOR access
 */
@WebServlet(name = "InvoiceController", urlPatterns = {"/doctor/invoices"})
public class InvoiceController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(InvoiceController.class.getName());
    private DAOInvoice daoInvoice;
    private DAOPatient daoPatient;
    private DAOMedicalRecord daoMedicalRecord;
    
    @Override
    public void init() throws ServletException {
        super.init();
        daoInvoice = new DAOInvoice();
        daoPatient = new DAOPatient();
        daoMedicalRecord = new DAOMedicalRecord();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkDoctorAccess(request, response)) {
            return;
        }
        
        String action = request.getParameter("action");
        String medicalRecordId = request.getParameter("medicalRecordId");
        
        // If no action but has medicalRecordId, check if invoice exists
        if (action == null && medicalRecordId != null) {
            handleMedicalRecordInvoice(request, response);
            return;
        }
        
        // Otherwise process specific actions
        try {
            switch (action) {
                case "new":
                    handleNewInvoice(request, response);
                    break;
                case "edit":
                    handleEditInvoice(request, response);
                    break;
                case "view":
                    handleViewInvoice(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/homepage");
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in InvoiceController: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=system_error");
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
                    handleAddInvoice(request, response);
                    break;
                case "update":
                    handleUpdateInvoice(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/doctor/invoices");
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in InvoiceController POST: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=system_error");
        }
    }
    
    // ===== GET HANDLERS =====
    
    private void handleMedicalRecordInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String medicalRecordId = request.getParameter("medicalRecordId");
        if (medicalRecordId == null || medicalRecordId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invalid_record");
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(medicalRecordId);
        if (medicalRecord == null) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=record_not_found");
            return;
        }
        
        // Get the single invoice for this medical record
        List<Invoice> invoices = daoInvoice.getInvoicesByMedicalRecord(medicalRecordId);
        
        // If invoice exists, show it. If not, create new one
        if (!invoices.isEmpty()) {
            Invoice invoice = invoices.get(0);
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=view&invoiceId=" + invoice.getInvoiceId());
        } else {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + medicalRecordId);
        }
    }
    
    private void handleNewInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String medicalRecordId = request.getParameter("medicalRecordId");
        if (medicalRecordId == null || medicalRecordId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invalid_record");
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(medicalRecordId);
        if (medicalRecord == null) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=record_not_found");
            return;
        }
        
        Patient patient = daoPatient.getPatientById(medicalRecord.getPatientId());
        
        // Load reference data
        List<MedicalService> services = daoInvoice.getAllServices();
        List<MedicalSupply> supplies = daoInvoice.getAllSupplies();
        List<Medicine> medicines = daoInvoice.getAllMedicines();
        
        request.setAttribute("medicalRecord", medicalRecord);
        request.setAttribute("patient", patient);
        request.setAttribute("services", services);
        request.setAttribute("supplies", supplies);
        request.setAttribute("medicines", medicines);
        request.setAttribute("action", "add");
        
        request.getRequestDispatcher("/jsp/invoice-form.jsp").forward(request, response);
    }
    
    private void handleEditInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        if (invoiceId == null || invoiceId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invalid_invoice");
            return;
        }
        
        Invoice invoice = daoInvoice.getInvoiceById(invoiceId);
        if (invoice == null) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invoice_not_found");
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(invoice.getMedicalRecordId());
        Patient patient = daoPatient.getPatientById(invoice.getPatientId());
        
        // Load reference data
        List<MedicalService> services = daoInvoice.getAllServices();
        List<MedicalSupply> supplies = daoInvoice.getAllSupplies();
        List<Medicine> medicines = daoInvoice.getAllMedicines();
        
        request.setAttribute("invoice", invoice);
        request.setAttribute("medicalRecord", medicalRecord);
        request.setAttribute("patient", patient);
        request.setAttribute("services", services);
        request.setAttribute("supplies", supplies);
        request.setAttribute("medicines", medicines);
        request.setAttribute("action", "update");
        
        request.getRequestDispatcher("/jsp/invoice-form.jsp").forward(request, response);
    }
    
    private void handleViewInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String invoiceId = request.getParameter("invoiceId");
        if (invoiceId == null || invoiceId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invalid_invoice");
            return;
        }
        
        Invoice invoice = daoInvoice.getInvoiceById(invoiceId);
        if (invoice == null) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invoice_not_found");
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(invoice.getMedicalRecordId());
        Patient patient = daoPatient.getPatientById(invoice.getPatientId());
        
        request.setAttribute("invoice", invoice);
        request.setAttribute("medicalRecord", medicalRecord);
        request.setAttribute("patient", patient);
        
        request.getRequestDispatcher("/jsp/invoice-view.jsp").forward(request, response);
    }
    
    // ===== POST HANDLERS =====
    
    private void handleAddInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        String userId = currentUser != null ? currentUser.getId() : "system";
        
        try {
            String medicalRecordId = request.getParameter("medicalRecordId");
            String patientIdStr = request.getParameter("patientId");
            String doctorIdStr = request.getParameter("doctorId");
            String notes = request.getParameter("notes");
            String discountAmountStr = request.getParameter("discountAmount");
            
            // Validate required fields
            if (medicalRecordId == null || patientIdStr == null) {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=missing_data");
                return;
            }
            
            int patientId = Integer.parseInt(patientIdStr);
            Integer doctorId = (doctorIdStr != null && !doctorIdStr.isEmpty()) ? Integer.parseInt(doctorIdStr) : null;
            BigDecimal discountAmount = (discountAmountStr != null && !discountAmountStr.isEmpty()) ? 
                                       new BigDecimal(discountAmountStr) : BigDecimal.ZERO;
            
            // Create invoice
            Invoice invoice = new Invoice();
            invoice.setMedicalRecordId(medicalRecordId);
            invoice.setPatientId(patientId);
            invoice.setDoctorId(doctorId);
            invoice.setNotes(notes);
            invoice.setDiscountAmount(discountAmount);
            invoice.setCreatedBy(userId);
            invoice.setUpdatedBy(userId);
            
            // Parse invoice items
            List<InvoiceItem> items = parseInvoiceItems(request);
            
            // Calculate totals
            invoice.setInvoiceItems(items);
            invoice.calculateTotals();
            
            // Save invoice
            boolean success = daoInvoice.addInvoice(invoice, items);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?medicalRecordId=" + medicalRecordId + "&success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?medicalRecordId=" + medicalRecordId + "&error=add_failed");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in add invoice: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invalid_data");
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("already exists")) {
                // Invoice already exists for this medical record
                String redirectMedicalRecordId = request.getParameter("medicalRecordId");
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?medicalRecordId=" + redirectMedicalRecordId + "&error=invoice_exists");
            } else {
                // Other state errors (e.g. insufficient stock)
                LOGGER.log(Level.WARNING, "Insufficient stock: {0}", e.getMessage());
                String redirectMedicalRecordId = request.getParameter("medicalRecordId");
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + redirectMedicalRecordId + "&error=insufficient_stock&message=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding invoice: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=system_error");
        }
    }
    
    private void handleUpdateInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        String userId = currentUser != null ? currentUser.getId() : "system";
        
        try {
            String invoiceId = request.getParameter("invoiceId");
            String notes = request.getParameter("notes");
            String discountAmountStr = request.getParameter("discountAmount");
            
            if (invoiceId == null || invoiceId.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invalid_invoice");
                return;
            }
            
            Invoice existingInvoice = daoInvoice.getInvoiceById(invoiceId);
            if (existingInvoice == null) {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invoice_not_found");
                return;
            }
            
            BigDecimal discountAmount = (discountAmountStr != null && !discountAmountStr.isEmpty()) ? 
                                       new BigDecimal(discountAmountStr) : BigDecimal.ZERO;
            
            // Update invoice
            existingInvoice.setNotes(notes);
            existingInvoice.setDiscountAmount(discountAmount);
            existingInvoice.setUpdatedBy(userId);
            
            // Parse invoice items
            List<InvoiceItem> items = parseInvoiceItems(request);
            
            // Calculate totals
            existingInvoice.setInvoiceItems(items);
            existingInvoice.calculateTotals();
            
            // Save invoice
            boolean success = daoInvoice.updateInvoice(existingInvoice, items);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=view&invoiceId=" + existingInvoice.getInvoiceId() + "&success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=edit&invoiceId=" + existingInvoice.getInvoiceId() + "&error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in update invoice: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=invalid_data");
        } catch (IllegalStateException e) {
            // Lỗi tồn kho không đủ
            LOGGER.log(Level.WARNING, "Insufficient stock for update: {0}", e.getMessage());
            String redirectInvoiceId = request.getParameter("invoiceId");
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=edit&invoiceId=" + redirectInvoiceId + "&error=insufficient_stock&message=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating invoice: {0}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=system_error");
        }
    }
    
    // ===== UTILITY METHODS =====
    
    private List<InvoiceItem> parseInvoiceItems(HttpServletRequest request) {
        List<InvoiceItem> items = new ArrayList<>();
        
        // Parse services
        String[] serviceIds = request.getParameterValues("serviceId");
        String[] serviceQuantities = request.getParameterValues("serviceQuantity");
        if (serviceIds != null && serviceQuantities != null) {
            for (int i = 0; i < serviceIds.length; i++) {
                if (serviceIds[i] != null && !serviceIds[i].isEmpty() && 
                    serviceQuantities[i] != null && !serviceQuantities[i].isEmpty()) {
                    try {
                        int serviceId = Integer.parseInt(serviceIds[i]);
                        int quantity = Integer.parseInt(serviceQuantities[i]);
                        String serviceName = request.getParameter("serviceName_" + serviceId);
                        String servicePriceStr = request.getParameter("servicePrice_" + serviceId);
                        
                        if (serviceName != null && servicePriceStr != null) {
                            BigDecimal price = new BigDecimal(servicePriceStr);
                            InvoiceItem item = new InvoiceItem(null, "service", serviceId, serviceName, quantity, price);
                            items.add(item);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Invalid service data: {0}", e.getMessage());
                    }
                }
            }
        }
        
        // Parse supplies
        String[] supplyIds = request.getParameterValues("supplyId");
        String[] supplyQuantities = request.getParameterValues("supplyQuantity");
        if (supplyIds != null && supplyQuantities != null) {
            for (int i = 0; i < supplyIds.length; i++) {
                if (supplyIds[i] != null && !supplyIds[i].isEmpty() && 
                    supplyQuantities[i] != null && !supplyQuantities[i].isEmpty()) {
                    try {
                        int supplyId = Integer.parseInt(supplyIds[i]);
                        int quantity = Integer.parseInt(supplyQuantities[i]);
                        String supplyName = request.getParameter("supplyName_" + supplyId);
                        String supplyPriceStr = request.getParameter("supplyPrice_" + supplyId);
                        
                        if (supplyName != null && supplyPriceStr != null) {
                            BigDecimal price = new BigDecimal(supplyPriceStr);
                            InvoiceItem item = new InvoiceItem(null, "supply", supplyId, supplyName, quantity, price);
                            items.add(item);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Invalid supply data: {0}", e.getMessage());
                    }
                }
            }
        }
        
        // Parse medicines
        String[] medicineIds = request.getParameterValues("medicineId");
        String[] medicineQuantities = request.getParameterValues("medicineQuantity");
        if (medicineIds != null && medicineQuantities != null) {
            for (int i = 0; i < medicineIds.length; i++) {
                if (medicineIds[i] != null && !medicineIds[i].isEmpty() && 
                    medicineQuantities[i] != null && !medicineQuantities[i].isEmpty()) {
                    try {
                        int medicineId = Integer.parseInt(medicineIds[i]);
                        int quantity = Integer.parseInt(medicineQuantities[i]);
                        String medicineName = request.getParameter("medicineName_" + medicineId);
                        String medicinePriceStr = request.getParameter("medicinePrice_" + medicineId);
                        
                        if (medicineName != null && medicinePriceStr != null) {
                            BigDecimal price = new BigDecimal(medicinePriceStr);
                            InvoiceItem item = new InvoiceItem(null, "medicine", medicineId, medicineName, quantity, price);
                            items.add(item);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Invalid medicine data: {0}", e.getMessage());
                    }
                }
            }
        }
        
        return items;
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
        
        // Allow Doctor to access
        if (currentUser.getRole() != User.Role.DOCTOR) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }
        
        return true;
    }
} 