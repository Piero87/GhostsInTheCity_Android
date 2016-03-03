package com.ghostsinthecity_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_COARSE);	//default
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // user defines the criteria

        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        provider = locationManager.getBestProvider(criteria, false);

        // the last known location of this provider
        Location location = locationManager.getLastKnownLocation(provider);

        mylistener = new MyLocationListener();

        if (location != null) {
            mylistener.onLocationChanged(location);
        } else {
            // leads to the settings because there is no last known location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        // location updates: at least 1 meter and 200millsecs change
        locationManager.requestLocationUpdates(provider, 500, 1, mylistener);
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
            System.out.println("Latitude: "+String.valueOf(location.getLatitude()));
            System.out.println("Longitude: " + String.valueOf(location.getLongitude()));
            System.out.println(provider + " provider has been selected.");

            Toast.makeText(GameActivity.this, "Location changed: Lat(" + String.valueOf(location.getLatitude()) + ") Long(" + String.valueOf(location.getLongitude()) + ")",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(MainActivity.this, provider + "'s status changed to "+status +"!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(MainActivity.this, "Provider " + provider + " disabled!",Toast.LENGTH_SHORT).show();
        }
    }
}
