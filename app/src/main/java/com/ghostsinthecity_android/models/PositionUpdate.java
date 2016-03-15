package com.ghostsinthecity_android.models;

/**
 * Created by Piero on 15/03/16.
 */
public class PositionUpdate {

    String event;
    Point pos;

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
}
