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
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ChooseSong extends AppCompatActivity {
    private ArrayList<XMLParser.Song> songList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SongsAdapter mAdapter;
    static final int LOAD_KML_REQUEST = 1;
    static final int LOAD_XML_REQUEST = 2;

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

    private class ParseXMLTask extends AsyncTask<String, Void, ArrayList<XMLParser.Song>> {
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
                String xml = data.getStringExtra("xmlString");
                new ParseXMLTask().execute(xml);
            }
        }
        else if(requestCode == LOAD_KML_REQUEST){
            if (resultCode == RESULT_OK){
                String kml = data.getStringExtra("xmlString");
                Intent mapIntent = new Intent(ChooseSong.this, GameUI.class);
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
        kmlIntent.putExtra("url", "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + String.valueOf(numberView.getText()) + "/map1.kml");
        startActivityForResult(kmlIntent, LOAD_KML_REQUEST);
    }

    public void onClickRandom(View view) {
        TextView textView = (TextView) recyclerView.findViewHolderForLayoutPosition(1).itemView.findViewById(R.id.Number);
    }
}

