package com.ghostsinthecity_android;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by Piero on 02/03/16.
 */
public class ConnectionManager {

    private static ConnectionManager instance = null;
    private GameEvent ge;

    private WebSocketClient webSocket = null;
    public String username;
    public String uid;

    protected ConnectionManager() {
    }

    // Lazy Initialization (If required then only)
    public static ConnectionManager getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    instance = new ConnectionManager();
                }
            }
        }
        return instance;
    }

    public void setChangeListener(GameEvent listener) {
        this.ge = listener;
    }

    public void openWebSocket(String username) {

        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(0, 8);
        this.uid = uuid;
        this.username = username;

        URI uri;
        try {
            uri = new URI("ws://ghosts-in-the-city.herokuapp.com/login/"+username+"/"+uuid);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocket = new WebSocketClient(uri) {

            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("Websocket opened");
                if (ge != null) {
                    ge.connected();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("Websocket closed: "+s);
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Websocket error: "+e.getMessage());
            }

            @Override
            public void onMessage(String s) {
                System.out.println("Messaggio arrivato: "+s);
                try {
                    JSONObject obj = new JSONObject(s);
                    System.out.println("Chiave: "+obj.getString("event"));
                    if (obj.getString("event").equals("games_list")) {
                        System.out.println("Ricevuto messaggio Lista Partite");
                        ge.refreshGameList(obj.getJSONArray("list"));
                    } else if (obj.getString("event").equals("game_ready")) {
                        System.out.println("Ricevuto messaggio Game Ready");
                        ge.openGame(obj.getJSONObject("game"));
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        };

        webSocket.connect();
    }

    public void sendMessage(String json) {
        System.out.println("Richiesta invio messaggio...");
        webSocket.send(json);
    }
}
