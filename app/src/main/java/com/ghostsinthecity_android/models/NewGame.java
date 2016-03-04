package com.ghostsinthecity_android.models;

public class NewGame {

    String event;
    String name;
    int n_players;
    Point pos;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
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

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }
}
