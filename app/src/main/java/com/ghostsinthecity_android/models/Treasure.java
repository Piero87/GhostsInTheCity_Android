package com.ghostsinthecity_android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Treasure implements Parcelable {

    String uid;
    int status;
    Point pos;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    protected Treasure(Parcel in) {
        uid = in.readString();
        status = in.readInt();
        pos = (Point) in.readValue(Point.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeInt(status);
        dest.writeValue(pos);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Treasure> CREATOR = new Parcelable.Creator<Treasure>() {
        @Override
        public Treasure createFromParcel(Parcel in) {
            return new Treasure(in);
        }

        @Override
        public Treasure[] newArray(int size) {
            return new Treasure[size];
        }
    };
}
