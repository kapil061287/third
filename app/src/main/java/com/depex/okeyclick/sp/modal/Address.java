package com.depex.okeyclick.sp.modal;

import java.io.Serializable;

/**
 * Created by we on 1/11/2018.
 */

public class Address implements Serializable {

    private City city;
    private  Country country;
    private  State state;
    private String addressLine;
    private int pinCode;

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (getPinCode() != address.getPinCode()) return false;
        if (getCity() != null ? !getCity().equals(address.getCity()) : address.getCity() != null)
            return false;
        if (getCountry() != null ? !getCountry().equals(address.getCountry()) : address.getCountry() != null)
            return false;
        if (getState() != null ? !getState().equals(address.getState()) : address.getState() != null)
            return false;
        return getAddressLine() != null ? getAddressLine().equals(address.getAddressLine()) : address.getAddressLine() == null;
    }

    @Override
    public int hashCode() {
        int result = getCity() != null ? getCity().hashCode() : 0;
        result = 31 * result + (getCountry() != null ? getCountry().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getAddressLine() != null ? getAddressLine().hashCode() : 0);
        result = 31 * result + getPinCode();
        return Math.abs(result);
    }
}
