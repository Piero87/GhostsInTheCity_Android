package com.ghostsinthecity_android.ToDelete;

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
import javax.jmdns.ServiceInfo;

import android.widget.AdapterView;
import android.widget.ListView;
import java.util.Iterator;
import android.widget.AdapterView.OnItemClickListener;

import com.ghostsinthecity_android.R;

import java.io.InputStream;
import java.net.Socket;
import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;

public class DiscoveryService extends FragmentActivity {


    private static final String TAG = "Ghost_DiscoveryService";
    final static String DEFAULT_DOMAIN="local.";
    final static String SERVICE_TYPE="_http._tcp."+DEFAULT_DOMAIN;

    private ArrayList<ServiceEvent> serviceList;
    CustomAdapterService adapter;
    JmDNS jmdns;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_discovery_service);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        ListView listView = (ListView)findViewById(R.id.service_list);

        serviceList = new ArrayList<ServiceEvent>();
        //adapter = new CustomAdapterService(this, R.layout.row, serviceList);
        //listView.setAdapter(adapter);

        OnItemClickListener clickListener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Riga selezionata: " + serviceList.get(position).getName());

                ServiceInfo info = serviceList.get(position).getDNS().getServiceInfo(serviceList.get(position).getType(), serviceList.get(position).getName());

                Log.d(TAG, info.getHostAddresses()[0]+":"+info.getPort());

                SocketRequest task = new SocketRequest();
                task.host_address = info.getHostAddresses()[0];
                task.port = info.getPort();
                task.execute();

            }
        };

        listView.setOnItemClickListener(clickListener);

        RetrieveDiscoveryList job = new RetrieveDiscoveryList();
        job.execute();

    }

    class SocketRequest extends AsyncTask<Void, Void, Void> {

        public String host_address;
        public int port;
        String response = "";

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Socket socket = new Socket(host_address,8081);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"EXCEPTION: "+e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;
                    //message += "#" + count + " from " + socket.getInetAddress() + ":" + socket.getPort() + "\n";

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    class RetrieveDiscoveryList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

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
            jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
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

