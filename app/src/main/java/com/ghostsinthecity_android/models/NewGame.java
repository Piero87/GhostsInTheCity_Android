package com.ghostsinthecity_android.models;

public class NewGame {

    String event;
    String name;
    int n_players;
    Point pos;
    int game_area_edge;
    String game_type;

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

    public int getPlayersNumber() {
        return n_players;
    }

    public void setPlayersNumber(int n_players) {
        this.n_players = n_players;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public int getArenaSideDimension() {
        return game_area_edge;
    }

    public void setArenaSideDimension(int game_area_edge) {
        this.game_area_edge = game_area_edge;
    }

    public String getGameType() {
        return game_type;
    }

    public void setGameType(String game_type) {
        this.game_type = game_type;
    }
}
