package com.depex.okeyclick.sp.appscreens;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.adapters.ServiceHistoryAdapter;
import com.depex.okeyclick.sp.adapters.ServiceHistoryItemListener;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.modal.ServiceHistory;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServiceHistoryActivity extends AppCompatActivity implements ServiceHistoryItemListener {


    @BindView(R.id.when_no_service_history)
    FrameLayout whenNoServiceHistory;

    @BindView(R.id.recycler_service_history)
    RecyclerView serviceHistoryRecycler;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_history);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Service History");
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceHistoryActivity.super.onBackPressed();
            }
        });
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        initTaskHistory();
    }

    private void initTaskHistory() {

        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("user_id", preferences.getString("user_id", "0"));
            requestData.put("RequestData", data);
            Log.i("requestData", "Service History Request : "+requestData);
        } catch (JSONException e) {
            Log.e("responseDataError", e.toString());
        }

        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .getServiceHistory(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String resString=response.body();
                        Log.i("responseData", "Service History : "+resString);
                        try {
                            JSONObject resJson=new JSONObject(resString);
                            boolean success=resJson.getBoolean("successBool");
                            if(success){
                                JSONObject res=resJson.getJSONObject("response");
                                JSONArray serviceHistoryArr=res.getJSONArray("list");
                                Gson gson=new Gson();
                                ServiceHistory[] arr=gson.fromJson(serviceHistoryArr.toString(), ServiceHistory[].class);
                                List<ServiceHistory> serviceHistories=new ArrayList<>(Arrays.asList(arr));
                                ServiceHistoryAdapter adapter=new ServiceHistoryAdapter(ServiceHistoryActivity.this, serviceHistories,ServiceHistoryActivity.this);
                                serviceHistoryRecycler.setLayoutManager(new LinearLayoutManager(ServiceHistoryActivity.this));
                                serviceHistoryRecycler.setAdapter(adapter);
                                if(serviceHistories.size()>0){
                                    whenNoServiceHistory.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("responseDataError", "Service History : "+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("responseData", "Service History : "+t.toString());
                    }
                });
    }




    @Override
    public void onServiceHistoryItemClick(ServiceHistory serviceHistory) {
        Gson gson=new Gson();
        String serviceJson=gson.toJson(serviceHistory);
            Log.i("responseData", "Service History Item  : "+serviceJson);
    }
}