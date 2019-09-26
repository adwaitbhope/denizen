package com.township.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import okhttp3.ResponseBody;
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

    public TextView tvShowNumbers;

    public NumberPicker admin_number_picker, security_number_picker;

    private OnFragmentInteractionListener mListener;
    private Button proceedPaymentButton;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_society_step_two_admin_login_details, container, false);
        // Inflate the layout for this fragment

        admin_number_picker = view.findViewById(R.id.admin_number_picker);
        admin_number_picker.setMinValue(0);
        admin_number_picker.setMaxValue(99);
        admin_number_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%01d", i);
            }
        });
        admin_number_picker.setOnValueChangedListener(this);


        security_number_picker = view.findViewById(R.id.security_number_picker);
        security_number_picker.setMinValue(0);
        security_number_picker.setMaxValue(99);
        security_number_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%01d", i);
            }
        });
        security_number_picker.setOnValueChangedListener(this);

        proceedPaymentButton = view.findViewById(R.id.registration_step_two_proceed_payment_button);

        proceedPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generateChecksum();
            }
        });
        return view;
    }

    private void generateChecksum() {
           final PaytmConstants paytmConstants=new PaytmConstants();  //currently set as default value as UI is not developed
//        String orderId="200"; //will fetch from server once done
//        String custId="200"; //will fetch from servr once done
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        paytmConstants.setTXN_AMOUNT("100");
        Retrofit retrofit = builder.build();




        FileUploadService fileUploadService = retrofit.create(FileUploadService.class);

        Call<JsonArray> call = fileUploadService.getChecksum(
                PaytmConstants.CHANNEL_ID,
                paytmConstants.getTXN_AMOUNT(),
                PaytmConstants.WEBSITE,
                PaytmConstants.CALLBACK_URL,
                PaytmConstants.INDUSTRY_TYPE_ID

        );

        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                  String jsonstring=response.body().getAsJsonArray().toString();

                try {
                    JSONArray jsonArray=new JSONArray(jsonstring);;
                    JSONObject jsonObject=jsonArray.getJSONObject(0);

                    paytmConstants.setCALLBACK_URL(jsonObject.getString("CALLBACK_URL"));
                    paytmConstants.setCHECKSUMHASH(jsonObject.getString("CHECKSUMHASH"));
                    paytmConstants.setCUST_ID(jsonObject.getString("CUST_ID"));
                    paytmConstants.setORDER_ID(jsonObject.getString("ORDER_ID"));
                    initializePayment(paytmConstants);


                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                Log.d("patymerror",throwable.toString()+ " "+ call.toString());

            }
        });

    }

    private void initializePayment(PaytmConstants paytmConstants) {

        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        //PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", PaytmConstants.MID);
        paramMap.put("ORDER_ID", paytmConstants.getORDER_ID());
        paramMap.put("CUST_ID", paytmConstants.getCUST_ID());
        paramMap.put("CHANNEL_ID", PaytmConstants.CHANNEL_ID);
        paramMap.put("TXN_AMOUNT", paytmConstants.getTXN_AMOUNT());
        paramMap.put("WEBSITE", PaytmConstants.WEBSITE);
        paramMap.put("CALLBACK_URL", paytmConstants.getCALLBACK_URL());
        paramMap.put("CHECKSUMHASH", paytmConstants.getCHECKSUMHASH());
        paramMap.put("INDUSTRY_TYPE_ID", PaytmConstants.INDUSTRY_TYPE_ID);


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

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
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        Toast.makeText(getActivity(), inResponse.toString(), Toast.LENGTH_LONG).show();

        try {
            JSONArray jsonArray = new JSONArray(inResponse.toString());
            JSONObject jsonObject=jsonArray.getJSONObject(0);
            if(jsonObject.getString("STATUS").equals("SUCCESS")){
                Toast.makeText(getActivity(),"Transaction successfull wait for verification",Toast.LENGTH_SHORT).show();
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl(getString(R.string.server_addr))
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();

                FileUploadService fileUploadService = retrofit.create(FileUploadService.class);

                Call<JsonArray> call = fileUploadService.checksumVerify(
                        inResponse.toString()
                );
                call.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                        assert response.body() != null;
                        String jsonstring=response.body().getAsJsonArray().toString();
                        try {
                            JSONArray jsonArray=new JSONArray(jsonstring);;
                            JSONObject jsonObject2=jsonArray.getJSONObject(0);

                            if(jsonObject2.getString("checksum_verified").equals("true")){
                                Toast.makeText(getActivity(),"transaction verified",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(),"transaction not verified",Toast.LENGTH_SHORT).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable throwable) {

                    }
                });
            }
            else{
                Toast.makeText(getActivity(),"Transaction failed",Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        ;



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
    }
}
