package com.gypsee.sdk.models;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class PerformanceModel implements Parcelable {
    String title, description;
    Drawable drawable;
    int rating;

    public PerformanceModel(String title, String description, Drawable drawable, int rating) {
        this.title = title;
        this.description = description;
        this.drawable = drawable;
        this.rating = rating;
    }

    protected PerformanceModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        rating = in.readInt();
    }

    public static final Creator<PerformanceModel> CREATOR = new Creator<PerformanceModel>() {
        @Override
        public PerformanceModel createFromParcel(Parcel in) {
            return new PerformanceModel(in);
        }

        @Override
        public PerformanceModel[] newArray(int size) {
            return new PerformanceModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(rating);
    }
}
