package com.township.manager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoticeBoardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoticeBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoticeBoardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    AppDatabase appDatabase;
    ArrayList<Notice> dataset = new ArrayList<>();
    NoticeDao noticeDao;

    String userType;

    public NoticeBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoticeBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoticeBoardFragment newInstance(String param1, String param2) {
        NoticeBoardFragment fragment = new NoticeBoardFragment();
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

        DBManager dbManager = new DBManager(getContext().getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        userType = cursor.getString(cursor.getColumnIndexOrThrow("Type"));
        noticeDao = appDatabase.noticeDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notice_board, container, false);

        if (userType.equals("admin")) {
            FloatingActionButton addNoticeButton = view.findViewById(R.id.notice_board_add_notice_fab);
            addNoticeButton.setVisibility(View.VISIBLE);
            addNoticeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), AddNoticeAdminActivity.class);
                    startActivity(intent);
                }
            });
        }

        updateRecyclerView();

        recyclerView = view.findViewById(R.id.notice_board_recycler_view);
        adapter = new NoticesAdapter(dataset, getContext());
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
        recyclerView.smoothScrollToPosition(0);
    }

    public void updateRecyclerView() {
//        new Thread() {
//            public void run() {
//                NoticesAsyncTask asyncTask = new NoticesAsyncTask();
//                asyncTask.execute();
//            }
//        }.start();
        new NoticesAsyncTask().execute();
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

    private class NoticesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dataset.clear();
            dataset.addAll(noticeDao.getAll());

            for (Notice notice : dataset) {
                notice.setWings((ArrayList<Wing>) noticeDao.getWings(notice.getNotice_id()));
                notice.setComments((ArrayList<Notice.Comment>) noticeDao.getComments(notice.getNotice_id()));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

}
