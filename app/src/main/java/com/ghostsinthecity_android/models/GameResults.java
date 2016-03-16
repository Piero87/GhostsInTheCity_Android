package com.ghostsinthecity_android.models;

import java.util.List;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

public class GameResults implements Parcelable {

    int team;
    List<Player> players;

    public GameResults() {

    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public GameResults(Parcel in) {
        team = in.readInt();
        if (in.readByte() == 0x01) {
            players = new ArrayList<Player>();
            in.readList(players, Player.class.getClassLoader());
        } else {
            players = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(team);
        if (players == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(players);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GameResults> CREATOR = new Parcelable.Creator<GameResults>() {
        @Override
        public GameResults createFromParcel(Parcel in) {
            return new GameResults(in);
        }

        @Override
        public GameResults[] newArray(int size) {
            return new GameResults[size];
        }
    };
}
