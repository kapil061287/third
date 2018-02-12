package com.depex.okeyclick.sp.appscreens;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AcceptServiceActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    CircularProgressBar progressBar;
    TextView updateTimeText;
    GoogleMap googleMap;
    Polyline polyline;
    TextView serviceName;
    SharedPreferences preferences;
    MyTask myTask;
    TextView serviceAddress;

    @BindView(R.id.imageView3)
    ImageView backgroundGray;

    @BindView(R.id.reject_btn)
    Button rejectBtn;

    @BindView(R.id.call_to_customer)
    LinearLayout callToCustomerLayout;

    @BindView(R.id.customer_call_btn)
    Button callBtn;
    Marker Spmarker;

    LatLng customerLatlng;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;

    String customerMobile;

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_accept_service_fragment);
        serviceName = findViewById(R.id.service_name_accept);
        serviceAddress = findViewById(R.id.service_address_accept);
        preferences = getSharedPreferences("service_pref", MODE_PRIVATE);


        ButterKnife.bind(this);
        callBtn.setOnClickListener(this);
        //View view = inflater.inflate(R.layout.content_accept_service_fragment, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_in_accept__service_frgment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mapFragment.getView().setElevation(2);
        }
        updateTimeText = findViewById(R.id.update_time_txt);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/digital.ttf");
        updateTimeText.setTypeface(typeface);
        progressBar = findViewById(R.id.circularProgressBar);
        progressBar.setOnClickListener(this);
        progressBar.setProgress(0);
        myTask = new MyTask();


        Bundle bundle = getIntent().getExtras();
        long time = bundle.getLong("requestTime");
        String customerName = bundle.getString("customerName");
        String customerMobile = bundle.getString("customerMobile");
        String customerAddress = bundle.getString("customerAddress");
        String serviceNameText = bundle.getString("subcategory");


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                List<Location> locations = locationResult.getLocations();

                for (int i = 0; i < locations.size(); i++) {
                    Location location = locations.get(i);
                    MarkerOptions options = new MarkerOptions();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (Spmarker != null) {
                        Spmarker.remove();
                    }
                    options.position(latLng);
                    options.title(preferences.getString("fullname", "You"));
                    options.visible(true);

                    Spmarker = googleMap.addMarker(options);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                }
            }
        };

        LocationRequest request = new LocationRequest();
        request.setFastestInterval(500);
        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

       // fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, null);

        serviceName.setText(serviceNameText);
        serviceAddress.setText(customerAddress);


        Log.i("requestTime", time + "");
        long openTime = new Date().getTime();
        Log.i("requestTime", openTime + "");


        long remainTime = openTime - time;

        Log.i("requestTime", remainTime + "");

        myTask.execute(0);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onBackPressed() {
        myTask.cancel(true);
        super.onBackPressed();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circularProgressBar:
                Toast.makeText(this, "Hello Progress Bar ", Toast.LENGTH_LONG).show();
                Bundle bundle = getIntent().getExtras();
                String task_id = bundle.getString("task_id");


                JSONObject requestData = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    data.put("v_code", getString(R.string.v_code));
                    data.put("apikey", getString(R.string.apikey));
                    data.put("accepted_by", preferences.getString("user_id", "0"));
                    data.put("userToken", preferences.getString("userToken", "0"));
                    data.put("task_id", task_id);
                    requestData.put("RequestData", data);
                    Log.i("requestData", requestData.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final Retrofit.Builder builder = new Retrofit.Builder();
                builder.addConverterFactory(new StringConvertFactory())
                        .baseUrl(Utils.SITE_URL)
                        .build()
                        .create(ProjectAPI.class)
                        .acceptRequest(requestData.toString())
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Log.i("responseCode", response.body());
                                String responseString = response.body();
                                try {
                                    JSONObject res = new JSONObject(responseString);
                                    boolean success = res.getBoolean("successBool");
                                    if (success) {
                                        progressBar.setVisibility(View.GONE);
                                        rejectBtn.setVisibility(View.GONE);
                                        backgroundGray.setVisibility(View.GONE);
                                        callToCustomerLayout.setVisibility(View.VISIBLE);
                                        serviceName.setVisibility(View.GONE);
                                        serviceAddress.setVisibility(View.GONE);
                                        updateTimeText.setVisibility(View.GONE);


                                        JSONObject jsonObject = res.getJSONObject("response");
                                        String customerLatitudeStr = jsonObject.getString("customer_latitude");
                                        String customerLongitudeStr = jsonObject.getString("customer_longitude");
                                        double customerLat = Double.parseDouble(customerLatitudeStr);
                                        double customerLng = Double.parseDouble(customerLongitudeStr);
                                        String customerName = jsonObject.getString("customer_name");
                                        String customerAddress = jsonObject.getString("customer_address");

                                        customerLatlng = new LatLng(customerLat, customerLng);
                                        MarkerOptions options = new MarkerOptions();
                                        options.position(customerLatlng);
                                        options.visible(true);

                                        options.title(customerName);
                                        options.snippet(customerAddress);
                                        googleMap.addMarker(options);
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(customerLatlng));

                                        if (ActivityCompat.checkSelfPermission(AcceptServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AcceptServiceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(final Location location) {
                                                if (location == null) return;

                                                //for google direction api
                                                Retrofit.Builder builder1 = new Retrofit.Builder();
                                                builder1.addConverterFactory(new StringConvertFactory());
                                                builder1.baseUrl("https://maps.googleapis.com/");
                                                Call<String> polylineCall = builder1.build().create(ProjectAPI.class).getPolyLineDirection(customerLatlng.latitude + "," + customerLatlng.longitude,
                                                        String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()),
                                                        getString(R.string.server_key));
                                                String url = polylineCall.request().url().encodedPath();
                                                Log.i("responseDataGoogle", url);
                                                polylineCall.enqueue(new Callback<String>() {
                                                    @Override
                                                    public void onResponse(Call<String> call, Response<String> response) {

                                                        String responseString = response.body();
                                                       // Log.i("responseDataGoogle", responseString);
                                                        try {
                                                            JSONObject googleRouteJson = new JSONObject(responseString);
                                                            JSONArray routeArray = googleRouteJson.getJSONArray("routes");
                                                            for (int i = 0; i < routeArray.length(); i++) {
                                                                JSONObject jsonForLags = routeArray.getJSONObject(i);
                                                                JSONArray legsArray = jsonForLags.getJSONArray("legs");
                                                                for (int j = 0; j < legsArray.length(); j++) {
                                                                    JSONObject jsonForSteps = legsArray.getJSONObject(j);
                                                                    JSONArray stepsArray = jsonForSteps.getJSONArray("steps");

                                                                    for (int k = 0; k < stepsArray.length(); k++) {
                                                                        JSONObject jsonForPolyLine = stepsArray.getJSONObject(k);
                                                                        JSONObject polyLine=jsonForPolyLine.getJSONObject("polyline");
                                                                        String polyPoint=polyLine.getString("points");
                                                                        //Log.i("responseDataGoogle", polyPoint);
                                                                        //Toast.makeText(AcceptServiceActivity.this , polyPoint, Toast.LENGTH_LONG).show();
                                                                        List<LatLng> polyPoints = PolyUtil.decode(polyPoint);
                                                                        PolylineOptions polylineOptions = new PolylineOptions();
                                                                        polylineOptions.addAll(polyPoints)
                                                                                .width(15)
                                                                                .color(Color.BLUE);
                                                                        Log.i("responseDataGoogle", polyPoints.toString());

                                                                        polyline = googleMap.addPolyline(polylineOptions);
                                                                        if(Spmarker!=null){
                                                                            Spmarker.remove();
                                                                        }

                                                                        MarkerOptions markerOptions=new MarkerOptions();
                                                                        markerOptions.visible(true);
                                                                        markerOptions.title(preferences.getString("fullname", "You"));
                                                                        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                                                                        Spmarker=googleMap.addMarker(markerOptions);

                                                                       // Toast.makeText(AcceptServiceActivity.this, "Service Provider is add to marker", Toast.LENGTH_LONG).show();

                                                                        LocationRequest request = new LocationRequest();
                                                                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                                                        request.setInterval(10000);
                                                                        request.setFastestInterval(500);

                                                                        if (ActivityCompat.checkSelfPermission(AcceptServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AcceptServiceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                                            return;
                                                                        }
                                                                        fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, null);
                                                                    }
                                                                }
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
                                        });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
                break;
            case R.id.customer_call_btn:

                break;

        }
    }

    public void callTocustomer(String number){

    }

    private class MyTask extends AsyncTask<Integer, Integer, String> {
        boolean isIncrease;
        int updateTimeValue=0;
        @Override
        protected String doInBackground(Integer... integers) {

            for (int i=integers[0];i<=120;i++){
                try {
                    Thread.sleep(250);
                    publishProgress(i);
                    Thread thread=Thread.currentThread();
                    Log.i("threadLog", thread.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Success";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values.length>0) {
                float progress_step=100.f/120.f;
                float current_progress=progress_step*values[0];
                ViewGroup.LayoutParams params=progressBar.getLayoutParams();
                ConstraintLayout.LayoutParams params1= (ConstraintLayout.LayoutParams) params;
                setProgress(current_progress);
                if(values[0]%4==0){
                    int updateTime=30-updateTimeValue;
                    if(updateTime>-1) {
                        if (updateTime < 10) {
                            updateTimeText.setText("00 : 0" + updateTime);
                        } else {
                            updateTimeText.setText("00 : " + updateTime);
                        }
                    }
                    updateTimeValue++;
                }
            }
        }
    }

    public void setProgress(float f){
        progressBar.setProgress(f);
    }
}
