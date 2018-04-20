package com.depex.okeyclick.sp.modal;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class Membership {

    @SerializedName("id")
    private int id;
    @SerializedName("plan_name")
    private String planName;
    @SerializedName("one_month_amount")
    private String oneMonthAmount;
    @SerializedName("six_month_amount")
    private String sixMonthAmount;
    @SerializedName("yearly_amount")
    private String yearlyAmount;
    @SerializedName("no_of_services")
    private String noOfServices;
    @SerializedName("admin_commission")
    private String adminCommission;
    @SerializedName("description")
    private String description;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getOneMonthAmount() {
        return oneMonthAmount;
    }

    public void setOneMonthAmount(String oneMonthAmount) {
        this.oneMonthAmount = oneMonthAmount;
    }

    public String getSixMonthAmount() {
        return sixMonthAmount;
    }

    public void setSixMonthAmount(String sixMonthAmount) {
        this.sixMonthAmount = sixMonthAmount;
    }

    public String getYearlyAmount() {
        return yearlyAmount;
    }

    public void setYearlyAmount(String yearlyAmount) {
        this.yearlyAmount = yearlyAmount;
    }

    public String getNoOfServices() {
        return noOfServices;
    }

    public void setNoOfServices(String noOfServices) {
        this.noOfServices = noOfServices;
    }

    public String getAdminCommission() {
        return adminCommission;
    }

    public void setAdminCommission(String adminCommission) {
        this.adminCommission = adminCommission;
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

        Membership that = (Membership) o;

        if (id != that.id) return false;
        if (planName != null ? !planName.equals(that.planName) : that.planName != null)
            return false;
        if (oneMonthAmount != null ? !oneMonthAmount.equals(that.oneMonthAmount) : that.oneMonthAmount != null)
            return false;
        if (sixMonthAmount != null ? !sixMonthAmount.equals(that.sixMonthAmount) : that.sixMonthAmount != null)
            return false;
        if (yearlyAmount != null ? !yearlyAmount.equals(that.yearlyAmount) : that.yearlyAmount != null)
            return false;
        if (noOfServices != null ? !noOfServices.equals(that.noOfServices) : that.noOfServices != null)
            return false;
        if (adminCommission != null ? !adminCommission.equals(that.adminCommission) : that.adminCommission != null)
            return false;
        return description != null ? description.equals(that.description) : that.description == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (planName != null ? planName.hashCode() : 0);
        result = 31 * result + (oneMonthAmount != null ? oneMonthAmount.hashCode() : 0);
        result = 31 * result + (sixMonthAmount != null ? sixMonthAmount.hashCode() : 0);
        result = 31 * result + (yearlyAmount != null ? yearlyAmount.hashCode() : 0);
        result = 31 * result + (noOfServices != null ? noOfServices.hashCode() : 0);
        result = 31 * result + (adminCommission != null ? adminCommission.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Membership{" +
                "id=" + id +
                ", planName='" + planName + '\'' +
                ", oneMonthAmount='" + oneMonthAmount + '\'' +
                ", sixMonthAmount='" + sixMonthAmount + '\'' +
                ", yearlyAmount='" + yearlyAmount + '\'' +
                ", noOfServices='" + noOfServices + '\'' +
                ", adminCommission='" + adminCommission + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    public String toJson(){
        Gson gson =new Gson();
        return gson.toJson(this);
    }

    public static Membership fromJson(String json){
        Gson gson=new Gson();
        return gson.fromJson(json, Membership.class);
    }
    public static class MemberComparator implements Comparator<Membership> {

        @Override
        public int compare(Membership membership, Membership t1) {
            return membership.getId()-t1.getId();
        }
    }
}
