package com.depex.okeyclick.sp.api;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface ProjectAPI {

    @GET("get_city.php")
    Call<JsonObject> getCities(@Query("apikey") String apikey, @Query("s_id") String stateId);

    @GET("get_service_list.php")
    Call<JsonObject> getServices(@Query("apikey") String apikey);

    @GET("country_list.php")
    Call<JsonObject> getCountries(@Query("apikey") String apikey);

    @GET("get_package.php")
    Call<JsonObject> getPackages(@Query("apikey") String apikey );

    @GET("state_list.php")
    Call<JsonObject> getStates(@Query("apikey") String apikey, @Query("c_id") String countryId);

    @GET("get_subservice.php")
    Call<JsonObject> getSubServices(@Query("apikey") String apikey, @Query("cat_id") String catId);

    @GET("send_otp.php")
    Call<JsonObject> getOtp(@Query("apikey") String apikey, @Query("mobile") String mobile);

    @POST("user_register.php")
    Call<String> signUp(@Body String body);


    @POST("user_login.php")
    Call<String> login(@Body String body);

    @POST("go_online_offline.php")
    Call<String> setStatus(@Body String body);

    @POST("update_location.php")
    Call<String> updateLocation(@Body String body);

    @POST("update_DeviceToken.php")
    Call<String> updateFcmToken(@Body String body);

    @POST("check_token.php")
    Call<String> checkToken(@Body String body);

    @POST("request_accept.php")
    Call<String> acceptRequest(@Body String body);

    @GET("maps/api/directions/json")
    Call<String> getPolyLineDirection(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String serverKey);

    @POST("is_any_notification.php")
    Call<String> getNotification(@Body String body);

    @POST("running_task.php")
    Call<String> changeStatusRunningTask(@Body String body);

    @POST("task_procedure.php")
    Call<String> changeStatus(@Body String body);

    @POST("generate_task_invoice.php")
    Call<String> generateTaskInvoice(@Body String body);

    @POST("send_invoice_to_customer.php")
    Call<String> sendInvoiceToCustomer(@Body String body);

    @POST("get_all_taskSP.php")
    Call<String> getServiceHistory(@Body  String body);

    @POST("userDetail.php")
    Call<String> getPublicProfile(@Query("apikey") String apikey, @Query("sp_id") String spid);

    @POST("check_task_status.php")
    Call<String> checkSpStatus(@Body String body);

    @POST("confirm_complete.php")
    Call<String> confirmComplete(@Body String body);

    @POST("rating.php")
    Call<String> rating(@Body String body);

    @POST("edit_profile.php")
    Call<String> editProfile(@Body String body);

    @POST("member-ship-plan.php")
    Call<String> getMemberShipPlan(@Query("apikey") String apikey);

    @POST("confirm_available_time.php")
    Call<String> confirmAvailableTime(@Body String body);


    @POST("taskDetail.php")
    Call<String> taskDetails(@Body String body);

    @Multipart
    @POST("edit_profile_pic.php")
    Call<ResponseBody> upload(@Part("v_code") RequestBody v_code, @Part("apikey")RequestBody apikey,
                              @Part MultipartBody.Part file, @Part("userToken")RequestBody userToken,
                              @Part("user_id")RequestBody user_id);

    @POST("sp_task_payment_history.php")
    Call<String> getPaymentHistory(@Body  String body);
}