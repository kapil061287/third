package com.depex.okeyclick.sp.appscreens;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookLaterActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, BottomSheetTimePickerDialog.OnTimeSetListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    GoogleMap googleMap;
    Bundle bundle;
    @BindView(R.id.customer_pic)
    ImageView imageView;
    @BindView(R.id.customer_name)
    TextView customerName;

    @BindView(R.id.reject_btn)
    Button rejectBtn;

    @BindView(R.id.accept_btn)
    Button acceptBtn;

    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_later);
        ButterKnife.bind(this);
        toolbar.setTitle("Schedule Task Info");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        bundle=getIntent().getExtras();
        String url=bundle.getString("customer_pic");
        customerName.setText(bundle.getString("customer_name"));
        GlideApp.with(this).load(url).circleCrop().into(imageView);
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        preferences=getSharedPreferences(Utils.SITE_PREF, MODE_PRIVATE);
        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        double lat=Double.parseDouble(bundle.getString("customer_latitude"));
        double lng=Double.parseDouble(bundle.getString("customer_longitude"));
        LatLng latLng=new LatLng(lat, lng);
        CameraUpdate update=CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.moveCamera(update);
        MarkerOptions options=new MarkerOptions()
                .position(latLng)
                .visible(true)
                .title(bundle.getString("customer_name"))
                .snippet(bundle.getString("customer_address"));
        Marker marker=googleMap.addMarker(options);
        marker.showInfoWindow();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reject_btn:

                break;
            case R.id.accept_btn:
                Calendar calendar=Calendar.getInstance();
                GridTimePickerDialog grid = GridTimePickerDialog.newInstance(
                        this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(this));
                grid.show(getSupportFragmentManager(), "gridTime");
                break;
        }
    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("sp_id", preferences.getString("user_id", "0"));
            data.put("available_time", hourOfDay+":"+minute);
            data.put("task_id", bundle.get("task_id"));
            requestData.put("RequestData", data);

            Log.i("requestData", requestData.toString());

            new Retrofit.Builder()
                    .baseUrl(Utils.SITE_URL)
                    .addConverterFactory(new StringConvertFactory())
                    .build()
                    .create(ProjectAPI.class)
                    .confirmAvailableTime(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String responseString=response.body();
                            Log.i("responseData", "Book Later Api : "+responseString);
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("respnseDataError","Book Later Error : "+ t.toString());
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}