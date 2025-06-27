package com.mycompany.isp490_gr3.model;

public class Doctor {
    private int id;
    private String accountId;
    private String fullName;
    private int gender;
    private String phone;
    private boolean isDeleted;

    public Doctor() {
    }

    public Doctor(int id, String accountId, String fullName, int gender, String phone, boolean isDeleted) {
        this.id = id;
        this.accountId = accountId;
        this.fullName = fullName;
        this.gender = gender;
        this.phone = phone;
        this.isDeleted = isDeleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Doctor{" + "id=" + id + ", accountId=" + accountId + ", fullName=" + fullName + 
               ", gender=" + gender + ", phone=" + phone + ", isDeleted=" + isDeleted + '}';
    }
}