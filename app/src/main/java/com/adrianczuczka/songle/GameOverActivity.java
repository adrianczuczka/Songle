package com.adrianczuczka.songle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by s1550570 on 06/11/17.
 */

public class GameOverActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        TextView reason = findViewById(R.id.game_over_reason);
        if (getIntent().hasExtra("timer")) {
            reason.setText("You ran out of time!");
        } else if (getIntent().hasExtra("tries")) {
            reason.setText("You ran out of attempts!");
        }
    }
}
