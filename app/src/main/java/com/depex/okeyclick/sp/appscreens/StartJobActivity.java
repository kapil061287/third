package com.depex.okeyclick.sp.appscreens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.modal.TaskDetail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StartJobActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String START_JOB = "start job";
    private static final String FINISH_JOB = "finish job";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.sp_image)
    RoundedImageView spImage;

    @BindView(R.id.customer_image)
    RoundedImageView customerImage;

    @BindView(R.id.sp_name)
    TextView spName;

    @BindView(R.id.customer_name)
    TextView customerName;

    @BindView(R.id.start_job)
    Button startJob;

    SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_job);
        ButterKnife.bind(this);
        toolbar.setTitle("Job Progress");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        startJob.setOnClickListener(this);
        preferences=getSharedPreferences(Utils.SITE_PREF, MODE_PRIVATE);

        Bundle bundle=getIntent().getExtras();
        if(bundle.getString("taskDetailsJson")!=null){
            String json=bundle.getString("taskDetailsJson");
            Gson gson=new GsonBuilder().setDateFormat(getString(R.string.date_time_format_from_web)).create();
            TaskDetail detail=gson.fromJson(json, TaskDetail.class);
            String spurl=detail.getSpProfilePic();
            String csUrl=detail.getCsProfilePic();

            GlideApp.with(this).load(spurl).placeholder(R.drawable.user_dp_place_holder).into(spImage);
            GlideApp.with(this).load(csUrl).placeholder(R.drawable.user_dp_place_holder).into(customerImage);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_job:
                String text=startJob.getText().toString();
                    changeStatus(text);
                break;

        }
    }

    private void changeStatus(final String status) {

        JSONObject requestData2=new JSONObject();
        JSONObject data2=new JSONObject();
        try {
            data2.put("apikey", getString(R.string.apikey));
            data2.put("v_code", getString(R.string.v_code));
            data2.put("sp_id", preferences.getString("user_id", "0"));
            data2.put("userToken", preferences.getString("userToken", "0"));
            data2.put("task_id", preferences.getString("task_id", "0"));
            if(START_JOB.equalsIgnoreCase(status)){
                data2.put("task_status", "5");
            }else if(FINISH_JOB.equalsIgnoreCase(status)){
                data2.put("task_status", "7");
            }
            /*else if(ARRIVED.equalsIgnoreCase(status)){
                data2.put("task_status", "4");
            }*/
            else {
                return;
            }
            requestData2.put("RequestData", data2);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        new Retrofit.Builder()
                .addConverterFactory(new StringConvertFactory())
                .baseUrl(Utils.SITE_URL)
                .build()
                .create(ProjectAPI.class)
                .changeStatus(requestData2.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData", "Change Status : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                String taskStatus=resObj.getString("task_status");
                                if(taskStatus.equalsIgnoreCase("5")){
                                    startJob.setText(FINISH_JOB);
                                }if(taskStatus.equalsIgnoreCase("7")){
                                    Toast.makeText(StartJobActivity.this, "Job complete ",Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i("responseDataChange", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        changeStatus(status);
                        Log.e("responseData", "Change Status AcceptServiceActivity : "+t.toString());
                    }
                });

    }
}
