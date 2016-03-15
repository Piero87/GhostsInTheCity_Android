package com.ghostsinthecity_android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageCode implements Parcelable {

    int code;
    String option;

    public MessageCode() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public MessageCode(Parcel in) {
        code = in.readInt();
        option = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(option);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MessageCode> CREATOR = new Parcelable.Creator<MessageCode>() {
        @Override
        public MessageCode createFromParcel(Parcel in) {
            return new MessageCode(in);
        }

        @Override
        public MessageCode[] newArray(int size) {
            return new MessageCode[size];
        }
    };
}
