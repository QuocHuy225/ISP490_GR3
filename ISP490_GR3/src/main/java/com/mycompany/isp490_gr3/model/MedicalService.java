package com.mycompany.isp490_gr3.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * MedicalService model corresponding to medical_services table in database
 */
public class MedicalService {
    
    private int servicesId;
    private String serviceGroup;
    private String serviceName;
    private BigDecimal price;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isdeleted;
    
    // Default constructor
    public MedicalService() {
        this.isdeleted = false;
    }
    
    // Constructor for adding new service
    public MedicalService(String serviceGroup, String serviceName, BigDecimal price) {
        this.serviceGroup = serviceGroup;
        this.serviceName = serviceName;
        this.price = price;
        this.isdeleted = false;
    }
    
    // Full constructor
    public MedicalService(int servicesId, String serviceGroup, String serviceName, 
                         BigDecimal price, Timestamp createdAt, Timestamp updatedAt, boolean isdeleted) {
        this.servicesId = servicesId;
        this.serviceGroup = serviceGroup;
        this.serviceName = serviceName;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isdeleted = isdeleted;
    }
    
    // Getters and Setters
    public int getServicesId() {
        return servicesId;
    }
    
    public void setServicesId(int servicesId) {
        this.servicesId = servicesId;
    }
    
    public String getServiceGroup() {
        return serviceGroup;
    }
    
    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
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
        return "MedicalService{" +
                "servicesId=" + servicesId +
                ", serviceGroup='" + serviceGroup + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isdeleted=" + isdeleted +
                '}';
    }
} 