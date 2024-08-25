package com.gypsee.sdk.models;

public class WalletTransactionsModel {

    String id,amount,type,description,createdOn,lastUpdatedOn,debitAmount;

    public WalletTransactionsModel(String id, String amount, String type, String description, String createdOn,String lastUpdatedOn,String  debitAmount) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.debitAmount = debitAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }
}
