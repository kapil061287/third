package com.depex.okeyclick.sp.appscreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SPTimerActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences preferences;
    @BindView(R.id.timer_text)
    TextView timer_text;

    @BindView(R.id.progress_icno_image)
    ImageView timer_image;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.start_job)
    Button startJob;

    @BindView(R.id.customer_name_sptimer)
    TextView customerNameTimer;

    @BindView(R.id.service_name_sptimer)
    TextView serviceNameTimer;

    @BindView(R.id.address_sptimer)
    TextView addressSptimer;

    MyTask myTask;

    private boolean isTimerStart=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_timer);

        ButterKnife.bind(this);
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        myTask=new MyTask();
        myTask.execute();
        Typeface typeface= ResourcesCompat.getFont(this, R.font.digital);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        startJob.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPTimerActivity.super.onBackPressed();
            }
        });
        timer_text.setTypeface(typeface);

        Bundle bundle = getIntent().getExtras();
        long time = bundle.getLong("requestTime");
        String customerName = bundle.getString("customerName");
        String customerMobile = bundle.getString("customerMobile");
        String customerAddress = bundle.getString("customerAddress");
        String serviceNameText = bundle.getString("subcategory");
        customerNameTimer.setText(customerName);
        serviceNameTimer.setText(serviceNameText);
        addressSptimer.setText(customerAddress);

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_job:
                if("Start the job".equalsIgnoreCase(startJob.getText().toString())){
                        changeStatus("start");

                }else if ("Finish the job".equalsIgnoreCase(startJob.getText().toString())){
                        changeStatus("complete");

                }
                break;
        }
    }

    public void changeStatus(final String status){
        if("generate".equalsIgnoreCase(status)){
            startInvoiceActivity();
        }

        JSONObject requestData2=new JSONObject();
        JSONObject data2=new JSONObject();
        try {
            data2.put("apikey", getString(R.string.apikey));
            data2.put("v_code", getString(R.string.v_code));
            data2.put("sp_id", preferences.getString("user_id", "0"));
            data2.put("task_id", preferences.getString("task_id", "0"));
            data2.put("task_status", status);
            requestData2.put("RequestData", data2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Retrofit.Builder()
                .addConverterFactory(new StringConvertFactory())
                .baseUrl(Utils.SITE_URL)
                .build()
                .create(ProjectAPI.class)
                .changeStatusRunningTask(requestData2.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseDataChange", response.body());
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                if(status.equalsIgnoreCase("start")) {
                                   // myTask.execute();
                                    isTimerStart=true;
                                    startJob.setText("Finish the job");
                                    JSONObject resObj=res.getJSONObject("response");
                                    String serviceStartTime=resObj.getString("start_date");
                                    long millis=parseServerTime(serviceStartTime);
                                    preferences.edit().putLong("startServiceTime", millis).apply();
                                    preferences.edit().putBoolean("start_service", true).apply();


                                }else if (status.equalsIgnoreCase("complete")){
                                    isTimerStart=false;
                                    isInProgress=false;
                                    timer_image.setBackgroundResource(R.drawable.progress_icon_2);
                                    myTask.cancel(true);
                                    startJob.setText("Generate Invoice");
                                }
                            }
                        } catch (JSONException e) {
                           Log.e("responseDataError", e.toString());
                        }
                    }



                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("responseDataError", t.toString());
                    }
                });
    }

    private void startInvoiceActivity() {
        Bundle bundle=new Bundle();
        bundle.putString("task_duration", timer_text.getText().toString());
        Intent invoiceIntent=new Intent(this, InvoiceActivity.class);
        invoiceIntent.putExtras(bundle);
        startActivity(invoiceIntent);
    }

    boolean isInProgress=true;

    class MyTask extends AsyncTask<Integer, Integer, Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            int i=1;
            while (isInProgress)
            {
                try {
                    Log.i("responseDataChange", "Task is running !");
                    if(isTimerStart) {
                        publishProgress(i);
                        Thread.sleep(1000);
                        Log.i("responseDataChange", "Status i : " + String.valueOf(i) + " ");
                        i++;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
                String timerText=updateTimer(values[0]);
                timer_text.setText(timerText);
        }
    }



    public String updateTimer(int seconds){

        int hours=0;
        int minuts=0;
        int seconds2=0;

        int remainder=0;
        hours=seconds/(60*60);
        remainder=seconds%(60*60);
        minuts=remainder/60;
        remainder=remainder%60;
        seconds2=remainder;

        String hoursString="";
        String minutString="";
        String seconds2String="";

        if(hours<10) hoursString="0"+hours;
        else hoursString=String.valueOf(hours);

        if(minuts<10) minutString="0"+minuts;
        else minutString=String.valueOf(minuts);

        if(seconds2<10) seconds2String="0"+seconds2;
        else seconds2String=String.valueOf(seconds2);

        return hoursString+":"+minutString+":"+seconds2String;
    }


    public static long parseServerTime(String time) {

        SimpleDateFormat format=new SimpleDateFormat("y-M-d H:m:s");
        try {
            Date date=format.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            Log.e("responseDateError", "Parse date error : "+e.toString());
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTimerStart=false;
        isInProgress=false;
        myTask.cancel(true);
    }
}