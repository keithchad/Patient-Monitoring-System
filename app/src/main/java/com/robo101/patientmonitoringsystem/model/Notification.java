package com.robo101.patientmonitoringsystem.model;

public class Notification {

    private String userId;
    private double textIssue;
    private String textIssueName;
    private boolean isHeartBeat;
    private boolean isBloodOxygen;
    private boolean isBloodPressure;
    private boolean isTemperature;

    public Notification() {}

    public Notification(String userId, double textIssue, String textIssueName, boolean isHeartBeat, boolean isBloodOxygen, boolean isBloodPressure, boolean isTemperature) {
        this.userId = userId;
        this.textIssue = textIssue;
        this.textIssueName = textIssueName;
        this.isHeartBeat = isHeartBeat;
        this.isBloodOxygen = isBloodOxygen;
        this.isBloodPressure = isBloodPressure;
        this.isTemperature = isTemperature;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTextIssue() {
        return textIssue;
    }

    public void setTextIssue(double textIssue) {
        this.textIssue = textIssue;
    }

    public String getTextIssueName() {
        return textIssueName;
    }

    public void setTextIssueName(String textIssueName) {
        this.textIssueName = textIssueName;
    }

    public boolean isHeartBeat() {
        return isHeartBeat;
    }

    public void setHeartBeat(boolean heartBeat) {
        isHeartBeat = heartBeat;
    }

    public boolean isBloodOxygen() {
        return isBloodOxygen;
    }

    public void setBloodOxygen(boolean bloodOxygen) {
        isBloodOxygen = bloodOxygen;
    }

    public boolean isBloodPressure() {
        return isBloodPressure;
    }

    public void setBloodPressure(boolean bloodPressure) {
        isBloodPressure = bloodPressure;
    }

    public boolean isTemperature() {
        return isTemperature;
    }

    public void setTemperature(boolean temperature) {
        isTemperature = temperature;
    }
}
