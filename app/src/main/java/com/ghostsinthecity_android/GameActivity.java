package com.ghostsinthecity_android;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import com.beyondar.android.world.BeyondarObject;

import java.util.ArrayList;
import com.beyondar.android.world.World;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import android.widget.Toast;
import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.fragment.BeyondarFragment;

public class GameActivity extends FragmentActivity implements OnClickBeyondarObjectListener {

    private BeyondarFragmentSupport mBeyondarFragment;
    private World world;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);

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
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);
    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        // The first element in the array belongs to the closest BeyondarObject
        Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
    }
}
