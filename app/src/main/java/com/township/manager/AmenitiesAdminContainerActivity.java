package com.township.manager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentTransaction;

import static com.township.manager.R.color.white;

public class AmenitiesAdminContainerActivity extends AppCompatActivity implements AmenitiesFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities_admin_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.amenities_admin_container_toolbar);
//        toolbar.setTitleTextColor(getColor(R.color.secondaryColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Amenities");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.amenities_admin_container_frame, new AmenitiesFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.amenities_action_bar_menu, menu);

        Drawable historydrawable = menu.findItem(R.id.action_booking_history_item).getIcon();
        historydrawable = DrawableCompat.wrap(historydrawable);
        DrawableCompat.setTint(historydrawable, ContextCompat.getColor(this, white));
        menu.findItem(R.id.action_booking_history_item).setIcon(historydrawable);


        Drawable listdrawable = menu.findItem(R.id.action_booking_list_item).getIcon();
        listdrawable = DrawableCompat.wrap(listdrawable);
        DrawableCompat.setTint(listdrawable, ContextCompat.getColor(this, white));
        menu.findItem(R.id.action_booking_list_item).setIcon(listdrawable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_booking_history_item) {

            startActivity (new Intent(this, ResidentBookingHistoryActivity.class));
            return true;
        }
        if (id == R.id.action_booking_list_item) {

            // Do something
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


