package com.ghostsinthecity_android;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.beyondar.android.world.GeoObject;
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
        waiting_label.setText("Waiting " + (currentGame.getN_players() - currentGame.getPlayers().size()) + " more players...");
        waiting_label.setVisibility(View.GONE);

        Location l = SocketLocation.getInstance().getLastLocation();

        world = new World(this);
        world.setGeoPosition(l.getLatitude(), l.getLongitude());
        world.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

        GeoObject go1 = new GeoObject(1l);
        go1.setGeoPosition(44.139768, 12.243406);
        go1.setImageResource(R.drawable.ghost);
        go1.setName("Ingresso Via Sacchi");

        GeoObject go2 = new GeoObject(2l);
        go2.setGeoPosition(44.139716, 12.242636);
        go2.setImageResource(R.drawable.ghost);
        go2.setName("Cessi Piano Terra");

        GeoObject go3 = new GeoObject(3l);
        go3.setGeoPosition(44.139997, 12.242686);
        go3.setImageResource(R.drawable.ghost);
        go3.setName("Biblioteca");

        GeoObject go4 = new GeoObject(4l);
        go4.setGeoPosition(44.139841, 12.243162);
        go4.setImageResource(R.drawable.ghost);
        go4.setName("Segreteria");

        GeoObject go5 = new GeoObject(5l);
        go5.setGeoPosition(44.146015, 12.252614);
        go5.setImageResource(R.drawable.ghost);
        go5.setName("Segreteria");

        world.addBeyondarObject(go1);
        world.addBeyondarObject(go2);
        world.addBeyondarObject(go3);
        world.addBeyondarObject(go4);
        world.addBeyondarObject(go5);


        mBeyondarFragment.setWorld(world);
        mBeyondarFragment.setMaxDistanceToRender(50);
        mBeyondarFragment.setSensorDelay(SensorManager.SENSOR_DELAY_GAME);
        mBeyondarFragment.showFPS(true);
    }


    @Override
    public void updateLocation(Location location) {

        world.setGeoPosition(location.getLatitude(), location.getLongitude());
        mBeyondarFragment.setWorld(world);
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
