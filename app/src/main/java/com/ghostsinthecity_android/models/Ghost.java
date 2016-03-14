package com.ghostsinthecity_android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ghost implements Parcelable {

    String uid;
    int level;
    int mood;
    Point pos;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    protected Ghost(Parcel in) {
        uid = in.readString();
        level = in.readInt();
        mood = in.readInt();
        pos = (Point) in.readValue(Point.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeInt(level);
        dest.writeInt(mood);
        dest.writeValue(pos);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Ghost> CREATOR = new Parcelable.Creator<Ghost>() {
        @Override
        public Ghost createFromParcel(Parcel in) {
            return new Ghost(in);
        }

        @Override
        public Ghost[] newArray(int size) {
            return new Ghost[size];
        }
    };
}
