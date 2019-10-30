package com.township.manager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.township.manager.NoticeBoardFragment.ADD_NOTICE_RESULT;

public class ServiceVendorAdapter extends RecyclerView.Adapter {

    ArrayList<ServiceVendors> dataset;
    Context context;
    String TOWNSHIP_ID;
    Intent intent;
    String username, password;
    ServiceVendorDao serviceVendorDao;
    AppDatabase appDatabase;

    public ServiceVendorAdapter(ArrayList<ServiceVendors> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_service_vendor, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        DBManager dbManager = new DBManager(context);
        Cursor cursor = dbManager.getDataLogin();
        int typeCol = cursor.getColumnIndexOrThrow("Type");
        cursor.moveToFirst();
        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        String type = cursor.getString(typeCol);

        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        if (type.equals("admin")) {
            viewHolder.threeDots.setVisibility(View.VISIBLE);
        }

        TOWNSHIP_ID = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final ServiceVendors serviceVendors = dataset.get(position);
        viewHolder.name.setText(serviceVendors.getFirst_name() + " " + serviceVendors.getLast_name());
        viewHolder.description.setText(serviceVendors.getWork());

        final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + TOWNSHIP_ID + "/servicevendors/" + serviceVendors.getVendor_id() + ".png";
        Picasso.get()
                .load(url)
                .into(viewHolder.dispayPic);

        viewHolder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_item:
                                intent = new Intent(context, AddServiceVendorActivity.class);
                                intent.putExtra("first_name", serviceVendors.getFirst_name());
                                intent.putExtra("last_name", serviceVendors.getLast_name());
                                intent.putExtra("work", serviceVendors.getPhone());
                                intent.putExtra("phone", serviceVendors.getPhone());
                                intent.putExtra("request", "0");
                                intent.putExtra("id", serviceVendors.getVendor_id());
                                context.startActivity(intent);
                                break;
                            case R.id.delete_item:
                                deleteServiceVendor(serviceVendors);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.servcie_vendors_pop_up_menu);
                popupMenu.show();
            }

        });
        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri u = Uri.parse("tel:" + serviceVendors.getPhone());
                Intent i = new Intent(Intent.ACTION_DIAL, u);
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    private void deleteServiceVendor(final ServiceVendors serviceVendors) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.deleteServiceVendors(username, password, serviceVendors.getVendor_id());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("login_status").equals("1")) {
                        if (loginJson.getString("request_status").equals("1")) {
                            int position = dataset.indexOf(serviceVendors);
                            dataset.remove(position);
                            notifyItemRemoved(position);

                            new DeleteServiceVendor().execute(serviceVendors);
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

    private class DeleteServiceVendor extends AsyncTask<ServiceVendors, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ServiceVendors... serviceVendors) {
            serviceVendorDao = appDatabase.serviceVendorDao();
            serviceVendorDao.delete(serviceVendors[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
//            finish();
            super.onPostExecute(aVoid);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, description;
        ImageView dispayPic;
        MaterialButton threeDots;
        MaterialButton call;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vendor_name);
            description = itemView.findViewById(R.id.vendor_service);
            dispayPic = itemView.findViewById(R.id.vendor_profile_image);
            call = itemView.findViewById(R.id.vendor_call_button);
            threeDots = itemView.findViewById(R.id.service_vendor_three_dots);
        }
    }
}
