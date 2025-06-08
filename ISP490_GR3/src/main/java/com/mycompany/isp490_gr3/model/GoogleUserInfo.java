package com.mycompany.isp490_gr3.model;

/**
 * Model class to represent Google user information
 */
public class GoogleUserInfo {
    private String id;
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String locale;
    private boolean verifiedEmail;
    
    public GoogleUserInfo() {}
    
    public GoogleUserInfo(String id, String email, String name, String givenName, 
                         String familyName, String picture, String locale, boolean verifiedEmail) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
        this.locale = locale;
        this.verifiedEmail = verifiedEmail;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGivenName() {
        return givenName;
    }
    
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    
    public String getFamilyName() {
        return familyName;
    }
    
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    
    public String getPicture() {
        return picture;
    }
    
    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    public String getLocale() {
        return locale;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public boolean isVerifiedEmail() {
        return verifiedEmail;
    }
    
    public void setVerifiedEmail(boolean verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }
    
    @Override
    public String toString() {
        return "GoogleUserInfo{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", picture='" + picture + '\'' +
                ", locale='" + locale + '\'' +
                ", verifiedEmail=" + verifiedEmail +
                '}';
    }
} 