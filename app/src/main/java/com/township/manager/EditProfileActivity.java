package com.township.manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pusher.pushnotifications.PushNotifications;

import static com.township.manager.ProfileActivity.PROFILE_EDITED;
import static com.township.manager.ProfileActivity.PROFILE_NOT_EDITED;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showExitDialog();
                return true;

            case R.id.edit_profile_save_changes_button:
                showSaveChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showExitDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Discard changes?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(PROFILE_NOT_EDITED);
                        EditProfileActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showSaveChangesDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Save changes?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveChanges();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveChanges() {
        // show progress bar

        // send request to server here

        // move this function call to on successful response from server
        onResponseSuccess();
    }

    private void onResponseSuccess() {
        // hide progress bar

        // update local database here

        setResult(PROFILE_EDITED);
        finish();

    }


}
