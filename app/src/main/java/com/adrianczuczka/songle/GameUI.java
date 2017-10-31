package com.adrianczuczka.songle;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class GameUI extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private final Looper looper = Looper.getMainLooper();
    private final LocationRequest mLocationRequest = new LocationRequest();
    private final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
    private boolean mRequestingLocationUpdates;
    private LocationCallback mLocationCallback;
    private static final int LOAD_KML_REQUEST = 1;
    private final HashMap<Marker, String> MarkerWordMap = new HashMap<>();
    private final HashMap<Marker, String> SuccessWordMap = new HashMap<>();
    private final ArrayList<String> SuccessList = new ArrayList<>();
    private final ArrayList<LatLng> latLngList = new ArrayList<>();
    private Boolean isHeatmap = false;
    private Boolean isMarkers = true;
    HeatmapTileProvider mProvider;
    TileOverlay mOverlay;

    private void startLocationUpdates() {
        try {
            if (!mRequestingLocationUpdates) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, looper);
                mRequestingLocationUpdates = true;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void createLocationRequest(LocationRequest locReq) {
        locReq.setInterval(10000);
        locReq.setFastestInterval(5000);
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void mapReadyFunction() {
        try {
            mMap.setMyLocationEnabled(true);
            mRequestingLocationUpdates = false;
            /*mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                //startLocationUpdates();
                            }
                        }
                    });
            */
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                    String kml = getIntent().getStringExtra("kml");
                    new createKMLtask().execute(kml);
                    startLocationUpdates();
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int REQUEST_CHECK_SETTINGS = 1;
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(GameUI.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });
            /*Intent kmlIntent = new Intent(GameUI.this, NetworkActivity.class);
            kmlIntent.putExtra("url", "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/01/map1.kml");
            startActivityForResult(kmlIntent, LOAD_KML_REQUEST);*/
        } catch (SecurityException e) {
            //warning
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ui);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*EXPERIMENTAL

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        EXPERIMENTAL*/

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest(mLocationRequest);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (!MarkerWordMap.isEmpty() && isMarkers) {
                    double minDistance = -1;
                    Marker minMarker = null;
                    for (Location location : locationResult.getLocations()) {
                        for (Marker marker : MarkerWordMap.keySet()) {
                            MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                            Location location1 = new Location("location");
                            Double latitude = marker.getPosition().latitude;
                            Double longitude = marker.getPosition().longitude;
                            location1.setLatitude(latitude);
                            location1.setLongitude(longitude);
                            double locDistance = Double.parseDouble(String.valueOf(location.distanceTo(location1)));
                            if (locDistance < 50) {
                                assert markerInfo != null;
                                if (!markerInfo.isGreen) {
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.success_marker));
                                    markerInfo.isGreen = true;
                                }
                            } else {
                                assert markerInfo != null;
                                if (markerInfo.isGreen) {
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(getMarkerStyle(markerInfo)));
                                    markerInfo.isGreen = false;
                                }
                            }
                            if (minDistance < 0) {
                                minDistance = locDistance;
                                minMarker = marker;
                            } else if (minDistance > locDistance) {
                                minDistance = locDistance;
                                minMarker = marker;
                            }
                        }
                    }
                }
            }
        };
        LinearLayout view = (LinearLayout) findViewById(R.id.game_ui_bottom_sheet);
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(view);
        mBottomSheetBehavior.setPeekHeight(144);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Button showList = (Button) findViewById(R.id.show_list);
        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment wordListFragment = WordListFragment.newInstance(SuccessList);
                wordListFragment.show(getSupportFragmentManager(), "hello");
            }
        });
        Button guessSong = (Button) findViewById(R.id.guess_song);
        guessSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText answerInput = (EditText) findViewById(R.id.guess_song_input);
                String answer = answerInput.getText().toString();
                String title = getIntent().getStringExtra("title");
                Log.e("levDistance", String.valueOf(levDistance(answer, title)));
                if (levDistance(answer, title) <= 2) {
                    Log.e("success", "SUCCESS");
                } else {
                    Log.e("incorrect", "still success");
                }
            }
        });

        /*
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("Lyrics"));
        tabLayout.addTab(tabLayout.newTab().setText("Guess"));
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));*/
                /*
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
                */
    }

    private int levDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    /*
    private void setupViewPager(ViewPager viewPager) {
        for(int i= 0; i < 100; i++){
            test.add("hello");
        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new WordListFragment().newInstance(test), "Test");
        viewPagerAdapter.addFragment(new WordListFragment().newInstance(test), "Test");
        viewPagerAdapter.addFragment(new WordListFragment().newInstance(test), "Test");
        /*viewPagerAdapter.addFragment(new WorldCharts(), "World Charts");
        viewPagerAdapter.addFragment(new NewMusic(), "New Music");
        viewPagerAdapter.addFragment(new AfricaHot(), "Africa Hot");
        viewPagerAdapter.addFragment(new Playlists(), "Playlists");
        viewPagerAdapter.addFragment(new Recommended(), "Recommended");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        public void addFragment(Fragment fragment, String name) {
            fragmentList.add(fragment);
            fragmentTitles.add(name);
        }
    }
    */
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mRequestingLocationUpdates = false;
    }
    /*
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    */

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //show explanation
            } else {*/
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            //}
        } else {
            mapReadyFunction();
        }
        LatLng northWestLatLng = new LatLng(55.946233, -3.192473);
        LatLng northEastLatLng = new LatLng(55.946233, -3.184319);
        LatLng southEastLatLng = new LatLng(55.942617, -3.184319);
        LatLng southWestLatLng = new LatLng(55.942617, -3.192473);
        LatLng centralLatLng = new LatLng(55.944425, -3.188396);
        CameraPosition central = new CameraPosition(centralLatLng, 15, 0, 0);
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .add(northEastLatLng, northWestLatLng, southWestLatLng, southEastLatLng, northEastLatLng)
                .width(5)
                .color(Color.RED));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(central));
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                SuccessList.add(MarkerWordMap.get(marker));
                assert markerInfo != null;
                if (markerInfo.isGreen) {
                    //success!
                    SuccessWordMap.put(marker, MarkerWordMap.get(marker));
                    SuccessList.add(MarkerWordMap.get(marker));
                    MarkerWordMap.remove(marker);
                    marker.remove();
                }
                return false;
            }
        });
        ImageButton heatmapButton = (ImageButton) findViewById(R.id.heatmap_button);
        heatmapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isHeatmap) {
                    isHeatmap = true;
                    mProvider = new HeatmapTileProvider.Builder()
                            .data(latLngList)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                }
                else {
                    isHeatmap = false;
                    mOverlay.remove();
                }
            }
        });
        ImageButton markerButton = (ImageButton) findViewById(R.id.marker_button);
        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isMarkers) {
                    isMarkers = true;
                    for (Marker marker : MarkerWordMap.keySet()) {
                        marker.setVisible(true);
                    }
                }
                else {
                    isMarkers = false;
                    for (Marker marker : MarkerWordMap.keySet()) {
                        marker.setVisible(false);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (permissions.length == 1 &&
                        permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapReadyFunction();
                } else {
                    //permission denied
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    //EXPERIMENTAL

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOAD_KML_REQUEST) {
            if (resultCode == RESULT_OK) {
                String kml = data.getStringExtra("kml");
                new createKMLtask().execute(kml);
            }
        }
    }

    private class createKMLtask extends AsyncTask<String, Void, KmlLayer> {
        @Override
        protected KmlLayer doInBackground(String... params) {
            try {
                InputStream stream = new ByteArrayInputStream(params[0].getBytes(StandardCharsets.UTF_8.name()));
                return new KmlLayer(mMap, stream, GameUI.this);
            } catch (XmlPullParserException | IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(KmlLayer kmlLayer) {
            try {
                kmlLayer.addLayerToMap();
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            for (KmlContainer containers : kmlLayer.getContainers()) {
                for (KmlPlacemark placemark : containers.getPlacemarks()) {
                    if (placemark.getGeometry().getGeometryType().equals("Point")) {
                        KmlPoint point = (KmlPoint) placemark.getGeometry();
                        LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                        latLngList.add(latLng);
                        Marker marker;
                        switch (placemark.getStyleId()) {
                            case "#unclassified":
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.white_blank))
                                        .title("unclassified"));
                                marker.setTag(new MarkerInfo("unclassified"));
                                MarkerWordMap.put(marker, findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                            case "#boring":
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.yellow_blank))
                                        .title("boring"));
                                marker.setTag(new MarkerInfo("boring"));
                                MarkerWordMap.put(marker, findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                            case "#notboring":
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.yellow_circle))
                                        .title("not boring"));
                                marker.setTag(new MarkerInfo("not boring"));
                                MarkerWordMap.put(marker, findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                            case "#interesting":
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.orange_diamond))
                                        .title("interesting"));
                                marker.setTag(new MarkerInfo("interesting"));
                                MarkerWordMap.put(marker, findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                            case "#veryinteresting":
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.red_stars))
                                        .title("very interesting"));
                                marker.setTag(new MarkerInfo("very interesting"));
                                MarkerWordMap.put(marker, findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                        }
                    }
                }
            }
            kmlLayer.removeLayerFromMap();
        }
    }

    private String findLyric(String lyrics, String wordLoc) {
        String[] coordinates = wordLoc.split(":");
        String result;
        String[] lines = lyrics.split("[0-9]+\t");
        result = lines[Integer.parseInt(coordinates[0])].split("\\s")[Integer.parseInt(coordinates[1]) - 1];
        return result;
    }

    private int getMarkerStyle(MarkerInfo markerInfo) {
        String key = markerInfo.key;
        int result = 0;
        switch (key) {
            case "unclassified":
                result = R.mipmap.white_blank;
                break;
            case "boring":
                result = R.mipmap.yellow_blank;
                break;
            case "not boring":
                result = R.mipmap.yellow_circle;
                break;
            case "interesting":
                result = R.mipmap.orange_diamond;
                break;
            case "very interesting":
                result = R.mipmap.red_stars;
                break;
        }
        return result;
    }

    public class MarkerInfo {
        private final String key;
        private boolean isGreen;

        MarkerInfo(String key) {
            this.key = key;
            this.isGreen = false;
        }
    }

    /*
    private class parseXMLTask extends AsyncTask<String, Void, ArrayList<KMLParser.Placemark>> {
        @Override
        protected ArrayList<KMLParser.Placemark> doInBackground(String... params) {
            try {
                InputStream stream = new ByteArrayInputStream(params[0].getBytes(StandardCharsets.UTF_8.name()));
                KMLParser parser = new KMLParser();
                KmlLayer layer = new KmlLayer(mMap, stream, GameUI.this);
                return parser.parse(stream);
            } catch (XmlPullParserException | IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<KMLParser.Placemark> list) {
            IconGenerator unclassified = new IconGenerator(GameUI.this);
            unclassified.setStyle(IconGenerator.STYLE_WHITE);
            IconGenerator boring = new IconGenerator(GameUI.this);
            boring.setStyle();
            for (int i = 0; i < list.size(); i++) {
                KMLParser.Placemark placemark = list.get(i);
                switch (list.get(i).description) {
                    case "unclassified":
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(placemark.coordinates[1], placemark.coordinates[0]))
                                .title(placemark.name))
                                .setIcon(BitmapDescriptorFactory.fromBitmap(unclassified.makeIcon()));
                        break;
                    case "boring":
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(placemark.coordinates[1], placemark.coordinates[0]))
                                .title(placemark.name))
                                .setIcon(BitmapDescriptorFactory.defaultMarker());
                        break;
                    case "notboring":
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(placemark.coordinates[1], placemark.coordinates[0]))
                                .title(placemark.name))
                                .setIcon(BitmapDescriptorFactory.defaultMarker());
                }
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(placemark.coordinates[1], placemark.coordinates[0]))
                        .title(placemark.name));

            }
        }
    }*/
}
