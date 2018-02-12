package com.depex.okeyclick.sp.modal;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.constants.UtilsMethods;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Country implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (getId() != null ? !getId().equals(country.getId()) : country.getId() != null)
            return false;
        return getName() != null ? getName().equals(country.getName()) : country.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
