package com.ghostsinthecity_android;

import android.location.Location;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import com.ghostsinthecity_android.models.Game;
import com.ghostsinthecity_android.models.GameResults;
import com.ghostsinthecity_android.models.Ghost;
import com.ghostsinthecity_android.models.MessageCode;
import com.ghostsinthecity_android.models.Player;
import com.ghostsinthecity_android.models.Point;
import com.ghostsinthecity_android.models.PositionUpdate;
import com.ghostsinthecity_android.models.ResumeGame;
import com.ghostsinthecity_android.models.Trap;
import com.ghostsinthecity_android.models.Treasure;
import com.google.gson.*;
import java.util.Arrays;

public class ConnectionManager {

    private static ConnectionManager instance = null;
    private GameEvent ge;

    private WebSocketClient webSocket = null;
    public String username;
    public String uid;
    public String game_uid;
    public GameResults game_result = null;

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

    public void initializeAccount(String username) {

        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(0, 8);
        this.username = username;
        this.uid = uuid;
        this.game_uid = "";
    }
    public void setChangeListener(GameEvent listener) {
        this.ge = listener;
    }

    public void openWebSocket() {

        URI uri;
        try {
            //uri = new URI("ws://ghosts-in-the-city.herokuapp.com/login/"+username+"/"+uid);
            uri = new URI("ws://192.168.1.112:9000/login/"+username+"/"+uid);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocket = new WebSocketClient(uri) {

            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("Websocket opened");
                if (ge != null && game_uid.equals("")) {
                    ge.connected();
                } else if (ge != null && !game_uid.equals("")) {

                    //C'era una partita in corso spedisco un resume
                    ResumeGame resume = new ResumeGame();
                    resume.setEvent("resume_game");

                    Location l = SocketLocation.getInstance().getLastLocation();

                    PositionUpdate pos_update = new PositionUpdate();
                    pos_update.setEvent("");
                    Point p = new Point();
                    p.setLatitude(l.getLatitude());
                    p.setLongitude(l.getLongitude());

                    resume.setPos(p);
                    resume.setGame_id(game_uid);

                    sendMessage(new Gson().toJson(resume));
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("Websocket closed: "+s);
                openWebSocket();
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

                        Game[] games_list = new Gson().fromJson(obj.getJSONArray("list").toString(), Game[].class);

                        if (ge != null) ge.refreshGameList(games_list);

                    } else if (obj.getString("event").equals("game_ready")) {
                        System.out.println("Ricevuto messaggio Game Ready");

                        Game game = new Gson().fromJson(obj.getJSONObject("game").toString(), Game.class);
                        game_uid = game.getId();
                        game_result = null;

                        if (ge != null) ge.openGame(game);
                    } else if (obj.getString("event").equals("game_status")) {

                        Game game = new Gson().fromJson(obj.getJSONObject("game").toString(), Game.class);

                        if (ge != null) ge.gameStatusChanged(game);

                    } else if (obj.getString("event").equals("update_player_position")) {

                        Player player = new Gson().fromJson(obj.getJSONObject("player").toString(), Player.class);

                        if (ge != null) ge.updatePlayerPosition(player);

                    } else if (obj.getString("event").equals("update_player_info")) {

                        Player player = new Gson().fromJson(obj.getJSONObject("player").toString(), Player.class);

                        if (ge != null) ge.updatePlayerInfo(player);
                    } else if (obj.getString("event").equals("update_ghosts_positions")) {

                        Ghost[] ghosts = new Gson().fromJson(obj.getJSONArray("ghosts").toString(), Ghost[].class);

                        if (ge != null) ge.updateGhostsPositions(Arrays.asList(ghosts));

                    } else if (obj.getString("event").equals("update_treasures")) {

                        Treasure[] treasures = new Gson().fromJson(obj.getJSONArray("treasures").toString(), Treasure[].class);

                        if (ge != null) ge.updateTreasures(Arrays.asList(treasures));
                    } else if (obj.getString("event").equals("new_trap")) {

                        Trap trap = new Gson().fromJson(obj.getJSONObject("trap").toString(), Trap.class);

                        if (ge != null) ge.addTrap(trap);

                    } else if (obj.getString("event").equals("active_trap")) {

                        Trap trap = new Gson().fromJson(obj.getJSONObject("trap").toString(), Trap.class);

                        if (ge != null) ge.activateTrap(trap);
                    } else if (obj.getString("event").equals("remove_trap")) {

                        Trap trap = new Gson().fromJson(obj.getJSONObject("trap").toString(), Trap.class);

                        if (ge != null) ge.removeTrap(trap);
                    } else if (obj.getString("event").equals("message")) {

                        MessageCode message = new MessageCode();
                        message.setCode(obj.getInt("code"));
                        message.setOption(obj.getString("options"));

                        if (ge != null) ge.showMessage(message);
                    } else if (obj.getString("event").equals("game_results")) {

                        game_uid = "";
                        GameResults game_results_tmp = new GameResults();
                        game_results_tmp.setTeam(obj.getInt("team"));
                        Player[] players = new Gson().fromJson(obj.getJSONArray("players").toString(), Player[].class);
                        game_results_tmp.setPlayers(Arrays.asList(players));

                        game_result = game_results_tmp;
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
