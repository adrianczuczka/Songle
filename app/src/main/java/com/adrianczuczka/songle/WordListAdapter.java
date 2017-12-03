package com.adrianczuczka.songle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for showing the list of words already found while playing a game.
 */
class WordListAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<String> wordList;

    public WordListAdapter(Context context, ArrayList<String> wordList) {
        this.mContext = context;
        this.wordList = wordList;
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public String getItem(int position) {
        return wordList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(mContext);
        view.setText(getItem(position));
        view.setTextSize(Float.valueOf("20"));
        view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return view;
    }
}
