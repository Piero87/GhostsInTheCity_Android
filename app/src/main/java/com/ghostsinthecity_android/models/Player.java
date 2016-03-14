package com.ghostsinthecity_android.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * Created by andreabuscarini on 03/03/16.
 */
public class Player implements Parcelable {

    String uid;
    String name;
    String p_type;
    int team;
    Point pos;
    int gold;
    List<Key> keys;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getP_type() {
        return p_type;
    }

    public void setP_type(String p_type) {
        this.p_type = p_type;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    protected Player(Parcel in) {
        uid = in.readString();
        name = in.readString();
        p_type = in.readString();
        team = in.readInt();
        pos = (Point) in.readValue(Point.class.getClassLoader());
        gold = in.readInt();
        if (in.readByte() == 0x01) {
            keys = new ArrayList<Key>();
            in.readList(keys, Key.class.getClassLoader());
        } else {
            keys = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(p_type);
        dest.writeInt(team);
        dest.writeValue(pos);
        dest.writeInt(gold);
        if (keys == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(keys);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}
