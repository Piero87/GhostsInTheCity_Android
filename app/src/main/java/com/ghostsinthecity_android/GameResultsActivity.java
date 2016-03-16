package com.ghostsinthecity_android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.ghostsinthecity_android.models.GameResults;

public class GameResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_game_results);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        GameResults game_result = ConnectionManager.getInstance().game_result;
    }
}
