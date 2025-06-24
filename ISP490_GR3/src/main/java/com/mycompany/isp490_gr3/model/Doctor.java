package com.mycompany.isp490_gr3.model;

// Loại bỏ các import không cần thiết nếu không dùng List nữa
// import java.util.List;
// import java.util.ArrayList;

public class Doctor {
    private int id;
    private String accountId;
    private String fullName;
    private int gender;
    private String phone;
    private int departmentId;
    private String specializationName; // Tên chuyên khoa sẽ lấy từ bảng department

    // Bỏ tất cả các trường sau:
    // private int yearsOfExperience;
    // private String position;
    // private String workingPlace;
    // private String introduction;
    // private List<String> achievements;
    // private List<Experience> experiences;

    public Doctor() {
        // Không cần khởi tạo achievements và experiences nữa
        // this.achievements = new ArrayList<>();
        // this.experiences = new ArrayList<>();
    }

    // Constructor đơn giản hơn nếu cần
    public Doctor(int id, String fullName, int gender, String phone, int departmentId, String specializationName) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.phone = phone;
        this.departmentId = departmentId;
        this.specializationName = specializationName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getGender() { return gender; }
    public void setGender(int gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getSpecializationName() { return specializationName; }
    public void setSpecializationName(String specializationName) { this.specializationName = specializationName; }

   
}