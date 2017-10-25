package com.adrianczuczka.songle;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.widget.RelativeLayout;

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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

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
    private Looper looper = Looper.getMainLooper();
    private LocationRequest mLocationRequest = new LocationRequest();
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
    private boolean mRequestingLocationUpdates;
    private LocationCallback mLocationCallback;
    static final int LOAD_KML_REQUEST = 1;
    private BottomSheetBehavior mBottomSheetBehavior = null;
    private HashMap<Marker, String> MarkerWordMap = new HashMap<>();
    private ArrayList<Marker> MarkerList = new ArrayList<>();
    private String lyrics = null;

    private void startLocationUpdates() {
        try {
            if (!mRequestingLocationUpdates) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, looper);
                mRequestingLocationUpdates = true;
            }
        } catch (SecurityException e) {

        }
    }

    protected void createLocationRequest(LocationRequest locReq) {
        locReq.setInterval(10000);
        locReq.setFastestInterval(5000);
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void mapReadyFunction() {
        try {
            mMap.setMyLocationEnabled(true);
            mRequestingLocationUpdates = false;
            mFusedLocationClient.getLastLocation()
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest(mLocationRequest);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (!MarkerList.isEmpty()) {
                    double minDistance = -1;
                    Marker minMarker = null;
                    for (Location location : locationResult.getLocations()) {
                        for (Marker marker : MarkerList) {
                            Location location1 = new Location("location");
                            Double latitude = marker.getPosition().latitude;
                            Double longitude = marker.getPosition().longitude;
                            location1.setLatitude(latitude);
                            location1.setLongitude(longitude);
                            double locDistance = Double.parseDouble(String.valueOf(location.distanceTo(location1)));
                            if (minDistance < 0) {
                                minDistance = locDistance;
                                minMarker = marker;
                            } else {
                                if (minDistance <= locDistance) {
                                } else {
                                    minDistance = locDistance;
                                    minMarker = marker;
                                }
                            }
                        }
                    }
                    Log.e("distance", String.valueOf(minDistance));
                }
            }
        };
        RelativeLayout view = (RelativeLayout) findViewById(R.id.pullup);
        mBottomSheetBehavior = BottomSheetBehavior.from(view);
        mBottomSheetBehavior.setPeekHeight(200);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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

    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
                KmlLayer layer = new KmlLayer(mMap, stream, GameUI.this);
                return layer;
            } catch (XmlPullParserException | IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(KmlLayer kmlLayer) {
            try {
                kmlLayer.addLayerToMap();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            for (KmlContainer containers : kmlLayer.getContainers()) {
                for (KmlPlacemark placemark : containers.getPlacemarks()) {
                    if (placemark.getGeometry().getGeometryType().equals("Point")) {
                        Log.e("style", String.valueOf(placemark.getStyleId()));
                        KmlPoint point = (KmlPoint) placemark.getGeometry();
                        LatLng latLng = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                        switch (placemark.getStyleId()) {
                            case "#unclassified":
                                MarkerWordMap.put(mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.white_blank))
                                        .title("unclassified")), findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                            case "#boring":
                                MarkerWordMap.put(mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.yellow_blank))
                                        .title("boring")), findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                            case "#notboring":
                                MarkerWordMap.put(mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.yellow_circle))
                                        .title("not boring")), findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                            case "#interesting":
                                MarkerWordMap.put(mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.orange_diamond))
                                        .title("interesting")), findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                            case "#veryinteresting":
                                MarkerWordMap.put(mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.red_stars))
                                        .title("very interesting")), findLyric(getIntent().getStringExtra("lyrics"), placemark.getProperty("name")));
                                break;
                        }
                    }
                }
            }
            kmlLayer.removeLayerFromMap();
        }
    }

    public String findLyric(String lyrics, String wordLoc) {
        String[] coordinates = wordLoc.split(":");
        String result = null;
        String[] lines = lyrics.split("[0-9]+\t");
        result = lines[Integer.parseInt(coordinates[0])].split("\\s")[Integer.parseInt(coordinates[1]) - 1];
        Log.e("result", result);
        return result;
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
