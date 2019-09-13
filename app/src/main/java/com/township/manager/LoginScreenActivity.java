package com.township.manager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class LoginScreenActivity extends FragmentActivity {
    public Button forgotpassword,registersociety,contactus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        forgotpassword = findViewById(R.id.forgotpassword);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPasswordDialogFragment();
            }
        });
        registersociety = findViewById(R.id.registersociety);
        registersociety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterSocietyDialogFragment();

            }
        });
        contactus = findViewById(R.id.contactus);
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }
    public void openDialog() {
        ContactUsDialog exampleDialog = new ContactUsDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void openForgotPasswordDialogFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new ForgotPasswordFragment();
        dialogFragment.show(ft, getString(R.string.dialog));
    }
    public void openRegisterSocietyDialogFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new RegistrationDialogFragment();
        dialogFragment.show(ft, getString(R.string.dialog));
    }
}
