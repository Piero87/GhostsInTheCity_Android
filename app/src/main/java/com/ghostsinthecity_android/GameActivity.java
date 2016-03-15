package com.ghostsinthecity_android;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.beyondar.android.world.BeyondarObjectList;
import com.beyondar.android.world.GeoObject;
import com.ghostsinthecity_android.models.EventString;
import com.ghostsinthecity_android.models.Game;
import android.content.Intent;
import android.view.Window;
import android.view.View;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.World;
import com.ghostsinthecity_android.models.Ghost;
import com.ghostsinthecity_android.models.MessageCode;
import com.ghostsinthecity_android.models.Player;
import com.ghostsinthecity_android.models.Point;
import com.ghostsinthecity_android.models.PositionUpdate;
import com.ghostsinthecity_android.models.Trap;
import com.ghostsinthecity_android.models.Treasure;
import com.google.gson.Gson;
import android.view.MotionEvent;

import java.util.ArrayList;
import android.location.Location;
import java.util.List;

import com.ghostsinthecity_android.SimpleGestureFilter.SimpleGestureListener;

public class GameActivity extends FragmentActivity implements OnClickBeyondarObjectListener,GameEvent,LocationEvent,SimpleGestureListener {

    static class GameStatus {
        public static final int WAITING = 0;
        public static final int STARTED = 1;
        public static final int PAUSED = 2;
        public static final int FINISHED = 3;
    }

    static class Team {
        public static final int NO_ENOUGH_PLAYER = -2;
        public static final int UNKNOWN = -1;
        public static final int RED = 0;
        public static final int BLUE = 1;
    }

    static class TrapStatus {
        public static final int UNACTIVE = 0;
        public static final int ACTIVE = 1;
    }

    static class GhostLevel {
        public static final int LEVEL_1 = 1;
        public static final int LEVEL_2 = 2;
        public static final int LEVEL_3 = 3;
    }

    static class GhostMood {
        public static final int CALM = 0;
        public static final int ANGRY = 1;
        public static final int TRAPPED = 2;
    }

    static class WorldObjectType {
        public static final int PLAYER = 0;
        public static final int GHOST = 1;
        public static final int TREASURE = 2;
        public static final int TRAP = 2;
    }

    private Game currentGame;
    private BeyondarFragmentSupport mBeyondarFragment;
    private World world;

    private TextView waiting_label;
    private String my_uid;

