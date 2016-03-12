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

    public int getGame_area_edge() {
        return game_area_edge;
    }

    public void setGame_area_edge(int game_area_edge) {
        this.game_area_edge = game_area_edge;
    }

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }
}
