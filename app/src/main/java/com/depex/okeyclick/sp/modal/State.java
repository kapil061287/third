package com.depex.okeyclick.sp.modal;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class State implements Serializable {

    @SerializedName("id")
    private String stateID;
    @SerializedName("name")
    private String stateName;

    private String countryID;

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (getStateID() != null ? !getStateID().equals(state.getStateID()) : state.getStateID() != null)
            return false;

        if (getCountryID() != null ? !getCountryID().equals(state.getCountryID()) : state.getCountryID() != null)
            return false;

        return getStateName() != null ? getStateName().equals(state.getStateName()) : state.getStateName() == null;
    }

    @Override
    public int hashCode() {
        int result = getStateID() != null ? getStateID().hashCode() : 0;
        result = 31 * result + (getStateName() != null ? getStateName().hashCode() : 0);
        result = 31 * result + (getCountryID() != null ? getCountryID().hashCode() : 0);
        return result;
    }
}