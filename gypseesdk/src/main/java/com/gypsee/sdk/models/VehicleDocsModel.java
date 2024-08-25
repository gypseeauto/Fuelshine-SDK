package com.gypsee.sdk.models;

import android.graphics.Bitmap;

import java.io.File;

public class VehicleDocsModel {

    private String documentId, documentLink, createdOn, lastUpdatedOn,docName;
    private boolean verified;
    private Bitmap bitmap;
    private File file;
    private long documentTypeId;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public VehicleDocsModel(String documentId, String documentLink, String createdOn, String lastUpdatedOn, boolean verified, long documentTypeId, String docName) {


        this.documentId = documentId;
        this.documentLink = documentLink;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.verified = verified;
        this.documentTypeId = documentTypeId;
        this.docName = docName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }
}
