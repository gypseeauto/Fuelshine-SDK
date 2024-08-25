package com.gypsee.sdk.models;

public class MemberModel {

    String id, name, city, mobileNo, email, relation;

    public MemberModel(String id, String name, String city, String mobileNo, String email, String relation) {
        this.name = name;
        this.city = city;
        this.mobileNo = mobileNo;
        this.email = email;
        this.relation = relation;
        this.id = id;
    }


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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
