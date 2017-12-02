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
import android.widget.Toast;

/**
 * Activity that shows the welcome screen. Has a new game button, a resume game button, and a settings button.
 */
public class WelcomeScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        boolean isOnline = isOnline();
        if(! isOnline){
            Toast toast = Toast.makeText(WelcomeScreen.this, "No Internet Connection", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * {@link android.view.View.OnClickListener} for the new game button. When clicked, redirects to the choose song screen. If there is an existing
     * game already,
     * shows an {@link AreYouSureFragment} first
     *
     * @param view should always be new game button.
     */
    public void startChooseSong(View view) {
        if(! isOnline()){
            Toast toast = Toast.makeText(WelcomeScreen.this, "No Internet Connection", Toast.LENGTH_LONG);
            toast.show();
        } else{
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String lyrics = sharedPreferences.getString("lyrics", "null");
            String kml = sharedPreferences.getString("kml", "null");
            String title = sharedPreferences.getString("title", "null");
            if(! (lyrics.equals("null") || kml.equals("null") || title.equals("null"))){
                AreYouSureFragment areYouSureFragment = AreYouSureFragment.newInstance();
                areYouSureFragment.show(getSupportFragmentManager(), "are you sure");
            } else{
                Intent intent = new Intent(WelcomeScreen.this, ChooseSong.class);
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = sharedPreferences.edit();
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
     * {@link android.view.View.OnClickListener} for the resume song button. Redirects to {@link GameUI}.
     *
     * @param view Should always be the resume song button.
     */
    public void startResumeSong(View view) {
        Intent mapIntent = new Intent(WelcomeScreen.this, GameUI.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lyrics = sharedPreferences.getString("lyrics", "null");
        String kml = sharedPreferences.getString("kml", "null");
        String title = sharedPreferences.getString("title", "null");
        if(! (lyrics.equals("null") || kml.equals("null") || title.equals("null"))){
            mapIntent.putExtra("resumed", true);
            mapIntent.putExtra("lyrics", lyrics);
            mapIntent.putExtra("kml", kml);
            mapIntent.putExtra("title", title);
            startActivity(mapIntent);
            finish();
        }
    }

    /**
     * {@link android.view.View.OnClickListener} for the settings button. Redirects to {@link SettingsActivity}.
     *
     * @param view Should always be the settings button.
     */
    public void startSettings(View view) {
        Intent intent = new Intent(WelcomeScreen.this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Checks if the app has access to the Internet.
     *
     * @return True if connected, false if not.
     */
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
