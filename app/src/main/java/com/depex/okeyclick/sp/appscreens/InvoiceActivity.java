package com.depex.okeyclick.sp.appscreens;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Random;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InvoiceActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences preferences;
    ProgressDialog progressDialog;


    @BindView(R.id.job_id_txt)
    TextView jobId;

    @BindView(R.id.total_amount_txt)
    TextView totalAmount;

    @BindView(R.id.customer_name_txt)
    TextView customerName;

    @BindView(R.id.date_txt)
    TextView dateInvoice;

    @BindView(R.id.sub_total_txt)
    TextView subTotal;

    @BindView(R.id.basic_fare_txt)
    TextView basicFare;

    @BindView(R.id.service_hours_txt)
    TextView serviceHours;

    @BindView(R.id.confirm_payment_txt)
    TextView confirmPaymentTxt;

   /* @BindView(R.id.payment_processing_fee_txt)
    TextView paymentProcessingFee;*/

    @BindView(R.id.service_tex_txt)
    TextView serviceTex;

    @BindView(R.id.city_tex_txt)
    TextView cityTex;

    @BindView(R.id.send_btn)
    Button sendBtn;

    @BindView(R.id.total_amount2)
    TextView totalAmount2;


    String taskDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        preferences = getSharedPreferences("service_pref", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Invoice is generating ...");
        progressDialog.show();
        ButterKnife.bind(this);
        sendBtn.setOnClickListener(this);
        preferences.edit().putBoolean("isInInvoice", true).apply();
        Bundle bundle = getIntent().getExtras();
        taskDuration = bundle.getString("task_duration", "0");
        //for testing only !
        /*Random random=new Random();
        int rn=random.nextInt(3);
        String taskDuration="0"+rn+":"+"00:00";*/

        generateInvoice(taskDuration);
    }

    private void generateInvoice(String taskDuration) {

        //end for Testing only
        String user_id = preferences.getString("user_id", "0");
        String task_id = preferences.getString("task_id", "0");
        String userToken = preferences.getString("userToken", "0");
        JSONObject requestData = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", userToken);
            data.put("user_id", user_id);
            data.put("task_id", task_id);
            data.put("task_WDuration", taskDuration);
            requestData.put("RequestData", data);
            Log.i("requestData", "Generating Invoice : " + requestData.toString());
            new Retrofit.Builder()
                    .baseUrl(Utils.SITE_URL)
                    .addConverterFactory(new StringConvertFactory())
                    .build()
                    .create(ProjectAPI.class)
                    .generateTaskInvoice(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.i("responseData", "Generate Invoice : " + response.body());

                            NumberFormat numberFormat = NumberFormat.getInstance();
                            numberFormat.setMaximumFractionDigits(2);

                            String responseString = response.body();
                            try {
                                JSONObject res = new JSONObject(responseString);
                                boolean success = res.getBoolean("successBool");
                                if (success) {
                                    JSONObject resObj = res.getJSONObject("response");
                                    double baseFare = resObj.getDouble("base_fare");
                                    double subTotal = resObj.getDouble("subtotal");

                                    double total = resObj.getDouble("total");
                                    String customerName = resObj.getString("cs_name");
                                    String invoiceDate = resObj.getString("created_date");
                                    String cityTex = resObj.getString("city_tax") + "%";
                                    String serviceTax = resObj.getString("service_tax") + "%";
                                    String serviceHours = resObj.getString("task_WDuration");
                                    String taskKey = resObj.getString("task_key");


                                    basicFare.setText(numberFormat.format(baseFare));
                                    InvoiceActivity.this.subTotal.setText(numberFormat.format(subTotal));
                                    InvoiceActivity.this.customerName.setText(customerName);
                                    InvoiceActivity.this.cityTex.setText(cityTex);
                                    InvoiceActivity.this.totalAmount.setText("Total " + numberFormat.format(total));
                                    InvoiceActivity.this.dateInvoice.setText(invoiceDate);
                                    InvoiceActivity.this.serviceTex.setText(serviceTax);
                                    InvoiceActivity.this.serviceHours.setText(serviceHours);
                                    InvoiceActivity.this.totalAmount2.setText(numberFormat.format(total));
                                    InvoiceActivity.this.jobId.setText("Job ID : " + taskKey);
                                }
                            } catch (JSONException e) {
                                Log.e("responseDataError", e.toString());
                            }

                            progressDialog.hide();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            progressDialog.hide();
                        }
                    });
        } catch (JSONException e) {
            progressDialog.hide();
            e.printStackTrace();
        }


    }


    private void sendInvoiceToCustomer(String taskDuration) {

        String user_id = preferences.getString("user_id", "0");
        String task_id = preferences.getString("task_id", "0");
        String userToken = preferences.getString("userToken", "0");
        JSONObject requestData = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", userToken);
            data.put("user_id", user_id);
            data.put("task_id", task_id);
            data.put("task_WDuration", taskDuration);
            requestData.put("RequestData", data);
            Log.i("requestData", "Generating Invoice : " + requestData.toString());
            new Retrofit.Builder()
                    .baseUrl(Utils.SITE_URL)
                    .addConverterFactory(new StringConvertFactory())
                    .build()
                    .create(ProjectAPI.class)
                    .sendInvoiceToCustomer(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.i("responseData", "Generate Invoice : " + response.body());

                            String responseString = response.body();
                            try {
                                JSONObject res = new JSONObject(responseString);
                                boolean success = res.getBoolean("successBool");
                                if (success) {
                                    // MyTask myTask=new MyTask();
                                    //  myTask.execute();
                                    /*JSONObject resObj=res.getJSONObject("response");
                                    String baseFare=resObj.getString("base_fare");
                                    String subTotal=resObj.getString("subtotal");
                                    String total=resObj.getString("total");
                                   // String customerName=resObj.getString("cs_name");
                                    String invoiceDate=resObj.getString("created_date");
                                    String cityTex=resObj.getString("city_tax")+"%";
                                    String serviceTax=resObj.getString("service_tax")+"%";
                                    String serviceHours=resObj.getString("task_WDuration");
                                    String taskKey=resObj.getString("task_key");*/


                                  /*  basicFare.setText(baseFare);
                                    InvoiceActivity.this.subTotal.setText(subTotal);
                                    //InvoiceActivity.this.customerName.setText(customerName);
                                    InvoiceActivity.this.cityTex.setText(cityTex);
                                    InvoiceActivity.this.totalAmount.setText("Total "+total);
                                    InvoiceActivity.this.dateInvoice.setText(invoiceDate);
                                    InvoiceActivity.this.serviceTex.setText(serviceTax);
                                    InvoiceActivity.this.serviceHours.setText(serviceHours);
                                    InvoiceActivity.this.jobId.setText("Job ID : "+taskKey);*/
                                }
                            } catch (JSONException e) {
                                Log.e("responseDataError", e.toString());
                            }

                            progressDialog.hide();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            progressDialog.hide();
                        }
                    });
        } catch (JSONException e) {
            progressDialog.hide();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.edit().putBoolean("isInInvoice", false).apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                sendInvoiceToCustomer(taskDuration);
                break;
        }
    }

}








   /* String confirmPayment;

    private void checkServiceProviderRunningStatus() {

        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();

        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("user_id", preferences.getString("user_id", "0"));
            data.put("task_id", preferences.getString("task_id", "0"));
            requestData.put("RequestData", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .checkSpStatus(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString =response.body();
                        if(responseString==null){
                            return;
                        }
                        Log.i("responseDataRunning", "invoice Activity : "+responseString+"" );
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                int task_status=resObj.getInt("task_status");

                                JSONObject spData=resObj.getJSONObject("sp_Data");
                                String firstName=spData.getString("first_name");

                                //String serviceName

                            if(task_status!=8){
                                checkServiceProviderRunningStatus();
                            }else {
                                confirmPayment="Payment Confirm";
                                confirmPaymentTxt.setText("Payment Confirmed");
                                confirmPaymentTxt.setVisibility(View.VISIBLE);
                                sendBtn.setVisibility(View.GONE);
                            }
                            }
                        } catch (JSONException e) {
                            Log.e("responseDataError", e.toString());
                        }


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("responseDataError", t.toString());
                    }
                });
    }
*/
    /*class MyTask extends AsyncTask<String, String ,String>{

        @Override
        protected String doInBackground(String... strings) {
            checkServiceProviderRunningStatus();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}*/