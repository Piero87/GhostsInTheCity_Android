package com.ghostsinthecity_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location;

import com.ghostsinthecity_android.models.*;
import com.ghostsinthecity_android.util.visualLibrary;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GameActivity extends AppCompatActivity implements LocationEvent,GameEvent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LocationManager.getInstance().setChangeListener(this);

        HashMap<String, Point> testPos = new HashMap<String, Point>();

        double latitude = 44.236125;
        double longitude = 12.071073;
        Point myPos = new Point();
        myPos.setLatitude(latitude);
        myPos.setLongitude(longitude);

        double last_latitude = 44.236137;
        double last_longitude = 12.071041;
        Point myLastPos = new Point();
        myPos.setLatitude(last_latitude);
        myPos.setLongitude(last_longitude);

        System.out.println("@@@ DEBUG 1");
        Point pos_0 = new Point();
        pos_0.setLatitude(44.236112);
        pos_0.setLongitude(12.071131);
        testPos.put("pos_0", pos_0);

        Point pos_1 = new Point();
        pos_1.setLatitude(44.236101);
        pos_1.setLongitude(12.071056);
        testPos.put("pos_1", pos_1);

        Point pos_2 = new Point();
        pos_2.setLatitude(44.236138);
        pos_2.setLongitude(12.071021);
        testPos.put("pos_2", pos_2);

        Point pos_3 = new Point();
        pos_3.setLatitude(44.236263);
        pos_3.setLongitude(12.071148);
        testPos.put("pos_3", pos_3);

        double orientation = visualLibrary.getOrientation(myPos, myLastPos);
        System.out.println("@@@ DEBUG: orientation =  "+testPos.size());
        ArrayList<Point> triangolo = visualLibrary.getVisualTriangle(myPos, orientation);

        System.out.println("@@@ DEBUG: testPos.size() =  "+testPos.size());
        for(int i=0; i<testPos.size(); i++){

            String index = "pos_"+i;
            System.out.println("@@@ DEBUG: "+i+" = "+testPos.get(index));
            boolean isInternalPoint = visualLibrary.pointInTriangle(myPos, triangolo.get(0), triangolo.get(1), testPos.get(index));
            System.out.println("@@@ DEBUG: il punto "+i+" è interno? "+isInternalPoint);
        }


    }

    @Override
    public void connected() {

    }

    @Override
    public void refreshGameList(Game[] games_list) {

    }

    @Override
    public void openGame(Game game) {

    }

    @Override
    public void updateLocation(Location location) {
        System.out.println("Latitude: "+String.valueOf(location.getLatitude()));
        System.out.println("Longitude: " + String.valueOf(location.getLongitude()));
    }
}
