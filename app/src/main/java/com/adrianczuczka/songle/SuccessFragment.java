package com.adrianczuczka.songle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by s1550570 on 04/11/17.
 */

public class SuccessFragment extends DialogFragment {
    int tries, markersFound, totalMarkers;
    long time, hours, minutes, seconds;
    String name;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Set<String> finishedSongsList;

    public static SuccessFragment newInstance(int tries, long time, String name, int markersFound, int totalMarkers) {
        SuccessFragment successFragment = new SuccessFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("markersFound", markersFound);
        args.putInt("totalMarkers", totalMarkers);
        args.putInt("tries", tries);
        args.putLong("time", time);
        successFragment.setArguments(args);
        return successFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        tries = args.getInt("tries");
        time = args.getLong("time");
        name = args.getString("name");
        markersFound = args.getInt("markersFound");
        totalMarkers = args.getInt("totalMarkers");
        hours = time / 3600000;
        minutes = (time % 3600000) / 60000;
        seconds = (time % 60000) / 1000;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.success_fragment, container, false);
        TextView congratsText = view.findViewById(R.id.congrats_text);
        TextView triesAmount = view.findViewById(R.id.tries_amount);
        TextView timeTaken = view.findViewById(R.id.time_taken);
        TextView markerAmount = view.findViewById(R.id.marker_amount);
        congratsText.setText("You guessed it! The song was " + name);
        triesAmount.setText("Attempts needed: " + tries);
        timeTaken.setText("Time taken: " + formatTime(time));
        markerAmount.setText("Markers found: " + markersFound + ", " + (totalMarkers / markersFound));
        return view;
    }

    private String formatTime(long millis) {
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        long seconds = (millis % 60000) / 1000;
        String hoursString, minutesString, secondsString;
        if (hours < 10) {
            hoursString = "0" + String.valueOf(hours);
        } else {
            hoursString = String.valueOf(hours);
        }
        if (minutes < 10) {
            minutesString = "0" + String.valueOf(minutes);
        } else {
            minutesString = String.valueOf(minutes);
        }
        if (seconds < 10) {
            secondsString = "0" + String.valueOf(seconds);
        } else {
            secondsString = String.valueOf(seconds);
        }
        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    public void onFinishClick(View view){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        editor = sharedPreferences.edit();
        finishedSongsList = sharedPreferences.getStringSet("finishedSongsList", new HashSet<String>());
        finishedSongsList.add(name);
        editor.putStringSet("finishedSongsList", finishedSongsList);
    }
}
