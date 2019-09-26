package com.township.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadService {
    @Multipart
    @POST("/register/new/")
    Call<ResponseBody> registerApplicant(
            @Part("applicant_name") RequestBody applicant_name,
            @Part("applicant_phone") RequestBody applicant_phone,
            @Part("applicant_email") RequestBody applicant_email,
            @Part("applicant_designation") RequestBody applicant_designation,
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("phone") RequestBody phone,
            @Part("geo_address ") RequestBody geo_address,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part MultipartBody.Part certificate
    );
    @FormUrlEncoded
    @POST("/payment/paytm/initiate/")
    Call<JsonArray> getChecksum(
            @Field("CHANNEL_ID") String channelId,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId
    );

    @POST("/payment/paytm/verify/")
    Call<JsonArray> checksumVerify(
        @Field("paytm_response") String paytm_response
    );



}
