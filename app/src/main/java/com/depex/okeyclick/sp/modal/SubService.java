package com.depex.okeyclick.sp.modal;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class SubService implements Serializable {


    @SerializedName("id")
    private String id;
    @SerializedName("s_id")
    private String serviceId;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("subcategory_image")
    private String subCatImageUrl;
    @SerializedName("description")
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSubCatImageUrl() {
        return subCatImageUrl;
    }

    public void setSubCatImageUrl(String subCatImageUrl) {
        this.subCatImageUrl = subCatImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubService that = (SubService) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getServiceId() != null ? !getServiceId().equals(that.getServiceId()) : that.getServiceId() != null)
            return false;
        if (getServiceName() != null ? !getServiceName().equals(that.getServiceName()) : that.getServiceName() != null)
            return false;
        if (getSubCatImageUrl() != null ? !getSubCatImageUrl().equals(that.getSubCatImageUrl()) : that.getSubCatImageUrl() != null)
            return false;
        return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getServiceId() != null ? getServiceId().hashCode() : 0);
        result = 31 * result + (getServiceName() != null ? getServiceName().hashCode() : 0);
        result = 31 * result + (getSubCatImageUrl() != null ? getSubCatImageUrl().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}