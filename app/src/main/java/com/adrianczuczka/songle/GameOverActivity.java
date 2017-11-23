package com.adrianczuczka.songle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by s1550570 on 06/11/17.
 */

public class GameOverActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        TextView reason = findViewById(R.id.game_over_reason);
        if (getIntent().hasExtra("timer")) {
            reason.setText(getResources().getString(R.string.you_ran_out_of_time));
        } else if (getIntent().hasExtra("tries")) {
            reason.setText(getResources().getString(R.string.you_ran_out_of_attempts));
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        editor.remove("successList");
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GameOverActivity.this, WelcomeScreen.class);
        startActivity(intent);
        finish();
    }

    public void onReturnClick(View view) {
        Intent intent = new Intent(GameOverActivity.this, WelcomeScreen.class);
        startActivity(intent);
        finish();
    }
}
