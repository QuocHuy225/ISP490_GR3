package com.mycompany.isp490_gr3.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Invoice {
    private String invoiceId;
    private String medicalRecordId;
    private int patientId;
    private Integer doctorId;
    private Timestamp invoiceDate;
    
    // Tổng tiền các loại
    private BigDecimal totalServiceAmount;
    private BigDecimal totalSupplyAmount;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    
    // Ghi chú
    private String notes;
    
    // Quản lý thời gian và người thao tác
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean isDeleted;
    
    // Relations
    private List<InvoiceItem> invoiceItems;
    private Patient patient;
    private Doctor doctor;
    private MedicalRecord medicalRecord;
    
    // Payment Receipts
    private PaymentReceipt firstReceipt;
    private PaymentReceipt secondReceipt;
    
    // Constructors
    public Invoice() {
        this.totalServiceAmount = BigDecimal.ZERO;
        this.totalSupplyAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.finalAmount = BigDecimal.ZERO;
        this.isDeleted = false;
    }
    
    public Invoice(String invoiceId, String medicalRecordId, int patientId) {
        this();
        this.invoiceId = invoiceId;
        this.medicalRecordId = medicalRecordId;
        this.patientId = patientId;
    }
    
    // Business Logic Methods
    public void calculateTotals() {
        if (invoiceItems != null && !invoiceItems.isEmpty()) {
            BigDecimal serviceTotal = BigDecimal.ZERO;
            BigDecimal supplyTotal = BigDecimal.ZERO;
            BigDecimal medicineTotal = BigDecimal.ZERO;
            
            for (InvoiceItem item : invoiceItems) {
                switch (item.getItemType()) {
                    case "service":
                        serviceTotal = serviceTotal.add(item.getTotalAmount());
                        break;
                    case "supply":
                        supplyTotal = supplyTotal.add(item.getTotalAmount());
                        break;
                    case "medicine":
                        medicineTotal = medicineTotal.add(item.getTotalAmount());
                        break;
                }
            }
            
            this.totalServiceAmount = serviceTotal;
            this.totalSupplyAmount = supplyTotal.add(medicineTotal);
            
            // Tính tổng tiền không có phí khám bệnh
            this.totalAmount = serviceTotal.add(supplyTotal).add(medicineTotal);
            
            // Tính final amount sau khi trừ discount
            this.finalAmount = this.totalAmount.subtract(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO);
        }
    }
    
    // Methods for Payment Receipts
    public boolean hasSecondReceipt() {
        return secondReceipt != null && secondReceipt.getReceiptItems() != null && !secondReceipt.getReceiptItems().isEmpty();
    }
    
    public PaymentReceipt getFirstReceipt() {
        if (firstReceipt == null) {
            firstReceipt = new PaymentReceipt();
            firstReceipt.setReceiptNumber(1);
            firstReceipt.setInvoiceId(this.invoiceId);
        }
        return firstReceipt;
    }
    
    public PaymentReceipt getSecondReceipt() {
        if (secondReceipt == null) {
            secondReceipt = new PaymentReceipt();
            secondReceipt.setReceiptNumber(2);
            secondReceipt.setInvoiceId(this.invoiceId);
        }
        return secondReceipt;
    }
    
    // Getters and Setters
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    
    public String getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(String medicalRecordId) { this.medicalRecordId = medicalRecordId; }
    
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    
    public Timestamp getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Timestamp invoiceDate) { this.invoiceDate = invoiceDate; }
    
    public BigDecimal getTotalServiceAmount() { return totalServiceAmount; }
    public void setTotalServiceAmount(BigDecimal totalServiceAmount) { this.totalServiceAmount = totalServiceAmount; }
    
    public BigDecimal getTotalSupplyAmount() { return totalSupplyAmount; }
    public void setTotalSupplyAmount(BigDecimal totalSupplyAmount) { this.totalSupplyAmount = totalSupplyAmount; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    
    public List<InvoiceItem> getInvoiceItems() { return invoiceItems; }
    public void setInvoiceItems(List<InvoiceItem> invoiceItems) { this.invoiceItems = invoiceItems; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }
    
    public void setFirstReceipt(PaymentReceipt firstReceipt) { this.firstReceipt = firstReceipt; }
    public void setSecondReceipt(PaymentReceipt secondReceipt) { this.secondReceipt = secondReceipt; }
} 