package com.township.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RegistrationStepOne extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static int LOCATION_REQUEST_CODE = 1;
    private static final int DEFAULT_ZOOM = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_step_one);
        Button setLocationButton = findViewById(R.id.registration_set_location_button);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitleTextColor(getColor(R.color.secondaryColor));
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationStepOne.this, MapsActivityStepOne.class);
//                startActivity(intent);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_embedded_map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    LatLng currentLocation = data.getExtras().getParcelable("LatLong");
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation));
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(currentLocation, DEFAULT_ZOOM));
                }
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        {
            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
//                    (MapsActivityStepOne).getDeviceLocation();
                }
            });
        }

        // Add a marker in Sydney and move the camera
        LatLng pune = new LatLng(18.4915, 73.8217);
        mMap.addMarker(new MarkerOptions().position(pune).title("Marker in Pune"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pune));
    }


}
