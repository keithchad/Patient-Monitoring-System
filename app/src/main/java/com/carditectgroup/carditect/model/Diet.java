package com.carditectgroup.carditect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Diet {

    public String dietName;
    public String dietDescription;
    public int dietImage;

    public Diet() {}

    public Diet(String dietName, String dietDescription, int dietImage) {
        this.dietName = dietName;
        this.dietDescription = dietDescription;
        this.dietImage = dietImage;
    }

    public String getDietName() {
        return dietName;
    }

    public void setDietName(String dietName) {
        this.dietName = dietName;
    }

    public String getDietDescription() {
        return dietDescription;
    }

    public void setDietDescription(String dietDescription) {
        this.dietDescription = dietDescription;
    }

    public int getDietImage() {
        return dietImage;
    }

    public void setDietImage(int dietImage) {
        this.dietImage = dietImage;
    }
}
