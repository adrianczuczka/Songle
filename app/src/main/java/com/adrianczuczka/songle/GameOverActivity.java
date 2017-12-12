package com.adrianczuczka.songle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Activity for when the user either runs out of time, or runs out of tries while trying to guess a song. Has a return button, and shows the reason
 * why the user lost.
 */
public class GameOverActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        TextView reason = findViewById(R.id.game_over_reason);
        Button returnButton = findViewById(R.id.game_over_return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishGame();
            }
        });
        if (getIntent().hasExtra("timer")) {
            reason.setText(getResources().getString(R.string.you_ran_out_of_time));
        } else if (getIntent().hasExtra("tries")) {
            reason.setText(getResources().getString(R.string.you_ran_out_of_attempts));
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("successList");
        editor.remove("title");
        editor.remove("kml");
        editor.remove("lyrics");
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishGame();
    }

    private void finishGame() {
        Intent intent = new Intent(GameOverActivity.this, WelcomeScreen.class);
        startActivity(intent);
        finish();
    }
}
