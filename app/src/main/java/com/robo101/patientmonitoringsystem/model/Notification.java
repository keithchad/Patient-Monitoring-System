package com.robo101.patientmonitoringsystem.model;

public class Notification {

    private String userId;
    private String textIssue;

    public Notification() {}

    public Notification(String userId, String textIssue) {
        this.userId = userId;
        this.textIssue = textIssue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTextIssue() {
        return textIssue;
    }

    public void setTextIssue(String textIssue) {
        this.textIssue = textIssue;
    }
}
