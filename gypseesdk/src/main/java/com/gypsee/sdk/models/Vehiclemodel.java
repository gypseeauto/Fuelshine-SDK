package com.gypsee.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class Vehiclemodel implements Parcelable {

    private String userVehicleId, vehicleName, regNumber, purchaseDate, vehicleBrand, vehicleModel, latitude, longitude, vehicleAlerts, createdOn, lastUpdatedOn, odoMeterRdg, distancePostCC, serviceReminderkm, insuranceReminderDate, pollutionReminderDate, serviceReminderDate, Vin, chassisNo, engineNo, vehicleClass, mvTaxUpto, fitness;

    boolean approved, isObdConnected = false, vinAvl;
    float epaArAiMileage;
    int ecoSpeedStartRange,ecoSpeedEndRange;
    String customerName;

    private String newServiceReminderRemainingKm, newServiceReminderRequiredKm, rsaCouponCode, fuelType;

    public Vehiclemodel(String userVehicleId, String vehicleName, String regNumber, String purchaseDate,
                        String vehicleBrand, String vehicleModel, String latitude, String longitude,
                        String vehicleAlerts, String createdOn, String lastUpdatedOn, String odoMeterRdg, String distancePostCC,
                        String serviceReminderkm, String insuranceReminderDate, String pollutionReminderDate,
                        String serviceReminderDate, String vin, boolean approved, boolean vinAvl, String newServiceReminderRemainingKm, String newServiceReminderRequiredKm, String rsaCouponCode,String fuelType, String chassisNo, String engineNo, String customerName, String vehicleClass, String mvTaxUpto, String fitness, float epaArAiMileage, int ecoSpeedStartRange, int ecoSpeedEndRange) {
        this.userVehicleId = userVehicleId;
        this.vehicleName = vehicleName;
        this.regNumber = regNumber;
        this.purchaseDate = purchaseDate;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vehicleAlerts = vehicleAlerts;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.odoMeterRdg = odoMeterRdg;
        this.distancePostCC = distancePostCC;
        this.serviceReminderkm = serviceReminderkm;
        this.insuranceReminderDate = insuranceReminderDate;
        this.pollutionReminderDate = pollutionReminderDate;
        this.serviceReminderDate = serviceReminderDate;
        Vin = vin;
        this.approved = approved;
        this.vinAvl = vinAvl;

        this.newServiceReminderRemainingKm = newServiceReminderRemainingKm;
        this.newServiceReminderRequiredKm = newServiceReminderRequiredKm;
        this.rsaCouponCode = rsaCouponCode;
        this.fuelType = fuelType;

        this.chassisNo = chassisNo;
        this.engineNo = engineNo;
        this.customerName = customerName;

        this.vehicleClass = vehicleClass;
        this.mvTaxUpto = mvTaxUpto;
        this.fitness = fitness;
        this.epaArAiMileage = epaArAiMileage;
        this.ecoSpeedStartRange = ecoSpeedStartRange;
        this.ecoSpeedEndRange = ecoSpeedEndRange;
    }


    protected Vehiclemodel(Parcel in) {

        userVehicleId = in.readString();
        vehicleName = in.readString();
        regNumber = in.readString();
        purchaseDate = in.readString();
        vehicleBrand = in.readString();
        vehicleModel = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        vehicleAlerts = in.readString();
        createdOn = in.readString();
        lastUpdatedOn = in.readString();
        odoMeterRdg = in.readString();
        distancePostCC = in.readString();
        serviceReminderkm = in.readString();
        insuranceReminderDate = in.readString();
        pollutionReminderDate = in.readString();
        serviceReminderDate = in.readString();
        Vin = in.readString();
        approved = in.readByte() != 0;
        isObdConnected = in.readByte() != 0;
        vinAvl = in.readByte() != 0;
        newServiceReminderRemainingKm = in.readString();
        newServiceReminderRequiredKm = in.readString();
        rsaCouponCode = in.readString();
        fuelType = in.readString();
        isDeleting = in.readByte() != 0;
        chassisNo = in.readString();
        engineNo = in.readString();
        customerName = in.readString();
        vehicleClass = in.readString();
        mvTaxUpto = in.readString();
        fitness = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userVehicleId);
        dest.writeString(vehicleName);
        dest.writeString(regNumber);
        dest.writeString(purchaseDate);
        dest.writeString(vehicleBrand);
        dest.writeString(vehicleModel);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(vehicleAlerts);
        dest.writeString(createdOn);
        dest.writeString(lastUpdatedOn);
        dest.writeString(odoMeterRdg);
        dest.writeString(distancePostCC);
        dest.writeString(serviceReminderkm);
        dest.writeString(insuranceReminderDate);
        dest.writeString(pollutionReminderDate);
        dest.writeString(serviceReminderDate);
        dest.writeString(Vin);
        dest.writeByte((byte) (approved ? 1 : 0));
        dest.writeByte((byte) (isObdConnected ? 1 : 0));
        dest.writeByte((byte) (vinAvl ? 1 : 0));
        dest.writeString(newServiceReminderRemainingKm);
        dest.writeString(newServiceReminderRequiredKm);
        dest.writeString(rsaCouponCode);
        dest.writeString(fuelType);
        dest.writeByte((byte) (isDeleting ? 1 : 0));
        dest.writeString(chassisNo);
        dest.writeString(engineNo);
        dest.writeString(customerName);
        dest.writeString(vehicleClass);
        dest.writeString(mvTaxUpto);
        dest.writeString(fitness);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vehiclemodel> CREATOR = new Creator<Vehiclemodel>() {
        @Override
        public Vehiclemodel createFromParcel(Parcel in) {
            return new Vehiclemodel(in);
        }

        @Override
        public Vehiclemodel[] newArray(int size) {
            return new Vehiclemodel[size];
        }
    };

    public String getChassisNo() {
        return chassisNo;
    }

    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public String getMvTaxUpto() {
        return mvTaxUpto;
    }

    public void setMvTaxUpto(String mvTaxUpto) {
        this.mvTaxUpto = mvTaxUpto;
    }

    public String getFitness() {
        return fitness;
    }

    public void setFitness(String fitness) {
        this.fitness = fitness;
    }

    public String getcustomerName() {
        return customerName;
    }

    public void setcustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getUserVehicleId() {
        return userVehicleId;
    }

    public void setUserVehicleId(String userVehicleId) {
        this.userVehicleId = userVehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getVehicleAlerts() {
        return vehicleAlerts;
    }

    public void setVehicleAlerts(String vehicleAlerts) {
        this.vehicleAlerts = vehicleAlerts;
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

    public String getOdoMeterRdg() {
        return odoMeterRdg;
    }

    public void setOdoMeterRdg(String odoMeterRdg) {
        this.odoMeterRdg = odoMeterRdg;
    }

    public String getDistancePostCC() {
        return distancePostCC;
    }

    public void setDistancePostCC(String distancePostCC) {
        this.distancePostCC = distancePostCC;
    }

    public String getServiceReminderkm() {
        return serviceReminderkm;
    }

    public void setServiceReminderkm(String serviceReminderkm) {
        this.serviceReminderkm = serviceReminderkm;
    }

    public String getInsuranceReminderDate() {
        return insuranceReminderDate;
    }

    public void setInsuranceReminderDate(String insuranceReminderDate) {
        this.insuranceReminderDate = insuranceReminderDate;
    }

    public String getPollutionReminderDate() {
        return pollutionReminderDate;
    }

    public void setPollutionReminderDate(String pollutionReminderDate) {
        this.pollutionReminderDate = pollutionReminderDate;
    }

    public String getServicereminderduedate() {
        return serviceReminderDate;
    }

    public void setServicereminderduedate(String servicereminderduedate) {
        this.serviceReminderDate = servicereminderduedate;
    }

    public String getVin() {
        return Vin;
    }

    public void setVin(String vin) {
        Vin = vin;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isObdConnected() {
        return isObdConnected;
    }

    public void setObdConnected(boolean obdConnected) {
        isObdConnected = obdConnected;
    }

    public boolean isVinAvl() {
        return vinAvl;
    }

    public void setVinAvl(boolean vinAvl) {
        this.vinAvl = vinAvl;
    }

    public String getNewServiceReminderRemainingKm() {
        return newServiceReminderRemainingKm;
    }

    public void setNewServiceReminderRemainingKm(String newServiceReminderRemainingKm) {
        this.newServiceReminderRemainingKm = newServiceReminderRemainingKm;
    }

    public String getNewServiceReminderRequiredKm() {
        return newServiceReminderRequiredKm;
    }

    public void setNewServiceReminderRequiredKm(String newServiceReminderRequiredKm) {
        this.newServiceReminderRequiredKm = newServiceReminderRequiredKm;
    }

    public String getRsaCouponCode() {
        return rsaCouponCode;
    }

    public void setRsaCouponCode(String rsaCouponCode) {
        this.rsaCouponCode = rsaCouponCode;
    }


    boolean isDeleting;

    public boolean isDeleting() {
        return isDeleting;
    }

    public void setDeleting(boolean deleting) {
        isDeleting = deleting;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public float getEpaArAiMileage() {
        return epaArAiMileage;
    }

    public void setEpaArAiMileage(float epaArAiMileage) {
        this.epaArAiMileage = epaArAiMileage;
    }

    public int getEcoSpeedStartRange() {
        return ecoSpeedStartRange;
    }

    public void setEcoSpeedStartRange(int ecoSpeedStartRange) {
        this.ecoSpeedStartRange = ecoSpeedStartRange;
    }

    public int getEcoSpeedEndRange() {
        return ecoSpeedEndRange;
    }

    public void setEcoSpeedEndRange(int ecoSpeedEndRange) {
        this.ecoSpeedEndRange = ecoSpeedEndRange;
    }

    @Override
    public String toString() {
        return "Vehiclemodel{" +
                "userVehicleId='" + userVehicleId + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", regNumber='" + regNumber + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", vehicleBrand='" + vehicleBrand + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", vehicleAlerts='" + vehicleAlerts + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", lastUpdatedOn='" + lastUpdatedOn + '\'' +
                ", odoMeterRdg='" + odoMeterRdg + '\'' +
                ", distancePostCC='" + distancePostCC + '\'' +
                ", serviceReminderkm='" + serviceReminderkm + '\'' +
                ", insuranceReminderDate='" + insuranceReminderDate + '\'' +
                ", pollutionReminderDate='" + pollutionReminderDate + '\'' +
                ", servicereminderduedate='" + serviceReminderDate + '\'' +
                ", Vin='" + Vin + '\'' +
                ", chassisNo='" + chassisNo + '\'' +
                ", engineNo='" + engineNo + '\'' +
                ", vehicleClass='" + vehicleClass + '\'' +
                ", mvTaxUpto='" + mvTaxUpto + '\'' +
                ", fitness='" + fitness + '\'' +
                ", approved=" + approved +
                ", isObdConnected=" + isObdConnected +
                ", vinAvl=" + vinAvl +
                ", epaArAiMileage=" + epaArAiMileage +
                ", ecoSpeedStartRange=" + ecoSpeedStartRange +
                ", ecoSpeedEndRange=" + ecoSpeedEndRange +
                ", customerName='" + customerName + '\'' +
                ", newServiceReminderRemainingKm='" + newServiceReminderRemainingKm + '\'' +
                ", newServiceReminderRequiredKm='" + newServiceReminderRequiredKm + '\'' +
                ", rsaCouponCode='" + rsaCouponCode + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", isDeleting=" + isDeleting +
                '}';
    }
}
