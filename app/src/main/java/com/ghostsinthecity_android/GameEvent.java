package com.ghostsinthecity_android;

import com.ghostsinthecity_android.models.Game;
import com.ghostsinthecity_android.models.MessageCode;
import com.ghostsinthecity_android.models.Player;
import com.ghostsinthecity_android.models.Ghost;
import com.ghostsinthecity_android.models.Treasure;
import com.ghostsinthecity_android.models.Trap;

import java.util.List;

public interface GameEvent {

    /**
     * GameEvent listener method that it's called when the websocket connect to the server.
     * Then open the WaitingGPSActivity.
     */
    void connected ();

    /**
     *
     * GameEvent listener method to open GameActivity view
     *
     * @param games_list Game List Entity
     */
    void refreshGameList(Game [] games_list);

    /**
     *
     * GameEvent listener method to open GameActivity view
     *
     * @param game Game Entity
     */
    void openGame(Game game);

    /**
     *
     * GameEvent listener method that notify when GameStatus change
     *
     * @param game Game Entity
     */
    void gameStatusChanged(Game game);

    /**
     *
     * GameEvent listener method that notify when a player position change
     *
     * @param player Player Entity
     */
    void updatePlayerPosition(Player player);

    /**
     *
     * GameEvent listener method that notify when a player info change
     *
     * @param player Player Entity
     */
    void updatePlayerInfo(Player player);

    /**
     *
     * GameEvent listener method that notify when ghosts position change
     * @param ghosts Ghost List Entity
     */
    void updateGhostsPositions(List<Ghost> ghosts);

    /**
     *
     * GameEvent listener method that notify when player position change and give the visible treasures around player
     *
     * @param treasures Treasures List Entity
     */
    void updateTreasures(List<Treasure> treasures);

    /**
     * GameEvent listener method that notify when a new trap is added
     *
     * @param trap Trap Entity
     */
    void addTrap(Trap trap);

    /**
     * GameEvent listener method that notify when a trap is activated
     *
     * @param trap Trap Entity
     */
    void activateTrap(Trap trap);

    /**
     * GameEvent listener method that notify when a trap removed
     *
     * @param trap Trap Entity
     */
    void removeTrap(Trap trap);

    /**
     * GameEvent listener method that notify when show a info message
     *
     * @param msg_code MessageCode Entity
     */
    void showMessage(MessageCode msg_code);

    /**
     * GameEvent listener method that notify when player position change and give the visible traps around player
     *
     * @param traps Trap Entity
     */
    void updateVisibleTraps(List<Trap> traps);

    /**
     * GameEvent listener method that notify when player position change and give the visible players around player
     *
     * @param players Trap Entity
     */
    void updateVisiblePlayers(List<Player> players);

}
