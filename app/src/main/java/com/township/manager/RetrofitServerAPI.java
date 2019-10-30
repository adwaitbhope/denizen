package com.township.manager;

import com.google.gson.JsonArray;
import com.squareup.moshi.Json;

import org.json.JSONArray;

import java.io.FileInputStream;
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
            @QueryMap Map<String, Object> wingdata,
            @QueryMap Map<String, Object> amenitydata,
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

    @FormUrlEncoded
    @POST("/complaints/new/")
    Call<JsonArray> addComplaint(
            @Field("username") String username,
            @Field("password") String password,
            @Field("title") String title,
            @Field("description") String description
    );

    @GET("/register/check_verification/")
    Call<JsonArray> checkstatus(
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

    @FormUrlEncoded
    @POST("notices/new/")
    Call<JsonArray> addNotice(
            @Field("username") String username,
            @Field("password") String password,
            @Field("title") String title,
            @Field("description") String description,
            @Field("num_wings") String num_wings,
            @QueryMap Map<String, String> wing_ids
    );

    @FormUrlEncoded
    @POST("/visitors/new/")
    Call<JsonArray> addNewVisitor(
            @Field("username") String username,
            @Field("password") String password,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("wing_id") String wing_id,
            @Field("apartment") String apartment
    );

    @FormUrlEncoded
    @POST("visitors/get/")
    Call<JsonArray> getVisitorHistory(
            @Field("username") String username,
            @Field("password") String password,
            @Field("timestamp") String timestamp
    );

    @FormUrlEncoded
    @POST("profile/edit/")
    Call<JsonArray> editProfile(
            @Field("org_username") String org_username,
            @Field("org_password") String org_password,
            @Field("username") String username,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("designation") String designation,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name
    );

    @GET("profile/check_username_availability/")
    Call<JsonArray> checkUsernameAvailability(
            @Query("username") String username
    );

    @FormUrlEncoded
    @POST("/complaints/")
    Call<JsonArray> getComplaints(
            @Field("username") String username,
            @Field("password") String password,
            @Field("timestamp") String timestamp,
            @Field("resolved") Boolean resolved

    );

    @FormUrlEncoded
    @POST("/complaints/resolve/")
    Call<JsonArray> resolveComplaints(
            @Field("username") String username,
            @Field("password") String password,
            @Field("complaint_id") String complaint_id
    );

    @FormUrlEncoded
    @POST("/maintenance/")
    Call<JsonArray> getMaintenance(
            @Field("username") String username,
            @Field("password") String password,
            @Field("timestamp") String timestamp
    );

    @FormUrlEncoded
    @POST("/maintenance/add/")
    Call<JsonArray> addMaintenance(
            @Field("username") String username,
            @Field("password") String password,
            @Field("wing_id") String wing_id,
            @Field("apartment") String apartment,
            @Field("amount") String amount,
            @Field("payment_mode") String payment_mode,
            @Field("cheque_no ") String cheque_no
    );

    @FormUrlEncoded
    @POST("/maintenance/pay/initiate/")
    Call<JsonArray> intiateMaintenancePayment(
            @Field("username") String username,
            @Field("password") String password,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("CHANNEL_ID") String channelId,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId
    );

    @FormUrlEncoded
    @POST("/maintenance/pay/verify/")
    Call<JsonArray> verifyMaintenancePayment(
            @Field("username") String username,
            @Field("password") String passsword,
            @Field("ORDER_ID") String ORDER_ID
    );

    @FormUrlEncoded
    @POST("/amenities/")
    Call<JsonArray> getAmenities(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/amenities/availability/")
    Call<JsonArray> getAmenitySlots(
            @Field("username") String username,
            @Field("password") String password,
            @Field("amenity_id") String amenity_id,
            @Field("day") int day,
            @Field("month") int month,
            @Field("year") int year
    );

    @FormUrlEncoded
    @POST("amenities/book/")
    Call<JsonArray> bookAmenity(
            @Field("username") String username,
            @Field("password") String password,
            @Field("amenity_id") String amenity_id,
            @Field("day") int day,
            @Field("month") int month,
            @Field("year") int year,
            @Field("hour") int hour
    );

    @FormUrlEncoded
    @POST("/amenities/book/pay/initiate/")
    Call<JsonArray> initiateAmenityBookingPayment(
            @Field("username") String username,
            @Field("password") String password,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("CHANNEL_ID") String channelId,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId
    );

    @FormUrlEncoded
    @POST("/amenities/book/pay/verify/")
    Call<JsonArray> verifyAmenityBookingPayment(
            @Field("username") String username,
            @Field("password") String password,
            @Field("ORDER_ID") String ORDER_ID,
            @Field("amenity_id") String amenity_id,
            @Field("day") int day,
            @Field("month") int month,
            @Field("year") int year,
            @Field("hour") int hour
    );

    @FormUrlEncoded
    @POST("/amenities/membership/")
    Call<JsonArray> getMembershipPayments(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/amenities/membership/check/")
    Call<JsonArray> checkMembershipStatus(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/amenities/booking_history/")
    Call<JsonArray> getAmenityBookingHistory(
            @Field("username") String username,
            @Field("password") String password,
            @Field("with_payments_only") Boolean with_payments_only
    );

    @FormUrlEncoded
    @POST("/amenities/membership/pay/initiate/")
    Call<JsonArray> initiateAmenityMembershipPayment(
            @Field("username") String username,
            @Field("password") String password,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("CHANNEL_ID") String channelId,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId
    );

    @FormUrlEncoded
    @POST("/amenities/membership/pay/verify/")
    Call<JsonArray> verifyAmenityMembershipPayment(
            @Field("username") String username,
            @Field("password") String password,
            @Field("ORDER_ID") String ORDER_ID
    );

    @FormUrlEncoded
    @POST("/finances/")
    Call<JsonArray> getFinances(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/finances/credit/new/")
    Call<JsonArray> addFinanacesCredit(
            @Field("username") String username,
            @Field("password") String password,
            @Field("day") String credit_day,
            @Field("month") String credit_month,
            @Field("year") String credit_year,
            @Field("mode_of_payment") String credit_mode_of_payment,
            @Field("amount") String credit_amount,
            @Field("title") String credit_title
    );

    @FormUrlEncoded
    @POST("/finances/debit/new/")
    Call<JsonArray> addFinanacesDebit(
            @Field("username") String username,
            @Field("password") String password,
            @Field("day") String debit_day,
            @Field("month") String debit_month,
            @Field("year") String debit_year,
            @Field("mode_of_payment") String debit_mode_of_payment,
            @Field("amount") String debit_amount,
            @Field("title") String debit_title
    );


}
