package com.adrianczuczka.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Activity that shows the welcome screen. Has a new game button, a resume game button, and a
 * settings button.
 */
public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Button newGameButton = findViewById(R.id.welcome_screen_play_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChooseSong();
            }
        });
        Button resumeGameButton = findViewById(R.id.welcome_screen_resume_button);
        resumeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startResumeSong();
            }
        });
        Button settingsButton = findViewById(R.id.welcome_screen_settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettings();
            }
        });
        boolean isOnline = isOnline();
        if(! isOnline){
            Toast toast = Toast.makeText(WelcomeScreen.this, "No Internet Connection", Toast
                    .LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * {@link android.view.View.OnClickListener} for the new game button. When clicked, redirects
     * to the choose song screen. If there is an existing
     * game already,
     * shows an {@link AreYouSureFragment} first
     */
    private void startChooseSong() {
        if(! isOnline()){
            Toast toast = Toast.makeText(WelcomeScreen.this, "No Internet Connection", Toast
                    .LENGTH_LONG);
            toast.show();
        } else{
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                    (getApplicationContext());
            String lyrics = sharedPreferences.getString("lyrics", "null");
            String kml = sharedPreferences.getString("kml", "null");
            String title = sharedPreferences.getString("title", "null");
            if(! (lyrics.equals("null") || kml.equals("null") || title.equals("null"))){
                AreYouSureFragment areYouSureFragment = AreYouSureFragment.newInstance();
                areYouSureFragment.show(getSupportFragmentManager(), "are you sure");
            } else{
                Intent intent = new Intent(WelcomeScreen.this, ChooseSong.class);
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                        (getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("successList");
                editor.remove("title");
                editor.remove("kml");
                editor.remove("lyrics");
                editor.apply();
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * {@link android.view.View.OnClickListener} for the resume song button. Redirects to
     * {@link GameUI}.
     */
    private void startResumeSong() {
        Intent mapIntent = new Intent(WelcomeScreen.this, GameUI.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());
        String lyrics = sharedPreferences.getString("lyrics", "null");
        String kml = sharedPreferences.getString("kml", "null");
        String title = sharedPreferences.getString("title", "null");
        int difficulty = sharedPreferences.getInt("difficulty", 0);
        if(! (lyrics.equals("null") || kml.equals("null") || title.equals("null"))){
            mapIntent.putExtra("difficulty", 0);
            mapIntent.putExtra("resumed", true);
            mapIntent.putExtra("lyrics", lyrics);
            mapIntent.putExtra("kml", kml);
            mapIntent.putExtra("title", title);
            startActivity(mapIntent);
            finish();
        }
    }

    /**
     * {@link android.view.View.OnClickListener} for the settings button. Redirects to
     * {@link SettingsActivity}.
     */
    private void startSettings() {
        Intent intent = new Intent(WelcomeScreen.this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Checks if the app has access to the Internet.
     *
     * @return True if connected, false if not.
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
