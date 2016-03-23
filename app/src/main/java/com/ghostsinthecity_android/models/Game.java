package com.ghostsinthecity_android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by Piero on 03/03/16.
 */
public class Game implements Parcelable {

    String id;
    String name;
    int n_players;
    int status;
    String g_type;
    List<Player> players;
    List<Ghost> ghosts;
    List<Treasure> treasures;
    List<Trap> traps;
    Polygon area;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getN_players() {
        return n_players;
    }

    public void setN_players(int n_players) {
        this.n_players = n_players;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getG_type() {
        return g_type;
    }

    public void setG_type(String g_type) {
        this.g_type = g_type;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public void setGhosts(List<Ghost> ghosts) {
        this.ghosts = ghosts;
    }

    public List<Treasure> getTreasures() {
        return treasures;
    }

    public void setTreasures(List<Treasure> treasures) {
        this.treasures = treasures;
    }

    public List<Trap> getTraps() {
        return traps;
    }

    public void setTraps(List<Trap> traps) {
        this.traps = traps;
    }

    public Polygon getArea() {
        return area;
    }

    public void setArea(Polygon area) {
        this.area = area;
    }

    public static Creator<Game> getCREATOR() {
        return CREATOR;
    }

    protected Game(Parcel in) {
        id = in.readString();
        name = in.readString();
        n_players = in.readInt();
        status = in.readInt();
        g_type = in.readString();
        if (in.readByte() == 0x01) {
            players = new ArrayList<Player>();
            in.readList(players, Player.class.getClassLoader());
        } else {
            players = null;
        }
        if (in.readByte() == 0x01) {
            ghosts = new ArrayList<Ghost>();
            in.readList(ghosts, Ghost.class.getClassLoader());
        } else {
            ghosts = null;
        }
        if (in.readByte() == 0x01) {
            treasures = new ArrayList<Treasure>();
            in.readList(treasures, Treasure.class.getClassLoader());
        } else {
            treasures = null;
        }
        if (in.readByte() == 0x01) {
            traps = new ArrayList<Trap>();
            in.readList(traps, Trap.class.getClassLoader());
        } else {
            traps = null;
        }

        area = (Polygon) in.readValue(Polygon.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(n_players);
        dest.writeInt(status);
        dest.writeString(g_type);
        if (players == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(players);
        }
        if (ghosts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ghosts);
        }
        if (treasures == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(treasures);
        }
        if (traps == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(traps);
        }
        dest.writeValue(area);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

}
