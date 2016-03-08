package com.ghostsinthecity_android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TableLayout;
import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceInfo;

/**
 * Created by andreabuscarini on 08/03/16.
 */
public class DiscoverService extends FragmentActivity {
    TableLayout table_layout;

    android.net.wifi.WifiManager.MulticastLock lock;

    JmDNS jmdns = null;
    private ServiceListener listener = null;
    private ServiceInfo serviceInfo;

    private static final String TAG = "GhostInTheCity_android";
    private String type = "_workstation._tcp.local.";

    android.os.Handler handler = new android.os.Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_lobby);

        table_layout = (TableLayout) findViewById(R.id.service_list);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setUp();
            }
        },1000);


    }
    private void setUp() {
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("mylockthereturn");
        lock.setReferenceCounted(true);
        lock.acquire();
        try {
            jmdns = JmDNS.create();
            jmdns.addServiceListener(type, listener = new ServiceListener() {

                @Override
                public void serviceResolved(ServiceEvent ev) {
                    String additions = "";
                    if (ev.getInfo().getInetAddresses() != null && ev.getInfo().getInetAddresses().length > 0) {
                        additions = ev.getInfo().getInetAddresses()[0].getHostAddress();
                    }
                    System.out.println("Service resolved: " + ev.getInfo().getQualifiedName() + " port:" + ev.getInfo().getPort() + additions);
                }

                @Override
                public void serviceRemoved(ServiceEvent ev) {
                    System.out.println("Service removed: " + ev.getName());
                }

                @Override
                public void serviceAdded(ServiceEvent event) {
                    // Required to force serviceResolved to be called again (after the first search)
                    jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
                }
            });
            serviceInfo = ServiceInfo.create("_test._tcp.local.", "AndroidTest", 0, "plain test service from android");
            jmdns.registerService(serviceInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (lock != null) lock.release();
    }

    protected void onStart() {
        super.onStart();
        //new Thread(){public void run() {setUp();}}.start();
    }

    protected void onStop() {
        if (jmdns != null) {
            if (listener != null) {
                jmdns.removeServiceListener(type, listener);
                listener = null;
            }
            jmdns.unregisterAllServices();
            try {
                jmdns.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jmdns = null;
        }
        //repo.stop();
        //s.stop();
        lock.release();
        super.onStop();
    }

}
