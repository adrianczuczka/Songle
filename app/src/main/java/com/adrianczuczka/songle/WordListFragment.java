package com.adrianczuczka.songle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * {@link DialogFragment} that shows the list of words already found while playing a game.
 */
public class WordListFragment extends DialogFragment {
    private ArrayList<String> wordList = new ArrayList<>();

    public static WordListFragment newInstance(ArrayList<String> list) {
        WordListFragment wordListFragment = new WordListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("wordList", list);
        wordListFragment.setArguments(args);
        return wordListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordList = getArguments().getStringArrayList("wordList");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.word_list_fragment, container, false);
        GridView grid = view.findViewById(R.id.grid);
        WordListAdapter wordListAdapter = new WordListAdapter(getActivity(), wordList);
        grid.setAdapter(wordListAdapter);
        return view;
    }
}
