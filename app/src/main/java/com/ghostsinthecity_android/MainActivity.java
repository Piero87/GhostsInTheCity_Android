package com.ghostsinthecity_android;

import android.app.ProgressDialog;
import android.location.Location;
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

public class MainActivity extends FragmentActivity implements GameEvent, LocationEvent {

    private static final String TAG = "Ghost_MainActivity";

    private ProgressDialog mDialog;
    private Button button;
    private EditText text_field;

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
                    alert.setPositiveButton("OK",null);
                    alert.show();
                }
            }
        });
    }

    public void requestConnectingWithName(String username) {
        ConnectionManager.getInstance().setChangeListener(MainActivity.this);
        ConnectionManager.getInstance().openWebSocket(username);
    }

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
            button.setVisibility(View.VISIBLE);
            text_field.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void connected() {
        Log.d(TAG, "Connected");
        runOnUiThread(new Runnable() {
            public void run() {
                // UI code goes here
                mDialog.hide();
                Intent i = new Intent(MainActivity.this, DiscoveryService.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void refreshGameList(Game[] games_list) {
        // TODO Auto-generated method stub

    }

    @Override
    public void openGame(Game game) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateLocation(Location location) {

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


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
