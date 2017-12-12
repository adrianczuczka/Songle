package com.adrianczuczka.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Activity for choosing the song the user will play. Can be random or from a list of already
 * guessed songs.
 */
public class ChooseSong extends AppCompatActivity {
    private final HashMap<String, XMLParser.Song> songList = new HashMap<>();
    private RecyclerView recyclerView;
    private static final int LOAD_KML_REQUEST = 1;
    private static final int LOAD_XML_REQUEST = 2;
    private static final int LOAD_LYRICS_REQUEST = 3;
    private final ArrayList<XMLParser.Song> notGuessedList = new ArrayList<>();
    private final ArrayList<XMLParser.Song> successList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_song);
        Button randomButton = findViewById(R.id.content_choose_song_random_button);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRandom();
            }
        });
        recyclerView = findViewById(R.id.content_choose_song_recycler_view);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext
                ());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent xmlIntent = new Intent(ChooseSong.this, NetworkActivity.class);
        xmlIntent.putExtra("url", "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs" +
                ".xml");
        startActivityForResult(xmlIntent, LOAD_XML_REQUEST);
    }

    private static class ParseXMLTask extends AsyncTask<String, Void, ArrayList<XMLParser.Song>> {

        @Override
        protected ArrayList<XMLParser.Song> doInBackground(String... strings) {
            try {
                InputStream stream = new ByteArrayInputStream(strings[0].getBytes
                        (StandardCharsets.UTF_8.name()));
                XMLParser parser = new XMLParser();
                return parser.parse(stream);
            } catch (XmlPullParserException | IOException e) {
                throw new Error("Could not parse file.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) throw new Error("Sorry, something went wrong :/");
        switch (requestCode) {
            case LOAD_XML_REQUEST:
                String xml = data.getStringExtra("string");
                ParseXMLTask parseXMLTask = new ParseXMLTask();
                try {
                    ArrayList<XMLParser.Song> songs = parseXMLTask.execute(xml).get();
                    for (XMLParser.Song song : songs) {
                        songList.put(song.Title, song);
                    }
                    for (String name : songList.keySet()) {
                        if (sharedPreferences.getStringSet("finishedSongsList", new
                                HashSet<String>())
                                .contains(name)) {
                            successList.add(songList.get(name));
                        } else {
                            notGuessedList.add(songList.get(name));
                        }
                    }
                    SongsAdapter mAdapter = new SongsAdapter(successList);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            case LOAD_KML_REQUEST:
                Intent lyricIntent = new Intent(ChooseSong.this, NetworkActivity.class);
                int difficulty = data.getIntExtra("difficulty", 0);
                String kml = data.getStringExtra("string");
                String number = data.getStringExtra("number");
                String title = data.getStringExtra("title");
                lyricIntent.putExtra("difficulty", difficulty);
                lyricIntent.putExtra("url", "http://www.inf.ed.ac" +
                        ".uk/teaching/courses/selp/data/songs/" + number + "/words.txt");
                lyricIntent.putExtra("kml", kml);
                lyricIntent.putExtra("title", title);
                startActivityForResult(lyricIntent, LOAD_LYRICS_REQUEST);
                break;
            case LOAD_LYRICS_REQUEST:
                Intent mapIntent = new Intent(ChooseSong.this, GameUI.class);
                int mapDifficulty = data.getIntExtra("difficulty", 0);
                String lyrics = data.getStringExtra("string");
                String mapKml = data.getStringExtra("kml");
                String mapTitle = data.getStringExtra("title");
                mapIntent.putExtra("difficulty", mapDifficulty);
                mapIntent.putExtra("lyrics", lyrics);
                mapIntent.putExtra("kml", mapKml);
                mapIntent.putExtra("title", mapTitle);
                startActivity(mapIntent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChooseSong.this, WelcomeScreen.class);
        startActivity(intent);
        finish();
    }

    /**
     * Listener for the list of already guessed songs. When a song is clicked, a
     * ChooseDifficultyFragment will be shown for the song.
     *
     * @param view The clicked song.
     */
    public void onClickSong(View view) {
        if (!isOnline()) {
            Toast toast = Toast.makeText(ChooseSong.this, "No Internet Connection", Toast
                    .LENGTH_LONG);
            toast.show();
        } else {
            TextView numberView = view.findViewById(R.id.Number);
            TextView titleView = view.findViewById(R.id.Title);
            Intent kmlIntent = new Intent(ChooseSong.this, NetworkActivity.class);
            kmlIntent.putExtra("url", "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" +
                    String.valueOf(numberView.getText()) + "/");
            kmlIntent.putExtra("number", String.valueOf(numberView.getText()));
            kmlIntent.putExtra("title", String.valueOf(titleView.getText()));
            DialogFragment chooseDifficultyFragment = ChooseDifficultyFragment.newInstance(kmlIntent);
            chooseDifficultyFragment.show(getSupportFragmentManager(), "hello");
        }
    }

    /**
     * Listener for the random button. When clicked, a random song will be picked from the list
     * of not yet guessed ones, then a ChooseDifficultyFragment will be shown for the song.
     */
    private void onClickRandom() {
        if (!isOnline()) {
            Toast toast = Toast.makeText(ChooseSong.this, "No Internet Connection", Toast
                    .LENGTH_LONG);
            toast.show();
        } else {
            int randomNum = ThreadLocalRandom.current().nextInt(0, notGuessedList.size());
            Intent kmlIntent = new Intent(ChooseSong.this, NetworkActivity.class);
            kmlIntent.putExtra("url", "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" +
                    notGuessedList.get(randomNum).Number + "/");
            kmlIntent.putExtra("number", notGuessedList.get(randomNum).Number);
            kmlIntent.putExtra("title", notGuessedList.get(randomNum).Title);
            DialogFragment chooseDifficultyFragment = ChooseDifficultyFragment.newInstance(kmlIntent);
            chooseDifficultyFragment.show(getSupportFragmentManager(), "hello");
        }
    }

    /**
     * Checks if the app has access to the Internet.
     *
     * @return True if connected, false if not.
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}

