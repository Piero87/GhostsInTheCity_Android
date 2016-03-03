package com.ghostsinthecity_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements GameEvent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        final Button button = (Button) findViewById(R.id.enter_btn);
        final EditText text_field = (EditText) findViewById(R.id.field_username);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (!text_field.getText().toString().isEmpty()) {
                    System.out.println("Effettuo Login...");
                    ConnectionManager.getInstance().setChangeListener(MainActivity.this);
                    ConnectionManager.getInstance().openWebSocket(text_field.getText().toString());
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

    @Override
    public void connected() {
        Intent i = new Intent(MainActivity.this, GameLobby.class);
        startActivity(i);
    }

    @Override
    public void refreshGameList(JSONArray arr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void openGame(JSONObject game) {
        // TODO Auto-generated method stub

    }
}
