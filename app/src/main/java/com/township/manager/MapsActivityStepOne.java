package com.township.manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivityStepOne extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location Last_Known_Location;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean marker_set;
    Button confirnButton;
    LatLng LatLong;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_step_one);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        confirnButton = (Button) findViewById(R.id.map_confirm_location_button);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        confirnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LatLong == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivityStepOne.this);
                    builder.setTitle("No location selected")
                            .setMessage("Please tap on the desired location and then proceed.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("LatLong", LatLong);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
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
        getDeviceLocation();

        // Add a marker in Pune and move the camera
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Pune"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                marker_set = true;
                LatLong = latLng;
            }
        });

    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
//        Toast.makeText(getContext(), "get device location", Toast.LENGTH_SHORT).show();

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        LatLng currentLocation = new LatLng(Last_Known_Location.getLatitude(), Last_Known_Location.getLongitude());
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Last_Known_Location = task.getResult();
                            currentLocation = new LatLng(Last_Known_Location.getLatitude(), Last_Known_Location.getLongitude());
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Pune"));
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(currentLocation, DEFAULT_ZOOM));
                            mMap.setMyLocationEnabled(true);
                        } else {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Pune"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                    }
                });
            } else {
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
//            Toast.makeText(getContext(), "getLocationPermission:mLocationPermissionGranted:-"+mLocationPermissionGranted, Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            Toast.makeText(getContext(), "getLocationPermission:mLocationPermissionGranted:-"+mLocationPermissionGranted, Toast.LENGTH_SHORT).show();
        }
        updateLocationUI();
//        onRequestPermissionsResult(PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION,PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    /**
     * Handles the result of the request for location permissions.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
//                    Toast.makeText(getContext(), "onRequestPermissionsResult:-", Toast.LENGTH_SHORT).show();
                }
            }
//            Toast.makeText(getContext(), "onRequestPermissionsResult:-"+mLocationPermissionGranted, Toast.LENGTH_SHORT).show();
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                Last_Known_Location = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
