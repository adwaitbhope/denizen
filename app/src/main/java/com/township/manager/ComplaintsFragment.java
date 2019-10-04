package com.township.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComplaintsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComplaintsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComplaintsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ComplaintsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComplaintsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComplaintsFragment newInstance(String param1, String param2) {
        ComplaintsFragment fragment = new ComplaintsFragment();
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
        View view = inflater.inflate(R.layout.fragment_complaints, container, false);

        SliderAdapter sliderAdapter = new SliderAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager());

        ComplaintsListFragment pendingListFragment = ComplaintsListFragment.newInstance(false);
        ComplaintsListFragment resolvedListFragment = ComplaintsListFragment.newInstance(true);

        sliderAdapter.addFragment(pendingListFragment, "Pending");
        sliderAdapter.addFragment(resolvedListFragment, "Resolved");

        // temporary dataset here
        ArrayList<Complaint> dataset = new ArrayList<>();
        Complaint complaint = new Complaint();
        complaint.setFirstName("Adwait");
        complaint.setLastName("Bhope");
        complaint.setWing("B3");
        complaint.setApartment("702");
        complaint.setTitle("Lift broken");
        complaint.setDescription("The only elevator in the building has stopped working. It becomes difficult for people living on the higher floors.");
        dataset.add(complaint);
        dataset.add(complaint);

        ArrayList<Complaint> pendingComplaints = getPendingComplaintsFromDatabase();
        ArrayList<Complaint> resolvedComplaints = getResolvedComplaintsFromDatabase();

        // pass these two lists as parameters below
        pendingListFragment.setDataset(dataset);
        resolvedListFragment.setDataset(dataset);

        ViewPager mSlideViewPager = (ViewPager) view.findViewById(R.id.complaints_view_pager);
        mSlideViewPager.setAdapter(sliderAdapter);
        mSlideViewPager.setOffscreenPageLimit(1);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.complaints_tab_layout);
        tabLayout.setupWithViewPager(mSlideViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_assignment_late_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_assignment_turned_in_black_24dp);

        FloatingActionButton button = view.findViewById(R.id.complaints_add_complaint_fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterComplaintActivity.class);
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    private ArrayList<Complaint> getPendingComplaintsFromDatabase() {
        ArrayList<Complaint> complaints = new ArrayList<>();

        // get pending complaints from database

        return complaints;
    }

    private ArrayList<Complaint> getResolvedComplaintsFromDatabase() {
        ArrayList<Complaint> complaints = new ArrayList<>();

        // get resolved complaints from database

        return complaints;
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
