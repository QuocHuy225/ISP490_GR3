package com.mycompany.isp490_gr3.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * MedicalSupply model tương ứng với bảng medical_supply trong database
 */
public class MedicalSupply {
    
    private int supplyId;
    private String supplyGroup;
    private String supplyName;
    private Integer quantity; // Nullable field - used for other functions
    private BigDecimal unitPrice;
    private int stockQuantity;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isdeleted;
    
    
    public MedicalSupply() {
        this.isdeleted = false;
    }
    
    
    public MedicalSupply(String supplyGroup, String supplyName, BigDecimal unitPrice, int stockQuantity) {
        this.supplyGroup = supplyGroup;
        this.supplyName = supplyName;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.isdeleted = false;
    }
    
   
    public MedicalSupply(int supplyId, String supplyGroup, String supplyName, 
                        Integer quantity, BigDecimal unitPrice, int stockQuantity, 
                        Timestamp createdAt, Timestamp updatedAt, boolean isdeleted) {
        this.supplyId = supplyId;
        this.supplyGroup = supplyGroup;
        this.supplyName = supplyName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isdeleted = isdeleted;
    }
    
    
    public int getSupplyId() {
        return supplyId;
    }
    
    public void setSupplyId(int supplyId) {
        this.supplyId = supplyId;
    }
    
    public String getSupplyGroup() {
        return supplyGroup;
    }
    
    public void setSupplyGroup(String supplyGroup) {
        this.supplyGroup = supplyGroup;
    }
    
    public String getSupplyName() {
        return supplyName;
    }
    
    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
    
    public boolean isIsdeleted() {
        return isdeleted;
    }
    
    public void setIsdeleted(boolean isdeleted) {
        this.isdeleted = isdeleted;
    }
    
    @Override
    public String toString() {
        return "MedicalSupply{" +
                "supplyId=" + supplyId +
                ", supplyGroup='" + supplyGroup + '\'' +
                ", supplyName='" + supplyName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", stockQuantity=" + stockQuantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isdeleted=" + isdeleted +
                '}';
    }
} 