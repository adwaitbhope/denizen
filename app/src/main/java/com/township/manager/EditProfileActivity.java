package com.township.manager;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditProfileActivity extends AppCompatActivity {
    public EditText editText;
    public FloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        button = findViewById(R.id.edit_profile_floatingActionButton);
        //button.setOnClickListener(this);
    }



}
