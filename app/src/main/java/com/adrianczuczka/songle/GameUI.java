package com.adrianczuczka.songle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Songle's main activity. Shows the map with markers on it. Also has a bottom sheet containing a
 * "show list of markers found" button, an input
 * field where the user can type in the song's name, and a confirm button that lets the user
 * guess the song based on the input field.
 */
public class GameUI extends AppCompatActivity implements OnMapReadyCallback {
    private static final Looper looper = Looper.getMainLooper();
    private final LocationRequest mLocationRequest = new LocationRequest();
    private final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
    private final HashMap<Marker, String> markerWordMap = new HashMap<>();
    private final ArrayList<String> successList = new ArrayList<>();
    private final ArrayList<LatLng> latLngList = new ArrayList<>();
    private final ArrayList<TimerMarkerWrapper> timeMarkerWrapperList = new ArrayList<>();
    private final ArrayList<Marker> timeMarkerList = new ArrayList<>();
    private HashMap<String, String> resumedSettingsMap = new HashMap<>();
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private int tries = 0;
    private boolean isTries;
    private int maxTries;
    private long timeStarted;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private boolean mRequestingLocationUpdates;
    private LocationCallback mLocationCallback;
    private Boolean isHeatmap = false;
    private Boolean isMarkers = true;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private CountDownTimer normalCountdownTimer;
    private int timerAmountResumed;
    private TextView timerView;
    private int difficulty;
    private static final LatLng defaultNorthEast = new LatLng(55.946233, - 3.184319);
    private static final LatLng defaultSouthWest = new LatLng(55.942617, - 3.192473);
    private static final LatLng defaultCentral = new LatLng(55.944425, - 3.188396);
    private static final double northDiff = defaultNorthEast.longitude - defaultCentral.longitude;
    private static final double eastDiff = defaultNorthEast.latitude - defaultCentral.latitude;
    private static final double southDiff = defaultSouthWest.longitude - defaultCentral.longitude;
    private static final double westDiff = defaultSouthWest.latitude - defaultCentral.latitude;
    private LatLng northEastLatLng;
    private LatLng southWestLatLng;
    private LatLng centralLatLng;
    private static final Gson gson = new Gson();
    private Boolean isResumed;
    private Boolean isExtremeMode;
    private Boolean isTimerBackground;
    private Boolean isTimer;
    private Boolean isSetLocation;
    private int timerAmount;
    private Location startLocation;

