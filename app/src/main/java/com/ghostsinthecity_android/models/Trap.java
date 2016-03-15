package com.ghostsinthecity_android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trap implements Parcelable {

    String uid;
    Point pos;
    int status;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    protected Trap(Parcel in) {
        uid = in.readString();
        pos = (Point) in.readValue(Point.class.getClassLoader());
        status = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeValue(pos);
        dest.writeInt(status);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trap> CREATOR = new Parcelable.Creator<Trap>() {
        @Override
        public Trap createFromParcel(Parcel in) {
            return new Trap(in);
        }

        @Override
        public Trap[] newArray(int size) {
            return new Trap[size];
        }
    };
}
