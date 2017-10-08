package com.adrianczuczka.songle;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adria_000 on 07/10/2017.
 */
public class KMLParser {
    public class Placemark {
        public final String name;
        public final String description;
        public final double[] coordinates;

        private Placemark(String name, String description, double[] coordinates){
            this.name = name;
            this.description = description;
            this.coordinates = coordinates;
        }
    }
    private final String ns = null;

    public ArrayList<Placemark> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private ArrayList<Placemark> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Placemark> placemarks = new ArrayList<Placemark>();
        //Log.e("GameUI", "made it to readfeed");
        parser.require(XmlPullParser.START_TAG, ns, "kml");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("Placemark")) {
                placemarks.add(readPlacemark(parser));
            } else {
                skip(parser);
            }
        }
        return placemarks;
    }

    private Placemark readPlacemark(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Log.e("GameUI", "made it to readplacemark");
        parser.require(XmlPullParser.START_TAG, ns, "Placemark");
        String name = null;
        String description = null;
        double[] coordinates = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("name")) {
                name = readName(parser);
            } else if (tagName.equals("description")) {
                description = readDescription(parser);
            } else if (tagName.equals("Point")) {
                coordinates = readPoint(parser);
            } else {
                skip(parser);
            }
        }
        //Log.e("GameUI",name + description + String.valueOf(coordinates) );
        return new Placemark(name, description, coordinates);
    }

    private String readName(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return name;
    }

    private double[] readPoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Point");
        double[] coordinates = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("coordinates")){
                coordinates = readCoordinates(parser);
                break;
            }
        }
        parser.nextTag();
        return coordinates;
    }

    private double[] readCoordinates(XmlPullParser parser) throws XmlPullParserException, IOException {
        double[] result = new double[2];
        parser.require(XmlPullParser.START_TAG, ns, "coordinates");
        String coordinates = readText(parser);
        String[] stringResult = coordinates.split(",");
        for (int i = 0; i < result.length; i++){
            result[i] = Double.parseDouble(stringResult[i]);
        }
        parser.require(XmlPullParser.END_TAG, ns, "coordinates");
        return result;
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