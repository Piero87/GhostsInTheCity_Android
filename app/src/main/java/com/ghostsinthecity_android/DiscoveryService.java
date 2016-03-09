package com.ghostsinthecity_android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.io.IOException;
import java.util.ArrayList;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import android.widget.AdapterView;
import android.widget.ListView;
import java.util.Iterator;
import android.widget.AdapterView.OnItemClickListener;

public class DiscoveryService extends FragmentActivity {


    private static final String TAG = "Ghost_DiscoveryService";
    final static String DEFAULT_DOMAIN="local.";
    final static String SERVICE_TYPE="_http._tcp."+DEFAULT_DOMAIN;

    private ArrayList<ServiceEvent> serviceList;
    CustomAdapterService adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_discovery_service);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        ListView listView = (ListView)findViewById(R.id.service_list);

        serviceList = new ArrayList<ServiceEvent>();
        adapter = new CustomAdapterService(this, R.layout.row, serviceList);
        listView.setAdapter(adapter);

        OnItemClickListener clickListener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG,"Riga selezionata "+position);
            }
        };

        listView.setOnItemClickListener(clickListener);

        RetrieveDiscoveryList job = new RetrieveDiscoveryList();
        job.execute();

    }

    class RetrieveDiscoveryList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            JmDNS jmdns = null;
            try {
                jmdns = JmDNS.create();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jmdns.addServiceListener(SERVICE_TYPE, new SampleListener());

            //Bisogna fermare il servizio quando abbiamo trovato l'indirizzo del dispositivo che vogliamo, tipo con questi metodi qui sotto
            //jmdns.removeServiceListener(type, listener);
            //jmdns.close();
            return null;
        }
    }


    public class SampleListener implements ServiceListener {

        @Override
        public void serviceAdded(ServiceEvent event) {
            Log.d(TAG, "Service added: " + event.getName() + "." + event.getType() + ":" + event.getInfo().getPort());
            serviceList.add(event);
            runOnUiThread(new Runnable() {
                public void run() {
                    // UI code goes here
                    adapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            Log.d(TAG, "Service removed: " + event.getName() + "." + event.getType());

            Iterator<ServiceEvent> it = serviceList.iterator();
            while (it.hasNext()) {
                if (it.next().getName().equals(event.getName())) {
                    it.remove();
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    // UI code goes here
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            Log.d(TAG, "Service resolved: " + event.getInfo());
        }
    }
}

