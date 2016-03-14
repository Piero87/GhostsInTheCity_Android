package com.ghostsinthecity_android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Key implements Parcelable {

    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    protected Key(Parcel in) {
        uid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Key> CREATOR = new Parcelable.Creator<Key>() {
        @Override
        public Key createFromParcel(Parcel in) {
            return new Key(in);
        }

        @Override
        public Key[] newArray(int size) {
            return new Key[size];
        }
    };
}
