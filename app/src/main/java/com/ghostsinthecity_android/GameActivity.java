package com.ghostsinthecity_android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.ghostsinthecity_android.models.Game;
import android.content.Intent;
import android.view.Window;
import android.view.View;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.World;


import java.util.ArrayList;
import android.location.Location;

public class GameActivity extends FragmentActivity implements OnClickBeyondarObjectListener,GameEvent,LocationEvent {

    /*
    static class GameStatus {
        public static final int WAITING = 0;
        public static final int STARTED = 1;
        public static final int PAUSED = 2;
        public static final int FINISHED = 3;
    }
    */

    private Game currentGame;
    private BeyondarFragmentSupport mBeyondarFragment;
    private World world;

    private TextView waiting_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_game);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        ConnectionManager.getInstance().setChangeListener(GameActivity.this);
        SocketLocation.getInstance().setChangeListener(GameActivity.this);

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);


        Intent mIntent  = getIntent();
        currentGame  = (Game )mIntent.getParcelableExtra("game");

        waiting_label = (TextView) findViewById(R.id.waiting_players_label);
        waiting_label.setText("Waiting " + (currentGame.getN_players() - currentGame.getPlayers().size())+" more players...");
        waiting_label.setVisibility(View.GONE);
    }


    @Override
    public void updateLocation(Location location) {

    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        // The first element in the array belongs to the closest BeyondarObject
        Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
    }



    @Override
    public void gameStatusChanged(Game game) {

        int old_status = currentGame.getStatus();

        currentGame = game;

        switch (game.getStatus()) {

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
