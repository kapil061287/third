package com.depex.okeyclick.sp.appscreens;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.constants.UtilsMethods;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.modal.TaskDetail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCardCollection;
import com.stripe.model.ExternalAccount;
import com.stripe.model.ExternalAccountCollection;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.depex.okeyclick.sp.constants.UtilsMethods.getRatingText;

public class StartJobActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

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
    String task_id;

    TaskDetail detail;

    RatingBar ratingBarBottomSheet;

    TextView rateTextView;

    Button submitRatingBtn;

    EditText rateCommentEdit;


    AlertDialog confirmRatingDialog;

    @BindView(R.id.service_hour)
    TextView serviceTime;

    @BindView(R.id.service_name)
    TextView serviceName;

    @BindView(R.id.service_address)
    TextView serviceAddress;

    @BindView(R.id.job_id)
    TextView jobId;

    @BindView(R.id.serviec_amount)
    TextView serviceAmount;

@BindView(R.id.sp_comm)
TextView commission;
@BindView(R.id.profit)
TextView profit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_job);
        ButterKnife.bind(this);
        toolbar.setTitle("Job Progress");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_color));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        startJob.setOnClickListener(this);
        preferences=getSharedPreferences(Utils.SITE_PREF, MODE_PRIVATE);
        task_id=preferences.getString("task_id", "0");

        Bundle bundle=getIntent().getExtras();
        if(bundle.getString("taskDetailsJson")!=null){
            String json=bundle.getString("taskDetailsJson");
            Log.i("responseData", "Task Details : "+json);
            Gson gson=new GsonBuilder().setDateFormat(getString(R.string.date_time_format_from_web)).create();
            detail=gson.fromJson(json, TaskDetail.class);
            initScreen(detail);

        }else {

                init();

        }
    }

    private void initScreen(TaskDetail detail) {
        String spurl=detail.getSpProfilePic();
        String csUrl=detail.getCsProfilePic();
        task_id=detail.getTaskId();
        spName.setText(detail.getSpName());
        customerName.setText(detail.getCsName());
        serviceTime.setText(detail.getWorkDuration());
        serviceName.setText(detail.getCategory());

        serviceAddress.setText(detail.getCsAddress());
        jobId.setText(detail.getTaskId());
        serviceAmount.setText(getString(R.string.uro)+detail.getTotal());
        commission.setText(detail.getAdminCommission()+"%");


        GlideApp.with(this).load(spurl).placeholder(R.drawable.user_dp_place_holder).circleCrop().into(spImage);
        GlideApp.with(this).load(csUrl).placeholder(R.drawable.user_dp_place_holder).circleCrop().into(customerImage);
    }

    private void init() {

        final JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();

        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", ""));
            data.put("user_id", preferences.getString("user_id", "0"));
            data.put("task_id", task_id);
            requestData.put("RequestData", data);
            Log.i("requestData", "Task Api Data : "+requestData);
        } catch (JSONException e) {
            Log.e("responseDataError", e.toString());
        }


        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .taskDetails(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                            String responseString=response.body();
                            Log.i("responseData", "Task Details api : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");

                                Gson gson=new GsonBuilder().setDateFormat(getString(R.string.date_time_format_from_web)).create();
                                detail=gson.fromJson(resObj.toString(), TaskDetail.class);
                                initScreen(detail);
                            }
                        } catch (JSONException e) {
                            Log.e("responseDataError", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("responseDataError"," "+t.toString());
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_job:
                String text=startJob.getText().toString();
                    changeStatus(text);
                break;
            case R.id.submit_rating_btn:
                    submitRating();
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
            data2.put("task_id", task_id);
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
            Log.e("responseDataError","Start Job Activity : "+ e.toString());
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
                                    showReviewRatingSheet();
                                    Toast.makeText(StartJobActivity.this, "Job complete ",Toast.LENGTH_LONG).show();

                                }
                            }
                        } catch (JSONException e) {
                            Log.e("responseData" , "Start Job : "+ e.toString());
                        }

                        Log.i("responseDataChange", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if(UtilsMethods.isInternetAvailable(StartJobActivity.this)) {
                            changeStatus(status);
                            Log.e("responseData", "Change Status AcceptServiceActivity : " + t.toString());
                        }
                    }
                });
    }

    private void showReviewRatingSheet() {
        // Toast.makeText(this, "Review Rating screen sheet " , Toast.LENGTH_LONG).show();
        View view= LayoutInflater.from(this).inflate(R.layout.content_review_rating_action_sheet, null, false);
        BottomSheetDialog dialog=new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
        ratingBarBottomSheet=view.findViewById(R.id.rating_bar_review_bottom_sheet);
        rateTextView=view.findViewById(R.id.rate_txt);
        submitRatingBtn=view.findViewById(R.id.submit_rating_btn);
        submitRatingBtn.setOnClickListener(this);
        rateTextView.setText(getRatingText(ratingBarBottomSheet.getRating()));
        ratingBarBottomSheet.setOnRatingBarChangeListener(this);
        rateCommentEdit=view.findViewById(R.id.write_review_edit);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        rateTextView.setText(getRatingText(v));
    }

    private void submitRating() {

        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("sender_id", preferences.getString("user_id","0"));
            data.put("receiver_id",detail.getCsId());
            data.put("task_id", task_id);
            data.put("rate", ratingBarBottomSheet.getRating());
            data.put("comment", rateCommentEdit.getText().toString());
            requestData.put("RequestData", data);
            Log.i("requestData", "Rating API : "+requestData);
        } catch (JSONException e) {
            Log.e("responseDatError","Submit Rating : "+e.toString());
        }


        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .rating(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData", "Rating Api : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                String msg=resObj.getString("msg");
                                showConfirmRatingDialog(msg);
                            }
                        } catch (JSONException e) {
                            Log.e("responseData", "submitRating :  "+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("responseData", "submitRating :  "+t.toString());
                    }
                });
    }

    private void showConfirmRatingDialog(String msg) {
        confirmRatingDialog =new AlertDialog.Builder(this)
                .setMessage(msg)
                .setTitle("Confirm Rating")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startHomeActivity();
                        confirmRatingDialog.dismiss();
                    }
                }).create();
        confirmRatingDialog.show();
    }


    private void startHomeActivity() {
        Intent homeIntent=new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }
}