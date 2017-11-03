package com.adrianczuczka.songle;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
        }
    }

    public void startSettings(View view) {
        Intent intent = new Intent(WelcomeScreen.this, SettingsActivity.class);
        startActivity(intent);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
