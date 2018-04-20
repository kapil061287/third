package com.depex.okeyclick.sp.modal;

public class ChooseAdapterModal{
    private String serviceName;
    private String subServiceName;
    private String serviceId;
    private String subServiceId;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSubServiceName() {
        return subServiceName;
    }

    public void setSubServiceName(String subServiceName) {
        this.subServiceName = subServiceName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSubServiceId() {
        return subServiceId;
    }

    public void setSubServiceId(String subServiceId) {
        this.subServiceId = subServiceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChooseAdapterModal that = (ChooseAdapterModal) o;

        if (getServiceName() != null ? !getServiceName().equals(that.getServiceName()) : that.getServiceName() != null)
            return false;
        if (getSubServiceName() != null ? !getSubServiceName().equals(that.getSubServiceName()) : that.getSubServiceName() != null)
            return false;
        if (getServiceId() != null ? !getServiceId().equals(that.getServiceId()) : that.getServiceId() != null)
            return false;
        return getSubServiceId() != null ? getSubServiceId().equals(that.getSubServiceId()) : that.getSubServiceId() == null;
    }

    @Override
    public int hashCode() {
        int result = getServiceName() != null ? getServiceName().hashCode() : 0;
        result = 31 * result + (getSubServiceName() != null ? getSubServiceName().hashCode() : 0);
        result = 31 * result + (getServiceId() != null ? getServiceId().hashCode() : 0);
        result = 31 * result + (getSubServiceId() != null ? getSubServiceId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AdapterModal{" +
                "serviceName='" + serviceName + '\'' +
                ", subServiceName='" + subServiceName + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", subServiceId='" + subServiceId + '\'' +
                '}';
    }


}