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
            // Redirect to not-found page with medical record context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=medical-record&id=" + medicalRecordId);
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
            // Redirect to not-found page with medical record context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=medical-record&id=" + medicalRecordId);
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
            // Redirect to not-found page with invoice context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=invoice&id=" + invoiceId);
            return;
        }
        
        MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(invoice.getMedicalRecordId());
        if (medicalRecord == null) {
            // Redirect to not-found page with medical record context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=medical-record&id=" + invoice.getMedicalRecordId());
            return;
        }
        
        // Check if medical record is completed - don't allow editing if completed
        if (medicalRecord.isCompleted()) {
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=view&invoiceId=" + invoiceId + "&error=medical_record_completed");
            return;
        }
        
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
            // Redirect to not-found page with invoice context
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp?type=invoice&id=" + invoiceId);
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
                LOGGER.log(Level.WARNING, "Missing required fields - medicalRecordId: {0}, patientId: {1}", 
                          new Object[]{medicalRecordId, patientIdStr});
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + medicalRecordId + "&error=missing_data");
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
                String invoiceId = invoice.getInvoiceId();
                // Redirect trực tiếp đến view invoice sau khi tạo thành công
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=view&invoiceId=" + invoiceId + "&success=added");
            } else {
                LOGGER.log(Level.SEVERE, "Failed to create invoice for medical record: {0}", medicalRecordId);
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + medicalRecordId + "&error=add_failed");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in add invoice: {0}", e.getMessage());
            String redirectMedicalRecordId = request.getParameter("medicalRecordId");
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + redirectMedicalRecordId + "&error=invalid_data");
        } catch (IllegalStateException e) {
            String redirectMedicalRecordId = request.getParameter("medicalRecordId");
            if (e.getMessage().contains("already exists")) {
                // Invoice already exists for this medical record - redirect to view existing invoice
                List<Invoice> existingInvoices = daoInvoice.getInvoicesByMedicalRecord(redirectMedicalRecordId);
                if (!existingInvoices.isEmpty()) {
                    Invoice existingInvoice = existingInvoices.get(0);
                    response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=view&invoiceId=" + existingInvoice.getInvoiceId() + "&error=invoice_exists");
                } else {
                    response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + redirectMedicalRecordId + "&error=invoice_exists");
                }
            } else if (e.getMessage().contains("Database tables are not up to date")) {
                LOGGER.log(Level.SEVERE, "Database tables missing for medical record: {0}", redirectMedicalRecordId);
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + redirectMedicalRecordId + "&error=database_update_needed&message=" + java.net.URLEncoder.encode("Cần chạy script cập nhật database. Vui lòng liên hệ admin.", "UTF-8"));
            } else {
                // Other state errors (e.g. insufficient stock)
                LOGGER.log(Level.WARNING, "Insufficient stock: {0}", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + redirectMedicalRecordId + "&error=insufficient_stock&message=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error adding invoice: {0}", e.getMessage());
            String redirectMedicalRecordId = request.getParameter("medicalRecordId");
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=new&medicalRecordId=" + redirectMedicalRecordId + "&error=system_error");
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
            
            // Check if medical record is completed - don't allow updating if completed
            MedicalRecord medicalRecord = daoMedicalRecord.getMedicalRecordById(existingInvoice.getMedicalRecordId());
            if (medicalRecord != null && medicalRecord.isCompleted()) {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=view&invoiceId=" + invoiceId + "&error=medical_record_completed");
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
                // Redirect đến view invoice sau khi update thành công
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=view&invoiceId=" + existingInvoice.getInvoiceId() + "&success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=edit&invoiceId=" + existingInvoice.getInvoiceId() + "&error=update_failed");
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in update invoice: {0}", e.getMessage());
            String redirectInvoiceId = request.getParameter("invoiceId");
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=edit&invoiceId=" + redirectInvoiceId + "&error=invalid_data");
        } catch (IllegalStateException e) {
            // Lỗi tồn kho không đủ
            LOGGER.log(Level.WARNING, "Insufficient stock for update: {0}", e.getMessage());
            String redirectInvoiceId = request.getParameter("invoiceId");
            response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=edit&invoiceId=" + redirectInvoiceId + "&error=insufficient_stock&message=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating invoice: {0}", e.getMessage());
            String redirectInvoiceId = request.getParameter("invoiceId");
            if (redirectInvoiceId != null && !redirectInvoiceId.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?action=edit&invoiceId=" + redirectInvoiceId + "&error=system_error");
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/invoices?error=system_error");
            }
        }
    }
    
    // ===== UTILITY METHODS =====
    
    private List<InvoiceItem> parseInvoiceItems(HttpServletRequest request) {
        List<InvoiceItem> items = new ArrayList<>();
        
        // Parse Receipt 1 items
        parseReceiptItems(request, "receipt1", items);
        
        // Parse Receipt 2 items (if enabled)
        String enableSecondReceipt = request.getParameter("enableSecondReceipt");
        if ("true".equals(enableSecondReceipt)) {
            parseReceiptItems(request, "receipt2", items);
        }
        
        return items;
    }
    
    private void parseReceiptItems(HttpServletRequest request, String receiptId, List<InvoiceItem> items) {
        // Parse services for this receipt
        parseItemsForType(request, receiptId, "service", items);
        
        // Parse supplies for this receipt
        parseItemsForType(request, receiptId, "supply", items);
        
        // Parse medicines for this receipt
        parseItemsForType(request, receiptId, "medicine", items);
    }
    
    private void parseItemsForType(HttpServletRequest request, String receiptId, String itemType, List<InvoiceItem> items) {
        // Get all parameters that match the pattern: receipt1_serviceId, receipt1_serviceQuantity, etc.
        String[] itemIds = request.getParameterValues(receiptId + "_" + itemType + "Id");
        String[] quantities = request.getParameterValues(receiptId + "_" + itemType + "Quantity");
        
        if (itemIds != null && quantities != null) {
            for (int i = 0; i < itemIds.length; i++) {
                if (i < quantities.length && itemIds[i] != null && !itemIds[i].isEmpty() && 
                    quantities[i] != null && !quantities[i].isEmpty()) {
                    try {
                        int itemId = Integer.parseInt(itemIds[i]);
                        int quantity = Integer.parseInt(quantities[i]);
                        
                        // Get item name and price from hidden fields
                        String itemName = request.getParameter(receiptId + "_" + itemType + "Name_" + itemId);
                        String itemPriceStr = request.getParameter(receiptId + "_" + itemType + "Price_" + itemId);
                        
                        if (itemName != null && itemPriceStr != null) {
                            BigDecimal price = new BigDecimal(itemPriceStr);
                            int receiptNumber = "receipt1".equals(receiptId) ? 1 : 2;
                            InvoiceItem item = new InvoiceItem(null, receiptNumber, itemType, itemId, itemName, quantity, price);
                            items.add(item);
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid item data - user will see validation on frontend
                    }
                }
            }
        }
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