package com.ghostsinthecity_android;

/**
 * Created by Piero on 02/03/16.
 */
import com.ghostsinthecity_android.models.Game;

public interface GameEvent {

    void connected ();

    void refreshGameList(Game [] games_list);

    void openGame(Game game);
}
