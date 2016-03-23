package com.ghostsinthecity_android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements Parcelable {

    List<Point> vertex;

    public List<Point> getVertex() {
        return vertex;
    }

    public void setVertex(List<Point> vertex) {
        this.vertex = vertex;
    }



    protected Polygon(Parcel in) {
        if (in.readByte() == 0x01) {
            vertex = new ArrayList<Point>();
            in.readList(vertex, Point.class.getClassLoader());
        } else {
            vertex = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (vertex == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(vertex);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Polygon> CREATOR = new Parcelable.Creator<Polygon>() {
        @Override
        public Polygon createFromParcel(Parcel in) {
            return new Polygon(in);
        }

        @Override
        public Polygon[] newArray(int size) {
            return new Polygon[size];
        }
    };
}
