package com.ghostsinthecity_android.models;

/**
 * Created by Piero on 03/03/16.
 */
public class JoinGame {

    String event;
    Game game;
    Point pos;

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
