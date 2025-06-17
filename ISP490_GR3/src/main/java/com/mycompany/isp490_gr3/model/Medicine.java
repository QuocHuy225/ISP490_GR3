package com.mycompany.isp490_gr3.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Medicine model tương ứng với bảng examination_medicines trong database
 */
public class Medicine {
    
    private int examMedicineId;
    private String medicineName;
    private Integer quantity; // Nullable field - used for other functions
    private String unitOfMeasure;
    private BigDecimal unitPrice;
    private int stockQuantity;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Default constructor
    public Medicine() {
    }
    
    // Constructor for adding new medicine
    public Medicine(String medicineName, String unitOfMeasure, BigDecimal unitPrice, int stockQuantity) {
        this.medicineName = medicineName;
        this.unitOfMeasure = unitOfMeasure;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
    }
    
    // Full constructor
    public Medicine(int examMedicineId, String medicineName, Integer quantity, 
                   String unitOfMeasure, BigDecimal unitPrice, int stockQuantity, 
                   Timestamp createdAt, Timestamp updatedAt) {
        this.examMedicineId = examMedicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unitOfMeasure = unitOfMeasure;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getExamMedicineId() {
        return examMedicineId;
    }
    
    public void setExamMedicineId(int examMedicineId) {
        this.examMedicineId = examMedicineId;
    }
    
    public String getMedicineName() {
        return medicineName;
    }
    
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }
    
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Medicine{" +
                "examMedicineId=" + examMedicineId +
                ", medicineName='" + medicineName + '\'' +
                ", quantity=" + quantity +
                ", unitOfMeasure='" + unitOfMeasure + '\'' +
                ", unitPrice=" + unitPrice +
                ", stockQuantity=" + stockQuantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 