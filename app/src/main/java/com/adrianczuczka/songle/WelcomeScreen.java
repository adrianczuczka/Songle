package com.adrianczuczka.songle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
    }

    //REDIRECTS TO MAPS ACTIVITY FOR TESTING PURPOSES
    public void startChooseSong(View view){
        Intent intent = new Intent(WelcomeScreen.this, ChooseSong.class);
        startActivity(intent);
    }
}
