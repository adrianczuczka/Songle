package com.adrianczuczka.songle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChooseSong extends AppCompatActivity {
    private ArrayList<XMLParser.Song> songList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SongsAdapter mAdapter;
    static final int LOAD_KML_REQUEST = 1;
    static final int LOAD_XML_REQUEST = 2;
    static final int LOAD_LYRICS_REQUEST = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_song);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    private class parseXMLTask extends AsyncTask<String, Void, ArrayList<XMLParser.Song>> {
        @Override
        protected ArrayList<XMLParser.Song> doInBackground(String... strings) {
            try {
                InputStream stream = new ByteArrayInputStream(strings[0].getBytes(StandardCharsets.UTF_8.name()));
                XMLParser parser = new XMLParser();
                return parser.parse(stream);
            } catch (XmlPullParserException | IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<XMLParser.Song> songs) {
            songList = songs;
            mAdapter = new SongsAdapter(songList);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOAD_XML_REQUEST) {
            if (resultCode == RESULT_OK) {
                String xml = data.getStringExtra("string");
                new parseXMLTask().execute(xml);
            }
        }
        else if(requestCode == LOAD_KML_REQUEST){
            if (resultCode == RESULT_OK){
                Intent lyricIntent = new Intent(ChooseSong.this, NetworkActivity.class);
                String kml = data.getStringExtra("string");
                String number = data.getStringExtra("number");
                lyricIntent.putExtra("url", "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + number + "/words.txt");
                lyricIntent.putExtra("kml", kml);
                startActivityForResult(lyricIntent, LOAD_LYRICS_REQUEST);
            }
        }
        else if (requestCode == LOAD_LYRICS_REQUEST){
            if (resultCode == RESULT_OK){
                Intent mapIntent = new Intent(ChooseSong.this, GameUI.class);
                String lyrics = data.getStringExtra("string");
                String kml = data.getStringExtra("kml");
                mapIntent.putExtra("lyrics", lyrics);
                mapIntent.putExtra("kml", kml);
                startActivity(mapIntent);
            }
        }
    }

    public void onClickSong(View view) {
        TextView numberView = (TextView) view.findViewById(R.id.Number);
        TextView artistView = (TextView) view.findViewById(R.id.Artist);
        TextView titleView = (TextView) view.findViewById(R.id.Title);
        Intent kmlIntent = new Intent(ChooseSong.this, NetworkActivity.class);
        kmlIntent.putExtra("url", "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + String.valueOf(numberView.getText()) + "/map3.kml");
        kmlIntent.putExtra("number", String.valueOf(numberView.getText()));
        startActivityForResult(kmlIntent, LOAD_KML_REQUEST);
    }

    public void onClickRandom(View view) {
        int randomNum = ThreadLocalRandom.current().nextInt(0, songList.size() + 1);
        TextView textView = (TextView) recyclerView.findViewHolderForLayoutPosition(randomNum).itemView.findViewById(R.id.Title);
        recyclerView.findViewHolderForAdapterPosition(randomNum).itemView.callOnClick();
    }
}

