package com.adrianczuczka.songle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by s1550570 on 04/11/17.
 */

public class SuccessFragment extends DialogFragment {
    int tries;
    long time, hours, minutes, seconds;

    public static SuccessFragment newInstance(int tries, long time) {
        SuccessFragment successFragment = new SuccessFragment();
        Bundle args = new Bundle();
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
        congratsText.setText("You finished this map!");
        triesAmount.setText("Attempts needed: " + tries);
        timeTaken.setText("Time taken: " + formatTime(time));
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
}
