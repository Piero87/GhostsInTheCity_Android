package com.ghostsinthecity_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghostsinthecity_android.models.EventString;
import com.ghostsinthecity_android.models.GameResults;
import com.ghostsinthecity_android.models.Player;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;

public class GameResultsActivity extends Activity {

    private TextView lbl_result;
    private TextView lbl_comment;
    private TextView lbl_coin;
    private TextView lbl_key;
    private ImageView img_victory;
    private ImageView img_defeat;
    private Button btn_play_again;

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

        setContentView(R.layout.activity_game_results);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        lbl_result = (TextView) findViewById(R.id.lbl_result);
        lbl_comment = (TextView) findViewById(R.id.lbl_comment);
        lbl_coin = (TextView) findViewById(R.id.lbl_coin);
        lbl_key = (TextView) findViewById(R.id.lbl_key);
        img_victory = (ImageView) findViewById(R.id.img_victory);
        img_defeat = (ImageView) findViewById(R.id.img_defeat);
        btn_play_again = (Button) findViewById(R.id.btn_play_again);

        GameResults game_result = ConnectionManager.getInstance().game_result;
        String my_uid = ConnectionManager.getInstance().uid;

        List<Player> players = game_result.getPlayers();
        int winner_team = game_result.getTeam();

        System.out.println("MIO UID: "+my_uid);
        System.out.println("WINNER TEAM: " + winner_team);

        lbl_result.setText("YOUR TEAM LOST");
        lbl_comment.setText("try again next time");
        img_victory.setVisibility(View.INVISIBLE);
        img_defeat.setVisibility(View.VISIBLE);

        if (winner_team == -2 ) {
            // La missione è finita prima del tempo, qualcuno se n'è andato

            lbl_result.setText("MISSION ABORTED");
            lbl_comment.setText("some coward left the game");

        } else if (winner_team == -1) {
            //La missione è finita in pari
            lbl_result.setText("MISSION DRAW");
            lbl_comment.setText("It ended in a draw");
        }

        for (Player p : players) {
            if (p.getUid().equals(my_uid)) {
                lbl_coin.setText(String.valueOf(p.getGold()));
                lbl_key.setText(String.valueOf(p.getKeys().size()));
                if (p.getTeam() == winner_team) {
                    lbl_result.setText("YOUR TEAM WON");
                    lbl_comment.setText("now nothing is strange in the neighborhood");
                    img_victory.setVisibility(View.VISIBLE);
                    img_defeat.setVisibility(View.INVISIBLE);
                    break;
                }
            }
        }

        btn_play_again.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                Intent i = new Intent(GameResultsActivity.this, GameLobby.class);
                startActivity(i);
            }
        });


    }
}
