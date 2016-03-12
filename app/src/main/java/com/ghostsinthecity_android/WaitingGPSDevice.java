package com.ghostsinthecity_android;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import android.net.wifi.WifiManager;

import java.net.InetAddress;
import android.text.format.Formatter;

public class WaitingGPSDevice extends Activity implements LocationEvent {

    private static final String TAG = "Ghost_WaitingGPSDevice";
    public static int SERVICE_INFO_PORT = 8081;
    private String SERVICE_INFO_TYPE = "_http._tcp.local.";
    private String SERVICE_INFO_NAME = "";
    JmDNS jmdns;
    ServiceInfo serviceInfo;
    private TextView code_gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_waiting_gpsdevice);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(0, 4);

        code_gps  = (TextView) findViewById(R.id.code_gps);

        code_gps.setText(uuid);

        SERVICE_INFO_NAME = "GhostInTheCity: "+uuid;

        SocketLocation.getInstance().startSocketServer();
        SocketLocation.getInstance().setChangeListener(WaitingGPSDevice.this);

        RegisterService job = new RegisterService();
        job.execute();
    }

    @Override
    public void updateLocation(Location location) {

        //E' arrivata la prima posizione vuol dire che posso andare oltre
        Intent i = new Intent(WaitingGPSDevice.this, GameLobby.class);
        startActivity(i);
        SocketLocation.getInstance().setChangeListener(null);

    }

    class RegisterService extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {

                WifiManager wifi =   (WifiManager) WaitingGPSDevice.this.getSystemService(android.content.Context.WIFI_SERVICE);

                String ip = Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress());
                Log.d(TAG,InetAddress.getByName(ip).toString());

                jmdns = JmDNS.create(InetAddress.getByName(ip));
                jmdns.unregisterAllServices();
                serviceInfo = ServiceInfo.create(SERVICE_INFO_TYPE, SERVICE_INFO_NAME , SERVICE_INFO_PORT,"Request GPS Information");
                jmdns.registerService(serviceInfo);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Bisogna fermare il servizio quando abbiamo trovato l'indirizzo del dispositivo che vogliamo, tipo con questi metodi qui sotto
            //jmdns.unregisterAllService();
            //jmdns.close();
            return null;
        }
    }
}
