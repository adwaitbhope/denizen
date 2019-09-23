package com.township.manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class RegistrationStepOne extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private GoogleMap mMap;
    private static int LOCATION_REQUEST_CODE = 1;
    private static final int DEFAULT_ZOOM = 15;
    Button documentUploadButton, submitButton;
    Uri fileUri;
    private TextInputLayout usernameTextLayout, administratorPhoneNumberTextLayout, designationTextLayout, emailTextLayout, societyNameTextLayout, societyPhoneNumberTextLayout, societyAddressTextLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_step_one);
        Button setLocationButton = findViewById(R.id.registration_set_location_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.registration_step_one_toolbar);
//        toolbar.setTitleTextColor(getColor(R.color.secondaryColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Society Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usernameTextLayout = findViewById(R.id.registration_step_one_username);
        administratorPhoneNumberTextLayout = findViewById(R.id.registratio_step_one_administrator_phone_number);
        designationTextLayout = findViewById(R.id.registration_step_one_designation);
        emailTextLayout = findViewById(R.id.register_step_one_email);
        societyNameTextLayout = findViewById(R.id.register_step_one_society_name);
        societyAddressTextLayout = findViewById(R.id.register_step_one_society_address);
        societyPhoneNumberTextLayout = findViewById(R.id.register_step_one_society_phone_number);

        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationStepOne.this, MapsActivityStepOne.class);
//                startActivity(intent);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
            }
        });
        documentUploadButton = findViewById(R.id.registration_step_one_documents_upload);
        submitButton = findViewById(R.id.registration_step_one_submit);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_embedded_map);
        mapFragment.getMapAsync(this);
        documentUpload();
        submit();

    }

    private void submit() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTextLayout.getEditText().getText().toString();
                String adminphonenumber = administratorPhoneNumberTextLayout.getEditText().getText().toString();
                String designation = designationTextLayout.getEditText().getText().toString();
                String email = emailTextLayout.getEditText().getText().toString();
                String societyname = societyNameTextLayout.getEditText().getText().toString();
                String societyaddress = societyAddressTextLayout.getEditText().getText().toString();
                String societyphonenumber = societyPhoneNumberTextLayout.getEditText().getText().toString();

                if (TextUtils.isEmpty(username)) {
                    usernameTextLayout.setError("Please enter your username.");
                    usernameTextLayout.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(adminphonenumber)) {
                    usernameTextLayout.setError("Please enter your admin phonenumber.");
                    usernameTextLayout.setErrorEnabled(true);
                    usernameTextLayout.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(designation)) {
                    usernameTextLayout.setError("Please enter your designation.");
                    usernameTextLayout.setErrorEnabled(true);
                    usernameTextLayout.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    usernameTextLayout.setError("Please enter your email");
                    usernameTextLayout.setErrorEnabled(true);
                    usernameTextLayout.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(societyname)) {
                    usernameTextLayout.setError("Please enter your society name");
                    usernameTextLayout.setErrorEnabled(true);
                    usernameTextLayout.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(societyaddress)) {
                    usernameTextLayout.setError("Please enter your society address");
                    usernameTextLayout.setErrorEnabled(true);
                    usernameTextLayout.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(societyphonenumber)) {
                    usernameTextLayout.setError("Please enter your admin society phone number.");
                    usernameTextLayout.setErrorEnabled(true);
                    usernameTextLayout.requestFocus();
                    return;
                }
//              RegistrationDetailsStepOne registrationDetailsStepOne =new RegistrationDetailsStepOne(username,adminphonenumber,email,designation,societyname,societyaddress,societyphonenumber, );


            }
        });
    }

    private void documentUpload() {
        documentUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 112);

            }
        });

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
        if (requestCode == REQUEST_WRITE_STORAGE) {
            fileUri = data.getData();
            //Log.d("helloy",String.valueOf(fileUri));
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
