package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
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

    ComplaintsListFragment pendingListFragment, resolvedListFragment;
    public static final int ADD_COMPLAINT_REQUEST = 69;
    public static final int ADD_COMPLAINT_RESULT = 70;

    public String username, password, townshipId, type;
    Boolean isAdmin = false;


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

        DBManager dbManager = new DBManager(getContext().getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        townshipId = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));
        type = cursor.getString(cursor.getColumnIndexOrThrow("Type"));
        if (type.equals("admin")) {
            isAdmin = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaints, container, false);

        SliderAdapter sliderAdapter = new SliderAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager());

        pendingListFragment = ComplaintsListFragment.newInstance(false, username, password, townshipId, isAdmin);
        resolvedListFragment = ComplaintsListFragment.newInstance(true, username, password, townshipId, isAdmin);

        sliderAdapter.addFragment(pendingListFragment, "Pending");
        sliderAdapter.addFragment(resolvedListFragment, "Resolved");

        DBManager dbManager = new DBManager(getContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();
        int typeCol;
        typeCol = cursor.getColumnIndexOrThrow("Type");

        ViewPager mSlideViewPager = (ViewPager) view.findViewById(R.id.complaints_view_pager);
        mSlideViewPager.setAdapter(sliderAdapter);
        mSlideViewPager.setOffscreenPageLimit(1);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.complaints_tab_layout);
        tabLayout.setupWithViewPager(mSlideViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_assignment_late_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_assignment_turned_in_black_24dp);

        FloatingActionButton button = view.findViewById(R.id.complaints_add_complaint_fab);

        if (cursor.getString(typeCol).equals("resident")) {
            button.setVisibility(View.VISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterComplaintActivity.class);
                startActivityForResult(intent, ADD_COMPLAINT_REQUEST);
            }
        });

        return view;
    }

    public void updateRecyclerView() {
        if (pendingListFragment.getContext() != null) {
            pendingListFragment.updateRecyclerView();
        }
        if (resolvedListFragment.getContext() != null) {
            resolvedListFragment.updateRecyclerView();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_COMPLAINT_REQUEST) {
            if (resultCode == ADD_COMPLAINT_RESULT) {
                Log.d("added", "add");
                pendingListFragment.updateRecyclerView();
                pendingListFragment.recyclerView.smoothScrollToPosition(0);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
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