    private void startLocationUpdates() {
        try{
            if(! mRequestingLocationUpdates){
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, looper);
                mRequestingLocationUpdates = true;
            }
        } catch(SecurityException e){
            e.printStackTrace();
        }
    }

    private void createLocationRequest(LocationRequest locReq) {
        locReq.setInterval(10000);
        locReq.setFastestInterval(5000);
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void mapReadyFunction() {
        try{
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMyLocationEnabled(true);
            mRequestingLocationUpdates = false;
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if(location != null){
                                startLocation = location;
                                if(isSetLocation){
                                    centralLatLng = new LatLng(startLocation.getLatitude(),
                                            startLocation.getLongitude());
                                } else{
                                    centralLatLng = defaultCentral;
                                }
                                northEastLatLng = new LatLng(centralLatLng.latitude + eastDiff,
                                        centralLatLng.longitude + northDiff);
                                southWestLatLng = new LatLng(centralLatLng.latitude + westDiff,
                                        centralLatLng.longitude + southDiff);
                                CameraPosition central = new CameraPosition(centralLatLng, 15, 0,
                                        0);
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(central));
                            }
                        }
                    });
            task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                    String kml = getIntent().getStringExtra("kml");
                    new createKMLtask(mMap, GameUI.this, latLngList, successList, markerWordMap,
                            getIntent().getStringExtra("lyrics"), difficulty,
                            timeMarkerWrapperList, isResumed, timeMarkerList, northEastLatLng,
                            southWestLatLng, centralLatLng, isSetLocation).execute(kml);

                    startLocationUpdates();
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int REQUEST_CHECK_SETTINGS = 1;
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch(statusCode){
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try{
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(GameUI.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch(IntentSender.SendIntentException sendEx){
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
            if(isResumed){
                Log.e("hello", "hello");
                resumeSettings();
            } else{
                setSettings();
            }
        } catch(SecurityException e){
            //warning
        }
    }

    public void resumeSettings() {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        resumedSettingsMap = gson.fromJson(sharedPreferences.getString("resumed_settings_map",
                null), type);
        String mapType = resumedSettingsMap.get("set_timer_switch");
        switch(mapType){
            case "1":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "0":
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "-1":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        isTimerBackground = Boolean.valueOf(resumedSettingsMap.get("set_timer_background"));
        isExtremeMode = Boolean.valueOf(resumedSettingsMap.get("set_extreme_mode_switch"));
        isTimer = Boolean.valueOf(resumedSettingsMap.get("set_timer_switch"));
        isSetLocation = Boolean.valueOf(resumedSettingsMap.get("set_location_switch"));
        if(isExtremeMode){
            isTries = true;
            maxTries = 1;
            timerView.setVisibility(View.VISIBLE);
            timerAmountResumed = sharedPreferences.getInt("set_timer_amount_resumed", - 1);
            normalCountdownTimer = startTimer(timerAmountResumed);
        } else{
            isTries = sharedPreferences.getBoolean("set_try_switch", false);
            maxTries = Integer.valueOf(sharedPreferences.getString("set_try_amount", "5"));
            timerAmount = sharedPreferences.getInt("set_timer_amount_resumed", 1800000);
            if(isTimer){
                timerView.setVisibility(View.VISIBLE);
                normalCountdownTimer = startTimer(timerAmount);
            }
        }
    }

    public void setSettings() {
        String mapType = sharedPreferences.getString("set_map_type_list", "1");
        switch(mapType){
            case "1":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "0":
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "-1":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        isSetLocation = sharedPreferences.getBoolean("set_location_switch", false);
        resumedSettingsMap.put("set_location_switch", String.valueOf(isSetLocation));
        isTimerBackground = sharedPreferences.getBoolean("set_timer_background", false);
        isExtremeMode = sharedPreferences.getBoolean("set_extreme_mode_switch", false);
        isTimer = sharedPreferences.getBoolean("set_timer_switch", false);
        if(isExtremeMode){
            isTries = true;
            maxTries = 1;
            timerView.setVisibility(View.VISIBLE);
            timerAmount = isResumed ? sharedPreferences.getInt
                    ("set_timer_amount_resumed", 900000) : 900000;
            normalCountdownTimer = startTimer(timerAmount);
        } else{
            isTries = sharedPreferences.getBoolean("set_try_switch", false);
            maxTries = Integer.valueOf(sharedPreferences.getString("set_try_amount", "5"));
            timerAmount = getIntent().hasExtra("resumed") ? sharedPreferences.getInt
                    ("set_timer_amount_resumed", 1800000) : sharedPreferences
                    .getInt("set_timer_amount", 1800000);
            if(isTimer){
                timerView.setVisibility(View.VISIBLE);
                normalCountdownTimer = startTimer(timerAmount);
            }
        }
        resumedSettingsMap.put("set_map_type_list", mapType);
        resumedSettingsMap.put("set_extreme_mode_switch", String.valueOf(isExtremeMode));
        resumedSettingsMap.put("set_try_switch", String.valueOf(isTries));
        resumedSettingsMap.put("set_try_amount", String.valueOf(maxTries));
        resumedSettingsMap.put("set_timer_switch", String.valueOf(isTimer));
        resumedSettingsMap.put("set_timer_amount", String.valueOf(timerAmount));
        resumedSettingsMap.put("set_timer_background", String.valueOf(isTimerBackground));
        editor.putString("resumed_settings_map", gson.toJson(resumedSettingsMap));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ui);
        timerView = findViewById(R.id.game_ui_timer_view);
        isResumed = getIntent().getBooleanExtra("resumed", false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        editor.putString("lyrics", getIntent().getStringExtra("lyrics"));
        editor.putString("kml", getIntent().getStringExtra("kml"));
        editor.putString("title", getIntent().getStringExtra("title"));
        editor.putInt("difficulty", getIntent().getIntExtra("difficulty", 0));
        ArrayList<String> tempList1 = isResumed ? gson.fromJson
                (sharedPreferences.getString("successList",
                        null), ArrayList.class) : new ArrayList<String>();
        successList.addAll(tempList1);
        Type listType = new TypeToken<ArrayList<TimerMarkerWrapper>>() {
        }.getType();
        ArrayList<TimerMarkerWrapper> tempList2 = isResumed ? (ArrayList<TimerMarkerWrapper>)
                gson.fromJson
                        (sharedPreferences.getString("timeMarkerWrapperList",
                                null), listType) : new ArrayList<TimerMarkerWrapper>();
        timeMarkerWrapperList.addAll(tempList2);
        editor.putString("successList", gson.toJson(successList));
        editor.apply();
        difficulty = sharedPreferences.getInt("difficulty", 0);
        timeStarted = new Date().getTime();
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest(mLocationRequest);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(! markerWordMap.isEmpty() && isMarkers){
                    for(Location location : locationResult.getLocations()){
                        for(Marker marker : markerWordMap.keySet()){
                            MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                            Location location1 = new Location("location");
                            Double latitude = marker.getPosition().latitude;
                            Double longitude = marker.getPosition().longitude;
                            location1.setLatitude(latitude);
                            location1.setLongitude(longitude);
                            double locDistance = Double.parseDouble(String.valueOf(location
                                    .distanceTo(location1)));
                            if(locDistance < 40){
                                if(! markerInfo.getGreen()){
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap
                                            .success_marker));
                                    markerInfo.setGreen(true);
                                }
                            } else{
                                assert markerInfo != null;
                                if(markerInfo.getGreen()){
                                    marker.setIcon(BitmapDescriptorFactory.fromResource
                                            (getMarkerStyle(markerInfo)));
                                    markerInfo.setGreen(false);
                                }
                            }
                        }
                        for(Marker marker : timeMarkerList){
                            MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                            Location location1 = new Location("location");
                            Double latitude = marker.getPosition().latitude;
                            Double longitude = marker.getPosition().longitude;
                            location1.setLatitude(latitude);
                            location1.setLongitude(longitude);
                            double locDistance = Double.parseDouble(String.valueOf(location
                                    .distanceTo(location1)));
                            if(locDistance < 100){
                                if(! markerInfo.getGreen()){
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap
                                            .clock_in_range));
                                    markerInfo.setGreen(true);
                                }
                            } else{
                                assert markerInfo != null;
                                if(markerInfo.getGreen()){
                                    marker.setIcon(BitmapDescriptorFactory.fromResource
                                            (getMarkerStyle(markerInfo)));
                                    markerInfo.setGreen(false);
                                }
                            }
                        }
                    }
                }
            }
        };
        LinearLayout view = findViewById(R.id.game_ui_bottom_sheet);
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(view);
        mBottomSheetBehavior.setPeekHeight(125);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Button showList = findViewById(R.id.show_list);
        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordListFragment wordListFragment = WordListFragment.newInstance(successList);
                wordListFragment.show(getSupportFragmentManager(), "hello");
            }
        });
        Button guessSong = findViewById(R.id.guess_song);
        guessSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tries++;
                EditText answerInput = findViewById(R.id.guess_song_input);
                final TextView triesView = findViewById(R.id.game_ui_tries_amount);
                String answer = answerInput.getText().toString();
                String title = getIntent().getStringExtra("title");
                if(levDistance(answer, title) <= 2){
                    if(normalCountdownTimer != null){
                        normalCountdownTimer.cancel();
                    }
                    SuccessFragment successFragment = SuccessFragment.newInstance(tries, new
                            Date().getTime() - timeStarted, title, successList.size(), latLngList
                            .size());
                    successFragment.show(getSupportFragmentManager(), "success");
                } else{
                    if(isTries){
                        if(tries < maxTries){
                            triesView.setText(getResources().getString(R.string.attempts_left,
                                    (maxTries - tries)));
                        } else{
                            if(normalCountdownTimer != null){
                                normalCountdownTimer.cancel();
                            }
                            Intent intent = new Intent(GameUI.this, GameOverActivity.class);
                            intent.putExtra("tries", "tries");
                            startActivity(intent);
                        }
                    } else{
                        triesView.setVisibility(View.VISIBLE);
                        new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                triesView.setVisibility(View.GONE);
                            }
                        }.start();
                    }
                }
            }
        });
    }

    private CountDownTimer startTimer(int time) {
        return new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                timerAmountResumed = (int) l;
                timerView.setText(formatTime(l));
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(GameUI.this, GameOverActivity.class);
                intent.putExtra("timer", "timer");
                startActivity(intent);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Boolean timerBackground = sharedPreferences.getBoolean("set_timer_background", false);
        editor.putInt("difficulty", difficulty);
        if(normalCountdownTimer != null){
            editor.putInt("set_timer_amount_resumed", timerAmountResumed);
            if(! timerBackground){
                normalCountdownTimer.cancel();
            }
        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean timerBackground = sharedPreferences.getBoolean("set_timer_background", false);
        int amount = sharedPreferences.getInt("set_timer_amount_resumed", - 1);
        if(! timerBackground && amount != - 1 && normalCountdownTimer != null){
            normalCountdownTimer.cancel();
            normalCountdownTimer = startTimer(amount);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(GameUI.this, WelcomeScreen.class));
    }


    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mRequestingLocationUpdates = false;
    }

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
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //show explanation
            } else {*/
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            //}
        } else{
            mapReadyFunction();
        }
        TextView triesView = findViewById(R.id.game_ui_tries_amount);
        if(isTries){
            triesView.setText(getResources().getString(R.string.attempts_left, maxTries));
        } else{
            triesView.setVisibility(View.GONE);
            triesView.setText(getResources().getString(R.string.incorrect_try_again));
        }
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                if(markerInfo.isGreen){
                    if(markerInfo.getKey().equals("timer")){
                        normalCountdownTimer.cancel();
                        timerAmountResumed += (markerInfo.getMinutes() * 60000);
                        normalCountdownTimer = startTimer(timerAmountResumed);
                        List<TimerMarkerWrapper> toRemove = new ArrayList<>();
                        for(TimerMarkerWrapper tmw : timeMarkerWrapperList){
                            if(tmw.getLatLng().equals(marker.getPosition())){
                                toRemove.add(tmw);
                            }
                        }
                        timeMarkerWrapperList.removeAll(toRemove);
                        timeMarkerList.remove(marker);
                        editor.putString("timeMarkerWrapperList", gson.toJson
                                (timeMarkerWrapperList));
                        marker.remove();
                    } else{
                        successList.add(markerWordMap.get(marker));
                        editor.putString("successList", gson.toJson(successList));
                        markerWordMap.remove(marker);
                        marker.remove();
                    }
                }
                editor.commit();
                return false;
            }
        });
        ImageButton heatmapButton = findViewById(R.id.heatmap_button);
        heatmapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! isHeatmap){
                    isHeatmap = true;
                    mProvider = new HeatmapTileProvider.Builder()
                            .data(latLngList)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider
                            (mProvider));
                } else{
                    isHeatmap = false;
                    mOverlay.remove();
                }
            }
        });
        ImageButton markerButton = findViewById(R.id.marker_button);
        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! isMarkers){
                    isMarkers = true;
                    for(Marker marker : markerWordMap.keySet()){
                        marker.setVisible(true);
                    }
                } else{
                    isMarkers = false;
                    for(Marker marker : markerWordMap.keySet()){
                        marker.setVisible(false);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String permissions[], @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:{
                if(permissions.length == 1 &&
                        permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mapReadyFunction();
                } else{
                    Toast toast = Toast.makeText(GameUI.this, "Location permissions must be " +
                                    "given for Songle to work properly. Please enable this in " +
                                    "settings.",
                            Toast
                                    .LENGTH_LONG);
                    toast.show();
                    //permission denied
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private static void addTimerMarkers(int difficulty, HashMap<Marker, String>
            markerWordMap, GoogleMap mMap, ArrayList<TimerMarkerWrapper> timeMarkerWrapperList,
            ArrayList<Marker> timeMarkerList, LatLng northEastLatLng, LatLng southWestLatLng) {

        if(difficulty != 1 && difficulty != 0){
            //Set amount of time markers to 10% of actual markers;
            int amount = markerWordMap.keySet().size() / 10;
            for(int i = 0; i < amount; i++){
                Double gauss = (ThreadLocalRandom.current().nextGaussian() + difficulty);
                int gaussian = gauss.intValue();
                double latitude = ThreadLocalRandom.current().nextDouble(southWestLatLng.latitude,
                        northEastLatLng.latitude);
                double longitude = ThreadLocalRandom.current().nextDouble(southWestLatLng
                        .longitude, northEastLatLng.longitude);
                LatLng latLng = new LatLng(latitude, longitude);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.clock))
                        .title("more time!"));
                marker.setTag(new MarkerInfo("timer", gaussian));
                timeMarkerWrapperList.add(new TimerMarkerWrapper(marker));
                timeMarkerList.add(marker);
            }
        }
    }

    private static String findLyric(String lyrics, String wordLoc) {
        String[] coordinates = wordLoc.split(":");
        String result;
        String[] lines = lyrics.split("[0-9]+\t");
        result = lines[Integer.parseInt(coordinates[0])].split("\\s")[Integer.parseInt
                (coordinates[1]) - 1];
        return result;
    }

    private static class createKMLtask extends AsyncTask<String, Void, KmlLayer> {
        final GoogleMap mMap;
        final ArrayList<LatLng> latLngList;
        final ArrayList<String> successList;
        final HashMap<Marker, String> markerWordMap;
        final WeakReference<Context> contextWeakReference;
        final String lyrics;
        final int difficulty;
        final ArrayList<TimerMarkerWrapper> timeMarkerWrapperList;
        final Boolean isResumed;
        final ArrayList<Marker> timeMarkerList;
        final LatLng northEastLatLng, southWestLatLng;
        final LatLng centralLatLng;
        final Boolean isSetLocation;

        createKMLtask(GoogleMap mMap, Context context, ArrayList<LatLng> latLngList,
                ArrayList<String> successList, HashMap<Marker, String> markerWordMap, String
                lyrics, int difficulty, ArrayList<TimerMarkerWrapper> timeMarkerWrapperList,
                Boolean isResumed,
                ArrayList<Marker> timeMarkerList, LatLng northEastLatLng, LatLng southWestLatLng,
                LatLng centralLatLng, Boolean isSetLocation) {
            this.mMap = mMap;
            this.contextWeakReference = new WeakReference<>(context);
            this.latLngList = latLngList;
            this.successList = successList;
            this.markerWordMap = markerWordMap;
            this.lyrics = lyrics;
            this.difficulty = difficulty;
            this.timeMarkerWrapperList = timeMarkerWrapperList;
            this.isResumed = isResumed;
            this.timeMarkerList = timeMarkerList;
            this.northEastLatLng = northEastLatLng;
            this.southWestLatLng = southWestLatLng;
            this.centralLatLng = centralLatLng;
            this.isSetLocation = isSetLocation;
        }

        @Override
        protected KmlLayer doInBackground(String... params) {
            try{
                InputStream stream = new ByteArrayInputStream(params[0].getBytes(StandardCharsets
                        .UTF_8.name()));
                return new KmlLayer(mMap, stream, contextWeakReference.get());
            } catch(XmlPullParserException | IOException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(KmlLayer kmlLayer) {
            try{
                kmlLayer.addLayerToMap();
            } catch(IOException | XmlPullParserException e){
                e.printStackTrace();
            }
            for(KmlContainer containers : kmlLayer.getContainers()){
                for(KmlPlacemark placemark : containers.getPlacemarks()){
                    if(placemark.getGeometry().getGeometryType().equals("Point")){
                        KmlPoint point = (KmlPoint) placemark.getGeometry();
                        LatLng latLng;
                        Double latitude = point.getGeometryObject().latitude;
                        Double longitude = point.getGeometryObject().longitude;
                        if(isSetLocation){
                            double latDiff = centralLatLng.latitude - defaultCentral.latitude;
                            double longDiff = centralLatLng.longitude - defaultCentral.longitude;
                            latLng = new LatLng(latitude + latDiff, longitude + longDiff);
                        } else{
                            latLng = new LatLng(latitude, longitude);
                        }
                        latLngList.add(latLng);
                        String lyric = findLyric(lyrics, placemark.getProperty("name"));
                        if(! successList.contains(lyric)){
                            Marker marker;
                            switch(placemark.getStyleId()){
                                case "#unclassified":
                                    marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap
                                                    .white_blank))
                                            .title("unclassified"));
                                    marker.setTag(new MarkerInfo("unclassified"));
                                    markerWordMap.put(marker, lyric);
                                    break;
                                case "#boring":
                                    marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap
                                                    .yellow_blank))
                                            .title("boring"));
                                    marker.setTag(new MarkerInfo("boring"));
                                    markerWordMap.put(marker, lyric);
                                    break;
                                case "#notboring":
                                    marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap
                                                    .yellow_circle))
                                            .title("not boring"));
                                    marker.setTag(new MarkerInfo("not boring"));
                                    markerWordMap.put(marker, lyric);
                                    break;
                                case "#interesting":
                                    marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap
                                                    .orange_diamond))
                                            .title("interesting"));
                                    marker.setTag(new MarkerInfo("interesting"));
                                    markerWordMap.put(marker, lyric);
                                    break;
                                case "#veryinteresting":
                                    marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap
                                                    .red_stars))
                                            .title("very interesting"));
                                    marker.setTag(new MarkerInfo("very interesting"));
                                    markerWordMap.put(marker, lyric);
                                    break;
                            }
                        }
                    }
                }
            }
            Boolean isTimer = sharedPreferences.getBoolean("set_timer_switch", false);
            Boolean isExtremeMode = sharedPreferences.getBoolean("set_extreme_mode_switch", false);
            if(isTimer && ! isExtremeMode && ! isResumed){
                if(timeMarkerWrapperList.isEmpty()){
                    addTimerMarkers(difficulty, markerWordMap, mMap, timeMarkerWrapperList,
                            timeMarkerList, northEastLatLng, southWestLatLng);
                } else{
                    for(TimerMarkerWrapper marker : timeMarkerWrapperList){
                        marker.addMarker(mMap);
                        /*MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                        Marker tempMarker = mMap.addMarker(new MarkerOptions()
                                .position(marker.getPosition())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.clock))
                                .title("more time!"));
                        tempMarker.setTag(new MarkerInfo("timer", markerInfo.getMinutes()));*/
                    }
                }
            }
            editor.putString("timeMarkerWrapperList", gson.toJson(timeMarkerWrapperList));
            editor.commit();
            kmlLayer.removeLayerFromMap();
        }
    }

    private int levDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];
        for(int j = 0; j < costs.length; j++)
            costs[j] = j;
        for(int i = 1; i <= a.length(); i++){
            costs[0] = i;
            int nw = i - 1;
            for(int j = 1; j <= b.length(); j++){
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b
                        .charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }


    private String formatTime(long millis) {
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        long seconds = (millis % 60000) / 1000;
        String hoursString, minutesString, secondsString;
        if(hours < 10){
            hoursString = "0" + String.valueOf(hours);
        } else{
            hoursString = String.valueOf(hours);
        }
        if(minutes < 10){
            minutesString = "0" + String.valueOf(minutes);
        } else{
            minutesString = String.valueOf(minutes);
        }
        if(seconds < 10){
            secondsString = "0" + String.valueOf(seconds);
        } else{
            secondsString = String.valueOf(seconds);
        }
        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    private int getMarkerStyle(MarkerInfo markerInfo) {
        String key = markerInfo.key;
        int result = 0;
        switch(key){
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
            case "timer":
                result = R.mipmap.clock;
        }
        return result;
    }

    private static class MarkerInfo {
        private final String key;
        private boolean isGreen;
        private int minutes;

        MarkerInfo(String key) {
            this.key = key;
            this.isGreen = false;
            this.minutes = - 1;
        }

        MarkerInfo(String key, int minutes) {
            this.key = key;
            this.isGreen = false;
            this.minutes = minutes;
        }

        void setGreen(boolean green) {
            isGreen = green;
        }

        Boolean getGreen() {
            return isGreen;
        }

        String getKey() {
            return key;
        }

        int getMinutes() {
            return minutes;
        }
    }

    private static class TimerMarkerWrapper {
        private LatLng latLng;
        private String title;
        private MarkerInfo markerInfo;

        TimerMarkerWrapper(Marker marker) {
            this.latLng = marker.getPosition();
            this.title = marker.getTitle();
            this.markerInfo = (MarkerInfo) marker.getTag();
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public MarkerInfo getMarkerInfo() {
            return markerInfo;
        }

        public Marker addMarker(GoogleMap mMap) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.clock)));
            marker.setTag(new MarkerInfo("timer", markerInfo.getMinutes()));
            return marker;
        }
    }

}
