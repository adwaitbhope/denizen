package com.township.manager;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ContactUsDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


//        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());//
//                materialAlertDialogBuilder.setTitle("Contact Us")
//                .setMessage("E-mail: hi@pvgcoet.ac.in\nPhone: +91-69420-69420")
//                .setPositiveButton("Close", null)
//                .show();


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_call_black_24dp);
        builder.setTitle("CONTACT US")
                .setMessage("E-mail: hi@pvgcoet.ac.in\nPhone: +91-69420-69420")
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

//        return materialAlertDialogBuilder.create();
        return builder.create();
    }
}
