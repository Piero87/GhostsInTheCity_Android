package com.ghostsinthecity_android;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import com.ghostsinthecity_android.models.Game;
import com.ghostsinthecity_android.models.Ghost;
import com.ghostsinthecity_android.models.MessageCode;
import com.ghostsinthecity_android.models.Player;
import com.ghostsinthecity_android.models.Trap;
import com.ghostsinthecity_android.models.Treasure;

import java.util.List;

public class MainActivity extends FragmentActivity implements GameEvent {

    private static final String TAG = "Ghost_MainActivity";

    private ProgressDialog mDialog;
    private Button button;
    private EditText text_field;

    /**
     *
     * Default OnCreate method of Android, initialize main component
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);


        button  = (Button) findViewById(R.id.enter_btn);
        text_field = (EditText) findViewById(R.id.field_username);

        button.setVisibility(View.GONE);
        text_field.setVisibility(View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (!text_field.getText().toString().isEmpty()) {
                    mDialog.show();
                    requestConnectingWithName(text_field.getText().toString());
                } else {
                    Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("You need to insert a name");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
            }
        });
    }

    /**
     *
     * This method inizialitze the ConnectionManager Singleton, and the this class with the GameEvent Listener
     *
     * @param username name of the user account
     */
    public void requestConnectingWithName(String username) {
        ConnectionManager.getInstance().setChangeListener(MainActivity.this);
        ConnectionManager.getInstance().initializeAccount(username);
        ConnectionManager.getInstance().openWebSocket();
    }

    /**
     * Default onResume method of Android, that will be called when the view appear
     */
    @Override
    public void onResume() {
        super.onResume();

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
        mDialog.show();


        boolean isEmulator = "generic".equals(Build.BRAND.toLowerCase());

        if (isEmulator) {
            mDialog.show();
            requestConnectingWithName("Simulator");
        } else if (getDeviceName().toLowerCase().contains("moverio")) {
            mDialog.show();
            requestConnectingWithName("Moverio");
        } else {
            mDialog.show();
            requestConnectingWithName("Test");
            //button.setVisibility(View.VISIBLE);
            //text_field.setVisibility(View.VISIBLE);
        }
    }

    /**
     * GameEvent listener method that it's called when the websocket connect to the server.
     * Then open the WaitingGPSActivity.
     */
    @Override
    public void connected() {
        Log.d(TAG, "Connected");
        runOnUiThread(new Runnable() {
            public void run() {
                // UI code goes here
                mDialog.hide();
                Intent i = new Intent(MainActivity.this, WaitingGPSDevice.class);
                startActivity(i);
            }
        });
    }

    /**
     *
     * GameEvent listener method to refresh games available list
     *
     * @param games_list list of games available
     */
    @Override
    public void refreshGameList(Game[] games_list) {
        // TODO Auto-generated method stub

    }


    /**
     *
     * GameEvent listener method to open GameActivity view
     *
     * @param game Game Entity
     */
    @Override
    public void openGame(Game game) {
        // TODO Auto-generated method stub

    }

    /**
     *
     * GameEvent listener method that notify when GameStatus change
     *
     * @param game Game Entity
     */
    @Override
    public void gameStatusChanged(Game game) {

    }

    /**
     *
     * GameEvent listener method that notify when a player position change
     *
     * @param player Player Entity
     */
    @Override
    public void updatePlayerPosition(Player player) {

    }

    /**
     *
     * GameEvent listener method that notify when a player info change
     *
     * @param player Player Entity
     */
    @Override
    public void updatePlayerInfo(Player player) {

    }

    /**
     *
     * GameEvent listener method that notify when ghosts position change
     * @param ghosts Ghost List Entity
     */
    @Override
    public void updateGhostsPositions(List<Ghost> ghosts) {

    }

    /**
     *
     * GameEvent listener method that notify when player position change and give the visible treasures around player
     *
     * @param treasures Treasures List Entity
     */
    @Override
    public void updateTreasures(List<Treasure> treasures) {

    }

    /**
     * GameEvent listener method that notify when a new trap is added
     *
     * @param trap Trap Entity
     */
    @Override
    public void addTrap(Trap trap) {

    }

    /**
     * GameEvent listener method that notify when a trap is activated
     *
     * @param trap Trap Entity
     */
    @Override
    public void activateTrap(Trap trap) {

    }

    /**
     * GameEvent listener method that notify when a trap removed
     *
     * @param trap Trap Entity
     */
    @Override
    public void removeTrap(Trap trap) {

    }

    /**
     * GameEvent listener method that notify when show a info message
     *
     * @param msg_code MessageCode Entity
     */
    @Override
    public void showMessage(MessageCode msg_code) {

    }

    /**
     * GameEvent listener method that notify when player position change and give the visible traps around player
     *
     * @param traps Trap Entity
     */
    @Override
    public void updateVisibleTraps(List<Trap> traps) {

    }

    /**
     * GameEvent listener method that notify when player position change and give the visible players around player
     *
     * @param players Player list Entity
     */
    @Override
    public void updateVisiblePlayers(List<Player> players) {

    }

    /**
     *
     * Give the name of the device
     *
     * @return string Device name
     */
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    /**
     * If the parameter is not null capitalize the first letter
     * @param s string to capitize
     * @return
     */
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
