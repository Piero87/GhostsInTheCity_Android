package com.ghostsinthecity_android;

/**
 * Created by Piero on 02/03/16.
 */
import org.json.JSONArray;
import org.json.JSONObject;

public interface GameEvent {

    void connected ();

    void refreshGameList(JSONArray arr);

    void openGame(JSONObject game);
}
