package com.adrianczuczka.songle;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by s1550570 on 08/10/17.
 */

public class XMLParser {
    public class Song {
        public final String Number;
        public final String Artist;
        public final String Title;
        public final String Link;

        private Song(String Number, String Artist, String Title, String Link) {
            this.Number = Number;
            this.Artist = Artist;
            this.Title = Title;
            this.Link = Link;
        }
    }

    private final String ns = null;

    public ArrayList<Song> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            //Log.e("GameUI", "made it to kmlparse");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<Song> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Song> songs = new ArrayList<Song>();
        //Log.e("GameUI", "made it to readfeed");
        parser.require(XmlPullParser.START_TAG, ns, "Songs");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("Song")) {
                songs.add(readSong(parser));
            } else {
                skip(parser);
            }
        }
        return songs;
    }

    private Song readSong(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.e("GameUI", "made it to readSong");
        parser.require(XmlPullParser.START_TAG, ns, "Song");
        String number = null;
        String artist = null;
        String title = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("Number")) {
                number = readNumber(parser);
            } else if (tagName.equals("Artist")) {
                artist = readArtist(parser);
            } else if (tagName.equals("Title")) {
                title = readTitle(parser);
            } else if (tagName.equals("Link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        //Log.e("GameUI",name + description + String.valueOf(coordinates) );
        return new Song(number, artist, title, link);
    }

    private String readNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Number");
        String number = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Number");
        return number;
    }

    private String readArtist(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Artist");
        String artist = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Artist");
        return artist;
    }

    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Title");
        return title;
    }

    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Link");
        return link;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
