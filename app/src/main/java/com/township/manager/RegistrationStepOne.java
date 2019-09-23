package com.township.manager;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import androidx.annotation.NonNull;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class RegistrationStepOne extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_WRITE_STORAGE =112 ;
    private GoogleMap mMap;
    private static int LOCATION_REQUEST_CODE = 1;
    private static final int DEFAULT_ZOOM = 15;
    Button documentUploadButton, submitButton;
    Uri fileUri;
    private TextInputLayout usernameTextLayout,administratorPhoneNumberTextLayout,designationTextLayout,emailTextLayout,societyNameTextLayout,societyPhoneNumberTextLayout,societyAddressTextLayout;
    private double latitude,longitude;
    private String geoaddress;

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

        usernameTextLayout=findViewById(R.id.registration_step_one_username);
        administratorPhoneNumberTextLayout=findViewById(R.id.registratio_step_one_administrator_phone_number);
        designationTextLayout=findViewById(R.id.registration_step_one_designation);
        emailTextLayout=findViewById(R.id.register_step_one_email);
        societyNameTextLayout=findViewById(R.id.register_step_one_society_name);
        societyAddressTextLayout=findViewById(R.id.register_step_one_society_address);
        societyPhoneNumberTextLayout=findViewById(R.id.register_step_one_society_phone_number);



        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationStepOne.this, MapsActivityStepOne.class);
