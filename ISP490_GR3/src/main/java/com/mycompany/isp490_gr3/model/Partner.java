package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;

/**
 * Partner model corresponding to partners table in database
 */
public class Partner {
    
    private int partnerId;
    private String name;
    private String phone;
    private String address;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isdeleted;
    
    // Default constructor
    public Partner() {
        this.isdeleted = false;
    }
    
    // Constructor for adding new partner
    public Partner(String name, String phone, String address, String description) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.isdeleted = false;
    }
    
    // Full constructor
    public Partner(int partnerId, String name, String phone, String address, String description, 
                   Timestamp createdAt, Timestamp updatedAt, boolean isdeleted) {
        this.partnerId = partnerId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isdeleted = isdeleted;
    }
    
    // Getters and Setters
    public int getPartnerId() {
        return partnerId;
    }
    
    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
        return "Partner{" +
                "partnerId=" + partnerId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isdeleted=" + isdeleted +
                '}';
    }
} 