package com.township.manager;

import android.content.Context;
import android.content.Intent;
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
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SecurityPersonnelAdapter extends RecyclerView.Adapter {

    ArrayList<SecurityPersonnel> dataset;
    Context context;
    String TOWNSHIP_ID, username, password;
    SecurityPersonnelDao personnelDao;

    String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public SecurityPersonnelAdapter(ArrayList<SecurityPersonnel> dataset, Context context, SecurityPersonnelDao personnelDao) {
        this.dataset = dataset;
        this.context = context;
        this.personnelDao = personnelDao;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_security_personnel, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SecurityPersonnel securityPersonnel = dataset.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + TOWNSHIP_ID + "/security_personnel/" + securityPersonnel.getPersonnel_id() + ".png";
        Picasso.get()
                .load(url)
                .into(viewHolder.photo);

        viewHolder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenImageViewActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });

        viewHolder.name.setText(securityPersonnel.getFirst_name() + " " + securityPersonnel.getLast_name());
        viewHolder.shift.setText(getFormattedTime(securityPersonnel.getShift_start()) + " to " + getFormattedTime(securityPersonnel.getShift_end()));
        viewHolder.phone.setText(securityPersonnel.getPhone());
        viewHolder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_security_personnel:
                                Intent intent = new Intent(context, AddSecurityPersonnelActivity.class);
                                intent.putExtra("type", "edit");
                                intent.putExtra("township_id", TOWNSHIP_ID);
                                intent.putExtra("id", securityPersonnel.getPersonnel_id());
                                intent.putExtra("first_name", securityPersonnel.getFirst_name());
                                intent.putExtra("last_name", securityPersonnel.getLast_name());
                                intent.putExtra("phone", securityPersonnel.getPhone());
                                intent.putExtra("shift_from", securityPersonnel.getShift_start());
                                intent.putExtra("shift_till", securityPersonnel.getShift_end());
                                context.startActivity(intent);
                                return true;
                            case R.id.delete_security_personnel:
                                deleteFromServer(securityPersonnel);
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.menu_edit_security_personnel);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

//    @Override
//    public boolean onMenuItemClick(MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.edit_security_personnel:
//                Intent intent = new Intent(context, AddSecurityPersonnelActivity.class);
//                intent.putExtra("type", "edit");
//                intent.putExtra("id", securityPersonnel.getPersonnel_id());
//                intent.putExtra("first_name", securityPersonnel.getFirst_name());
//                intent.putExtra("last_name", securityPersonnel.getLast_name());
//                intent.putExtra("phone", securityPersonnel.getPhone());
//                intent.putExtra("shift_from", securityPersonnel.getShift_start());
//                intent.putExtra("shift_till", securityPersonnel.getShift_end());
//                context.startActivity(intent);
//                return true;
//            case R.id.delete_security_personnel:
//                deleteFromServer();
//                return true;
//        }
//        return false;
//    }

    private void deleteFromServer(final SecurityPersonnel personnel) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.deleteSecurityPersonnel(
                username,
                password,
                personnel.getPersonnel_id()
        );
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("login_status").equals("1")) {
                        if (loginJson.getString("request_status").equals("1")) {
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();

                            int position = dataset.indexOf(personnel);
                            dataset.remove(position);
                            notifyItemRemoved(position);
//                            new SecurityPersonnelAsyncTask().execute(personnel);
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

    private class SecurityPersonnelAsyncTask extends AsyncTask<SecurityPersonnel, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(SecurityPersonnel... personnel) {
            personnelDao.delete(personnel[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton threeDots;
        ImageView photo;
        TextView name, phone, shift;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            threeDots = itemView.findViewById(R.id.recycler_security_personnel_three_dots);
            photo = itemView.findViewById(R.id.recycler_security_personnel_profile_image);
            name = itemView.findViewById(R.id.recycler_security_personnel_name_text_view);
            shift = itemView.findViewById(R.id.recycler_security_personnel_shift_text_view);
            phone = itemView.findViewById(R.id.recycler_security_personnel_phone_text_view);
        }
    }

    public String getFormattedDate(String timestamp) {
        String day = timestamp.substring(8, 10);
        String digit = day.substring(1);

        if (digit.equals("1")) {
            day = day + "st";
        } else if (digit.equals("2")) {
            day = day + "nd";
        } else if (digit.equals("3")) {
            day = day + "rd";
        } else {
            day = day + "th";
        }

        String month = months[Integer.valueOf(timestamp.substring(5, 7)) - 1];

        String year = timestamp.substring(2, 4);

        return month + " " + day + ", '" + year;
    }

    public String getFormattedTime(String timestamp) {
        int hourInt = Integer.valueOf(timestamp.substring(0, 2));
        String period = "am";
        if (hourInt > 12) {
            hourInt -= 12;
            period = "pm";
        }
        String hour = String.valueOf(hourInt);
        if (hourInt == 0) {
            hour = "00";
        }
        String minute = "00";
        int minuteInt = Integer.valueOf(timestamp.substring(3, 5));
        if (minuteInt != 0) {
            minute = String.valueOf(minuteInt);
        }
        return hour + ":" + minute + " " + period;
    }

}
