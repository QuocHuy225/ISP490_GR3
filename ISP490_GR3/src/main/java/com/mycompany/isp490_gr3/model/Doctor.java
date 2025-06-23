/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.isp490_gr3.model;
import java.time.LocalDateTime;

public class Doctor {
    private int id; // Corresponds to doctors.id
    private String accountId; // Corresponds to doctors.account_id
    private String fullName; // Corresponds to doctors.full_name
    private int gender; // Corresponds to doctors.gender (TINYINT)
    private String phoneNumber; // Corresponds to doctors.phone

    private int departmentId; // Corresponds to doctors.department_id

    private boolean isDeleted; // Corresponds to doctors.is_deleted
    private LocalDateTime createdAt; // Corresponds to doctors.created_at
    private LocalDateTime updatedAt; // Corresponds to doctors.updated_at

    // --- Field derived from JOINs ---
    private String specializationName; // From department.name

    public Doctor() {
    }

    // Constructor for direct fields from 'doctors' table
    public Doctor(int id, String accountId, String fullName, int gender, String phoneNumber,
                  int departmentId, boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.fullName = fullName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.departmentId = departmentId;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor with derived/joined fields (used when mapping ResultSet)
    public Doctor(int id, String accountId, String fullName, int gender, String phoneNumber,
                  int departmentId, boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt,
                  String specializationName) {
        this(id, accountId, fullName, gender, phoneNumber, departmentId, isDeleted, createdAt, updatedAt);
        this.specializationName = specializationName;
    }

    // Getters
    public int getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getFullName() { return fullName; }
    public int getGender() { return gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getDepartmentId() { return departmentId; }
    public boolean getIsDeleted() { return isDeleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Getter for derived field
    public String getSpecializationName() { return specializationName; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setGender(int gender) { this.gender = gender; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public void setIsDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Setter for derived field
    public void setSpecializationName(String specializationName) { this.specializationName = specializationName; }


    @Override
    public String toString() {
        return "Doctor{" +
               "id=" + id +
               ", accountId='" + accountId + '\'' +
               ", fullName='" + fullName + '\'' +
               ", gender=" + gender +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", departmentId=" + departmentId +
               ", isDeleted=" + isDeleted +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               ", specializationName='" + specializationName + '\'' +
               '}';
    }
}
