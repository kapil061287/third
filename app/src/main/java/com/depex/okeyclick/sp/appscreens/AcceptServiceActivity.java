package com.depex.okeyclick.sp.appscreens;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

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
import com.depex.okeyclick.sp.modal.TaskDetail;
import com.depex.okeyclick.sp.services.IsTaskCancelService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.PolyUtil;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AcceptServiceActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String PAYMENT_WAIT_STR = "Wait for Payment";
    private static final String START = "start";
    private static final String ARRIVED = "Arrived";


    TextView updateTimeText;
    GoogleMap googleMap;
    Polyline polyline;

    boolean notifyFlag;

    TextView serviceName;

    ProgressDialog dialog;

    SharedPreferences preferences;
    MyTask myTask;
    String task_id;
    TextView serviceAddress;

    @BindView(R.id.imageView3)
    ImageView backgroundGray;

    @BindView(R.id.not_paid_call_btn)
    Button notPaidCallBtn;

    @BindView(R.id.reject_btn)
    Button rejectBtn;

    @BindView(R.id.circularProgressBar)
    CircularProgressBar progressBar;

    @BindView(R.id.call_to_customer)
    LinearLayout callToCustomerLayout;

    @BindView(R.id.customer_call_btn)
    Button callBtn;

    Marker spMarker;

    @BindView(R.id.navigate_btn)
    Button navigateBtn;

    @BindView(R.id.customer_name)
    TextView customerName;

    @BindView(R.id.customer_address)
    TextView customerAddress;

    @BindView(R.id.service_name)
    TextView serviceName2;

    boolean isPlayerSoundOff;

    LatLng customerLatlng;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;

    String customerMobile;
    FirebaseJobDispatcher dispatcher;
    //Job isCanceljob;
    private String acceptRequestStr="not_accept";
    MediaPlayer player;

    boolean isPaymentSucceed;
    boolean isTaskAccepted;
    boolean jobInProgress;


    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_accept_service_fragment);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_in_accept__service_frgment);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        serviceName = findViewById(R.id.service_name_accept);
        serviceAddress = findViewById(R.id.service_address_accept);
        preferences = getSharedPreferences(Utils.SITE_PREF, MODE_PRIVATE);
        dispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));

        player=MediaPlayer.create(this, R.raw.beep);

        notPaidCallBtn.setOnClickListener(this);

        registerBroadcast();

        rejectBtn.setOnClickListener(this);
        callBtn.setOnClickListener(this);
        //View view = inflater.inflate(R.layout.content_accept_service_fragment, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mapFragment.getView().setElevation(2);
        }
        updateTimeText = findViewById(R.id.update_time_txt);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/digital.ttf");
        updateTimeText.setTypeface(typeface);

        progressBar.setOnClickListener(this);
        progressBar.setProgress(0);
        //myTask = new MyTask();
        navigateBtn.setOnClickListener(this);

        Bundle bundle=getIntent().getExtras();
        if(bundle.getString("taskDetailsJson")!=null){
           // initFromServiceHistory(bundle.getString("taskDetailsJson"));
        }else {
            init(getIntent());
        }
    }


    private void initFromServiceHistory(String json) {
        isTaskAccepted=true;
        isPaymentSucceed=preferences.getBoolean(Utils.IS_PAYMENT_SUCCEED, false);
        Gson gson=new GsonBuilder().setDateFormat(getString(R.string.date_time_format_from_web)).create();
        TaskDetail detail=gson.fromJson(json, TaskDetail.class);

        afterAcceptRequest(detail.getCsLat(), detail.getCsLng(), detail.getCsName(), detail.getCsAddress());
        if(detail.getStatus().equalsIgnoreCase("3")){
            callBtn.setText(ARRIVED);
        }else if(detail.getStatus().equalsIgnoreCase("2")){
            callBtn.setText(START);
        }
        Log.i("responseData", "Task Details : "+detail.toJson());
    }

    private void registerBroadcast() {
        IntentFilter filter=new IntentFilter(Utils.CANCEL_ACTION);
        filter.addAction(Utils.CURRENT_LOCATION);
        filter.addAction(Utils.PAYMENT_CONFIRMATION_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadCastReciever, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle=intent.getExtras();

        String task_id=preferences.getString("task_id","0");
        String taskId=bundle.getString("task_id");
        Log.i("trackActivity", "Task ID in bundle : "+taskId);
        if(!task_id.equalsIgnoreCase(taskId)){
            init(intent);
        }
    }

    public void init(Intent intent){

        Log.i("trackActivity", "Init method : is task accepted :"+isTaskAccepted);
        Log.i("trackActivity", "Init method : is payment succeed :"+isPaymentSucceed);

        Bundle bundle = intent.getExtras();


        String customerName = bundle.getString("customerName");
        String customerMobile = bundle.getString("customerMobile");
        String customerAddress = bundle.getString("customerAddress");
        String serviceNameText = bundle.getString("subcategory");
        String taskId=bundle.getString("task_id");
        Log.i("trackActivity", "Task ID: "+taskId);
       /*
        serviceName2.setText(serviceNameText);
        this.customerName.setText(customerName);
        this.customerAddress.setText(customerAddress);*/
        String task_id=preferences.getString("task_id","0");

        if(task_id.equalsIgnoreCase(taskId)){

            isTaskAccepted=preferences.getBoolean(Utils.IS_ACCEPTED, false);
            if(!isTaskAccepted){
                try {
                    isPaymentSucceed=preferences.getBoolean(Utils.IS_PAYMENT_SUCCEED, false);
                    if(isPaymentSucceed){
                        callBtn.setText(START);
                        Log.i("trackActivity", "isPaymentSucceed true");
                    }

                    JSONObject jsonObject=new JSONObject(preferences.getString(Utils.TASK_DATA, "0"));
                    Log.i("trackActivity", "Call Accept Request");
                    afterAcceptRequest(
                            jsonObject.getString("customer_latitude"),
                            jsonObject.getString("customer_longitude"),
                            jsonObject.getString("customer_name"),
                            jsonObject.getString("customer_address"));
                } catch (Exception e) {
                    Log.e("responseDataError","Re TASK : "+ e.toString());
                }
            } else {


            }
        }else {
            if(dialog!=null)
            dialog.dismiss();

            setVisibility(View.VISIBLE, progressBar, rejectBtn, backgroundGray, serviceName, serviceAddress, updateTimeText);

            //setVisibility(View.VISIBLE, callToCustomerLayout);
            preferences.edit()
                    .putBoolean(Utils.IS_ACCEPTED, false)
                    .putBoolean(Utils.IS_PAYMENT_SUCCEED, false)
                    .apply();
            Log.i("trackActivity", "stare Preferences");
            myTask=new MyTask();

            myTask.execute(0);
        }

        serviceName.setText(serviceNameText);
        serviceAddress.setText(customerAddress);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(googleMap!=null) {
                    double lat=location.getLatitude();
                    double lng=location.getLongitude();
                    LatLng latLng=new LatLng(lat, lng);
                    AcceptServiceActivity.this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                    spMarker=googleMap.addMarker(new MarkerOptions()
                            .title(preferences.getString("fullname", "0"))
                            .position(latLng).visible(true));
                }else {

                }
            }
        });
    }



    @Override
    public void onBackPressed() {
        isPlayerSoundOff=true;
        if(myTask!=null)
        myTask.cancel(true);
        super.onBackPressed();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Bundle bundle=getIntent().getExtras();
        if(bundle.getString("taskDetailsJson")!=null){
            initFromServiceHistory(bundle.getString("taskDetailsJson"));
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circularProgressBar:
                        acceptRequest();
                break;
            case R.id.customer_call_btn:
                String text = callBtn.getText().toString();
                Log.i("responseData", "Call Btn Text  : " + text);
                if(text.equalsIgnoreCase(PAYMENT_WAIT_STR)){

                }else if (text.equalsIgnoreCase(START)) {
                    changeStatus(START);
                } else if (ARRIVED.equals(text)) {
                    changeStatus(ARRIVED);
                }


                break;
            case R.id.navigate_btn:
                startGoogleMapIntent();
                break;
            case R.id.reject_btn:
                finish();
                break;
            case R.id.not_paid_call_btn:
                callToCustomer();
                break;
        }
    }

    private void callToCustomer() {

    }


    private void acceptRequest() {
        Bundle bundle = getIntent().getExtras();
        task_id = bundle.getString("task_id");

            player.stop();

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
            Log.e("responseDataError", "Accept Request : "+e.toString());
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
                        String responseString = response.body();
                        Log.i("responseCode", "Accept Request : "+responseString);

                        try {
                            JSONObject res = new JSONObject(responseString);
                            boolean success = res.getBoolean("successBool");
                            if (success) {

                                JSONObject jsonObject=res.getJSONObject("response");
                                preferences.edit().putString(Utils.TASK_DATA, jsonObject.toString()).apply();
                                preferences.edit().putString("task_id",task_id ).apply();
                                preferences.edit().putBoolean(Utils.IS_ACCEPTED, true).apply();
                                isTaskAccepted=true;
                                afterAcceptRequest(
                                        jsonObject.getString("customer_latitude"),
                                        jsonObject.getString("customer_longitude"),
                                        jsonObject.getString("customer_name"),
                                        jsonObject.getString("customer_address"));
                            }
                        } catch (Exception e) {
                            Log.e("responseDataErrorJson","Json Error"+ e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("responseDataError", "Accept Request : "+t.toString());
                    }
                });
    }


    public void afterAcceptRequest(String csLat, String csLng, String csName, String csAddress) {

        String customerLatitudeStr = csLat;
        String customerLongitudeStr = csLng;
        double customerLat = Double.parseDouble(customerLatitudeStr);
        double customerLng = Double.parseDouble(customerLongitudeStr);
        String customerName = csName;
        String customerAddress = csAddress;


        dialog=new ProgressDialog(AcceptServiceActivity.this);
        dialog.setTitle("Wating for payment...");
        if(!isPaymentSucceed) {
            callBtn.setText(PAYMENT_WAIT_STR);
            dialog.show();
        }

        player.stop();
        isPlayerSoundOff=true;
        if(myTask!=null){
            myTask.cancel(true);
            myTask=null;
        }
        acceptRequestStr="accept";
        //preferences.edit().putBoolean("canCancel", true).apply();
        // dispatcher.schedule(isCanceljob);

                                        /*progressBar.setVisibility(View.GONE);
                                        rejectBtn.setVisibility(View.GONE);
                                        backgroundGray.setVisibility(View.GONE);
                                        callToCustomerLayout.setVisibility(View.VISIBLE);
                                        serviceName.setVisibility(View.GONE);
                                        serviceAddress.setVisibility(View.GONE);
                                        updateTimeText.setVisibility(View.GONE);
                                        navigateBtn.setVisibility(View.VISIBLE);*/
       // preferences.edit().putBoolean("spOnJob", true).apply();

        setVisibility(View.GONE, progressBar, rejectBtn, backgroundGray, serviceName, serviceAddress, updateTimeText);

        setVisibility(View.VISIBLE, callToCustomerLayout);


        customerLatlng = new LatLng(customerLat, customerLng);
        MarkerOptions options = new MarkerOptions();
        options.position(customerLatlng);
        options.visible(true);

        options.title(customerName);
        options.snippet(customerAddress);
        googleMap.addMarker(options);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatlng, 15));

        createPolyLine();


    }

    private void createPolyLine() {
        if (ActivityCompat.checkSelfPermission(AcceptServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AcceptServiceActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                JSONObject polyLine = jsonForPolyLine.getJSONObject("polyline");
                                String polyPoint = polyLine.getString("points");
                                //Log.i("responseDataGoogle", polyPoint);
                                //Toast.makeText(AcceptServiceActivity.this , polyPoint, Toast.LENGTH_LONG).show();
                                List<LatLng> polyPoints = PolyUtil.decode(polyPoint);
                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.addAll(polyPoints)
                                        .width(15)
                                        .color(Color.BLUE);
                                Log.i("responseDataGoogle", polyPoints.toString());

                                polyline = googleMap.addPolyline(polylineOptions);
                                if (spMarker != null) {
                                    spMarker.remove();
                                }

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.visible(true);
                                markerOptions.title(preferences.getString("fullname", "You"));
                                markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                                spMarker = googleMap.addMarker(markerOptions);

                                // Toast.makeText(AcceptServiceActivity.this, "Service Provider is add to marker", Toast.LENGTH_LONG).show();
                                                                     /*   LocationRequest request = new LocationRequest();
                                                                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                                                        request.setInterval(10000);
                                                                        request.setFastestInterval(500);

                                                                        if (ActivityCompat.checkSelfPermission(AcceptServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AcceptServiceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                                            return;
                                                                        }*/
                                //fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("responseDataError","responseError : "+ t.toString());
            }
        });

            }
        });
    }


    private void startGoogleMapIntent() {
                    double customerLat=customerLatlng.latitude;
                    double customerLng=customerLatlng.longitude;

                    String googleLocationUrl="google.navigation:q="+customerLat+","+customerLng;
                    Uri uri=Uri.parse(googleLocationUrl);
                    Intent mapIntent=new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
    }




    public void changeStatus(final String status){
        JSONObject requestData2=new JSONObject();
        JSONObject data2=new JSONObject();
        try {
            data2.put("apikey", getString(R.string.apikey));
            data2.put("v_code", getString(R.string.v_code));
            data2.put("sp_id", preferences.getString(Utils.USER_ID, "0"));
            data2.put("userToken", preferences.getString(Utils.USER_TOKEN, "0"));
            data2.put("task_id", task_id);
            if(START.equalsIgnoreCase(status)){
                data2.put("task_status", "3");
                preferences.edit().putBoolean(Utils.JOB_IN_PROGRESS, true).apply();
            }
            else if(ARRIVED.equalsIgnoreCase(status)){
                data2.put("task_status", "4");
            }else {
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
                                callBtn.setText(ARRIVED);
                                /*  if(status.equalsIgnoreCase("reached")){
                                    spTimerStart();
                                }*/

                                JSONObject resObj=res.getJSONObject("response");
                                String taskStatus=resObj.getString("task_status");
                                String taskId=resObj.getString("task_id");
                                if(taskStatus.equalsIgnoreCase("4")){
                                    spTimerStart();
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



    private void spTimerStart() {
        Intent intent=new Intent(AcceptServiceActivity.this, StartJobActivity.class);
        Bundle bundle=getIntent().getExtras();
        bundle.putDouble("lat", customerLatlng.latitude);
        bundle.putDouble("lng", customerLatlng.longitude);
        intent.putExtras(bundle);
        preferences.edit().putBoolean("canCancel", false).apply();
        startActivity(intent);
        dispatcher.cancel("is-task-cancel");
        finish();
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
                    if(isTaskAccepted)
                        break;
                    Thread thread=Thread.currentThread();
                    Log.i("threadLog", thread.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return acceptRequestStr;
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
           // super.onProgressUpdate(values);
            if(values.length>0) {
                player.stop();
                float progress_step=100.f/120.f;
                float current_progress=progress_step*values[0];
                ViewGroup.LayoutParams params=progressBar.getLayoutParams();
                ConstraintLayout.LayoutParams params1= (ConstraintLayout.LayoutParams) params;
                setProgress(current_progress);
                if(values[0]%4==0) {
                    int updateTime = 30 - updateTimeValue;
                    if (updateTime > -1) {
                        try {
                            if (updateTime < 10) {
                                if(!isPlayerSoundOff) {
                                    player.prepare();
                                    player.start();
                                }

                                updateTimeText.setText("00 : 0" + updateTime);
                            } else {
                                if(!isPlayerSoundOff) {
                                    player.prepare();
                                    player.start();
                                }
                                updateTimeText.setText("00 : " + updateTime);
                            }
                        } catch (Exception e) {
                                Log.e("responseDataError", "Player Error : "+e.toString());
                        }
                        updateTimeValue++;
                    }
                }
            }
        }



        @Override
        protected void onPostExecute(String s) {
            if(s!=null) {
                if (s.equalsIgnoreCase("accept")){
                        player.stop();
                }else if(s.equalsIgnoreCase("not_accept")){
                    player.stop();
                    Toast.makeText(AcceptServiceActivity.this, "Sorry you missed the job ", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }




    public void setProgress(float f){
        progressBar.setProgress(f);
    }


    public void setVisibility( int visible, View... view){
        for(View view1 : view){
            view1.setVisibility(visible);
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        preferences.edit().putBoolean("canCancel", false).apply();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadCastReciever);
        myTask.cancel(true);
    }



    BroadcastReceiver myBroadCastReciever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(action.equalsIgnoreCase(Utils.CANCEL_ACTION)){
                    new AlertDialog.Builder(AcceptServiceActivity.this)
                            .setTitle("Cancel Request")
                            .setMessage("Customer cancel your request sorry for inconvenience")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent1=new Intent(AcceptServiceActivity.this, HomeActivity.class);
                                    startActivity(intent1);
                                    finish();
                                }
                            })
                            .create()
                            .show();
                }
                if(action.equalsIgnoreCase(Utils.CURRENT_LOCATION)){
                    Bundle bundle=intent.getExtras();
                    double lat=bundle.getDouble("lat");
                    double lng=bundle.getDouble("lng");
                    LatLng spLatlng = new LatLng(lat, lng);
                    MarkerOptions options = new MarkerOptions();
                    options.position(spLatlng);
                    options.visible(true);

                    if(spMarker!=null){
                        spMarker.remove();
                    }

                    options.title(preferences.getString("fullname", "0"));
                    spMarker=googleMap.addMarker(options);
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatlng, 15));

                        Log.i("responseData", "Accept Service Activity : ");
                }
                if(action.equalsIgnoreCase(Utils.PAYMENT_CONFIRMATION_INTENT)){
                    //preferences.edit().putBoolean(Utils.IS_PAYMENT_SUCCEED, true).apply();
                    dialog.dismiss();

              new AlertDialog.Builder(AcceptServiceActivity.this)
                      .setTitle("Payment Comfirm")
                      .setMessage("Payment is confirm from customer you can proceed to job!")
                      .setPositiveButton("OK", null)
                      .create()
                      .show();

               Log.i("responseData", "Payment Process Complete");
                callBtn.setText(START);
                }
        }

    };
}