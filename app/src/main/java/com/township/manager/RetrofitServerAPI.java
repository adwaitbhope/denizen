package com.township.manager;

import com.google.gson.JsonArray;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RetrofitServerAPI {
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
    @POST("/register/existing/initiate/")
    Call<JsonArray> registrationStep2(
            @Field("application_id") String application_id,
            @Field("CHANNEL_ID") String channelId,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId,
            @QueryMap Map<String,Object> wingdata,
            @QueryMap Map<String,Object> amenitydata,
            @Field("admin_ids") Integer admin_ids,
            @Field("security_ids") Integer security_ids,
            @Field("wings_num") Integer wings_num,
            @Field("amenities_num") Integer amenities_num

    );

    @FormUrlEncoded
    @POST("/register/existing/verify/")
    Call<JsonArray> verifyChecksum(
            @Field("application_id") String application_id,
            @Field("ORDER_ID") String ORDER_ID
    );

    @GET("/register/check_verification/")
    Call<JsonArray>checkstatus(
            @Query("application_id") String application_id,
            @Query("email") String email
    );

    @FormUrlEncoded
    @POST("/notices/")
    Call<JsonArray> getNotices(
        @Field("username") String username,
        @Field("password") String password,
        @Field("timestamp") String timestamp
    );

    @FormUrlEncoded
    @POST("/notices/comments/new/")
    Call<JsonArray> addCommentToNotice(
            @Field("username") String username,
            @Field("password") String password,
            @Field("notice_id") String notice_id,
            @Field("content") String comment
    );


}
