package com.depex.okeyclick.sp.launch;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.appscreens.HomeActivity;
import com.depex.okeyclick.sp.appscreens.LoginScreenActivity;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashScreenActivity extends AppCompatActivity {


    SharedPreferences preferences;
    String TAG=getClass().getSimpleName();
    boolean isLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        isLogin=preferences.getBoolean("isLogin",false);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(isLogin){
                        Retrofit.Builder builder=new Retrofit.Builder();
                        builder.addConverterFactory(new StringConvertFactory());
                        builder.baseUrl(Utils.SITE_URL);
                        ProjectAPI projectAPI=builder.build().create(ProjectAPI.class);
                        JSONObject requestData=new JSONObject();
                        JSONObject data=new JSONObject();
                        data.put("v_code", getString(R.string.v_code));
                        data.put("apikey", getString(R.string.apikey));
                        data.put("deviceType", "android");
                        data.put("userToken", preferences.getString("userToken", "0"));
                        data.put("user_id", preferences.getString("user_id", "0"));
                        data.put("DeviceToken", preferences.getString("deviceToken", "0"));
                        requestData.put("RequestData", data);

                        projectAPI.checkToken(requestData.toString()).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                try {
                                    JSONObject res=new JSONObject(response.body());
                                    Log.i(TAG, "onResponse: "+res.toString());
                                    boolean success=res.getBoolean("successBool");
                                    if(!success){
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        preferences.edit().clear().commit();
                                        String token=FirebaseInstanceId.getInstance().getToken();
                                        preferences.edit().putString("deviceToken", token).commit();
                                        Intent loginIntent = new Intent(SplashScreenActivity.this, LoginScreenActivity.class);
                                        startActivity(loginIntent);
                                         finish();
                                    }else{
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Intent isOnlineIntent=new Intent(SplashScreenActivity.this, HomeActivity.class);
                                         startActivity(isOnlineIntent);
                                         finish();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });

                    }else {
                        Thread.sleep(2000);
                        Intent loginIntent = new Intent(SplashScreenActivity.this, LoginScreenActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}