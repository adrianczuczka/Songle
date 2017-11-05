package com.adrianczuczka.songle;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by s1550570 on 04/11/17.
 */

public class SuccessFragment extends DialogFragment {
    int tries, time, hours, minutes, seconds;

    public static SuccessFragment newInstance(int tries, int time) {
        SuccessFragment successFragment = new SuccessFragment();
        Bundle args = new Bundle();
        args.putInt("tries", tries);
        args.putInt("time", time);
        successFragment.setArguments(args);
        return successFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        tries = args.getInt("tries");
        time = args.getInt("time");
        Log.e("time", String.valueOf(time));
        hours = time / 3600000;
        Log.e("hours", String.valueOf(hours));
        minutes = (time % 3600000) / 60000;
        Log.e("minutes", String.valueOf(minutes));
        seconds = (time % 60000) / 1000;
        Log.e("seconds", String.valueOf(seconds));
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
        timeTaken.setText("Time taken: " + format(hours) + ":" + format(minutes) + ":" + format(seconds));
        return view;
    }

    private String format(int amount) {
        if (amount < 10) {
            return "0" + String.valueOf(amount);
        } else {
            return String.valueOf(amount);
        }
    }
}
