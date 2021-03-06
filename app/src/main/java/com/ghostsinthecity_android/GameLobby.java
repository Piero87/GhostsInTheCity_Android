package com.ghostsinthecity_android;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ghostsinthecity_android.models.Game;
import com.ghostsinthecity_android.models.GameList;
import com.ghostsinthecity_android.models.Ghost;
import com.ghostsinthecity_android.models.JoinGame;
import com.ghostsinthecity_android.models.MessageCode;
import com.ghostsinthecity_android.models.NewGame;
import com.ghostsinthecity_android.models.Player;
import com.ghostsinthecity_android.models.Point;
import com.ghostsinthecity_android.models.Trap;
import com.ghostsinthecity_android.models.Treasure;
import com.google.gson.Gson;
import android.location.Location;
import android.widget.TextView;

public class GameLobby extends FragmentActivity implements GameEvent {

    GameList games_list_request;
    private ArrayList<Game> gamesList;
    CustomAdapterService adapter;
    private int n_players = 2;
    private int arena_meters = 20;

    private ScheduledExecutorService worker;

    /**
     *
     * Default OnCreate method of Android, initialize main component
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_game_lobby);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        worker = Executors.newSingleThreadScheduledExecutor();
        ConnectionManager.getInstance().setChangeListener(GameLobby.this);

        final Button create_game = (Button) findViewById(R.id.create_btn);

        final TextView n_player_txt = (TextView) findViewById(R.id.n_player_text);
        n_player_txt.setText(Integer.toString(n_players));
        final Button sub_player_btn = (Button) findViewById(R.id.sub_players);
        sub_player_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (n_players > 2) {
                    n_players -= 2;
                    n_player_txt.setText(Integer.toString(n_players));
                }
            }
        });
        final Button add_player_btn = (Button) findViewById(R.id.add_players);
        add_player_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                n_players += 2;
                n_player_txt.setText(Integer.toString(n_players));
            }
        });

        final TextView arena_meters_txt = (TextView) findViewById(R.id.arena_meters);
        arena_meters_txt.setText(Integer.toString(arena_meters));
        final Button sub_meters_btn = (Button) findViewById(R.id.sub_meters);
        sub_meters_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (arena_meters > 20) {
                    arena_meters -= 5;
                    arena_meters_txt.setText(Integer.toString(arena_meters));
                }
            }
        });
        final Button add_meters_btn = (Button) findViewById(R.id.add_meters);
        add_meters_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                arena_meters += 5;
                arena_meters_txt.setText(Integer.toString(arena_meters));
            }
        });

        ListView listView = (ListView)findViewById(R.id.games_list);

        gamesList = new ArrayList<Game>();
        adapter = new CustomAdapterService(this, R.layout.row, gamesList);
        listView.setAdapter(adapter);


        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Invio Join");

                Location l = SocketLocation.getInstance().getLastLocation();

                Point p = new Point();
                p.setLatitude(l.getLatitude());
                p.setLongitude(l.getLongitude());

                JoinGame join = new JoinGame();
                join.setEvent("join_game");
                join.setGame(gamesList.get(position));
                join.setPos(p);

                ConnectionManager.getInstance().sendMessage(new Gson().toJson(join));
            }
        };

        listView.setOnItemClickListener(clickListener);

        games_list_request = new GameList();
        games_list_request.setEvent("games_list");
        games_list_request.setG_type("reality");

        create_game.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                Location l = SocketLocation.getInstance().getLastLocation();

                if (l == null)
                {
                    Builder alert = new AlertDialog.Builder(GameLobby.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Can't retrieve your position");
                    alert.setPositiveButton("OK", null);
                    alert.show();

                } else {
                    NewGame new_game = new NewGame();
                    new_game.setEvent("new_game");
                    new_game.setName(ConnectionManager.getInstance().username);
                    new_game.setPlayersNumber(Integer.parseInt(Integer.toString(n_players)));
                    new_game.setArenaSideDimension(Integer.parseInt(Integer.toString(arena_meters)));
                    new_game.setGameType("reality");

                    Point p = new Point();
                    p.setLatitude(l.getLatitude());
                    p.setLongitude(l.getLongitude());

                    new_game.setPos(p);

                    ConnectionManager.getInstance().sendMessage(new Gson().toJson(new_game));
                }
            }
        });
        requestGameList();

    }

    /**
     *
     * GameEvent listener method to open GameActivity view
     *
     * @param game Game Entity
     */
    public void openGame(Game game) {
        //Per sicurezza quando entrerò nella partita chiudo il worker per
        //evitare che continui a mandare richieste di list game al server
        ConnectionManager.getInstance().setChangeListener(null);
        worker.shutdown();
        Intent i = new Intent(GameLobby.this, GameActivity.class);
        i.putExtra("game",game);
        startActivity(i);
    }

