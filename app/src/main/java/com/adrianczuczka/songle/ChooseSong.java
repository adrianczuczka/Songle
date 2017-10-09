package com.adrianczuczka.songle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChooseSong extends AppCompatActivity {
    private List<XMLParser.Song> songList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SongsAdapter mAdapter;
    static final int LOAD_XML_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_song);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //mAdapter = new SongsAdapter(songList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(mAdapter);
        Intent kmlIntent = new Intent(ChooseSong.this, NetworkActivity.class);
        kmlIntent.putExtra("url", "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml");
        startActivityForResult(kmlIntent, LOAD_XML_REQUEST);
    }

    private class ParseXMLTask extends AsyncTask<String, Void, ArrayList<XMLParser.Song>>{
        @Override
        protected ArrayList<XMLParser.Song> doInBackground(String... strings) {
            try {
                InputStream stream = new ByteArrayInputStream(strings[0].getBytes(StandardCharsets.UTF_8.name()));
                XMLParser parser = new XMLParser();
                return parser.parse(stream);
            }
            catch (XmlPullParserException | IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<XMLParser.Song> songs) {
            Log.e("GameUI", String.valueOf(songs.get(10).Number));
            Log.e("GameUI", String.valueOf(songs.get(10).Artist));
            Log.e("GameUI", String.valueOf(songs.get(10).Title));
            songList = songs;
            mAdapter = new SongsAdapter(songList);
            Log.e("GameUI", String.valueOf(songList));
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("GameUI", "made it to onActivityResult");
        if (requestCode == LOAD_XML_REQUEST) {
            if (resultCode == RESULT_OK) {
                String xml = data.getStringExtra("xmlString");
                new ParseXMLTask().execute(xml);
            }
        }
    }

    public void setSong(View view){
        TextView v = (TextView) findViewById(R.id.Title);
        Log.e("GameUI", String.valueOf(v.getText()));
    }
}

