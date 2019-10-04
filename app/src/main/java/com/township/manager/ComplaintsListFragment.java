package com.township.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComplaintsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComplaintsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComplaintsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    boolean complaintExpanded = false;
    private OnFragmentInteractionListener mListener;

    public ComplaintsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComplaintsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComplaintsListFragment newInstance(String param1, String param2) {
        ComplaintsListFragment fragment = new ComplaintsListFragment();
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

        final View view = inflater.inflate(R.layout.fragment_complaints_list, container, false);
        final ImageView expandButton = view.findViewById(R.id.complaint_expand_button);
        View clickArea = view.findViewById(R.id.complaint_card_click_area);
        clickArea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (complaintExpanded) {
                    expandButton.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    ((MaterialButton) view.findViewById(R.id.resolve_complaint_button)).setVisibility(View.GONE);
                    ((MaterialTextView) view.findViewById(R.id.complaint_description_textview)).setVisibility(View.GONE);
                    ((ImageButton) view.findViewById(R.id.complaint_image_button)).setVisibility(View.GONE);
                    complaintExpanded = false;
                } else {
                    expandButton.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    ((MaterialButton) view.findViewById(R.id.resolve_complaint_button)).setVisibility(View.VISIBLE);
                    ((MaterialTextView) view.findViewById(R.id.complaint_description_textview)).setVisibility(View.VISIBLE);
                    ((ImageButton) view.findViewById(R.id.complaint_image_button)).setVisibility(View.VISIBLE);
                    complaintExpanded = true;
                }
            }
        });
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
