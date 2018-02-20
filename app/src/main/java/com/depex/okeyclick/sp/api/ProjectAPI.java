package com.depex.okeyclick.sp.api;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @POST("generate_task_invoice.php")
    Call<String> generateTaskInvoice(@Body String body);


}