package com.adrianczuczka.songle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by adria_000 on 27/10/2017.
 */

public class ChooseDifficultyFragment extends DialogFragment {
    private String lyrics,kml,title;
    public static ChooseDifficultyFragment newInstance(Intent mapIntent){
        ChooseDifficultyFragment fragment = new ChooseDifficultyFragment();
        Bundle args = mapIntent.getExtras();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        ArrayList<String> containsCheck = new ArrayList<>();
        containsCheck.add("lyrics");
        containsCheck.add("kml");
        containsCheck.add("title");
        if(args.keySet().containsAll(containsCheck)){
            lyrics = args.getString("lyrics");
            kml = args.getString("kml");
            title = args.getString("title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_difficulty_fragment, container, false);
        Button veryEasyButton = (Button) view.findViewById(R.id.very_easy_button);
        Button easyButton = (Button) view.findViewById(R.id.very_easy_button);
        Button mediumButton = (Button) view.findViewById(R.id.very_easy_button);
        Button hardButton = (Button) view.findViewById(R.id.very_easy_button);
        Button extremeButton = (Button) view.findViewById(R.id.very_easy_button);
        return view;
    }

    public void onClickDifficultyButton(View view){

    }
}
