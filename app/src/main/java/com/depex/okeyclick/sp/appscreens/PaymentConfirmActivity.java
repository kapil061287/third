package com.depex.okeyclick.sp.appscreens;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentConfirmActivity extends AppCompatActivity {

    @BindView(R.id.state_progress)
    StateProgressBar stateProgressBar;

    @BindView(R.id.sp_image)
    RoundedImageView spImage;

    @BindView(R.id.customer_image)
    RoundedImageView customerImage;

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirm);
        ButterKnife.bind(this);
        String[]arr=new String[]{"Service\nAccepted", "On the \nWay", "Arrived to \nLocation", "Start the \nJob", "Finish \nJob"};
        stateProgressBar.setStateDescriptionData(arr);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
        getSupportActionBar().setTitle("Review & Rating");
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        initserviceProvider();

    }

    private void initserviceProvider() {

        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();

        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("user_id", preferences.getString("user_id", "0"));
            data.put("task_id", preferences.getString("task_id", "0"));
            requestData.put("RequestData", data);
            Log.i("requestData", "Payment Confirmation : "+requestData.toString());
        } catch (JSONException e) {
            Log.e("responseDataError", "Payment Confirmation : "+e.toString());
        }


        new Retrofit.Builder()
                .addConverterFactory(new StringConvertFactory())
                .baseUrl(Utils.SITE_URL)
                .build()
                .create(ProjectAPI.class)
                .checkSpStatus(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData", "Payment Confirmation : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                JSONObject spData=resObj.getJSONObject("sp_Data");
                                String url=spData.getString("user_images");
                                GlideApp.with(PaymentConfirmActivity.this).load(url).placeholder(R.drawable.user_dp_place_holder).into(spImage);

                                JSONObject csData=resObj.getJSONObject("cs_Data");
                                String url2=csData.getString("user_images");
                                GlideApp.with(PaymentConfirmActivity.this).load(url).placeholder(R.drawable.user_dp_place_holder).into(customerImage);

                            }

                        } catch (JSONException e) {
                            Log.e("responseDataError", "Payment Confirmation : "+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }
}