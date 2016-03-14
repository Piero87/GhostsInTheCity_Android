package com.ghostsinthecity_android;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import com.ghostsinthecity_android.models.JoinGame;
import com.ghostsinthecity_android.models.NewGame;
import com.ghostsinthecity_android.models.Point;
import com.google.gson.Gson;
import android.location.Location;

public class GameLobby extends FragmentActivity implements GameEvent {

    GameList games_list_request;
    private ArrayList<Game> gamesList;
    CustomAdapterService adapter;

    private ScheduledExecutorService worker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_game_lobby);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        worker = Executors.newSingleThreadScheduledExecutor();
        ConnectionManager.getInstance().setChangeListener(GameLobby.this);

        final Button button = (Button) findViewById(R.id.create_btn);
        final EditText text_field = (EditText) findViewById(R.id.n_player_text);
        final EditText text_field2 = (EditText) findViewById(R.id.editText);

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

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (!text_field.getText().toString().isEmpty()) {

                    ProgressDialog mDialog = new ProgressDialog(GameLobby.this);
                    mDialog.setMessage("Getting Position...");
                    mDialog.setCancelable(false);
                    mDialog.show();

                    Location l = SocketLocation.getInstance().getLastLocation();

                    if (l == null)
                    {
                        mDialog.hide();
                        Builder alert = new AlertDialog.Builder(GameLobby.this);
                        alert.setTitle("Alert");
                        alert.setMessage("Can't retrieve your position");
                        alert.setPositiveButton("OK",null);
                        alert.show();

                    } else {
                        mDialog.hide();
                        NewGame new_game = new NewGame();
                        new_game.setEvent("new_game");
                        new_game.setName(ConnectionManager.getInstance().username);
                        new_game.setN_players(Integer.parseInt(text_field.getText().toString()));
                        new_game.setGame_area_edge(Integer.parseInt(text_field2.getText().toString()));
                        new_game.setGame_type("reality");

                        Point p = new Point();
                        p.setLatitude(l.getLatitude());
                        p.setLongitude(l.getLongitude());

                        new_game.setPos(p);

                        ConnectionManager.getInstance().sendMessage(new Gson().toJson(new_game));
                    }

                } else {
                    Builder alert = new AlertDialog.Builder(GameLobby.this);
                    alert.setTitle("Alert");
                    alert.setMessage("You need to insert the number of players");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
            }
        });
        requestGameList();

    }

    public void openGame(Game game) {
        //Per sicurezza quando entrerò nella partita chiudo il worker per
        //evitare che continui a mandare richieste di list game al server
        ConnectionManager.getInstance().setChangeListener(null);
        worker.shutdown();
        Intent i = new Intent(GameLobby.this, GameActivity.class);
        i.putExtra("game",game);
        startActivity(i);
    }

    void requestGameList() {
        System.out.println("Richiedo Lista Partite...");

        ConnectionManager.getInstance().sendMessage(new Gson().toJson(games_list_request));
    }

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

    @Override
    public void connected() {
        // TODO Auto-generated method stub

    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ITALIAN);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm", cal).toString();
        return date;
    }
}
