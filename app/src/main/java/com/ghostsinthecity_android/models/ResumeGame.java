package com.ghostsinthecity_android.models;

/**
 * Created by Piero on 16/03/16.
 */
public class ResumeGame {

    String event;
    Point pos;
    String game_id;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }
}
