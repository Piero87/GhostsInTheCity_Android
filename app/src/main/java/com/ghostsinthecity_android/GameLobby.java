package com.ghostsinthecity_android;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.ghostsinthecity_android.models.Game;
import com.ghostsinthecity_android.models.JoinGame;
import com.ghostsinthecity_android.models.NewGame;
import com.ghostsinthecity_android.models.Point;
import com.ghostsinthecity_android.models.EventString;
import com.google.gson.Gson;
import android.location.Location;

public class GameLobby extends AppCompatActivity implements GameEvent {

    TableLayout table_layout;
    EventString games_list_request;

    private ScheduledExecutorService worker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);
        table_layout = (TableLayout) findViewById(R.id.games_list);
        worker = Executors.newSingleThreadScheduledExecutor();
        ConnectionManager.getInstance().setChangeListener(GameLobby.this);

        final Button button = (Button) findViewById(R.id.create_btn);
        final EditText text_field = (EditText) findViewById(R.id.n_player_text);

        games_list_request = new EventString();
        games_list_request.setEvent("games_list");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (!text_field.getText().toString().isEmpty()) {

                    ProgressDialog mDialog = new ProgressDialog(GameLobby.this);
                    mDialog.setMessage("Getting Position...");
                    mDialog.setCancelable(false);
                    mDialog.show();

                    Location l = LocationManager.getInstance().getLastLocation();

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
        worker.shutdown();
        Intent i = new Intent(GameLobby.this, GameActivity.class);
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
    public void refreshGameList(Game[] games_list) {
        System.out.println("Ricevuto Lista Partite...");

        final Game[] t = games_list;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                table_layout.removeAllViews();

                if (t.length != 0) {
                    for (int i = 0; i < t.length; i++) {

                        final Game game = t[i];

                        if (game.getName().contains("__")) {

                            TableRow row = new TableRow(GameLobby.this);
                            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
                            TextView c1 = new TextView(GameLobby.this);
                            c1.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

                            String[] parts;

                            parts = game.getName().split("__");
                            String part1 = parts[0];
                            String part2 = parts[1];
                            String tmp_date = getDate(Long.parseLong(part2));

                            c1.setText(String.format("Mission created by %s at %s", part1, tmp_date));
                            c1.setTextColor(Color.WHITE);
                            c1.setTextSize(18);
                            row.addView(c1);
                            Button btn = new Button(GameLobby.this);
                            btn.setText("JOIN");
                            btn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    System.out.println("Invio Join");

                                    Location l = LocationManager.getInstance().getLastLocation();

                                    Point p = new Point();
                                    p.setLatitude(l.getLatitude());
                                    p.setLongitude(l.getLongitude());

                                    JoinGame join = new JoinGame();
                                    join.setEvent("join_game");
                                    join.setGame(game);
                                    join.setPos(p);

                                    ConnectionManager.getInstance().sendMessage(new Gson().toJson(join));
                                }
                            });
                            row.addView(btn);
                            table_layout.addView(row);
                        }
                    }
                }


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
