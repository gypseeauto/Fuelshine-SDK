package com.gypsee.sdk.models;

public class AppversionModel {

    private String appVersion,versionId,deleteData,logoutUser,active,lastUpdatedOn,forceUpdate,isActive,createdOn,latest,versionCode;

    public AppversionModel(String appVersion, String versionId, String deleteData, String logoutUser, String active, String lastUpdatedOn,
                           String forceUpdate, String isActive, String createdOn, String latest,String versionCode) {
        this.appVersion = appVersion;
        this.versionId = versionId;
        this.deleteData = deleteData;
        this.logoutUser = logoutUser;
        this.active = active;
        this.lastUpdatedOn = lastUpdatedOn;
        this.forceUpdate = forceUpdate;
        this.isActive = isActive;
        this.createdOn = createdOn;
        this.latest = latest;
        this.versionCode = versionCode;
    }

    public String getAppVersion ()
    {
        return appVersion;
    }

    public void setAppVersion (String appVersion)
    {
        this.appVersion = appVersion;
    }

    public String getVersionId ()
    {
        return versionId;
    }

    public void setVersionId (String versionId)
    {
        this.versionId = versionId;
    }

    public String getDeleteData ()
    {
        return deleteData;
    }

    public void setDeleteData (String deleteData)
    {
        this.deleteData = deleteData;
    }

    public String getLogoutUser ()
    {
        return logoutUser;
    }

    public void setLogoutUser (String logoutUser)
    {
        this.logoutUser = logoutUser;
    }

    public String getActive ()
    {
        return active;
    }

    public void setActive (String active)
    {
        this.active = active;
    }

    public String getLastUpdatedOn ()
    {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn (String lastUpdatedOn)
    {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getForceUpdate ()
    {
        return forceUpdate;
    }

    public void setForceUpdate (String forceUpdate)
    {
        this.forceUpdate = forceUpdate;
    }

    public String getIsActive ()
    {
        return isActive;
    }

    public void setIsActive (String isActive)
    {
        this.isActive = isActive;
    }

    public String getCreatedOn ()
    {
        return createdOn;
    }

    public void setCreatedOn (String createdOn)
    {
        this.createdOn = createdOn;
    }

    public String getLatest ()
    {
        return latest;
    }

    public void setLatest (String latest)
    {
        this.latest = latest;
    }

    @Override
    public String toString() {
        return "AppversionModel{" +
                "appVersion='" + appVersion + '\'' +
                ", versionId='" + versionId + '\'' +
                ", deleteData='" + deleteData + '\'' +
                ", logoutUser='" + logoutUser + '\'' +
                ", active='" + active + '\'' +
                ", lastUpdatedOn='" + lastUpdatedOn + '\'' +
                ", forceUpdate='" + forceUpdate + '\'' +
                ", isActive='" + isActive + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", latest='" + latest + '\'' +
                ", versionCode='" + versionCode + '\'' +
                '}';
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
