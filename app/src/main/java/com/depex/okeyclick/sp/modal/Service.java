package com.depex.okeyclick.sp.modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by we on 1/15/2018.
 */

public class Service {

    @SerializedName("id")
    private String id;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("images")
    private String imagesUrl;
    @SerializedName("description")
    private String description;

    @SerializedName("subcategory")
    List<SubService> subServices;

    public void setSubServices(List<SubService> subServices) {
        this.subServices = subServices;
    }

    public List<SubService> getSubServices() {
        return subServices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        this.imagesUrl = imagesUrl;
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

        Service service = (Service) o;

        if (getId() != null ? !getId().equals(service.getId()) : service.getId() != null)
            return false;
        if (getServiceName() != null ? !getServiceName().equals(service.getServiceName()) : service.getServiceName() != null)
            return false;
        if (getImagesUrl() != null ? !getImagesUrl().equals(service.getImagesUrl()) : service.getImagesUrl() != null)
            return false;
        return getDescription() != null ? getDescription().equals(service.getDescription()) : service.getDescription() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getServiceName() != null ? getServiceName().hashCode() : 0);
        result = 31 * result + (getImagesUrl() != null ? getImagesUrl().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", imagesUrl='" + imagesUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
