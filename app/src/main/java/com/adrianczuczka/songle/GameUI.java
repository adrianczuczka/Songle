package com.adrianczuczka.songle;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class GameUI extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private Looper looper = Looper.getMainLooper();
    private LocationRequest mLocationRequest = new LocationRequest();
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
    private boolean mRequestingLocationUpdates = false;
    private LocationCallback mLocationCallback;

    private void startLocationUpdates() {
        try {
            Log.e("GameUI", "made it to startlocationupdates");
            Log.e("GameUI", String.valueOf(mLocationRequest));
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, looper);
            mRequestingLocationUpdates = true;
        } catch (SecurityException e) {

        }
    }

    protected void createLocationRequest(LocationRequest locReq) {
        locReq.setInterval(10000);
        locReq.setFastestInterval(5000);
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.e("GameUI", String.valueOf(mLocationRequest) + "inside createlocationrequest");
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
        Log.e("GameUI", String.valueOf(mLocationRequest));
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.e("GameUI", "made it to callback");
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.e("GameUI", "made it to tracking location");
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(loc).title("Marker in Forrest Hill"));
                }
            }

            ;
        };
        /*
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
        Log.e("GameUI", "made it to onMapReady");
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
            try {
                mMap.setMyLocationEnabled(true);
                Log.e("GameUI", String.valueOf(mMap.isMyLocationEnabled()));
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
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
                        Log.e("GameUI", String.valueOf(mLocationRequest));
                        Log.e("GameUI", "hello6");
                        startLocationUpdates();
                    }
                });
                task.addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GameUI", "Hello11");
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
            } catch (SecurityException e) {
            }
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

    /*
        @Override
        protected void onResume() {
            super.onResume();
            if (!mRequestingLocationUpdates) {
                try {
                    Log.e("GameUI","made it to onResume");
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //show explanation
                } else {add comment line
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                1);
                        //}
                    }
                    else {
                        startLocationUpdates();
                    }
                } catch (SecurityException e) {

                }
            }
        }
    */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (permissions.length == 1 &&
                        permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mMap.setMyLocationEnabled(true);
                        mRequestingLocationUpdates = true;
                        Log.e("GameUI", String.valueOf(mMap.isMyLocationEnabled()));
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
                                Log.e("GameUI", "hello7");
                                Log.e("GameUI", String.valueOf(mLocationRequest));
                                startLocationUpdates();
                            }
                        });
                        task.addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("GameUI", "Hello11");
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
                    } catch (SecurityException e) {
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
