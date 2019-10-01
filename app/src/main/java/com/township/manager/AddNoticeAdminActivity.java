package com.township.manager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddNoticeAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice_admin);


        Toolbar toolbar = (Toolbar) findViewById(R.id.add_notice_admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Notice");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
