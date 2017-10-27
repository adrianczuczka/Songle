package com.adrianczuczka.songle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adria_000 on 26/10/2017.
 */

public class WordListAdapter extends BaseAdapter{
    private final Context mContext;
    private final ArrayList<String> wordList;
    public WordListAdapter(Context context, ArrayList<String> wordList){
        this.mContext = context;
        this.wordList = wordList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
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
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(getItem(position));
        return dummyTextView;
    }
}
