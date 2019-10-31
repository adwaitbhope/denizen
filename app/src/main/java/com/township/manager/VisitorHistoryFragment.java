package com.township.manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VisitorHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VisitorHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitorHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerView;
    VisitorHistoryAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<Visitor> dataset = new ArrayList<>();
    ArrayList<Visitor> temporaryDataset;
    AppDatabase appDatabase;

    VisitorDao visitorDao;
    Context context;

    public VisitorHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitorHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitorHistoryFragment newInstance(String param1, String param2) {
        VisitorHistoryFragment fragment = new VisitorHistoryFragment();
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

        appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        visitorDao = appDatabase.visitorDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitor_history, container, false);

        DBManager dbManager = new DBManager(getContext().getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        String townshipId = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));

        adapter = new VisitorHistoryAdapter(dataset, getContext(), townshipId);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.visitor_history_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        updateRecyclerView();

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
        this.context = context;
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

    public void updateRecyclerView() {
        new GetVisitorsFromDatabase().execute();
    }

    private class GetVisitorsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (appDatabase == null) {
                appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "app-database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
            visitorDao = appDatabase.visitorDao();
            WingDao wingDao = appDatabase.wingDao();
            temporaryDataset = (ArrayList<Visitor>) visitorDao.getAll();
            for (Visitor visitor : temporaryDataset) {
                visitor.setWing(wingDao.getWingName(visitor.getWing_id()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dataset.clear();
            dataset.addAll(temporaryDataset);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            super.onPostExecute(aVoid);
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
