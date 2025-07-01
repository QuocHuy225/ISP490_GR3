package com.mycompany.isp490_gr3.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InvoiceItem {
    private int id;
    private String invoiceId;
    private String itemType; // service, supply, medicine
    private int itemId;
    private String itemName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private Timestamp createdAt;
    
    // Constructors
    public InvoiceItem() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
    }
    
    public InvoiceItem(String invoiceId, String itemType, int itemId, String itemName, int quantity, BigDecimal unitPrice) {
        this();
        this.invoiceId = invoiceId;
        this.itemType = itemType;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotalAmount();
    }
    
    // Item Type Enum
    public enum ItemType {
        SERVICE("service"),
        SUPPLY("supply"),
        MEDICINE("medicine");
        
        private final String value;
        
        ItemType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static ItemType fromString(String value) {
            for (ItemType type : ItemType.values()) {
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return SERVICE;
        }
    }
    
    // Business Logic Methods
    public void calculateTotalAmount() {
        if (unitPrice != null && quantity > 0) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalAmount = BigDecimal.ZERO;
        }
    }
    
    public void setQuantityAndCalculateTotal(int quantity) {
        this.quantity = quantity;
        calculateTotalAmount();
    }
    
    public void setUnitPriceAndCalculateTotal(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalAmount();
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        calculateTotalAmount();
    }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice;
        calculateTotalAmount();
    }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
} 