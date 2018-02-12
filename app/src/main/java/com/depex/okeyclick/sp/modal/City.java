package com.depex.okeyclick.sp.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class City implements Serializable {

    @SerializedName("id")
    private  String cityID;
    @SerializedName("city_name")
    private  String cityName;
    private String stateID;


    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (getCityID() != null ? !getCityID().equals(city.getCityID()) : city.getCityID() != null)
            return false;
        return getCityName() != null ? getCityName().equals(city.getCityName()) : city.getCityName() == null;
    }

    @Override
    public int hashCode() {
        int result = getCityID() != null ? getCityID().hashCode() : 0;
        result = 31 * result + (getCityName() != null ? getCityName().hashCode() : 0);
        return result;
    }
}
