package com.ghostsinthecity_android;

import com.ghostsinthecity_android.models.Game;
import com.ghostsinthecity_android.models.MessageCode;
import com.ghostsinthecity_android.models.Player;
import com.ghostsinthecity_android.models.Ghost;
import com.ghostsinthecity_android.models.Treasure;
import com.ghostsinthecity_android.models.Trap;

import java.util.List;

public interface GameEvent {

    void connected ();

    void refreshGameList(Game [] games_list);

    void openGame(Game game);

    void gameStatusChanged(Game game);

    void updatePlayerPosition(Player player);

    void updatePlayerInfo(Player player);

    void updateGhostsPositions(List<Ghost> ghosts);

    void updateTreasures(List<Treasure> treasures);

    void addTrap(Trap trap);

    void activateTrap(Trap trap);

    void removeTrap(Trap trap);

    void showMessage(MessageCode msg_code);
}
