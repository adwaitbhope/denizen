package com.township.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationSocietyStepTwoAmenitiesDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationSocietyStepTwoAmenitiesDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationSocietyStepTwoAmenitiesDetailsFragment extends Fragment implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SwitchCompat switchCompat;


    private OnFragmentInteractionListener mListener;

    public RegistrationSocietyStepTwoAmenitiesDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationSocietyStepTwoAmenitiesDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationSocietyStepTwoAmenitiesDetailsFragment newInstance(String param1, String param2) {
        RegistrationSocietyStepTwoAmenitiesDetailsFragment fragment = new RegistrationSocietyStepTwoAmenitiesDetailsFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration_society_step_two_amenities_details, container, false);

        String[] BILLING_PERIOD = new String[] {"Hourly", "Daily", "Weekly", "Monthly", "Yearly"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        BILLING_PERIOD);

        AutoCompleteTextView editTextFilledExposedDropdown =
                view.findViewById(R.id.billing_period__details_filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);



        String[] MEMBERS_FREE = new String[] {"Yes", "No"};

        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        MEMBERS_FREE);

        AutoCompleteTextView editTextFilledExposedDropdown1 =
                view.findViewById(R.id.free_for_members_filled_exposed_dropdown);
        editTextFilledExposedDropdown1.setAdapter(adapter1);



//        switchCompat = (SwitchCompat) view.findViewById(R.id.free_for_members_switch);
//        switchCompat.setOnCheckedChangeListener(this);


//        Spinner spinner = view.findViewById(R.id.billing_period_spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
//                R.array.billing_period_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);
        return view;
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (switchCompat.isChecked()) {
            Toast.makeText(getContext(), "Yes", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No", Toast.LENGTH_SHORT).show();
        }

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
