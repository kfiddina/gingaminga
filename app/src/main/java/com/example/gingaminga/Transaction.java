package com.example.gingaminga;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {

    private String id;
    private String title;
    private String category;
    private String nominal;
    private String description;
    private String date;

    public Transaction() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.category);
        dest.writeString(this.nominal);
        dest.writeString(this.description);
        dest.writeString(this.date);
    }

    protected Transaction(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.category = in.readString();
        this.nominal = in.readString();
        this.description = in.readString();
        this.date = in.readString();
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
