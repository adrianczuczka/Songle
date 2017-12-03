package com.adrianczuczka.songle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment for asking user whether he wants to start new game when a game already exists. Has a Proceed and a Go Back button.
 */
public class AreYouSureFragment extends DialogFragment {

    /**
     * Method for creating new instance.
     *
     * @return A new AreYouSureFragment.
     */
    public static AreYouSureFragment newInstance() {
        return new AreYouSureFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.are_you_sure_fragment, container, false);
        Button yesButton = view.findViewById(R.id.are_you_sure_yes_button);
        Button noButton = view.findViewById(R.id.are_you_sure_no_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onYesClick();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNoClick();
            }
        });
        return view;
    }

    /**
     * Proceed button listener. Erases existing game data, then closes this fragment and redirects to the choose song screen.
     *
     */
    private void onYesClick() {
        Intent intent = new Intent(getActivity(), ChooseSong.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getActivity().getApplicationContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("successList");
        editor.remove("title");
        editor.remove("kml");
        editor.remove("lyrics");
        editor.apply();
        startActivity(intent);
        getActivity().finish();
        dismiss();
    }

    /**
     * Go back button listener. Does nothing except close the fragment.
     *
     */
    private void onNoClick() {
        dismiss();
    }
}