    private SimpleGestureFilter detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_game);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        ConnectionManager.getInstance().setChangeListener(GameActivity.this);
        SocketLocation.getInstance().setChangeListener(GameActivity.this);
        my_uid = ConnectionManager.getInstance().uid;

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);


        Intent mIntent  = getIntent();
        currentGame  = (Game)mIntent.getParcelableExtra("game");

            waiting_label = (TextView) findViewById(R.id.waiting_players_label);
        waiting_label.setText("Waiting " + (currentGame.getN_players() - currentGame.getPlayers().size()) + " more players...");

            Location l = SocketLocation.getInstance().getLastLocation();

        world = new World(this);
        world.setGeoPosition(l.getLatitude(), l.getLongitude());
        world.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

        mBeyondarFragment.setWorld(world);
        mBeyondarFragment.setMaxDistanceToRender(50);
        mBeyondarFragment.setSensorDelay(SensorManager.SENSOR_DELAY_GAME);
        mBeyondarFragment.showFPS(true);

        detector = new SimpleGestureFilter(this,this);
    }


    @Override
    public void updateLocation(Location location) {

        world.setGeoPosition(location.getLatitude(), location.getLongitude());
        //mBeyondarFragment.setWorld(world); //sembra non serva


        PositionUpdate pos_update = new PositionUpdate();
        pos_update.setEvent("");
        Point p = new Point();
        p.setLatitude(location.getLatitude());
        p.setLongitude(location.getLongitude());
        pos_update.setPos(p);

        ConnectionManager.getInstance().sendMessage(new Gson().toJson(pos_update));

    }

    @Override
    public void onSwipe(int direction) {

        switch (direction) {
            case SimpleGestureFilter.SWIPE_DOWN:
                EventString set_trap = new EventString();
                set_trap.setEvent("set_trap");
                ConnectionManager.getInstance().sendMessage(new Gson().toJson(set_trap));
                break;
            case SimpleGestureFilter.SWIPE_UP:
                EventString open_treasure = new EventString();
                open_treasure.setEvent("open_treasure");
                ConnectionManager.getInstance().sendMessage(new Gson().toJson(open_treasure));
                break;
        }
    }

    @Override
    public void onDoubleTap() {
        EventString hit_player = new EventString();
        hit_player.setEvent("hit_player");
        ConnectionManager.getInstance().sendMessage(new Gson().toJson(hit_player));
    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        // The first element in the array belongs to the closest BeyondarObject
        Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
    }



    @Override
    public void gameStatusChanged(Game game) {

        currentGame = game;
        waiting_label = (TextView) findViewById(R.id.waiting_players_label);

        switch (game.getStatus()) {
            case GameStatus.STARTED:
                initializeWorld();
                waiting_label.setVisibility(View.GONE);
                break;
            case GameStatus.WAITING:
                waiting_label.setText("Game Paused");
                waiting_label.setVisibility(View.VISIBLE);
                break;
                //### Bloccare thread update fantasmi, oppure lasciarlo così come e semplicemente nascondere la vista
            case GameStatus.FINISHED:
                waiting_label.setVisibility(View.VISIBLE);
                waiting_label.setText("Game Finished");
                break;
                //### Bloccare thread chiudere connessioni
        }
    }

    @Override
    public void updatePlayerPosition(Player player) {

        for (BeyondarObject playerObject : world.getBeyondarObjectList(WorldObjectType.PLAYER)) {

            if (playerObject.getName().equals(player.getUid())) {

                world.remove(playerObject);

                initializePlayer(player);

                for (Player current_player : currentGame.getPlayers()) {

                    if (current_player.getUid().equals(player.getUid()))
                    {
                        current_player.setPos(player.getPos());
                        break;
                    }
                }

                break;
            }
        }
    }

    @Override
    public void updatePlayerInfo(Player player) {

        for (Player current_player : currentGame.getPlayers()) {

            if (current_player.getUid().equals(player.getUid()))
            {
                current_player.setPos(player.getPos());
                //### aggiornare valori HUD
                break;
            }
        }
    }

    @Override
    public void updateGhostsPositions(List<Ghost> ghosts) {

        for (BeyondarObject ghostObject : world.getBeyondarObjectList(WorldObjectType.GHOST)) {
            world.remove(ghostObject);
        }

        initializeGhosts(ghosts);

    }

    @Override
    public void updateTreasures(List<Treasure> treasures) {

        for (BeyondarObject ghostObject : world.getBeyondarObjectList(WorldObjectType.TREASURE)) {
            world.remove(ghostObject);
        }

        initializeTreasures(treasures);
    }

    @Override
    public void addTrap(Trap trap) {

        initializeTrap(trap,TrapStatus.UNACTIVE);
        currentGame.getTraps().add(trap);
    }

    @Override
    public void activateTrap(Trap trap) {

        for (BeyondarObject trapObject : world.getBeyondarObjectList(WorldObjectType.TRAP)) {

            if (trapObject.getName().equals(trap.getUid())) {
                world.remove(trapObject);
                initializeTrap(trap, TrapStatus.ACTIVE);

                for (Trap current_trap : currentGame.getTraps()) {

                    if (current_trap.getUid().equals(trap.getUid()))
                    {
                        current_trap.setStatus(TrapStatus.ACTIVE);
                        break;
                    }
                }

                break;
            }
        }
    }

    @Override
    public void removeTrap(Trap trap) {

        for (BeyondarObject trapObject : world.getBeyondarObjectList(WorldObjectType.TRAP)) {

            if (trapObject.getName().equals(trap.getUid())) {
                world.remove(trapObject);

                for (Trap current_trap : currentGame.getTraps()) {

                    if (current_trap.getUid().equals(trap.getUid()))
                    {
                        currentGame.getTraps().remove(current_trap);
                        break;
                    }
                }

                break;
            }
        }
    }

    @Override
    public void showMessage(MessageCode msg_code) {

        switch (msg_code.getCode()) {
            case -1:
                Toast.makeText(this, "You cannot set a trap!", Toast.LENGTH_LONG).show();
                break;
            case -2:
                Toast.makeText(this, "Come inside, quick, you'll catch a cold out there!", Toast.LENGTH_LONG).show();
                break;
            case -3:
                Toast.makeText(this, "This treasure is locked and you don't have the right key.", Toast.LENGTH_LONG).show();
                break;
            case -4:
                Toast.makeText(this, "Some moron leaved the mission and has not come back in time...", Toast.LENGTH_LONG).show();
                break;
            case -5:
                Toast.makeText(this, "oh oh, there's nothing here.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(this, "Ghost Attack! You lost " + msg_code.getOption() + "$!", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, "Ouch!", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(this, "You have found a key! Yay!", Toast.LENGTH_LONG).show();
                break;
            case 4:
                Toast.makeText(this, "You have found " + msg_code.getOption() + "$! You are filthy rich now!", Toast.LENGTH_LONG).show();
                break;
            case 5:
                Toast.makeText(this, "Jackpot! " + msg_code.getOption() + "$ and a key!", Toast.LENGTH_LONG).show();
                break;
            case 7:
                Toast.makeText(this, "Your team won this game! Congratulations!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void initializeGhosts(List<Ghost> ghosts) {

        currentGame.setGhosts(ghosts);

        for (int i = 0; i < ghosts.size(); i++) {

            GeoObject ghost = new GeoObject(1l);
            ghost.setGeoPosition(ghosts.get(i).getPos().getLatitude(), ghosts.get(i).getPos().getLongitude());

            switch (ghosts.get(i).getLevel()) {
                case GhostLevel.LEVEL_1:
                    switch (ghosts.get(i).getMood()) {
                        case GhostMood.CALM:
                            ghost.setImageResource(R.drawable.ghost_level1_calm);
                            break;
                        case GhostMood.ANGRY:
                            ghost.setImageResource(R.drawable.ghost_level1_angry);
                            break;
                        case GhostMood.TRAPPED:
                            ghost.setImageResource(R.drawable.ghost_level1_scared);
                            break;
                    }
                    break;
                case GhostLevel.LEVEL_2:
                    switch (ghosts.get(i).getMood()) {
                        case GhostMood.CALM:
                            ghost.setImageResource(R.drawable.ghost_level2_calm);
                            break;
                        case GhostMood.ANGRY:
                            ghost.setImageResource(R.drawable.ghost_level2_angry);
                            break;
                        case GhostMood.TRAPPED:
                            ghost.setImageResource(R.drawable.ghost_level2_scared);
                            break;
                    }
                    break;
                case GhostLevel.LEVEL_3:
                    switch (ghosts.get(i).getMood()) {
                        case GhostMood.CALM:
                            ghost.setImageResource(R.drawable.ghost_level3_calm);
                            break;
                        case GhostMood.ANGRY:
                            ghost.setImageResource(R.drawable.ghost_level3_angry);
                            break;
                        case GhostMood.TRAPPED:
                            ghost.setImageResource(R.drawable.ghost_level3_scared);
                            break;
                    }
                    break;
            }

            ghost.setName(ghosts.get(i).getUid());
            world.addBeyondarObject(ghost,WorldObjectType.GHOST);
        }
    }

    public void initializeTreasures(List<Treasure> treasures) {

        currentGame.setTreasures(treasures);

        for (int i = 0; i < treasures.size(); i++) {

            GeoObject treasure = new GeoObject(1l);
            treasure.setGeoPosition(treasures.get(i).getPos().getLatitude(),treasures.get(i).getPos().getLongitude());
            treasure.setImageResource(treasures.get(i).getStatus() == 0 ? R.drawable.treasure_close : R.drawable.treasure_open);
            treasure.setName(treasures.get(i).getUid());
            world.addBeyondarObject(treasure,WorldObjectType.TREASURE);
        }
    }

    public void initializeTrap(Trap trap, int status) {

        GeoObject trap_obj = new GeoObject(1l);
        trap_obj.setGeoPosition(trap.getPos().getLatitude(), trap.getPos().getLongitude());
        trap_obj.setImageResource(status == TrapStatus.UNACTIVE ? R.drawable.trap_disabled : R.drawable.trap_enabled);
        trap_obj.setName(trap.getUid());
        world.addBeyondarObject(trap_obj, WorldObjectType.TRAP);
    }

    public void initializePlayer(Player player) {

        GeoObject player_obj = new GeoObject(1l);
        player_obj.setGeoPosition(player.getPos().getLatitude(),player.getPos().getLongitude());
        player_obj.setImageResource(player.getTeam() == Team.RED ? R.drawable.team_red : R.drawable.team_blue);
        player_obj.setName(player.getUid());
        world.addBeyondarObject(player_obj,WorldObjectType.PLAYER);
    }

    public void initializeWorld() {

        world.clearWorld();

        initializeGhosts(currentGame.getGhosts());

        initializeTreasures(currentGame.getTreasures());

        for (int i = 0; i < currentGame.getTraps().size(); i++) {

            initializeTrap(currentGame.getTraps().get(i), TrapStatus.UNACTIVE);
        }

        for (int i = 0; i < currentGame.getPlayers().size(); i++) {

            if (!currentGame.getPlayers().get(i).getUid().equals(my_uid)) {
               initializePlayer(currentGame.getPlayers().get(i));
            }
        }
    }







    /*
    NON USATI IN QUESTA VISTA MA NON C'E' MODO DI METTERE OPZIONALI QUINDI VANNO MESSI
    SE NO DA ERRORE
     */
    @Override
    public void connected() {

    }

    @Override
    public void refreshGameList(Game[] games_list) {

    }

    @Override
    public void openGame(Game game) {

    }

}
