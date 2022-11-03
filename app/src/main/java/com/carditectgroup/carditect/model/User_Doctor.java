package com.carditectgroup.carditect.model;

public class User_Doctor {

    private String name;
    private String imageUrl;
    private String gender;
    private String email;
    private String hospital;
    private String specialization;

    public User_Doctor() {
    }

    public User_Doctor(String name, String imageUrl, String gender, String email, String hospital, String specialization) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.gender = gender;
        this.email = email;
        this.hospital = hospital;
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
