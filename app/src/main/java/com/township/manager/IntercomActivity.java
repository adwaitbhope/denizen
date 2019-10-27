package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class IntercomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercom);



        String[] WINGS = new String[] {"Wing A", "Wing B", "Wing C"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        WINGS);

        AutoCompleteTextView wingFilledExposedDropdown =
                findViewById(R.id.intercom_wing_selector_exposed_dropdown);
        wingFilledExposedDropdown.setAdapter(adapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.intercom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Intercom");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
