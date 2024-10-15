package com.gypsee.sdk.models;

public class User {

    private String userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
            userTypes, referCode, createdOn, lastUpdatedOn, walletAmount;

    private boolean approved, locked, signUpBonusCredited,referCodeApplied, inTrainingMode;

    private SubscriptionModel userSubscriptions;

    public User(String userId, String userName, String userFullName, String userEmail, String userPhoneNumber, String userAccessToken, String fcmToken, String userImg, String userDeviceMac, String userTypes, String referCode, String createdOn, String lastUpdatedOn, boolean approved, boolean locked, boolean signUpBonusCredited, boolean referCodeApplied, boolean inTrainingMode, String walletAmount) {
        this.userId = userId;
        this.userName = userName;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userAccessToken = userAccessToken;
        this.fcmToken = fcmToken;
        this.userImg = userImg;
        this.userDeviceMac = userDeviceMac;
        this.userTypes = userTypes;
        this.referCode = referCode;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.approved = approved;
        this.locked = locked;
        this.signUpBonusCredited = signUpBonusCredited;
        this.referCodeApplied = referCodeApplied;
        this.inTrainingMode = inTrainingMode;
        this.walletAmount = walletAmount;
    }

    public SubscriptionModel getUserSubscriptions() {
        return userSubscriptions;
    }

    public void setUserSubscriptions(SubscriptionModel userSubscriptions) {
        this.userSubscriptions = userSubscriptions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public boolean isInTrainingMode() {
        return inTrainingMode;
    }

    public void setInTrainingMode(boolean inTrainingMode) {
        this.inTrainingMode = inTrainingMode;
    }

    public String getUserAccessToken() {
        return userAccessToken;
    }

    public void setUserAccessToken(String userAccessToken) {
        this.userAccessToken = userAccessToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserDeviceMac() {
        return userDeviceMac;
    }

    public void setUserDeviceMac(String userDeviceMac) {
        this.userDeviceMac = userDeviceMac;
    }

    public String getUserTypes() {
        return userTypes;
    }

    public void setUserTypes(String userTypes) {
        this.userTypes = userTypes;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(String lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }



    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isSignUpBonusCredited() {
        return signUpBonusCredited;
    }

    public void setSignUpBonusCredited(boolean signUpBonusCredited) {
        this.signUpBonusCredited = signUpBonusCredited;
    }

    public boolean isReferCodeApplied() {
        return referCodeApplied;
    }

    public void setReferCodeApplied(boolean referCodeApplied) {
        this.referCodeApplied = referCodeApplied;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhoneNumber='" + userPhoneNumber + '\'' +
                ", userAccessToken='" + userAccessToken + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", userImg='" + userImg + '\'' +
                ", userDeviceMac='" + userDeviceMac + '\'' +
                ", userTypes='" + userTypes + '\'' +
                ", referCode='" + referCode + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", lastUpdatedOn='" + lastUpdatedOn + '\'' +
                ", approved=" + approved +
                ", locked=" + locked +
                ", signUpBonusCredited=" + signUpBonusCredited +
                ", referCodeApplied=" + referCodeApplied +
                '}';
    }
}

