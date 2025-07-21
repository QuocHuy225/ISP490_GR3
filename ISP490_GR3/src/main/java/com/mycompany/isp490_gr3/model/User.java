package com.mycompany.isp490_gr3.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * User model tương ứng với bảng user trong database
 */
public class User {
  
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
    private String otherContact;
    private Role role;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
    private boolean isDeleted;
    
    // Email verification fields
    private String verificationToken;
    private boolean isEmailVerified;
    private Timestamp verificationExpiryTime;
    
    // Reset password fields
    private String resetPasswordToken;
    private Timestamp resetPasswordExpiry;
    
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
                String otherContact, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.otherContact = otherContact;
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
    
    public String getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
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
    
    public String getVerificationToken() {
        return verificationToken;
    }
    
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
    
    public boolean isEmailVerified() {
        return isEmailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
    
    public Timestamp getVerificationExpiryTime() {
        return verificationExpiryTime;
    }
    
    public void setVerificationExpiryTime(Timestamp verificationExpiryTime) {
        this.verificationExpiryTime = verificationExpiryTime;
    }
    
    public String getResetPasswordToken() {
        return resetPasswordToken;
    }
    
    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }
    
    public Timestamp getResetPasswordExpiry() {
        return resetPasswordExpiry;
    }
    
    public void setResetPasswordExpiry(Timestamp resetPasswordExpiry) {
        this.resetPasswordExpiry = resetPasswordExpiry;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", otherContact='" + otherContact + '\'' +
                ", role=" + role +
                ", isEmailVerified=" + isEmailVerified +
                '}';
    }
} 