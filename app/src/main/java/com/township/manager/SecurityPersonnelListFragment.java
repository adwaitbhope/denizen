package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SecurityPersonnelListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SecurityPersonnelListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecurityPersonnelListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerView;
    SecurityPersonnelAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<SecurityPersonnel> dataset = new ArrayList<>();
    ArrayList<SecurityPersonnel> temporaryDataset = new ArrayList<>();

    AppDatabase appDatabase;
    SecurityPersonnelDao securityPersonnelDao;

    public static final int ADD_SECURITY_PERSONNEL_REQUEST = 69;
    public static final int ADD_SECURITY_PERSONNEL_RESULT = 70;

    public SecurityPersonnelListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecurityPersonnelListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecurityPersonnelListFragment newInstance(String param1, String param2) {
        SecurityPersonnelListFragment fragment = new SecurityPersonnelListFragment();
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
        securityPersonnelDao = appDatabase.securityPersonnelDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_security_personnel_list, container, false);
        FloatingActionButton fab = view.findViewById(R.id.add_security_personnel_floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddSecurityPersonnelActivity.class);
                intent.putExtra("type", "add");
                startActivityForResult(intent, ADD_SECURITY_PERSONNEL_REQUEST);

            }
        });

        updateRecyclerView();

        recyclerView = view.findViewById(R.id.security_personnel_recycler_view);
        adapter = new SecurityPersonnelAdapter(dataset, getContext());

        DBManager dbManager = new DBManager(getContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        adapter.username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        adapter.password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        adapter.TOWNSHIP_ID = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        return view;

    }

    public void updateRecyclerView() {
        new SecurityPersonnelAsyncTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
//        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_SECURITY_PERSONNEL_REQUEST) {
            if (resultCode == ADD_SECURITY_PERSONNEL_RESULT) {
                updateRecyclerView();
                recyclerView.smoothScrollToPosition(0);
            }
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

    private class SecurityPersonnelAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            temporaryDataset.clear();
            temporaryDataset.addAll(securityPersonnelDao.getAll());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataset.clear();
            dataset.addAll(temporaryDataset);

            for (SecurityPersonnel personnel : dataset) {
                Log.d("personnel", personnel.getFirst_name());
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
