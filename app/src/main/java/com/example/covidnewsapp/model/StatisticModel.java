package com.example.covidnewsapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatisticModel {
    private int carouselIcon;
    private String carouselTitle;
    private String carouselData;

    public StatisticModel(int carouselIcon, String carouselTitle, String carouselData) {
        this.carouselIcon = carouselIcon;
        this.carouselTitle = carouselTitle;
        this.carouselData = carouselData;
    }

    public StatisticModel() { }

    public StatisticModel(String name, String value) {
        this.name = name;
        this.value = value;
        setCarouselTitle();
        setCarouselData();
    }

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("value")
    @Expose
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCarouselIcon() {
        return carouselIcon;
    }

    public void setCarouselIcon(int carouselIcon) {
        this.carouselIcon = carouselIcon;
    }

    public String getCarouselTitle() {
        return carouselTitle;
    }

    public void setCarouselTitle() {
        this.carouselTitle = name;
    }

    public String getCarouselData() {
        return carouselData;
    }

    public void setCarouselData() {
        this.carouselData = value;
    }

    public void clear() {
        this.name = null;
        this.value = null;
    }
}
