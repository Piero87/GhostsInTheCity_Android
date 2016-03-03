package com.ghostsinthecity_android;

/**
 * Created by Piero on 02/03/16.
 */
import org.json.JSONArray;
import org.json.JSONObject;

public interface GameEvent {

    public void connected ();

    public void refreshGameList(JSONArray arr);

    public void openGame(JSONObject game);
}
