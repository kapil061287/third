package com.depex.okeyclick.sp.appscreens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.adapters.ActivityChoosAdapter;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.constants.UtilsMethods;
import com.depex.okeyclick.sp.modal.ChooseAdapterModal;
import com.depex.okeyclick.sp.modal.Service;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceChooseActivity extends AppCompatActivity {

    @BindView(R.id.service_list_view)
    ExpandableListView listView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ActivityChoosAdapter activityChoosAdapter;

    Set<ChooseAdapterModal> adapterModals=new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_choose);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            if(bundle.getString("json")!=null){
                Gson gson=new Gson();
                String json=bundle.getString("json");
                ChooseAdapterModal[] adapterModals=gson.fromJson(json, ChooseAdapterModal[].class);
//                List<ChooseAdapterModal> adapterModalList=new ArrayList<>(Arrays.asList(adapterModals));
                if(adapterModals!=null)
                this.adapterModals =new HashSet<>(Arrays.asList(adapterModals));


            }
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        new Retrofit
                .Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProjectAPI.class)
                .getServices(getString(R.string.apikey))
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        String responseString=response.body().toString();
                        Log.i("responseData", responseString);
                        JsonObject object=response.body();
                        boolean success=object.get("successBool").getAsBoolean();
                        if(success){
                            JsonObject res=object.get("response").getAsJsonObject();
                            JsonArray arr=res.get("List").getAsJsonArray();
                            Log.i("responseData", arr.toString());
                            Gson gson=new Gson();
                            Service[]serviceArr=gson.fromJson(arr.toString(), Service[].class);
                            List<Service> services=new ArrayList<>(Arrays.asList(serviceArr));
                            activityChoosAdapter=new ActivityChoosAdapter(ServiceChooseActivity.this, services, adapterModals);
                            //activityChoosAdapter.setAdapterModals(adapterModals);
                            listView.setAdapter(activityChoosAdapter);
                        }
                    }


                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                          Log.e("responseDataError", "Choose Activity : "+t.toString());
                        if(UtilsMethods.isInternetAvailable(ServiceChooseActivity.this)){
                            initRecyclerView();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.choose_activity_option_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done_menu:
                Gson gson=new Gson();
                if(activityChoosAdapter!=null){
                   Set<ChooseAdapterModal>  modals= activityChoosAdapter.getAdapterModals();
                   String json=gson.toJson(modals);
                    Intent intent=new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString("json", json);
                   setResult(RESULT_OK,intent.putExtras(bundle));
                   finish();
                   //Log.i("responseData", "Choose Adapter Modal : "+json);
                }
                 break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}