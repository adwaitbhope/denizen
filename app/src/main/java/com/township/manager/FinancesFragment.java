package com.township.manager;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FinancesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FinancesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinancesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    CalendarView calendarView;
    TextInputLayout titleTextInputLayout, typeOfExpenseTextInputLayout, amountTextInputLayout, paidViaTextInputLayout, chequeNumberTextInputLayout;
    EditText titleEdit, amountEdit, chequeNoEdit;
    String username, password, day, month, year, chequeNumber;
    MaterialButton saveButton;
    ExtendedFloatingActionButton generateReportFloatingActionButton;
    AutoCompleteTextView typeOfExpenseAutoCompleteTextView, paidViaAutoCompleteTextView;
    Date today;

    public FinancesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FinancesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FinancesFragment newInstance(String param1, String param2) {
        FinancesFragment fragment = new FinancesFragment();
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
        DBManager dbManager = new DBManager(getContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finances, container, false);

        titleTextInputLayout = view.findViewById(R.id.finance_title_textInputLayout);
        titleEdit = view.findViewById(R.id.finance_title_textInputLayout_child);

        amountTextInputLayout = view.findViewById(R.id.finance_amount_textInputLayout);
        amountEdit = view.findViewById(R.id.finance_amount_textInputLayout_child);

        typeOfExpenseTextInputLayout = view.findViewById(R.id.finance_type_of_expense_dropdown_til);
        typeOfExpenseAutoCompleteTextView = view.findViewById(R.id.finance_type_of_expense_dropdown);

        paidViaTextInputLayout = view.findViewById(R.id.finance_mode_of_payment_payment_mode_til);
        paidViaAutoCompleteTextView = view.findViewById(R.id.finance_mode_of_payment_mode_dropdown);

        chequeNumberTextInputLayout = view.findViewById(R.id.finance_cheque_no_payment_mode_til);
        chequeNoEdit = view.findViewById(R.id.finance_cheque_no_mode_edit_text);

        calendarView = view.findViewById(R.id.finance_calendarView);

        saveButton = view.findViewById(R.id.finances_save_button);

        generateReportFloatingActionButton = view.findViewById(R.id.finances_report_ex_fab);

        String[] type = new String[]{"Credit", "Debit"};

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                getContext(),
                R.layout.dropdown_menu_popup_item,
                type
        );

        typeOfExpenseAutoCompleteTextView.setAdapter(adapterType);

        String[] paymentmode = new String[]{"Cash", "Cheque", "Online"};

        ArrayAdapter<String> adapterPaymentMode = new ArrayAdapter<>(
                getContext(),
                R.layout.dropdown_menu_popup_item,
                paymentmode
        );

        paidViaAutoCompleteTextView.setAdapter(adapterPaymentMode);

        paidViaAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (paidViaAutoCompleteTextView.getText().toString().equals("Cheque")) {
                    chequeNumberTextInputLayout.setVisibility(View.VISIBLE);
                } else {
                    chequeNumberTextInputLayout.setVisibility(View.GONE);
                }
            }
        });

        generateReportFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinanceReport();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFinanceEntry();
            }
        });

        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        today = new Date(calendarView.getDate());


        String selectedDate = sdf.format(today);
        try {
            today = sdf.parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        day = selectedDate.substring(0, 2);
        month = selectedDate.substring(3, 5);
        year = selectedDate.substring(6, 10);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int yearCal, int monthCal, int dayCal) {
                monthCal++;
                month = String.valueOf(monthCal);
                day = String.valueOf(dayCal);
                year = String.valueOf(dayCal);

            }
        });

        if (paidViaAutoCompleteTextView.getText().toString().equals("Cheque")) {
            chequeNoEdit.setVisibility(View.VISIBLE);
        }


