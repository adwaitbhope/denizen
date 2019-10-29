package com.township.manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.pusher.pushnotifications.PushNotifications;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.room.Room;

public class LogOutDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();

                DBManager dbManager = new DBManager(getContext());
                dbManager.deleteAll();

                new Thread() {
                    public void run() {
                        AppDatabase appDatabase = Room.databaseBuilder(getContext().getApplicationContext(),
                                AppDatabase.class, "app-database")
                                .fallbackToDestructiveMigration()
                                .build();

                        appDatabase.intercomDao().deleteAll();
                        appDatabase.membershipPaymentDao().deleteAll();
                        appDatabase.amenityBookingDao().deleteAll();
                        appDatabase.amenityDao().deleteAll();
                        appDatabase.visitorDao().deleteAll();
                        appDatabase.commentDao().deleteAll();
                        appDatabase.noticeWingsDao().deleteAll();
                        appDatabase.noticeDao().deleteAll();
                        appDatabase.maintenanceDao().deleteAll();
                        appDatabase.complaintDao().deleteAll();
                        appDatabase.residentDao().deleteAll();
                        appDatabase.wingDao().deleteAll();
                    }
                }.start();

                try {
                    PushNotifications.clearAllState();
                    PushNotifications.stop();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(getContext(), LoginScreenActivity.class));

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }
}