    /**
     *
     * GameEvent listener method that notify when GameStatus change
     *
     * @param game Game Entity
     */
    @Override
    public void gameStatusChanged(Game game) {

    }

    /**
     *
     * GameEvent listener method that notify when a player position change
     *
     * @param player Player Entity
     */
    @Override
    public void updatePlayerPosition(Player player) {

    }

    /**
     *
     * GameEvent listener method that notify when a player info change
     *
     * @param player Player Entity
     */
    @Override
    public void updatePlayerInfo(Player player) {

    }

    /**
     *
     * GameEvent listener method that notify when ghosts position change
     * @param ghosts Ghost List Entity
     */
    @Override
    public void updateGhostsPositions(List<Ghost> ghosts) {

    }

    /**
     *
     * GameEvent listener method that notify when player position change and give the visible treasures around player
     *
     * @param treasures Treasures List Entity
     */
    @Override
    public void updateTreasures(List<Treasure> treasures) {

    }

    /**
     * GameEvent listener method that notify when a new trap is added
     *
     * @param trap Trap Entity
     */
    @Override
    public void addTrap(Trap trap) {

    }

    /**
     * GameEvent listener method that notify when a trap is activated
     *
     * @param trap Trap Entity
     */
    @Override
    public void activateTrap(Trap trap) {

    }

    /**
     * GameEvent listener method that notify when a trap removed
     *
     * @param trap Trap Entity
     */
    @Override
    public void removeTrap(Trap trap) {

    }

    /**
     * GameEvent listener method that notify when show a info message
     *
     * @param msg_code MessageCode Entity
     */
    @Override
    public void showMessage(MessageCode msg_code) {

    }

    /**
     * GameEvent listener method that notify when player position change and give the visible traps around player
     *
     * @param traps Trap Entity
     */
    @Override
    public void updateVisibleTraps(List<Trap> traps) {

    }

    /**
     * GameEvent listener method that notify when player position change and give the visible players around player
     *
     * @param players Trap Entity
     */
    @Override
    public void updateVisiblePlayers(List<Player> players) {

    }

    /**
     *
     * Send message to the server to request Games List available
     *
      */
    void requestGameList() {
        System.out.println("Richiedo Lista Partite...");

        ConnectionManager.getInstance().sendMessage(new Gson().toJson(games_list_request));
    }

    /**
     *
     * Schedule timer to request Game List every second
     *
     */
    void timerRequestGamesList() {
        if(!worker.isShutdown()){
            Runnable task = new Runnable() {
                public void run() {
                    requestGameList();
                }
            };
            worker.schedule(task, 1, TimeUnit.SECONDS);
        }
    }

    /**
     *
     * GameEvent listener method to open GameActivity view
     *
     * @param games_list Game List Entity
     */
    @Override
    public void refreshGameList(final Game[] games_list) {
        System.out.println("Ricevuto Lista Partite...");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gamesList.clear();
                Collections.addAll(gamesList, games_list);
                adapter.notifyDataSetChanged();

                timerRequestGamesList();
            }
        });
    }

    /**
     * GameEvent listener method that it's called when the websocket connect to the server.
     * Then open the WaitingGPSActivity.
     */
    @Override
    public void connected() {
        // TODO Auto-generated method stub

    }

    /**
     * Get the current date
     * @param time current unix time
     * @return
     */
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ITALIAN);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm", cal).toString();
        return date;
    }
}
