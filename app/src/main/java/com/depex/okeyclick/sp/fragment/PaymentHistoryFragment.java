package com.depex.okeyclick.sp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.adapters.PaymentHistoryAdapter;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.modal.PaymentHistoryModal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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


public class PaymentHistoryFragment extends Fragment {

    @BindView(R.id.payment_history_recycler)
    RecyclerView paymentHistoryRecycler;
    private Context context;

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_payment_history_fragment, container, false);
        ButterKnife.bind(this, view);
        preferences=context.getSharedPreferences(Utils.SITE_PREF, Context.MODE_PRIVATE);
        initScreen();

        return view;
    }

    private void initScreen() {

        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();

        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("user_id", preferences.getString("user_id", "0"));
            requestData.put("RequestData", data);

        } catch (JSONException e) {
            Log.e("responseData", "Payment History : "+e.toString());


        }

        new Retrofit.Builder()
                .addConverterFactory(new StringConvertFactory())
                .baseUrl(Utils.SITE_URL)
                .build()
                .create(ProjectAPI.class)
                .getPaymentHistory(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData", "Payment History : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                JSONArray arr=resObj.getJSONArray("list");
                                Gson gson=new GsonBuilder().setDateFormat(getString(R.string.date_time_format_from_web)).create();
                                PaymentHistoryModal[]paymentHistoryModalArr=gson.fromJson(arr.toString(), PaymentHistoryModal[].class);
                                List<PaymentHistoryModal> paymentHistoryModals=new ArrayList<>(Arrays.asList(paymentHistoryModalArr));
                                PaymentHistoryAdapter adapter=new PaymentHistoryAdapter(context, paymentHistoryModals);
                                LinearLayoutManager manager=new LinearLayoutManager(context);
                                DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(context, manager.getOrientation());
                                paymentHistoryRecycler.addItemDecoration(dividerItemDecoration);
                                paymentHistoryRecycler.setLayoutManager(manager);
                                paymentHistoryRecycler.setAdapter(adapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
}
