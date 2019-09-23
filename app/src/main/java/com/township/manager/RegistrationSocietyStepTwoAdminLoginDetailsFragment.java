package com.township.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationSocietyStepTwoAdminLoginDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationSocietyStepTwoAdminLoginDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationSocietyStepTwoAdminLoginDetailsFragment extends Fragment implements NumberPicker.OnValueChangeListener{
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

        admin_number_picker = view.findViewById(R.id.no_of_admins_numberpicker);
        admin_number_picker.setMinValue(0);
        admin_number_picker.setMaxValue(99);
        admin_number_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%01d", i);
            }
        });
        admin_number_picker.setOnValueChangedListener(this);


        security_number_picker = view.findViewById(R.id.no_of_security_desks_numberpicker);
        security_number_picker.setMinValue(0);
        security_number_picker.setMaxValue(99);
        security_number_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%01d", i);
            }
        });
        security_number_picker.setOnValueChangedListener(this);

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
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

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
