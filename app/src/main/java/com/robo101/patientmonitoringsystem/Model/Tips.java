package com.robo101.patientmonitoringsystem.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tips {

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("advice")
    @Expose
    private String tips;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
