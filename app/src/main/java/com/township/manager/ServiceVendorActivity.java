package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ServiceVendorActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    TextView popUpMenu;
    RecyclerView recyclerView;
    ServiceVendotAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    AppDatabase appDatabase;
    ArrayList<ServiceVendors> dataset = new ArrayList<>();
    ArrayList<ServiceVendors> temporaryDataset = new ArrayList<>();
    ServiceVendorDao serviceVendorDao;

    public static final int ADD_SERVICE_VENDOR_REQUEST = 69;
    public static final int ADD_SERVICE_VENODR_RESULT = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_vendor);
        popUpMenu=findViewById(R.id.three_dots);
        popUpMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(ServiceVendorActivity.this,view);
                popupMenu.setOnMenuItemClickListener(ServiceVendorActivity.this);
                popupMenu.inflate(R.menu.servcie_vendors_pop_up_menu);
                popupMenu.show();
            }

        });

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        FloatingActionButton addServiceVendor = findViewById(R.id.add_vendor_floatingActionButton);
        addServiceVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServiceVendorActivity.this, AddServiceVendorActivity.class));


            }
        });

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }
}
