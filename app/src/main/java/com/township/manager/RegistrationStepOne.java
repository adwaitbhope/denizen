package com.township.manager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationStepOne extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private GoogleMap mMap;
    private static int LOCATION_REQUEST_CODE = 1;
    private static final int DEFAULT_ZOOM = 15;
    Button documentUploadButton, submitButton;
    String filePath;
    private TextInputLayout usernameTextLayout, administratorPhoneNumberTextLayout, designationTextLayout, emailTextLayout, societyNameTextLayout, societyPhoneNumberTextLayout, societyAddressTextLayout;
    private double latitude, longitude;
    private String geoaddress;
    private String adminName, adminPhone, adminDesignation, adminEmail, societyName, societyAddress, societyPhone;
    private Boolean locationSelected=false,fileSelected=false;
    private TextView locationError,fileMessage;
    private File orignalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_step_one);
        Button setLocationButton = findViewById(R.id.registration_step_one_set_location_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.registration_step_one_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Society Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usernameTextLayout = findViewById(R.id.registration_step_one_username_til);
        administratorPhoneNumberTextLayout = findViewById(R.id.registration_step_one_admin_phone_til);
        designationTextLayout = findViewById(R.id.registration_step_one_admin_designation_til);
        emailTextLayout = findViewById(R.id.registration_step_one_admin_email_til);
        societyNameTextLayout = findViewById(R.id.registration_step_one_society_name_til);
        societyAddressTextLayout = findViewById(R.id.registration_step_one_society_address_til);
        societyPhoneNumberTextLayout = findViewById(R.id.registration_step_one_society_phone_til);
        locationError=findViewById(R.id.registration_step_one_location_error);
        fileMessage=findViewById(R.id.registration_step_one_file_message);



        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationStepOne.this, MapsActivityStepOne.class);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
            }
        });

        documentUploadButton = findViewById(R.id.registration_step_one_upload_documents_button);
        submitButton = findViewById(R.id.registration_step_one_submit_form_button);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_embedded_map);
        locationError.setVisibility(View.GONE);
        fileMessage.setVisibility(View.GONE);
        mapFragment.getMapAsync(this);
        documentUpload();
        error(usernameTextLayout);
        error(administratorPhoneNumberTextLayout);
        error(designationTextLayout);
        error(emailTextLayout);
        error(societyAddressTextLayout);
        error(societyNameTextLayout);
        error(societyPhoneNumberTextLayout);
        submit();

    }

    private void error(final TextInputLayout textInputLayout) {
        try {
            Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }


    private void submit() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminName = usernameTextLayout.getEditText().getText().toString();
                adminPhone = administratorPhoneNumberTextLayout.getEditText().getText().toString();
                adminDesignation = designationTextLayout.getEditText().getText().toString();
                adminEmail = emailTextLayout.getEditText().getText().toString();
                societyName = societyNameTextLayout.getEditText().getText().toString();
                societyAddress = societyAddressTextLayout.getEditText().getText().toString();
                societyPhone = societyPhoneNumberTextLayout.getEditText().getText().toString();

                if (TextUtils.isEmpty(adminName)) {
                    usernameTextLayout.setError("This field is required");
                    usernameTextLayout.requestFocus();
                    usernameTextLayout.setErrorIconDrawable(null);
                    return;
                }
                if (TextUtils.isEmpty(adminPhone)) {
                    administratorPhoneNumberTextLayout.setError("This field is required");
                    administratorPhoneNumberTextLayout.setErrorEnabled(true);
                    administratorPhoneNumberTextLayout.requestFocus();
                    administratorPhoneNumberTextLayout.setErrorIconDrawable(null);
                    return;
                }
                if (TextUtils.isEmpty(adminDesignation)) {
                    designationTextLayout.setError("This field is required");
                    designationTextLayout.setErrorEnabled(true);
                    designationTextLayout.requestFocus();
                    designationTextLayout.setErrorIconDrawable(null);
                    return;
                }
                if (TextUtils.isEmpty(adminEmail)) {
                    emailTextLayout.setError("This field is required");
                    emailTextLayout.setErrorEnabled(true);
                    emailTextLayout.requestFocus();
                    emailTextLayout.setErrorIconDrawable(null);
                    return;
                }

                if (TextUtils.isEmpty(societyName)) {
                    societyNameTextLayout.setError("This field is required");
                    societyNameTextLayout.setErrorEnabled(true);
                    societyNameTextLayout.requestFocus();
                    societyNameTextLayout.setErrorIconDrawable(null);
                    return;
                }
                if (TextUtils.isEmpty(societyAddress)) {
                    societyAddressTextLayout.setError("This field is required");
                    societyAddressTextLayout.setErrorEnabled(true);
                    societyAddressTextLayout.requestFocus();
                    societyAddressTextLayout.setErrorIconDrawable(null);
                    return;
                }
                if (TextUtils.isEmpty(societyPhone)) {
                    societyPhoneNumberTextLayout.setError("This field is required");
                    societyPhoneNumberTextLayout.setErrorEnabled(true);
                    societyPhoneNumberTextLayout.requestFocus();
                    societyPhoneNumberTextLayout.setErrorIconDrawable(null);
                    return;
                }
                if(!locationSelected) {
                    locationError.setText("Select a location");
                    locationError.setVisibility(View.VISIBLE);
                    locationError.setTextColor(Color.RED);
                    return;
                }
                if(!fileSelected) {
                    fileMessage.setText("Please select a file");
                    fileMessage.setVisibility(View.VISIBLE);
                    fileMessage.setTextColor(Color.RED);
                    return;
                }


                sendNetworkRequest();

            }
        });
    }

    private void sendNetworkRequest() {

        RequestBody filePart = RequestBody.create(MediaType.parse("multipart/form-data"), orignalFile);

        MultipartBody.Part file = MultipartBody.Part.createFormData("certificate", orignalFile.getName(), filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Call<ResponseBody> call = retrofitServerAPI.registerApplicant(createPartFromString(adminName),
                createPartFromString(adminPhone),
                createPartFromString(adminEmail),
                createPartFromString(adminDesignation),
                createPartFromString(societyName),
                createPartFromString(societyAddress),
                createPartFromString(societyPhone),
                createPartFromString(""),
                createPartFromString(String.valueOf(latitude)),
                createPartFromString(String.valueOf(longitude)),
                file);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(RegistrationStepOne.this, "Application submitted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
//        }


    }

    @NonNull
    private RequestBody createPartFromString(String s) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, s);
    }


    private void documentUpload() {
        documentUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionsAndOpenFilePicker();
            }
        });

    }

    public void openFilePicker() {
        new MaterialFilePicker()
                .withActivity(RegistrationStepOne.this)
                .withRequestCode(10)
                .withHiddenFiles(false)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF")
                .start();
    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFilePicker();
        }
    }

    private void showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    showError();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    LatLng currentLocation = data.getExtras().getParcelable("LatLong");
                    locationSelected=data.getExtras().getBoolean("locationCheck");
                    locationError.setVisibility(View.GONE);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation));
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(currentLocation, DEFAULT_ZOOM));
                    MarkerOptions markerOptions = new MarkerOptions();
                    latitude = currentLocation.latitude;
                    longitude = currentLocation.longitude;
//                    Geocoder geocoder = null;
//                    List<Address> addresses;
//                    try {
//                        addresses=geocoder.getFromLocation(latitude,longitude,1);
//                        geoaddress=addresses.get(0).getAddressLine(0);
//
//                    }
//                    catch (IOException e){
//                       e.printStackTrace();;
//                    }
                }
            }
        }

        if (requestCode == 10) {
            try {
                filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                try {
                    orignalFile = new File(filePath);

                    if (orignalFile.exists()) {
                        fileSelected=true;
                        fileMessage.setText(orignalFile.getName());
                        fileMessage.setTextColor(Color.BLACK);
                        documentUploadButton.setText("Select different document");
                        fileMessage.setVisibility(View.VISIBLE);
                    }

                    if(orignalFile.length()>2*1024*1024){
                        fileSelected=false;
                        fileMessage.setText("Select a file less than 2 MB");
                        fileMessage.setTextColor(View.VISIBLE);
                        fileMessage.setTextColor(Color.RED);
                    }
                }
                catch (NullPointerException e){

                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
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

//
    }


}
