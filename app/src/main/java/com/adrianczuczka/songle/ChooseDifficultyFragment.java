package com.adrianczuczka.songle;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Fragment where the user can choose the difficulty of the song he wishes to play.
 */
public class ChooseDifficultyFragment extends DialogFragment {

    private static final int LOAD_KML_REQUEST = 1;
    private String url, number, title;

    /**
     * Method for creating new instance.
     *
     * @return A new ChooseDifficultyFragment.
     */
    public static ChooseDifficultyFragment newInstance(Intent kmlIntent) {
        ChooseDifficultyFragment fragment = new ChooseDifficultyFragment();
        Bundle args = kmlIntent.getExtras();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        ArrayList<String> containsCheck = new ArrayList<>();
        containsCheck.add("url");
        containsCheck.add("number");
        containsCheck.add("title");
        if(args.keySet().containsAll(containsCheck)){
            url = args.getString("url");
            number = args.getString("number");
            title = args.getString("title");
        }
        if(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("set_extreme_mode_switch", false)){
            Intent kmlIntent = new Intent(getActivity(), NetworkActivity.class);
            kmlIntent.putExtra("url", url + "map1.kml");
            kmlIntent.putExtra("number", number);
            kmlIntent.putExtra("title", title);
            getActivity().startActivityForResult(kmlIntent, LOAD_KML_REQUEST);
            dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.choose_difficulty_fragment, container, false);
        Button veryEasyButton = view.findViewById(R.id.very_easy_button);
        Button easyButton = view.findViewById(R.id.easy_button);
        Button mediumButton = view.findViewById(R.id.medium_button);
        Button hardButton = view.findViewById(R.id.hard_button);
        Button extremeButton = view.findViewById(R.id.extreme_button);
        setOnClick(veryEasyButton, 5);
        setOnClick(easyButton, 4);
        setOnClick(mediumButton, 3);
        setOnClick(hardButton, 2);
        setOnClick(extremeButton, 1);
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int width = displaymetrics.widthPixels;
        view.setMinimumWidth(width - 200);
        return view;
    }

    private void setOnClick(Button button, final int num) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kmlIntent = new Intent(getActivity(), NetworkActivity.class);
                kmlIntent.putExtra("url", url + "map" + num + ".kml");
                kmlIntent.putExtra("number", number);
                kmlIntent.putExtra("title", title);
                getActivity().startActivityForResult(kmlIntent, LOAD_KML_REQUEST);
                dismiss();
            }
        });
    }

}
