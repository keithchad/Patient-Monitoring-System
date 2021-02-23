package com.robo101.patientmonitoringsystem.model;

public class Vitals {

    private String id;
    private double heartBeat;
    private double bloodOxygen;
    private double bloodPressure;
    private double bodyTemperature;

    public Vitals() {}

    public Vitals(String id, double heartBeat, double bloodOxygen, double bloodPressure, double bodyTemperature) {
        this.id = id;
        this.heartBeat = heartBeat;
        this.bloodOxygen = bloodOxygen;
        this.bloodPressure = bloodPressure;
        this.bodyTemperature = bodyTemperature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(double heartBeat) {
        this.heartBeat = heartBeat;
    }

    public double getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(double bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public double getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(double bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public double getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(double bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }
}
