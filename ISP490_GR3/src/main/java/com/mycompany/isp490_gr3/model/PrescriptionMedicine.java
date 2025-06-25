package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;

/**
 * PrescriptionMedicine model tương ứng với bảng prescription_medicine trong database
 */
public class PrescriptionMedicine {
    
    private int preMedicineId;
    private String medicineName;
    private Integer daysOfTreatment;
    private Integer unitsPerDay;
    private Integer totalQuantity;
    private String unitOfMeasure;
    private String administrationRoute;
    private String usageInstructions;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isDeleted;
    
    // Default constructor
    public PrescriptionMedicine() {
        this.isDeleted = false;
    }
    
    // Constructor with essential fields
    public PrescriptionMedicine(String medicineName, String unitOfMeasure, String administrationRoute) {
        this();
        this.medicineName = medicineName;
        this.unitOfMeasure = unitOfMeasure;
        this.administrationRoute = administrationRoute;
    }
    
    // Full constructor
    public PrescriptionMedicine(int preMedicineId, String medicineName, Integer daysOfTreatment, 
                              Integer unitsPerDay, Integer totalQuantity, String unitOfMeasure, 
                              String administrationRoute, String usageInstructions) {
        this();
        this.preMedicineId = preMedicineId;
        this.medicineName = medicineName;
        this.daysOfTreatment = daysOfTreatment;
        this.unitsPerDay = unitsPerDay;
        this.totalQuantity = totalQuantity;
        this.unitOfMeasure = unitOfMeasure;
        this.administrationRoute = administrationRoute;
        this.usageInstructions = usageInstructions;
    }
    
    // Getters and Setters
    public int getPreMedicineId() {
        return preMedicineId;
    }
    
    public void setPreMedicineId(int preMedicineId) {
        this.preMedicineId = preMedicineId;
    }
    
    public String getMedicineName() {
        return medicineName;
    }
    
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    public Integer getDaysOfTreatment() {
        return daysOfTreatment;
    }
    
    public void setDaysOfTreatment(Integer daysOfTreatment) {
        this.daysOfTreatment = daysOfTreatment;
    }
    
    public Integer getUnitsPerDay() {
        return unitsPerDay;
    }
    
    public void setUnitsPerDay(Integer unitsPerDay) {
        this.unitsPerDay = unitsPerDay;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }
    
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
    
    public String getAdministrationRoute() {
        return administrationRoute;
    }
    
    public void setAdministrationRoute(String administrationRoute) {
        this.administrationRoute = administrationRoute;
    }
    
    public String getUsageInstructions() {
        return usageInstructions;
    }
    
    public void setUsageInstructions(String usageInstructions) {
        this.usageInstructions = usageInstructions;
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
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    
    @Override
    public String toString() {
        return "PrescriptionMedicine{" +
                "preMedicineId=" + preMedicineId +
                ", medicineName='" + medicineName + '\'' +
                ", daysOfTreatment=" + daysOfTreatment +
                ", unitsPerDay=" + unitsPerDay +
                ", totalQuantity=" + totalQuantity +
                ", unitOfMeasure='" + unitOfMeasure + '\'' +
                ", administrationRoute='" + administrationRoute + '\'' +
                ", usageInstructions='" + usageInstructions + '\'' +
                '}';
    }
} 