package com.depex.okeyclick.sp.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by we on 1/13/2018.
 */

public class SignupSerailization implements Serializable {

    String v_code;
    String deviceType;
    String deviceID="82150528-23LG-4622-B303-68B4572F9305";
    String apikey;
    String accessType;
    String acccessName;
    String email;
    String password;
    String first_name;
    String last_name;
    String mobile;
    String country;
    String state;
    String category;
    String subcategory;
    @SerializedName("package")
    String pack;
    String city;
    String signupWith="";




    public String getV_code() {
        return v_code;
    }

    public void setV_code(String v_code) {
        this.v_code = v_code;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getAcccessName() {
        return acccessName;
    }

    public void setAcccessName(String acccessName) {
        this.acccessName = acccessName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSignupWith() {
        return signupWith;
    }

    public void setSignupWith(String signupWith) {
        this.signupWith = signupWith;
    }
}