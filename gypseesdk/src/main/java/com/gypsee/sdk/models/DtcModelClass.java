package com.gypsee.sdk.models;

public class DtcModelClass {
    String toubleCode, desc;

    public DtcModelClass(String toubleCode, String desc) {


        this.toubleCode = toubleCode;
        this.desc = desc;
    }

    public String getToubleCode() {
        return toubleCode;
    }

    public String getDesc() {
        return desc;
    }
}
