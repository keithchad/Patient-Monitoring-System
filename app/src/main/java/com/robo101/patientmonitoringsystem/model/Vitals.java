package com.robo101.patientmonitoringsystem.model;

public class Vitals {

    private String id;
    private int heartBeat;
    private int bloodOxygen;
    private int bloodPressure;
    private int bodyTemperature;

    public Vitals() {}

    public Vitals(String id, int heartBeat, int bloodOxygen, int bloodPressure, int bodyTemperature) {
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

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    public int getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(int bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public int getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(int bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public int getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(int bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }
}
