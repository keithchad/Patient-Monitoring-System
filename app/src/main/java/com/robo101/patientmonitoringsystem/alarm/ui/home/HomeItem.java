package com.robo101.patientmonitoringsystem.alarm.ui.home;

public class HomeItem {

    private String medicineName;
    private String dosageSummary;

    public HomeItem(String medicineName, String dosageSummary) {
        this.medicineName = medicineName;
        this.dosageSummary = dosageSummary;
    }

    String getMedicineName() {
        return medicineName;
    }

    String getDosageSummary() {
        return dosageSummary;
    }
}
