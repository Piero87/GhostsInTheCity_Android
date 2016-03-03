package com.ghostsinthecity_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity implements LocationEvent,GameEvent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LocationManager.getInstance().setChangeListener(this);
    }

    @Override
    public void connected() {

    }

    @Override
    public void refreshGameList(JSONArray arr) {

    }

    @Override
    public void openGame(JSONObject game) {

    }

    @Override
    public void updateLocation(Location location) {
        System.out.println("Latitude: "+String.valueOf(location.getLatitude()));
        System.out.println("Longitude: " + String.valueOf(location.getLongitude()));
    }
}
