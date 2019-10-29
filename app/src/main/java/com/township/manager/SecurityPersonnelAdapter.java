package com.township.manager;

import android.content.Context;
import android.content.Intent;
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


public class SecurityPersonnelAdapter extends RecyclerView.Adapter implements PopupMenu.OnMenuItemClickListener{

    ArrayList<SecurityPersonnel> dataset;
    Context context;
    SecurityPersonnel securityPersonnel;
    String TOWNSHIP_ID,username,password;

    public SecurityPersonnelAdapter(ArrayList<SecurityPersonnel> dataset, Context context) {
        this.dataset=dataset;
        this.context=context;
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

        final ViewHolder viewHolder = (ViewHolder) holder;
        securityPersonnel = dataset.get(position);

        final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + TOWNSHIP_ID + "/notices/" + securityPersonnel.getPersonnel_id()+ ".png";
        Picasso.get()
                .load(url)
                .into(viewHolder.securityPersonnelPhoto);

        viewHolder.securityPersonnelTimings.setText(securityPersonnel.getSecurity_personnel_timings_from()+"-"+securityPersonnel.getSecurity_personnel_timings_till());
        viewHolder.securityPersonnelPhone.setText(securityPersonnel.getSecurity_personnel_phone());
        viewHolder.securiyPersonnelName.setText(securityPersonnel.getSecurity_personnel_name());
        viewHolder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(context,view);
                popupMenu.setOnMenuItemClickListener(SecurityPersonnelAdapter.this);
                popupMenu.inflate(R.menu.menu_edit_security_personnel);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.edit_security_personnel:
                Intent intent=new Intent(context,AddSecurityPersonnelActivity.class);
                intent.putExtra("type","edit");
                intent.putExtra("id",securityPersonnel.getPersonnel_id());
                intent.putExtra("name",securityPersonnel.getSecurity_personnel_name());
                intent.putExtra("phone",securityPersonnel.getSecurity_personnel_phone());
                intent.putExtra("from",securityPersonnel.getSecurity_personnel_timings_from());
                intent.putExtra("till",securityPersonnel.getSecurity_personnel_timings_till());
                context.startActivity(intent);
                return true;
            case R.id.delete_security_personnel:
                deleteFromServer();
                return true;
        }
        return false;
    }

    private void deleteFromServer() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call=retrofitServerAPI.deleteSecurityPersonnel(
                username,
                password,
                securityPersonnel.getPersonnel_id()
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
                            Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        MaterialButton threeDots;
        ImageView securityPersonnelPhoto;
        TextView securiyPersonnelName,securityPersonnelPhone,securityPersonnelTimings;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);


            threeDots=itemView.findViewById(R.id.security_personnel_three_dots);
            securityPersonnelPhoto=itemView.findViewById(R.id.security_personnel_profile_image);
            securiyPersonnelName=itemView.findViewById(R.id.security_personnel_name);
            securityPersonnelTimings=itemView.findViewById(R.id.security_personnel_timings);
            securityPersonnelPhone=itemView.findViewById(R.id.security_personnel_phone_number);

        }
    }


}
