package com.depex.okeyclick.sp.appscreens;

import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InvoiceActivity extends AppCompatActivity {

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        generateInvoice();
    }

    private void generateInvoice() {
        Bundle bundle=getIntent().getExtras();
        String taskDuration=bundle.getString("task_duration","0");
        String user_id=preferences.getString("user_id", "0");
        String task_id=preferences.getString("task_id", "0");
        String userToken=preferences.getString("userToken", "0");
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", userToken);
            data.put("user_id", user_id);
            data.put("task_id", task_id);
            data.put("task_WDuration", taskDuration);
            requestData.put("RequestData", requestData);
            new Retrofit.Builder()
                    .baseUrl(Utils.SITE_URL)
                    .addConverterFactory(new StringConvertFactory())
                    .build()
                    .create(ProjectAPI.class)
                    .generateTaskInvoice(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.i("responseData", "Generate Invoice : "+response.body());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
