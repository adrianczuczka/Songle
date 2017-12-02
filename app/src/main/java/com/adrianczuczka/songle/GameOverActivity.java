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
 * Activity for when the user either runs out of time, or runs out of tries while trying to guess a song. Has a return button, and shows the reason
 * why the user lost.
 */
public class GameOverActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        TextView reason = findViewById(R.id.game_over_reason);
        if(getIntent().hasExtra("timer")){
            reason.setText(getResources().getString(R.string.you_ran_out_of_time));
        } else if(getIntent().hasExtra("tries")){
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

    /**
     * Button listener for the return button. When clicked, redirects to the welcome screen.
     *
     * @param view Should always be the return button.
     */
    public void onReturnClick(View view) {
        Intent intent = new Intent(GameOverActivity.this, WelcomeScreen.class);
        startActivity(intent);
        finish();
    }
}
