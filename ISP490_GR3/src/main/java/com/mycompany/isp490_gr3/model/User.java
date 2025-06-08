package com.mycompany.isp490_gr3.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * User model tương ứng với bảng user trong database
 */
public class User {
    
    // Enum cho Gender
    public enum Gender {
        NAM("Nam"),
        NU("Nữ"), 
        KHAC("Khác");
        
        private final String value;
        
        Gender(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Gender fromString(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            for (Gender gender : Gender.values()) {
                if (gender.getValue().equals(value)) {
                    return gender;
                }
            }
            return null; // Return null instead of default NAM
        }
    }
    
    // Enum cho Role
    public enum Role {
        PATIENT("Patient"),
        DOCTOR("Doctor"),
        RECEPTIONIST("Receptionist"),
        ADMIN("Admin");
        
        private final String value;
        
        Role(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Role fromString(String value) {
            for (Role role : Role.values()) {
                if (role.getValue().equals(value)) {
                    return role;
                }
            }
            return PATIENT; // default value
        }
    }
    
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private Date dob;
    private Gender gender;
    private String address;
    private Role role;
    private String googleId; // Google OAuth ID
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
    private boolean isDeleted;
    
    // Default constructor
    public User() {
        this.role = Role.PATIENT;
        this.isDeleted = false;
    }
    
    // Constructor with essential fields
    public User(String id, String fullName, String email, String password) {
        this();
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }
    
    // Full constructor
    public User(String id, String fullName, String email, String password, 
                String phone, Date dob, Gender gender, String address, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.role = role != null ? role : Role.PATIENT;
        this.isDeleted = false;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Date getDob() {
        return dob;
    }
    
    public void setDob(Date dob) {
        this.dob = dob;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getGoogleId() {
        return googleId;
    }
    
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", gender=" + gender +
                '}';
    }
} 