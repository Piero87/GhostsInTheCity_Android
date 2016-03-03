package com.ghostsinthecity_android.models;

/**
 * Created by andreabuscarini on 03/03/16.
 */
public class Treasure {

    String uid;
    int status;
    Point pos;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }
}
