package com.ghostsinthecity_android;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.beyondar.android.view.CameraView;
import android.hardware.SensorManager;
import java.util.ArrayList;
import java.util.List;

public class TestGPS_AR extends FragmentActivity implements OnClickBeyondarObjectListener, LocationListener {

    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;
    private LocationManager mLocationManager;
    private Location mLocation;
    private String provider;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_test_gpsar);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);



        mLocationManager = (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_COARSE);	//default
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // user defines the criteria

        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        provider = mLocationManager.getBestProvider(criteria, false);

        mLocation =  getBestLocation();

        if(null != mLocation){
            System.out.println(mLocation.getLatitude()+";"+mLocation.getLongitude());
        } else{
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location lNetwork = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(null != lNetwork){
                mLocation = (mLocation == null || (mLocation.getTime() > lNetwork.getTime() && mLocation.getAccuracy() > lNetwork.getAccuracy()))
                        ? mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        : mLocation;
            }

        }

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);

        mWorld = new World(this);



        if(null != mLocation){
            System.out.println("Prima posiizone: "+mLocation.toString());
            mWorld.setGeoPosition(mLocation.getLatitude(), mLocation.getLongitude());
        } else {
            mWorld.setGeoPosition(0,0);
        }

        mWorld.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

        /*
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

        mWorld.addBeyondarObject(go1);
        mWorld.addBeyondarObject(go2);
        mWorld.addBeyondarObject(go3);
        mWorld.addBeyondarObject(go4);
        mWorld.addBeyondarObject(go5);
        mWorld.addBeyondarObject(go6);
        mWorld.addBeyondarObject(go7);
        */

        GeoObject go1 = new GeoObject(1l);
        go1.setGeoPosition(44.139771, 12.243418);
        go1.setImageResource(R.drawable.ghost);
        go1.setName("Via Sacchi Fuori Cortile");

        GeoObject go2 = new GeoObject(2l);
        go2.setGeoPosition(44.139852, 12.242746);
        go2.setImageResource(R.drawable.ghost);
        go2.setName("Entrata Macchinetta Caffè");

        GeoObject go3 = new GeoObject(3l);
        go3.setGeoPosition(44.139922, 12.242885);
        go3.setImageResource(R.drawable.ghost);
        go3.setName("Albero lato vicino biblio");

        GeoObject go4 = new GeoObject(4l);
        go4.setGeoPosition(44.139729, 12.242750);
        go4.setImageResource(R.drawable.ghost);
        go4.setName("Aula C");

        GeoObject go5 = new GeoObject(5l);
        go5.setGeoPosition(44.139845, 12.243115);
        go5.setImageResource(R.drawable.ghost);
        go5.setName("Segreteria");

        GeoObject go6 = new GeoObject(6l);
        go6.setGeoPosition(44.140082, 12.243436);
        go6.setImageResource(R.drawable.ghost);
        go6.setName("Via Sacchi Lontano");

        GeoObject go7 = new GeoObject(7l);
        go7.setGeoPosition(44.236155, 12.070740);
        go7.setImageResource(R.drawable.ghost);
        go7.setName("Parcheggino dietro l'angolo");

        mWorld.addBeyondarObject(go1);
        mWorld.addBeyondarObject(go2);
        mWorld.addBeyondarObject(go3);
        mWorld.addBeyondarObject(go4);
        mWorld.addBeyondarObject(go5);
        mWorld.addBeyondarObject(go6);

        mBeyondarFragment.setWorld(mWorld);
        mBeyondarFragment.setMaxDistanceToRender(50);
        mBeyondarFragment.setSensorDelay(SensorManager.SENSOR_DELAY_GAME);
        mBeyondarFragment.showFPS(true);
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);

        

        mLocationManager.requestLocationUpdates(provider, 200, 1, this);
    }

    public Location getBestLocation(){
        Location loc = null;

        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        for(String provider : providers){
            Location l = mLocationManager.getLastKnownLocation(provider);

            if(l == null){
                continue;
            }

            if(bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }

        }

        return loc;

    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        // The first element in the array belongs to the closest BeyondarObject
        Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        final Location l_tmp = location;

        if(location != null){
            //runOnUiThread(new Runnable() {
                //@Override
              //  public void run() {

                    Toast.makeText(TestGPS_AR.this, "GPS: " + l_tmp.getLatitude()+l_tmp.getLongitude(), Toast.LENGTH_LONG).show();
                    System.out.println("New Position: " + l_tmp.toString());
                    mLocation = l_tmp;
                    mWorld.setGeoPosition(mLocation.getLatitude(), mLocation.getLongitude());
                    mBeyondarFragment.setWorld(mWorld);

                //}
            //});


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
