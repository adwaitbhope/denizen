package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationSocietyStepTwoAdminLoginDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationSocietyStepTwoAdminLoginDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationSocietyStepTwoAdminLoginDetailsFragment extends Fragment implements NumberPicker.OnValueChangeListener, PaytmPaymentTransactionCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<Wing> wings;
    ArrayList<Amenity> amenities;


    public TextView tvShowNumbers;

    public NumberPicker admin_number_picker, security_number_picker;

    private OnFragmentInteractionListener mListener;
    private Button proceedPaymentButton;

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }

    private String application_id;
    private TextInputLayout noOfAdmin, noOfSecurityDesks;

    public RegistrationSocietyStepTwoAdminLoginDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationSocietyStepTwoAdminLoginDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationSocietyStepTwoAdminLoginDetailsFragment newInstance(String param1, String param2) {
        RegistrationSocietyStepTwoAdminLoginDetailsFragment fragment = new RegistrationSocietyStepTwoAdminLoginDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_society_step_two_admin_login_details, container, false);
        noOfAdmin = view.findViewById(R.id.registration_step2_admin_no_of_security);
        noOfSecurityDesks = view.findViewById(R.id.registration_step2_admin_no_of_security);

        // Inflate the layout for this fragment

//        admin_number_picker = view.findViewById(R.id.admin_number_picker);
//        admin_number_picker.setMinValue(0);
//        admin_number_picker.setMaxValue(99);
//        admin_number_picker.setFormatter(new NumberPicker.Formatter() {
//            @Override
//            public String format(int i) {
//                return String.format("%01d", i);
//            }
//        });
//        admin_number_picker.setOnValueChangedListener(this);
//
//
//        security_number_picker = view.findViewById(R.id.security_number_picker);
//        security_number_picker.setMinValue(0);
//        security_number_picker.setMaxValue(99);
//        security_number_picker.setFormatter(new NumberPicker.Formatter() {
//            @Override
//            public String format(int i) {
//                return String.format("%01d", i);
//            }
//        });
//        security_number_picker.setOnValueChangedListener(this);

        error(noOfAdmin);
        error(noOfSecurityDesks);
        proceedPaymentButton = view.findViewById(R.id.registration_step_two_proceed_payment_button);

        proceedPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wings = mListener.getWingsData();
                if(mListener.getWingsError())
                {
                    Toast.makeText(getContext(),"Fill wing details",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mListener.getAmenitiesError()){
                    Toast.makeText(getContext(),"Fill amenity details",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getLoginError()){
                   Toast.makeText(getContext(),"Fill Login Error",Toast.LENGTH_SHORT).show();
                   return;
                }
                amenities = mListener.getAmenitiesData();
                Map<String, Object> amenitymap = null, wingmap = null;
                wingmap = new HashMap<>();
                for (int i = 0; i < wings.size(); i++) {
                    Wing wing = wings.get(i);
                    wingmap.put("wing_" + i + "_name", wing.getName());
                    wingmap.put("wing_" + i + "_floors", wing.getNumberOfFloors());
                    wingmap.put("wing_" + i + "_apts_per_floor", wing.getNumberOfApartmentsPerFloor());
                    wingmap.put("wing_" + i + "_naming_convention", wing.getNamingConvention());
                }
                amenitymap = new HashMap<>();
                for (int j = 0; j < amenities.size(); j++) {
                    Amenity amenity = amenities.get(j);

                    amenitymap.put("amenity_" + j + "_name", amenity.getName());
                    amenitymap.put("amenity_" + j + "_rate", amenity.getAmenityrate());
                    amenitymap.put("amenity_" + j + "_time_period", amenity.getBillingperiod());
                    amenitymap.put("amenity_" + j + "_free_for_members", amenity.getFreeornot());
                }
                Paytm.PaytmOrder paytmOrder = new Paytm.PaytmOrder();
                paytmOrder.setTXN_AMOUNT("100");
                generateChecksumFromServer(paytmOrder, wingmap, amenitymap);
            }
        });
        return view;
    }

    private boolean getLoginError() {
        boolean error=false;
        if(TextUtils.isEmpty(noOfAdmin.getEditText().getText().toString())){
            noOfAdmin.setError("Please enter your username");
            noOfAdmin.setErrorEnabled(true);
            noOfAdmin.requestFocus();
            noOfAdmin.setErrorIconDrawable(null);
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(noOfSecurityDesks.getEditText().getText().toString())){
            noOfSecurityDesks.setError("Please enter your username");
            noOfSecurityDesks.setErrorEnabled(true);
            noOfSecurityDesks.requestFocus();
            noOfSecurityDesks.setErrorIconDrawable(null);
            error=true;
            return error;
        }
        return error;
    }


    private void generateChecksumFromServer(final Paytm.PaytmOrder paytmOrder, Map<String, Object> wingmap, Map<String, Object> amenitymap) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        Integer noofadmins = Integer.valueOf(noOfAdmin.getEditText().getText().toString());
        Integer noofseurity = Integer.valueOf(noOfSecurityDesks.getEditText().getText().toString());
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.registrationStep2(
                application_id,
                Paytm.CHANNEL_ID,
                paytmOrder.getTXN_AMOUNT(),
                Paytm.WEBSITE,
                Paytm.CALLBACK_URL,
                Paytm.INDUSTRY_TYPE_ID,
                wingmap,
                amenitymap,
                noofadmins,
                noofseurity,
                wings.size(),
                amenities.size()
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                String jsonstring = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(jsonstring);
                    JSONObject jsonObject = jsonArray.getJSONObject(1);

                    paytmOrder.setORDER_ID(jsonObject.getString("ORDER_ID"));
                    paytmOrder.setCUST_ID(jsonObject.getString("CUST_ID"));
                    paytmOrder.setCHECKSUMHASH(jsonObject.getString("CHECKSUMHASH"));

                    initializePayment(paytmOrder);

                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                Log.d("Init call failure", throwable.toString() + " " + call.toString());

            }
        });

    }
    private void error(final TextInputLayout textInputLayout) {
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

    private void initializePayment(Paytm.PaytmOrder paytmOrder) {

        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        //PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", Paytm.MID);
        paramMap.put("CHANNEL_ID", Paytm.CHANNEL_ID);
        paramMap.put("WEBSITE", Paytm.WEBSITE);
        paramMap.put("INDUSTRY_TYPE_ID", Paytm.INDUSTRY_TYPE_ID);
        paramMap.put("ORDER_ID", paytmOrder.getORDER_ID());
        paramMap.put("CUST_ID", paytmOrder.getCUST_ID());
        paramMap.put("TXN_AMOUNT", paytmOrder.getTXN_AMOUNT());
        paramMap.put("CALLBACK_URL", Paytm.CALLBACK_URL + paytmOrder.getORDER_ID());
        paramMap.put("CHECKSUMHASH", paytmOrder.getCHECKSUMHASH());

        //creating a paytm order object using the hashmap
        com.paytm.pgsdk.PaytmOrder order = new com.paytm.pgsdk.PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(getActivity(), true, true, this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        // Toast.makeText(getActivity(), inResponse.toString(), Toast.LENGTH_LONG).show();

        JSONObject jsonObject = new JSONObject();
        Set<String> keys = inResponse.keySet();
        try {
            for (String key : keys) {
                jsonObject.put(key, JSONObject.wrap(inResponse.get(key)));
            }
            if (jsonObject.getString("STATUS").equals("TXN_SUCCESS")) {
                Toast.makeText(getActivity(), "Transaction completed, awaiting verification", Toast.LENGTH_SHORT).show();
                verifyTransactionStatusFromServer(jsonObject.getString("ORDERID"));
            } else {
                Toast.makeText(getActivity(), jsonObject.getString("STATUS"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.d("JsonException", e.toString());
        }

    }

    private void verifyTransactionStatusFromServer(String orderId) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.verifyChecksum(
                application_id,
                orderId
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                String jsonstring = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(jsonstring);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.getString("STATUS").equals("TXN_SUCCESS")) {
                        Toast.makeText(getActivity(), "Transaction verification: SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Transaction verification: FAILURE", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JsonException", e.toString());

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                Log.d("Verif. call failure", throwable.toString());
            }
        });
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(getActivity(), "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Toast.makeText(getActivity(), inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Toast.makeText(getActivity(), inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Toast.makeText(getActivity(), inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Toast.makeText(getActivity(), inErrorMessage + inResponse.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        ArrayList<Wing> getWingsData();

        ArrayList<Amenity> getAmenitiesData();
        Boolean getWingsError();
        Boolean getAmenitiesError();

    }
}