//                startActivity(intent);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
            }
        });
        documentUploadButton=findViewById(R.id.registration_step_one_documents_upload);
        submitButton=findViewById(R.id.registration_step_one_submit);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_embedded_map);
        mapFragment.getMapAsync(this);
        documentUpload();
        submit();

    }

    private void submit() {
      submitButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              String username=usernameTextLayout.getEditText().getText().toString();
              String adminphonenumber=administratorPhoneNumberTextLayout.getEditText().getText().toString();
              String designation=designationTextLayout.getEditText().getText().toString();
              String email=emailTextLayout.getEditText().getText().toString();
              String societyname=societyNameTextLayout.getEditText().getText().toString();
              String societyaddress=societyAddressTextLayout.getEditText().getText().toString();
              String societyphonenumber=societyPhoneNumberTextLayout.getEditText().getText().toString();

              if (TextUtils.isEmpty(username)) {
                  usernameTextLayout.setError("Please enter your username.");
                  usernameTextLayout.requestFocus();
                  return;
              }
              if (TextUtils.isEmpty(adminphonenumber)) {
                  administratorPhoneNumberTextLayout.setError("Please enter your admin phonenumber.");
                  administratorPhoneNumberTextLayout.setErrorEnabled(true);
                  administratorPhoneNumberTextLayout.requestFocus();
                  return;
              }
              if (TextUtils.isEmpty(designation)) {
                  designationTextLayout.setError("Please enter your designation.");
                  designationTextLayout.setErrorEnabled(true);
                  designationTextLayout.requestFocus();
                  return;
              }
              if (TextUtils.isEmpty(email)) {
                  emailTextLayout.setError("Please enter your email");
                  emailTextLayout.setErrorEnabled(true);
                  emailTextLayout.requestFocus();
                  return;
              }
              if (TextUtils.isEmpty(societyname)) {
                  societyNameTextLayout.setError("Please enter your society name");
                  societyNameTextLayout.setErrorEnabled(true);
                  societyNameTextLayout.requestFocus();
                  return;
              }
              if (TextUtils.isEmpty(societyaddress)) {
                  societyAddressTextLayout.setError("Please enter your society address");
                  societyAddressTextLayout.setErrorEnabled(true);
                  societyAddressTextLayout.requestFocus();
                  return;
              }
              if (TextUtils.isEmpty(societyphonenumber)) {
                  societyPhoneNumberTextLayout.setError("Please enter your admin society phone number.");
                  societyPhoneNumberTextLayout.setErrorEnabled(true);
                  societyPhoneNumberTextLayout.requestFocus();
                  return;
              }
              RegistrationDetailsStepOne registrationDetailsStepOne =new RegistrationDetailsStepOne(username,adminphonenumber,email,designation,societyname,societyaddress,societyphonenumber,geoaddress,String.valueOf(latitude),String.valueOf(longitude));
              sendNetworkRequest(registrationDetailsStepOne);

          }
      });
    }

    private void sendNetworkRequest(RegistrationDetailsStepOne registrationDetailsStepOne) {

        File orignalfile =new File(""+fileUri);
        Log.d("extern",Environment.getExternalStorageDirectory().toString());
            Log.d("lenghth",String.valueOf(orignalfile.length()));
//            registrationDetailsStepOne.setLat("1");
//            registrationDetailsStepOne.setLng("1");
//            registrationDetailsStepOne.setGeo_address("asas");
//            Log.d("address",registrationDetailsStepOne.getAddress());
//            Log.d("aname",registrationDetailsStepOne.getApplicant_name());
//            Log.d("geoadress",registrationDetailsStepOne.getGeo_address());
//            Log.d("latitude",registrationDetailsStepOne.getLat());
//            Log.d("longitude",registrationDetailsStepOne.getLng());
//            Log.d("phone",registrationDetailsStepOne.getApplicant_phone());
//            Log.d("email",registrationDetailsStepOne.getApplicant_email());
//            Log.d("sname",registrationDetailsStepOne.getName());
//            Log.d("sphone",registrationDetailsStepOne.getPhone());
//            Log.d("designation",registrationDetailsStepOne.getApplicant_designation());
            RequestBody filePart = RequestBody.create(MediaType.parse(Objects.requireNonNull(getContentResolver().getType(fileUri))), orignalfile);

            MultipartBody.Part file = MultipartBody.Part.createFormData("certificate",orignalfile.getName(),filePart);

            Retrofit.Builder builder=new Retrofit.Builder()
                    .baseUrl(getString(R.string.server_addr))
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit=builder.build();

            FileUploadService fileUploadService=retrofit.create(FileUploadService.class);
            Call<ResponseBody> call=fileUploadService.registerApplicant(createPartFromString(registrationDetailsStepOne.getApplicant_name()),createPartFromString(registrationDetailsStepOne.getApplicant_phone()),createPartFromString(registrationDetailsStepOne.getApplicant_email()),createPartFromString(registrationDetailsStepOne.getApplicant_designation()),createPartFromString(registrationDetailsStepOne.getName()),createPartFromString(registrationDetailsStepOne.getAddress()),createPartFromString(registrationDetailsStepOne.getPhone()),createPartFromString(geoaddress),createPartFromString(String.valueOf(latitude)),createPartFromString(String.valueOf(longitude)),file);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toast.makeText(RegistrationStepOne.this,"Done",Toast.LENGTH_SHORT).show();
                    Log.d("heloo","done");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(RegistrationStepOne.this,"something went wrong",Toast.LENGTH_SHORT).show();
                    Log.d("errpre",call.toString());
                    Log.d("erryty",t.toString());
                }
            });
//        catch (NullPointerException e){
//            Log.d("hie",e.toString());
//            e.printStackTrace();
//        }



    }
        @NonNull
        private RequestBody createPartFromString(String s){
           return RequestBody.create(okhttp3.MultipartBody.FORM,s);
        }


    private void documentUpload() {
        documentUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
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
                    MarkerOptions markerOptions=new MarkerOptions();
                    latitude=currentLocation.latitude;
                    longitude=currentLocation.longitude;
                    Geocoder geocoder = null;
                    List<Address> addresses;
                    try {
                        addresses=geocoder.getFromLocation(latitude,longitude,1);
                        geoaddress=addresses.get(0).getAddressLine(0);

                    }
                    catch (IOException e){
                       e.printStackTrace();;
                    }
                }


            }
        }
        if (requestCode == REQUEST_WRITE_STORAGE){
            try {
                fileUri = data.getData();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
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
