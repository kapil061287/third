package com.depex.okeyclick.sp.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by we on 1/16/2018.
 */

public class Package implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("pac_name")
    private String packageName;
    @SerializedName("pac_image")
    private String packageImage;
    @SerializedName("pac_price_per_hr")
    private String packagePrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageImage() {
        return packageImage;
    }

    public void setPackageImage(String packageImage) {
        this.packageImage = packageImage;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Package aPackage = (Package) o;

        if (getId() != null ? !getId().equals(aPackage.getId()) : aPackage.getId() != null)
            return false;
        if (getPackageName() != null ? !getPackageName().equals(aPackage.getPackageName()) : aPackage.getPackageName() != null)
            return false;
        if (getPackageImage() != null ? !getPackageImage().equals(aPackage.getPackageImage()) : aPackage.getPackageImage() != null)
            return false;
        return getPackagePrice() != null ? getPackagePrice().equals(aPackage.getPackagePrice()) : aPackage.getPackagePrice() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getPackageName() != null ? getPackageName().hashCode() : 0);
        result = 31 * result + (getPackageImage() != null ? getPackageImage().hashCode() : 0);
        result = 31 * result + (getPackagePrice() != null ? getPackagePrice().hashCode() : 0);
        return result;
    }
}