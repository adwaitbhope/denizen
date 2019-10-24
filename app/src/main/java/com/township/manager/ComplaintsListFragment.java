package com.township.manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComplaintsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComplaintsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class    ComplaintsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Boolean resolved;

    RecyclerView recyclerView;
    ComplaintsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Complaint> dataset;

    private OnFragmentInteractionListener mListener;

    public ComplaintsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param resolved Parameter 1.
     * @return A new instance of fragment ComplaintsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComplaintsListFragment newInstance(Boolean resolved) {
        ComplaintsListFragment fragment = new ComplaintsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, resolved);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            resolved = getArguments().getBoolean(ARG_PARAM1);
        }

        layoutManager = new LinearLayoutManager(getContext());
        adapter = new ComplaintsAdapter(dataset, getContext(), resolved);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_complaints_list, container, false);
        recyclerView = view.findViewById(R.id.complaints_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        TextView filter;
        HorizontalScrollView horizontalScrollView;
        DBManager dbManager = new DBManager(getContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();
        int typeCol;
        typeCol = cursor.getColumnIndexOrThrow("Type");

        filter=view.findViewById(R.id.filtertv);
        horizontalScrollView=view.findViewById(R.id.horizontalScrollView);

        if(cursor.getString(typeCol).equals("admin")){
            filter.setVisibility(View.VISIBLE);
            horizontalScrollView.setVisibility(View.VISIBLE);
        }

        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        return view;

    }

    public void setDataset(ArrayList<Complaint> dataset) {
        this.dataset = dataset;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
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
