package com.township.manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VisitorEntryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VisitorEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitorEntryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String username, password, townshipId, wingId;
    String firstName, lastName, apartment;

    TextInputLayout firstNameTIL, lastNameTIL, wingTIL, apartmentTIL;
    TextInputEditText firstNameEditText, lastNameEditText;
    AutoCompleteTextView wingACTV, apartmentACTV;

    ArrayAdapter<String> apartmentsAdapter, wingsAdapter;
    AppDatabase appDatabase;

    String[] wings, wingIds;
    String[] apartments;

    ArrayList<Wing> wingsList;
    ArrayList<Resident> residentsList;

    private OnFragmentInteractionListener mListener;

    public VisitorEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitorEntryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitorEntryFragment newInstance(String param1, String param2) {
        VisitorEntryFragment fragment = new VisitorEntryFragment();
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

        appDatabase = Room.databaseBuilder(getContext().getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        new GetWingsFromDatabase().execute();

        DBManager dbManager = new DBManager(getContext().getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        townshipId = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visitor_entry, container, false);

        firstNameTIL = view.findViewById(R.id.visitor_entry_first_name_til);
        lastNameTIL = view.findViewById(R.id.visitor_entry_last_name_til);
        wingTIL = view.findViewById(R.id.visitor_entry_wing_til);
        apartmentTIL = view.findViewById(R.id.visitor_entry_apartment_til);
        firstNameEditText = view.findViewById(R.id.visitor_entry_first_name_edittext);
        lastNameEditText = view.findViewById(R.id.visitor_entry_last_name_edittext);
        wingACTV = view.findViewById(R.id.visitor_entry_wing_drop_down);
        apartmentACTV = view.findViewById(R.id.visitor_entry_apartment_drop_down);

        apartmentsAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.dropdown_menu_popup_item,
                new String[]{"No apartments"});
        apartmentACTV.setClickable(false);
        apartmentACTV.setAdapter(apartmentsAdapter);

        Button button = view.findViewById(R.id.visitor_entry_go_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendQueryForNewVisitor();
            }
        });

        return view;
    }

    private void sendQueryForNewVisitor() {

        try {
            InputMethodManager manager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ProgressBar) getView().findViewById(R.id.add_visitor_entry_progress_bar)).setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            firstNameTIL.setErrorEnabled(true);
            firstNameTIL.setError("First name is required");
            firstNameTIL.requestFocus();
            firstNameTIL.setErrorIconDrawable(null);
            ((ProgressBar) getView().findViewById(R.id.add_visitor_entry_progress_bar)).setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameTIL.setErrorEnabled(true);
            lastNameTIL.setError("Last name is required");
            lastNameTIL.requestFocus();
            lastNameTIL.setErrorIconDrawable(null);
            ((ProgressBar) getView().findViewById(R.id.add_visitor_entry_progress_bar)).setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }

        RetrofitServerAPI retrofitServerAPI = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.addNewVisitor(
                username,
                password,
                firstName,
                lastName,
                wingId,
                apartment
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray array = new JSONArray(responseString);
                    JSONObject loginInfo = array.getJSONObject(0);
                    if (loginInfo.getInt("login_status") == 1) {
                        if (loginInfo.getInt("request_status") == 1) {

                            ((ProgressBar) getView().findViewById(R.id.add_visitor_entry_progress_bar)).setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            ((SecurityHomeScreenActivity) getActivity()).getVisitorHistoryFromServer();

                            Toast.makeText(getContext(), "Entry successful", Toast.LENGTH_SHORT).show();

                            firstNameEditText.setText("");
                            firstNameEditText.clearFocus();
                            lastNameEditText.setText("");
                            lastNameEditText.clearFocus();
                            wingACTV.setText(wingACTV.getHint());
                            wingACTV.clearFocus();
                            apartmentACTV.setText(apartmentACTV.getHint());
                            apartmentACTV.clearFocus();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });


    }

    private class GetWingsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wingsList = (ArrayList<Wing>) appDatabase.wingDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            wings = new String[wingsList.size()];
            wingIds = new String[wingsList.size()];
            for (int i = 0; i < wingsList.size(); i++) {
                wings[i] = wingsList.get(i).getName();
                wingIds[i] = wingsList.get(i).getWing_id();
            }
            wingsAdapter = new ArrayAdapter<>(
                    getContext(),
                    R.layout.dropdown_menu_popup_item,
                    wings);
            wingACTV.setAdapter(wingsAdapter);
            wingACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    wingId = wingIds[position];
                    new GetApartmentsFromDatabase().execute();
                }
            });
            super.onPostExecute(aVoid);
        }
    }

    private class GetApartmentsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            residentsList = (ArrayList<Resident>) appDatabase.residentDao().getAllFromWing(wingId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            apartments = new String[residentsList.size()];
            for (int i = 0; i < residentsList.size(); i++) {
                apartments[i] = residentsList.get(i).getApartment();
            }
            apartmentsAdapter = new ArrayAdapter<>(
                    getContext(),
                    R.layout.dropdown_menu_popup_item,
                    apartments);
            apartmentACTV.setAdapter(apartmentsAdapter);
            apartmentACTV.setClickable(true);
            apartmentACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    apartment = apartments[position];
                }
            });
            super.onPostExecute(aVoid);
        }
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