//        fab = view.findViewById(R.id.fab);
//        fab1 = view.findViewById(R.id.fab1);
//        fab2 = view.findViewById(R.id.fab2);
//
//        fabLayout1 = (CoordinatorLayout) view.findViewById(R.id.fabLayout1);
//        fabLayout2 = (CoordinatorLayout) view.(R.id.fabLayout2);
//        fabLayout = (CoordinatorLayout) view.findViewById(R.id.fabLayout);
//
//        fabBGLayout = view.findViewById(R.id.fabBGLayout);

//        fabLayout1.setVisibility(View.INVISIBLE);
//        fabLayout2.setVisibility(View.INVISIBLE);
//
//        fab1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getContext(), AddFinancesActivity.class));
//            }
//        });
//
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isFABOpen) {
//                    showFABMenu();
//                } else {
//                    closeFABMenu();
//                }
//            }
//        });
//        fabBGLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                closeFABMenu();
//            }
//        });
//
//        FloatingActionButton fabMenu = view.findViewById(R.id.finances_menu_fab);
//        FloatingActionButton fabCredit = view.findViewById(R.id.finances_credit_fab);
//        FloatingActionButton fabDebit = view.findViewById(R.id.finances_debit_fab);
//        View bgFabMenu = view.findViewById(R.id.bg_fab_menu);
//
//        fabMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isFABOpen) {
//                    showFABMenu();
//                } else {
//                    closeFABMenu();
//                }
//            }
//        });
//
//        bgFabMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                closeFABMenu();
//            }
//        });


        return view;
    }

    private void addFinanceEntry() {

        String title, amount;
        int typeOfExpense, paymentMode;
        title = titleEdit.getText().toString();
        amount = amountEdit.getText().toString();

        typeOfExpense = 0;
        paymentMode = 0;

        if (typeOfExpenseAutoCompleteTextView.getText().toString().equals("Credit"))
            typeOfExpense = 1;
        else if (typeOfExpenseAutoCompleteTextView.getText().toString().equals("Debit"))
            typeOfExpense = 2;

        if (paidViaAutoCompleteTextView.getText().toString().equals("Cash"))
            paymentMode = 1;
        else if (paidViaAutoCompleteTextView.getText().toString().equals("Cheque")) {
            paymentMode = 2;
            chequeNumber = chequeNoEdit.getText().toString();
        } else if (paidViaAutoCompleteTextView.getText().toString().equals("Online"))
            paymentMode = 3;

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.addNewFinance(
                username,
                password,
                title,
                typeOfExpense,
                amount,
                paymentMode,
                chequeNumber,
                day,
                month,
                year

        );
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("login_status").equals("1")) {
                        if (loginJson.getString("request_status").equals("1")) {
                            Toast.makeText(getActivity(), "Finance Added", Toast.LENGTH_SHORT).show();
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

    private void getFinanceReport() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.generateFinanceReport(
                username,
                password
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("login_status").equals("1")) {
                        if (loginJson.getString("authorization").equals("1")) {
                            Toast.makeText(getActivity(), "Report has been sent to your email!", Toast.LENGTH_SHORT).show();
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

//    private void showFABMenu() {
//        isFABOpen = true;
//        fabLayout1.setVisibility(View.VISIBLE);
//        fabLayout2.setVisibility(View.VISIBLE);
//
//        fabBGLayout.setVisibility(View.VISIBLE);
//        fab.animate().rotationBy(135);
//        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_10));
//        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_20));
//
//
//    }

//    private void closeFABMenu() {
//        isFABOpen = false;
//        fabBGLayout.setVisibility(View.GONE);
//        fab.animate().rotation(0);
//        fabLayout1.animate().translationY(0);
//        fabLayout2.animate().translationY(0);
//        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                if (!isFABOpen) {
//                    fabLayout1.setVisibility(View.GONE);
//                    fabLayout2.setVisibility(View.GONE);
//                }
///*                if (fab.getRotation() != -180) {
//                    fab.setRotation(-180);
//                }*/
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//    }
//
//
//    public void onBackPressed() {
//        if (isFABOpen) {
//            closeFABMenu();
//        } else {
//            onBackPressed();
//        }
//    }

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
