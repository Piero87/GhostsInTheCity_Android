package com.ghostsinthecity_android.ToDelete;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;

import com.ghostsinthecity_android.LocationEvent;

/**
 * Created by Piero on 03/03/16.
 */

public class LocationManager implements LocationListener {

    private static LocationManager instance = null;
    private LocationEvent le;

    private android.location.LocationManager locationManager;
    private String provider;
    private Criteria criteria;
    private Context context;
    private Location lastLocation;

    // Lazy Initialization (If required then only)
    public static LocationManager getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (LocationManager.class) {
                if (instance == null) {
                    instance = new LocationManager();
                }
            }
        }
        return instance;
    }

    //Per passare il context basta fare this nella classe in cui si lancia
    public void startLocation (Context context) {
        this.context = context;
        //Get the location manager
        locationManager = (android.location.LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_COARSE);	//default
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // user defines the criteria

        //high level accurancy to find location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        //get quickly find position using gps if no useless field aren't search
        criteria.setAltitudeRequired(false);

        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        provider = locationManager.getBestProvider(criteria, true);

        // the last known location of this provider
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        } else {
            // leads to the settings because there is no last known location
            System.out.println("Prova");
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        }
        // location updates: at least 1 meter and 200millsecs change
        locationManager.requestLocationUpdates(provider, 500, 1, this);
    }

    public void setChangeListener(LocationEvent listener) {
        this.le = listener;
    }

    public Location getLastLocation() {
        if (lastLocation == null)
        {
            lastLocation = locationManager.getLastKnownLocation(provider);
        }

        return lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Initialize the location fields
        lastLocation = location;
        if (this.le != null) {
            this.le.updateLocation(location);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
