package com.mycompany.isp490_gr3.model;

import java.sql.Timestamp;

/**
 * Model đại diện cho bảng actual_prescription_medicine (thuốc kê đơn thực tế).
 */
public class ActualPrescriptionMedicine {

    private Integer actualPreMedicineId;
    private String medicineName;
    private Integer daysOfTreatment;
    private Integer unitsPerDay;
    private Integer totalQuantity;
    private String unitOfMeasure;
    private String administrationRoute;
    private String usageInstructions;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean deleted;

    public ActualPrescriptionMedicine() {}

    public Integer getActualPreMedicineId() {
        return actualPreMedicineId;
    }

    public void setActualPreMedicineId(Integer actualPreMedicineId) {
        this.actualPreMedicineId = actualPreMedicineId;
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
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
} 