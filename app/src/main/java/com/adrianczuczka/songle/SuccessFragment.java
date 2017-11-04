package com.adrianczuczka.songle;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by s1550570 on 04/11/17.
 */

public class SuccessFragment extends DialogFragment {
    int tries, time;

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
        timeTaken.setText("Time taken: " + time);
        return view;
    }
}
