package com.adrianczuczka.songle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.data.kml.KmlLayer;

public class GameUI extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    /* EXPERIMENTAL
    private LocationRequest mLocationRequest;
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
    //SettingsClient client = LocationServices.getSettingsClient(this);
    //Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
    private boolean mRequestingLocationUpdates = true;
    private LocationCallback mLocationCallback;
    private LooperThread looper = new LooperThread();
    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        try{
             Experimental
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null looper);

        }
        catch (SecurityException e){

        }
    }
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ui);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*Experimental
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
                */
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
        Log.e("GameUI", "hello");
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
        }
        else{
            Log.e("GameUI", "hello5");
            try{
                mMap.setMyLocationEnabled(true);
                Log.e("GameUI", String.valueOf(mMap.isMyLocationEnabled()));
            }
            catch (SecurityException e){
                //warning
            }
            /* Experimental
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                startLocationUpdates();
                            }
                        }
                    });
                   */
        }
        LatLng northWestLatLng = new LatLng(55.946233, -3.192473);
        LatLng northEastLatLng = new LatLng(55.946233, -3.184319);
        LatLng southEastLatLng = new LatLng(55.942617, -3.184319);
        LatLng southWestLatLng = new LatLng(55.942617, -3.192473);
        LatLng centralLatLng = new LatLng(55.944425, -3.188396);
        CameraPosition central = new CameraPosition(centralLatLng, 15, 0, 0);
        mMap.addMarker(new MarkerOptions().position(northWestLatLng).title("Marker in Forrest Hill"));
        mMap.addMarker(new MarkerOptions().position(northEastLatLng).title("Marker in KFC"));
        mMap.addMarker(new MarkerOptions().position(southEastLatLng).title("Marker in Buccleuch Street bus stop"));
        mMap.addMarker(new MarkerOptions().position(southWestLatLng).title("Marker in Top of the Meadows"));
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
                    Log.e("GameUI", "hello3");
                    try {
                        Log.e("GameUI", "hello4");
                        mMap.setMyLocationEnabled(true);
                        Log.e("GameUI", String.valueOf(mMap.isMyLocationEnabled()));
                        /* Experimental
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            // Logic to handle location object
                                            startLocationUpdates();
                                        }
                                    }
                                });*/
                    } catch (SecurityException e) {
                        Log.e("GameUI", "error");
                        //warning
                    }

                } else {
                    //permission denied
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
/* Experimental
class LooperThread extends Thread {
    private Handler mHandler;

    public void run() {
        Looper.prepare();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // process incoming messages here
            }
        };

        Looper.loop();
    }
}
*/