package com.ghostsinthecity_android;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beyondar.android.world.GeoObject;
import com.ghostsinthecity_android.models.EventString;
import com.ghostsinthecity_android.models.Game;
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

import java.util.ArrayList;
import android.location.Location;
import java.util.List;
import android.os.Handler;

import com.ghostsinthecity_android.SimpleGestureFilter.SimpleGestureListener;

import android.view.MotionEvent;
import android.content.Intent;

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
        public static final int TRAP = 3;
    }

    private Game currentGame;
    private BeyondarFragmentSupport mBeyondarFragment;
    private World world;

    private TextView game_status_label;
    private ImageView coin_icon;
    private ImageView key_icon;
    private TextView coin_label;
    private TextView key_label;
    private ImageView bam_img;
    private ImageView pow_img;
    private TextView team_img;
    private TextView action_label;
    private Button results_btn;
    private Button leave_btn;

    private Handler handler;

    private String my_uid;

    private SimpleGestureFilter detector;

    private static final String TAG = "Ghost_GameActivity";

    /**
     *
     * Default OnCreate method of Android, initialize main component
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        handler = new Handler();

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

        game_status_label = (TextView) findViewById(R.id.game_status_label);
        coin_icon = (ImageView) findViewById(R.id.img_coin);
        key_icon = (ImageView) findViewById(R.id.img_key);
        coin_label = (TextView) findViewById(R.id.lbl_coin);
        key_label = (TextView) findViewById(R.id.lbl_key);
        action_label = (TextView) findViewById(R.id.action_label);
        bam_img = (ImageView) findViewById(R.id.hit2);
        pow_img = (ImageView) findViewById(R.id.hit1);
        team_img = (TextView) findViewById(R.id.player_team);
        results_btn = (Button) findViewById(R.id.results_btn);
        leave_btn = (Button) findViewById(R.id.leave_btn);

        action_label.setText("");

        results_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                ConnectionManager.getInstance().setChangeListener(null);
                SocketLocation.getInstance().setChangeListener(null);
                Intent i = new Intent(GameActivity.this, GameResultsActivity.class);
                startActivity(i);
            }
        });

        leave_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                ConnectionManager.getInstance().setChangeListener(null);
                SocketLocation.getInstance().setChangeListener(null);

                EventString leave_game_request = new EventString();
                leave_game_request.setEvent("leave_game");
                ConnectionManager.getInstance().sendMessage(new Gson().toJson(leave_game_request));

                Intent i = new Intent(GameActivity.this, GameLobby.class);
                startActivity(i);
            }
        });

        action_label.setVisibility(View.GONE);
        coin_icon.setVisibility(View.GONE);
        key_icon.setVisibility(View.GONE);
        coin_label.setVisibility(View.GONE);
        key_label.setVisibility(View.GONE);
        bam_img.setVisibility(View.GONE);
        pow_img.setVisibility(View.GONE);
        team_img.setVisibility(View.GONE);
        results_btn.setVisibility(View.GONE);

        Location l = SocketLocation.getInstance().getLastLocation();

        world = new World(this);
        world.setGeoPosition(l.getLatitude(), l.getLongitude());
        world.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

        mBeyondarFragment.setWorld(world);
        mBeyondarFragment.setMaxDistanceToRender(50);
        mBeyondarFragment.setSensorDelay(SensorManager.SENSOR_DELAY_GAME);
        mBeyondarFragment.showFPS(false);

        detector = new SimpleGestureFilter(this,this);
    }

    /**
     * Default onResume method of Android, that will be called when the view appear
     */
    @Override
    public void onResume() {
        super.onResume();

        if (ConnectionManager.getInstance().current_game != null) {
            gameStatusChanged(ConnectionManager.getInstance().current_game);
        }
    }

    /**
     * LocationEvent listener method that notify when the gps position change
     *
     * @param location Location Entity
     */
    @Override
    public void updateLocation(Location location) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            world.setGeoPosition(location.getLatitude(), location.getLongitude());
            //mBeyondarFragment.setWorld(world); //sembra non serva

            PositionUpdate pos_update = new PositionUpdate();
            pos_update.setEvent("update_player_position");
            Point p = new Point();
            p.setLatitude(location.getLatitude());
            p.setLongitude(location.getLongitude());
            pos_update.setPos(p);

            ConnectionManager.getInstance().sendMessage(new Gson().toJson(pos_update));
        }
    }

    /**
     * Called when a swipe is detected
     *
     * @param direction this is the direction of the swipe
     */
    @Override
    public void onSwipe(int direction) {

        if (currentGame.getStatus() == GameStatus.STARTED) {
            switch (direction) {
                case SimpleGestureFilter.SWIPE_DOWN:
                    Log.d(TAG,"SWIPE DOWN");
                    action_label.setText("SET TRAP!");
                    handler.postDelayed(emptyActionLabel, 1000);
                    EventString set_trap = new EventString();
                    set_trap.setEvent("set_trap");
                    ConnectionManager.getInstance().sendMessage(new Gson().toJson(set_trap));
                    break;
                case SimpleGestureFilter.SWIPE_UP:
                    Log.d(TAG,"SWIPE UP");
                    action_label.setText("OPEN TREASURE!");
                    handler.postDelayed(emptyActionLabel, 1000);
                    EventString open_treasure = new EventString();
                    open_treasure.setEvent("open_treasure");
                    ConnectionManager.getInstance().sendMessage(new Gson().toJson(open_treasure));
                    break;
            }
        }
    }

    /**
     * Called when a double tap on the screen is detected
     */
    @Override
    public void onDoubleTap() {

        if (currentGame.getStatus() == GameStatus.STARTED) {
            Log.d(TAG, "DOUBLE TAP");
            action_label.setText("HIT PLAYER!");
            handler.postDelayed(emptyActionLabel, 1000);
            EventString hit_player = new EventString();
            hit_player.setEvent("hit_player");
            ConnectionManager.getInstance().sendMessage(new Gson().toJson(hit_player));
        }
    }

    /**
     * Default method of the touch event
     * @param me
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);

        return super.dispatchTouchEvent(me);
    }

    /**
     * Listener method called when a BeyondObject is touched
     * @param beyondarObjects
     */
    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        // The first element in the array belongs to the closest BeyondarObject
        Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
    }


    /**
     * Update HUD info player
     * @param player
     */
    public void updateHUD(final Player player) {

        runOnUiThread(new Runnable() {
            public void run() {
                // UI code goes here
                coin_label.setText(Integer.toString(player.getGold()));
                key_label.setText(Integer.toString(player.getKeys().size()));
                team_img.setBackgroundResource(player.getTeam() == Team.RED ? R.drawable.team_red : R.drawable.team_blue);
            }
        });

    }

    /**
     * Called when the game is in a started status
     */
    public void startGame() {

        Log.d(TAG,"GAME STARTED");

        game_status_label.setVisibility(View.GONE);
        coin_icon.setVisibility(View.VISIBLE);
        key_icon.setVisibility(View.VISIBLE);

        for (Player player : currentGame.getPlayers()) {

            if (player.getUid().equals(my_uid))
            {
                updateHUD(player);
                break;
            }
        }

        action_label.setVisibility(View.VISIBLE);
        coin_label.setVisibility(View.VISIBLE);
        key_label.setVisibility(View.VISIBLE);
        team_img.setVisibility(View.VISIBLE);

        world.clearWorld();

        PositionUpdate pos_update = new PositionUpdate();
        pos_update.setEvent("update_player_position");
        Point p = new Point();

        Location l = SocketLocation.getInstance().getLastLocation();

        p.setLatitude(l.getLatitude());
        p.setLongitude(l.getLongitude());
        pos_update.setPos(p);

        ConnectionManager.getInstance().sendMessage(new Gson().toJson(pos_update));
    }

    /**
     * Called when the game is in a waiting status
     */
    public void waitingGame() {

        Log.d(TAG, "GAME WAITING");

        game_status_label.setText("Waiting " + (currentGame.getN_players() - currentGame.getPlayers().size()) + " more players...");
        game_status_label.setVisibility(View.VISIBLE);

        world.clearWorld();
    }

    /**
     * Called when the game is in a paused status
     */
    public void pausedGame() {

        Log.d(TAG, "GAME PAUSED");

        game_status_label.setText("Game Paused");
        game_status_label.setVisibility(View.VISIBLE);

        world.clearWorld();

    }

    /**
     * Called when the game is finished
     */
    public void gameFinished() {

        Log.d(TAG, "GAME FINISHED");

        results_btn.setVisibility(View.VISIBLE);
        game_status_label.setVisibility(View.VISIBLE);
        game_status_label.setText("Game Finished");
        world.clearWorld();
    }

    /**
     *
     * GameEvent listener method that notify when GameStatus change
     *
     * @param game Game Entity
     */
    @Override
    public void gameStatusChanged(final Game game) {

        runOnUiThread(new Runnable() {
            public void run() {
                // UI code goes here
                currentGame = game;

                switch (game.getStatus()) {
                    case GameStatus.STARTED:
                        startGame();
                        break;
                    case GameStatus.WAITING:
                        waitingGame();
                        break;
                    case GameStatus.FINISHED:
                        gameFinished();
                        break;
                    case GameStatus.PAUSED:
                        pausedGame();
                        break;
                }
            }
        });
    }

    /**
     *
     * GameEvent listener method that notify when a player position change
     *
     * @param player Player Entity
     */
    @Override
    public void updatePlayerPosition(Player player) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG,"UPDATE PLAYER POSITION");

            boolean player_find = false;

            if (world.getBeyondarObjectList(WorldObjectType.PLAYER) != null)
            {
                for (BeyondarObject playerObject : world.getBeyondarObjectList(WorldObjectType.PLAYER)) {

                    if (playerObject.getName().equals(player.getUid())) {

                        player_find = true;

                        world.remove(playerObject);

                        initializePlayer(player);

                        for (Player current_player : currentGame.getPlayers()) {

                            if (current_player.getUid().equals(player.getUid())) {
                                current_player.setPos(player.getPos());
                                break;
                            }
                        }

                        break;
                    }
                }
            }


            if (!player_find) initializePlayer(player);
        }
    }

    /**
     *
     * GameEvent listener method that notify when a player info change
     *
     * @param player Player Entity
     */
    @Override
    public void updatePlayerInfo(Player player) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG, "UPDATE PLAYER INFO");

            updateHUD(player);
        }
    }

    /**
     * GameEvent listener method that notify when player position change and give the visible players around player
     *
     * @param players Trap Entity
     */
    @Override
    public void updateVisiblePlayers(List<Player> players) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG, "UPDATE VISIBLE PLAYERS");

            if (world.getBeyondarObjectList(WorldObjectType.PLAYER) != null)
            {
                for (BeyondarObject playerObject : world.getBeyondarObjectList(WorldObjectType.PLAYER)) {
                    world.remove(playerObject);
                }
            }


            initializePlayers(players);
        }
    }

    /**
     *
     * GameEvent listener method that notify when ghosts position change
     * @param ghosts Ghost List Entity
     */
    @Override
    public void updateGhostsPositions(List<Ghost> ghosts) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG, "UPDATE GHOSTS POSITIONS");

            if (world.getBeyondarObjectList(WorldObjectType.GHOST) != null)
            {
                for (BeyondarObject ghostObject : world.getBeyondarObjectList(WorldObjectType.GHOST)) {
                    world.remove(ghostObject);
                }
            }


            initializeGhosts(ghosts);
        }
    }

    /**
     *
     * GameEvent listener method that notify when player position change and give the visible treasures around player
     *
     * @param treasures Treasures List Entity
     */
    @Override
    public void updateTreasures(List<Treasure> treasures) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG, "UPDATE TREASURES");

            if (world.getBeyondarObjectList(WorldObjectType.TREASURE) != null)
            {
                for (BeyondarObject treasureObject : world.getBeyondarObjectList(WorldObjectType.TREASURE)) {
                    world.remove(treasureObject);
                }
            }

            initializeTreasures(treasures);
        }
    }

    /**
     * GameEvent listener method that notify when a new trap is added
     *
     * @param trap Trap Entity
     */
    @Override
    public void addTrap(Trap trap) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG, "ADD TRAP");

            initializeTrap(trap);
            currentGame.getTraps().add(trap);
        }
    }

    /**
     * GameEvent listener method that notify when a trap is activated
     *
     * @param trap Trap Entity
     */
    @Override
    public void activateTrap(Trap trap) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG, "ACTIVATE TRAP");

            if (world.getBeyondarObjectList(WorldObjectType.TRAP) != null)
            {
                for (BeyondarObject trapObject : world.getBeyondarObjectList(WorldObjectType.TRAP)) {

                    if (trapObject.getName().equals(trap.getUid())) {
                        world.remove(trapObject);
                        initializeTrap(trap);

                        for (Trap current_trap : currentGame.getTraps()) {

                            if (current_trap.getUid().equals(trap.getUid())) {
                                current_trap.setStatus(trap.getStatus());
                                break;
                            }
                        }

                        break;
                    }
                }
            }
        }
    }

    /**
     * GameEvent listener method that notify when a trap removed
     *
     * @param trap Trap Entity
     */
    @Override
    public void removeTrap(Trap trap) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG, "REMOVE TRAP");

            if (world.getBeyondarObjectList(WorldObjectType.TRAP) != null)
            {
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
        }
    }

    /**
     * GameEvent listener method that notify when player position change and give the visible traps around player
     *
     * @param traps Trap Entity
     */
    @Override
    public void updateVisibleTraps(List<Trap> traps) {

        if (currentGame.getStatus() == GameStatus.STARTED) {

            Log.d(TAG, "UPDATE VISIBLE TRAP");

            if (world.getBeyondarObjectList(WorldObjectType.TRAP) != null)
            {
                for (BeyondarObject trapObject : world.getBeyondarObjectList(WorldObjectType.TRAP)) {
                    world.remove(trapObject);
                }
            }
        }

        initializeTraps(traps);
    }

    /**
     * GameEvent listener method that notify when show a info message
     *
     * @param msg_code MessageCode Entity
     */
    @Override
    public void showMessage(MessageCode msg_code) {

        if (currentGame.getStatus() == GameStatus.STARTED) {
            switch (msg_code.getCode()) {
                case -1:
                    action_label.setText("You cannot set a trap!");
                    handler.postDelayed(emptyActionLabel, 1000);
                    break;
                case -2:
                    action_label.setText("Come inside, quick, you'll catch a cold out there!");
                    break;
                case -3:
                    action_label.setText("This treasure is locked and you don't have the right key.");
                    handler.postDelayed(emptyActionLabel, 1000);
                    break;
                case -4:
                    action_label.setText("Some moron leaved the mission and has not come back in time...");
                    handler.postDelayed(emptyActionLabel, 1000);
                    break;
                case -5:
                    action_label.setText("oh oh, there's nothing here.");
                    handler.postDelayed(emptyActionLabel, 1000);
                    break;
                case 1:
                    action_label.setText("Ghost Attack! You lost " + msg_code.getOption() + "$!");
                    handler.postDelayed(emptyActionLabel, 1000);
                    bam_img.setVisibility(View.VISIBLE);
                    handler.postDelayed(hideBAM, 1000);

                    break;
                case 2:
                    action_label.setText("Ouch!");
                    handler.postDelayed(emptyActionLabel, 1000);
                    pow_img.setVisibility(View.VISIBLE);
                    handler.postDelayed(hidePOW, 1000);
                    break;
                case 3:
                    action_label.setText("You have found a key! Yay!");
                    handler.postDelayed(emptyActionLabel, 1000);
                    break;
                case 4:
                    action_label.setText("You have found " + msg_code.getOption() + "$! You are filthy rich now!");
                    handler.postDelayed(emptyActionLabel, 1000);
                    break;
                case 5:
                    action_label.setText("Jackpot! " + msg_code.getOption() + "$ and a key!");
                    handler.postDelayed(emptyActionLabel, 1000);
                    break;
                case 7:
                    action_label.setText("Back in game area");
                    handler.postDelayed(emptyActionLabel, 1000);
                    break;
            }
        }
    }

    /**
     * Iterate the ghosts list and create GeoObject and add to the world
     * @param ghosts List of Ghost Entity
     */
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
            world.addBeyondarObject(ghost, WorldObjectType.GHOST);
        }
    }

    /**
     * Iterate the treasures list and create GeoObject and add to the world
     * @param treasures
     */
    public void initializeTreasures(List<Treasure> treasures) {

        currentGame.setTreasures(treasures);

        for (int i = 0; i < treasures.size(); i++) {

            GeoObject treasure = new GeoObject(1l);
            treasure.setGeoPosition(treasures.get(i).getPos().getLatitude(),treasures.get(i).getPos().getLongitude());
            treasure.setImageResource(treasures.get(i).getStatus() == 0 ? R.drawable.treasure_close : R.drawable.treasure_open);
            treasure.setName(treasures.get(i).getUid());
            world.addBeyondarObject(treasure, WorldObjectType.TREASURE);
        }
    }

    /**
     * Iterate the traps list and call initialize method to create the GeoObject and add to the world
     * @param traps
     */
    public void initializeTraps(List<Trap> traps) {

        currentGame.setTraps(traps);

        for (int i = 0; i < currentGame.getTraps().size(); i++) {

            initializeTrap(currentGame.getTraps().get(i));
        }
    }

    /**
     * Create the GeoObject trap and add to the world
     * @param trap
     */
    public void initializeTrap(Trap trap) {

        GeoObject trap_obj = new GeoObject(1l);
        trap_obj.setGeoPosition(trap.getPos().getLatitude(), trap.getPos().getLongitude());
        trap_obj.setImageResource(trap.getStatus() == TrapStatus.UNACTIVE ? R.drawable.trap_disabled : R.drawable.trap_enabled);
        trap_obj.setName(trap.getUid());
        world.addBeyondarObject(trap_obj, WorldObjectType.TRAP);
    }

    /**
     * Create the GeoObject player and add to the world
     * @param player
     */
    public void initializePlayer(Player player) {

        GeoObject player_obj = new GeoObject(1l);
        player_obj.setGeoPosition(player.getPos().getLatitude(),player.getPos().getLongitude());
        player_obj.setImageResource(player.getTeam() == Team.RED ? R.drawable.team_red : R.drawable.team_blue);
        player_obj.setName(player.getUid());
        world.addBeyondarObject(player_obj, WorldObjectType.PLAYER);
    }

    /**
     * Iterate the players list and call initialize method to create the GeoObject and add to the world
     * @param players
     */
    public void initializePlayers(List<Player> players) {

        currentGame.setPlayers(players);

        for (int i = 0; i < currentGame.getPlayers().size(); i++) {

            if (!currentGame.getPlayers().get(i).getUid().equals(my_uid)) {
                initializePlayer(currentGame.getPlayers().get(i));
            }
        }
    }

    /**
     * Runnable method to empty the label
     */
    private Runnable emptyActionLabel = new Runnable() {
        @Override
        public void run() {
            action_label.setText("");
        }
    };

    /**
     * Runnable method to hide the image
     */
    private Runnable hideBAM = new Runnable() {
        @Override
        public void run() {
            bam_img.setVisibility(View.GONE);
        }
    };

    /**
     * Runnable method to hide the image
     */
    private Runnable hidePOW = new Runnable() {
        @Override
        public void run() {
            pow_img.setVisibility(View.GONE);
        }
    };





    /**
     * GameEvent listener method that it's called when the websocket connect to the server.
     * Then open the WaitingGPSActivity.
     */
    @Override
    public void connected() {

    }

    /**
     *
     * GameEvent listener method to open GameActivity view
     *
     * @param games_list Game List Entity
     */
    @Override
    public void refreshGameList(Game[] games_list) {

    }

    /**
     *
     * GameEvent listener method to open GameActivity view
     *
     * @param game Game Entity
     */
    @Override
    public void openGame(Game game) {

    }

}
