package com.ghostsinthecity_android;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.World;
import com.ghostsinthecity_android.models.Game;

import java.util.ArrayList;



public class GameActivity extends FragmentActivity implements OnClickBeyondarObjectListener,GameEvent,LocationEvent {

    static class GameStatus {
        public static final int WAITING = 0;
        public static final int STARTED = 1;
        public static final int PAUSED = 2;
        public static final int FINISHED = 3;
    }

    private BeyondarFragmentSupport mBeyondarFragment;
    private World world;
    private int gameStatus = GameStatus.WAITING;


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

        /*
        world = new World(this);
        world.setGeoPosition(44.236193, 12.071008);
        world.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

        GeoObject go1 = new GeoObject(1l);
        go1.setGeoPosition(44.236474, 12.071138);
        go1.setImageResource(R.drawable.ghost);
        go1.setName("Primo Parcheggio");

        GeoObject go2 = new GeoObject(2l);
        go2.setGeoPosition(44.236574, 12.071208);
        go2.setImageResource(R.drawable.ghost);
        go2.setName("Sesto Parcheggio");

        GeoObject go3 = new GeoObject(3l);
        go3.setGeoPosition(44.236404, 12.070797);
        go3.setImageResource(R.drawable.ghost);
        go3.setName("Incrocio");

        GeoObject go4 = new GeoObject(4l);
        go4.setGeoPosition(44.236503, 12.070051);
        go4.setImageResource(R.drawable.ghost);
        go4.setName("Casa Richard");

        GeoObject go5 = new GeoObject(5l);
        go5.setGeoPosition(44.237682, 12.072386);
        go5.setImageResource(R.drawable.ghost);
        go5.setName("Parchetto Pattini");

        GeoObject go6 = new GeoObject(6l);
        go6.setGeoPosition(44.236125, 12.071189);
        go6.setImageResource(R.drawable.ghost);
        go6.setName("Panchina Parchetto a sinistra");

        GeoObject go7 = new GeoObject(7l);
        go7.setGeoPosition(44.236155, 12.070740);
        go7.setImageResource(R.drawable.ghost);
        go7.setName("Parcheggino dietro l'angolo");

        world.addBeyondarObject(go1);
        world.addBeyondarObject(go2);
        world.addBeyondarObject(go3);
        world.addBeyondarObject(go4);
        world.addBeyondarObject(go5);
        world.addBeyondarObject(go6);
        world.addBeyondarObject(go7);


        mBeyondarFragment.setWorld(world);
        */
    }

    @Override
    public void updateLocation(Location location) {

    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        // The first element in the array belongs to the closest BeyondarObject
        Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
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