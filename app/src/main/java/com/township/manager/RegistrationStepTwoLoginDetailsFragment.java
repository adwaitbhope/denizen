package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationStepTwoLoginDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationStepTwoLoginDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationStepTwoLoginDetailsFragment extends Fragment implements NumberPicker.OnValueChangeListener, PaytmPaymentTransactionCallback {
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

    public RegistrationStepTwoLoginDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationStepTwoLoginDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationStepTwoLoginDetailsFragment newInstance(String param1, String param2) {
        RegistrationStepTwoLoginDetailsFragment fragment = new RegistrationStepTwoLoginDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_registration_step_two_login_details, container, false);

        noOfAdmin = view.findViewById(R.id.registration_step2_admin_no_of_admins);
        noOfSecurityDesks = view.findViewById(R.id.registration_step2_admin_no_of_security);

        final ProgressBar progressBar = view.findViewById(R.id.registration_initiate_payment_progress_bar);

        error(noOfAdmin);
        error(noOfSecurityDesks);
        proceedPaymentButton = view.findViewById(R.id.registration_step_two_proceed_payment_button);

        proceedPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                try {
                    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                wings = mListener.getWingsData();
                amenities = mListener.getAmenitiesData();
                if (mListener.getWingsError()) {
                    Toast.makeText(getContext(), "Fill wing details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mListener.getAmenitiesError()) {
                    Toast.makeText(getContext(), "Fill amenity details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (getLoginError()) {
                    Toast.makeText(getContext(), "Fill Login Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> wingsMap = new HashMap<>();
                for (int i = 0; i < wings.size(); i++) {
                    Wing wing = wings.get(i);
                    wingsMap.put("wing_" + i + "_name", wing.getName());
                    wingsMap.put("wing_" + i + "_floors", wing.getNumberOfFloors());
                    wingsMap.put("wing_" + i + "_apts_per_floor", wing.getNumberOfApartmentsPerFloor());
                    wingsMap.put("wing_" + i + "_naming_convention", wing.getNamingConvention());
                }
                Map<String, Object> amenitiesMap = new HashMap<>();
                for (int j = 0; j < amenities.size(); j++) {
                    Amenity amenity = amenities.get(j);

                    amenitiesMap.put("amenity_" + j + "_name", amenity.getName());
                    amenitiesMap.put("amenity_" + j + "_rate", amenity.getAmenityrate());
                    amenitiesMap.put("amenity_" + j + "_time_period", amenity.getBillingperiod());
                    amenitiesMap.put("amenity_" + j + "_free_for_members", amenity.getFreeornot());
                }

                Paytm.PaytmOrder paytmOrder = new Paytm.PaytmOrder();
                paytmOrder.setTXN_AMOUNT("100");
                generateChecksumFromServer(paytmOrder, wingsMap, amenitiesMap);
            }
        });
        return view;
    }

    private boolean getLoginError() {
        if (TextUtils.isEmpty(noOfAdmin.getEditText().getText().toString())) {
            noOfAdmin.setError("Required");
            noOfAdmin.setErrorEnabled(true);
            noOfAdmin.requestFocus();
            noOfAdmin.setErrorIconDrawable(null);
            return true;
        }
        if (TextUtils.isEmpty(noOfSecurityDesks.getEditText().getText().toString())) {
            noOfSecurityDesks.setError("Required");
            noOfSecurityDesks.setErrorEnabled(true);
            noOfSecurityDesks.requestFocus();
            noOfSecurityDesks.setErrorIconDrawable(null);
            return true;
        }
        return false;
    }


    private void generateChecksumFromServer(final Paytm.PaytmOrder paytmOrder, Map<String, Object> wingsMap, Map<String, Object> amenitiesMap) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        Integer noofadmins = Integer.valueOf(noOfAdmin.getEditText().getText().toString());
        Integer noofseurity = Integer.valueOf(noOfSecurityDesks.getEditText().getText().toString());
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Log.d("b", "b");
        Call<JsonArray> call = retrofitServerAPI.registrationStep2(
                application_id,
                Paytm.CHANNEL_ID,
                paytmOrder.getTXN_AMOUNT(),
                Paytm.WEBSITE,
                Paytm.CALLBACK_URL,
                Paytm.INDUSTRY_TYPE_ID,
                wingsMap,
                amenitiesMap,
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
                    Log.d("pay", e.toString());
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                Log.d("Init call failure", throwable.toString() + " " + call.toString());

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
                        getActivity().finish();
                        getActivity().startActivity(new Intent(getActivity(), RegistrationSuccessfulActivity.class));
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
