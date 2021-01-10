package com.robo101.patientmonitoringsystem.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.robo101.patientmonitoringsystem.model.Tips;

public class TipsResponse {

    @SerializedName("slip")
    @Expose
    private Tips tips;

    public Tips getTips() {
        return tips;
    }

    public void setTips(Tips tips) {
        this.tips = tips;
    }
}
