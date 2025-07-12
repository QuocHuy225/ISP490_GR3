package com.mycompany.isp490_gr3.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class PaymentReceipt {
    private String receiptId;
    private String invoiceId;
    private int receiptNumber; // 1 hoặc 2
    private Timestamp receiptDate;
    private BigDecimal totalServiceAmount;
    private BigDecimal totalSupplyAmount;
    private BigDecimal totalMedicineAmount;
    private BigDecimal totalAmount;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean isDeleted;
    
    // Relations
    private List<InvoiceItem> receiptItems;
    
    // Constructors
    public PaymentReceipt() {
        this.totalServiceAmount = BigDecimal.ZERO;
        this.totalSupplyAmount = BigDecimal.ZERO;
        this.totalMedicineAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.isDeleted = false;
    }
    
    public PaymentReceipt(String receiptId, String invoiceId, int receiptNumber) {
        this();
        this.receiptId = receiptId;
        this.invoiceId = invoiceId;
        this.receiptNumber = receiptNumber;
    }
    
    // Business Logic Methods
    public void calculateTotals() {
        if (receiptItems != null && !receiptItems.isEmpty()) {
            BigDecimal serviceTotal = BigDecimal.ZERO;
            BigDecimal supplyTotal = BigDecimal.ZERO;
            BigDecimal medicineTotal = BigDecimal.ZERO;
            
            for (InvoiceItem item : receiptItems) {
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
            this.totalSupplyAmount = supplyTotal;
            this.totalMedicineAmount = medicineTotal;
            this.totalAmount = serviceTotal.add(supplyTotal).add(medicineTotal);
        }
    }
    
    // Getters and Setters
    public String getReceiptId() { return receiptId; }
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }
    
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    
    public int getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(int receiptNumber) { this.receiptNumber = receiptNumber; }
    
    public Timestamp getReceiptDate() { return receiptDate; }
    public void setReceiptDate(Timestamp receiptDate) { this.receiptDate = receiptDate; }
    
    public BigDecimal getTotalServiceAmount() { return totalServiceAmount; }
    public void setTotalServiceAmount(BigDecimal totalServiceAmount) { this.totalServiceAmount = totalServiceAmount; }
    
    public BigDecimal getTotalSupplyAmount() { return totalSupplyAmount; }
    public void setTotalSupplyAmount(BigDecimal totalSupplyAmount) { this.totalSupplyAmount = totalSupplyAmount; }
    
    public BigDecimal getTotalMedicineAmount() { return totalMedicineAmount; }
    public void setTotalMedicineAmount(BigDecimal totalMedicineAmount) { this.totalMedicineAmount = totalMedicineAmount; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
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
    
    public List<InvoiceItem> getReceiptItems() { return receiptItems; }
    public void setReceiptItems(List<InvoiceItem> receiptItems) { 
        this.receiptItems = receiptItems;
        calculateTotals();
    }
} 