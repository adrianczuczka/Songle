package com.adrianczuczka.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        boolean isOnline = isOnline();
        if(!isOnline){
            Toast toast = Toast.makeText(WelcomeScreen.this, "No Internet Connection",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //REDIRECTS TO MAPS ACTIVITY FOR TESTING PURPOSES
    public void startChooseSong(View view){
        if(!isOnline()){
            Toast toast = Toast.makeText(WelcomeScreen.this, "No Internet Connection",Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Intent intent = new Intent(WelcomeScreen.this, ChooseSong.class);
            startActivity(intent);
            finish();
        }
    }

    public void startResumeSong(View view){
        Intent mapIntent = new Intent(WelcomeScreen.this, GameUI.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lyrics = sharedPreferences.getString("lyrics", "null");
        String kml = sharedPreferences.getString("kml", "null");
        String title = sharedPreferences.getString("title", "null");
        if(!(lyrics.equals("null")||kml.equals("null")||title.equals("null"))){
            mapIntent.putExtra("lyrics", lyrics);
            mapIntent.putExtra("kml", kml);
            mapIntent.putExtra("title", title);
            startActivity(mapIntent);
            finish();
        }
    }

    public void startSettings(View view) {
        Intent intent = new Intent(WelcomeScreen.this, SettingsActivity.class);
        startActivity(intent);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
