package com.ghostsinthecity_android;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import java.io.InputStreamReader;
import java.io.BufferedReader;

import android.location.Location;

public class SocketLocation {

    private static final String TAG = "Ghost_SocketLocation";

    private static SocketLocation instance = null;

    private LocationEvent le;
    private Location lastLocation;

    /**
     *
     * Initialize the Singleton SocketLocation instance if is not already initialized
     *
     * @return ConnectionManager instance
     */
    public static SocketLocation getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (SocketLocation.class) {
                if (instance == null) {
                    instance = new SocketLocation();
                }
            }
        }
        return instance;
    }

    /**
     * Set last location in lastLocation variable
     * @param location
     */
    void setLastLocation (Location location) {
        this.lastLocation = location;

        if (this.le != null) {
            this.le.updateLocation(lastLocation);
        }

    }

    /**
     * Get last location retrived
     * @return
     */
    public Location getLastLocation () {
        return this.lastLocation;
    }

    /**
     *
     * Set the listener of the GameEvent interface
     *
     * @param listener GameEvent listener
     */
    public void setChangeListener(LocationEvent listener) {
        this.le = listener;
    }

    /**
     * Initialize the Thread that handle and start the socket server
     */
    public void startSocketServer () {

        SocketServerThread socketServerThread = new SocketServerThread();
        socketServerThread.start();
    }

    /**
     * This class handle the Server Socket which expects a device available to send the GPS signal
     */
    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {

                Log.d(TAG,"Starting SocketServer...");

                serverSocket = new ServerSocket(SocketServerPORT);

                while (true) {

                    //creating socket and waiting for client connection
                    Socket socket = serverSocket.accept();

                    SocketConnection sc = new SocketConnection(socket);
                    sc.start();
                }

                //System.out.println("Shutting down Socket server!!");
                //close the ServerSocket object
                //serverSocket.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * This class handle the single socket connection
     */
    public class SocketConnection extends Thread {
        Socket socket;

        public SocketConnection(Socket s){
            this.socket = s;
        }

        @Override
        public void run() {
            Log.d(TAG, "Socket Connection Started");
            try {

                BufferedReader br = new BufferedReader( new InputStreamReader(socket.getInputStream(),"UTF-8"));
                String line =null ;
                while((line = br.readLine())!=null)
                {
                    Log.d(TAG, "Message: "+line);
                    String[] parts = line.split(",");
                    Location location = new Location("User Position");
                    location.setLatitude(Double.parseDouble(parts[0]));
                    location.setLongitude(Double.parseDouble(parts[1]));
                    setLastLocation(location);
                }
                br.close();

            } catch (IOException e) {
                e.printStackTrace();

            }

        }

    }

    /**
     * Find IP Address
     * @throws SocketException
     */
    void enumrateIPAddress() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                System.out.println("Ghost_"+i.getHostAddress());
            }
        }
    }

}
