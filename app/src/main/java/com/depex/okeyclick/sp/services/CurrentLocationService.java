package com.depex.okeyclick.sp.services;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.appscreens.AcceptServiceActivity;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CurrentLocationService extends JobService {
    public static final int REQ_CODE = 1;
    LocationManager manager;
    String userid;
    String userToken;
    FusedLocationProviderClient mFusedLocationclient;
    String fullname;
    boolean isOnline;
    LocationCallback mLocationCallback;
    SharedPreferences preferences;

    @Override
    public boolean onStartJob(JobParameters job) {
        //manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        preferences =getSharedPreferences("service_pref", MODE_PRIVATE);
        userToken=preferences.getString("userToken", "0");
        isOnline=preferences.getBoolean("isOnline", false);
        userid=preferences.getString("user_id", "0");
        fullname=preferences.getString("fullname", "0");



        //checkRequestfromCustomer();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

        }
        //manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, CurrentLocationService.this);
        //manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, CurrentLocationService.this);

        LocationRequest request=new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(500);

        mLocationCallback= new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for(Location location : locationResult.getLocations()){
                    changeLocation(location);
                }
            }
        };

        mFusedLocationclient= LocationServices.getFusedLocationProviderClient(this);

        if (!isOnline) {
            Log.i("jobDispatcher", "Job can be closed ");
            isOnline = preferences.getBoolean("isOnline", false);
            Log.i("jobDispatcher", String.valueOf(isOnline));
            if(mFusedLocationclient!=null){
               // if(mLocationCallback!=null)
                    //mFusedLocationclient.removeLocationUpdates(mLocationCallback);
                Log.i("jobDispatcher", "Location Client is stoped !");
            }
            return false;
        }else {
            Log.i("jobDispatcher", "user is online now ");
            mFusedLocationclient.requestLocationUpdates(request, mLocationCallback  , null);
        }
        return true;
    }

    private void checkRequestfromCustomer() {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", "u"));
            data.put("user_id", preferences.getString("user_id", "0"));
            requestData.put("RequestData", data);
            new Retrofit.Builder()
                    .baseUrl(Utils.SITE_URL)
                    .addConverterFactory(new StringConvertFactory())
                    .build()
                    .create(ProjectAPI.class)
                    .getNotification(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.i("responseData", "NotificationAPI : "+ response.body());
                            String responseBody=response.body();
                            try {
                                JSONObject res=new JSONObject(responseBody);
                                boolean success=res.getBoolean("successBool");
                                if(success){
                                    JSONObject resObj=res.getJSONObject("response");
                                    JSONArray jsonArray=resObj.getJSONArray("List");
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                       String task_id= jsonObject.getString("task_id");
                                       String pref_task=preferences.getString("task_id", "0");
                                       if(!pref_task.equals(task_id)){
                                           preferences
                                                   .edit()
                                                   .putString("task_id", task_id).apply();
                                           jsonObject.getString("notification_data");
                                       }
                                    }

                                    if(!preferences.getBoolean("isOffline", false)){
                                        checkRequestfromCustomer();
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


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i("jobDispatcher", "Location Job is closed !");
        //if(mLocationCallback!=null)
      //  mFusedLocationclient.removeLocationUpdates(mLocationCallback);
        return true;
    }


    public void changeLocation(Location location) {
        Log.i("currentLocationApi", location.toString());
            double lat=location.getLatitude();
            double lng=location.getLongitude();
        LatLng latLng=new LatLng(lat, lng);
        Geocoder geocoder=new Geocoder(this);
        try {
            List<Address> list=geocoder.getFromLocation(lat, lng, 1);
            if(list.size()>0){
                Address address=list.get(0);
                int max=address.getMaxAddressLineIndex();
                Log.i("locationUpdate", address.getAddressLine(0));
                //Toast.makeText(this, address.getAddressLine(0), Toast.LENGTH_LONG).show();

                Retrofit.Builder builder=new Retrofit.Builder();
                builder.baseUrl(Utils.SITE_URL);
                builder.addConverterFactory(new StringConvertFactory());
                Retrofit retrofit=builder.build();
                ProjectAPI projectAPI=retrofit.create(ProjectAPI.class);
                JSONObject requestData=new JSONObject();
                JSONObject data=new JSONObject();
                data.put("v_code", getString(R.string.v_code));
                data.put("apikey", getString(R.string.apikey));
                data.put("userToken", userToken);
                data.put("user_id", userid);
                data.put("latitude", String.valueOf(lat));
                data.put("longitude", String.valueOf(lng));
                data.put("address", address.getAddressLine(0));
                requestData.put("RequestData", data);

                Call<String> requestCall=projectAPI.updateLocation(requestData.toString());
                requestCall.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.i("responseData", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("responseData", t.toString());
                    }

                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


public void sendNotificationToSp(String task_id, String customer_data){

        String[]arr=customer_data.split(":");

        Log.i("responseDataCustomer", "Customer Address : "+arr.length);

        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelid="notify_chennel1";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel(channelid, "NotifyChannel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

       /* String task=dataMap.get("task_id");
        String msg=null;
        if(task!=null){
            msg="You have a request from a customer !";
        }else{
            msg="Another Notification";
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this, channelid);
        builder.setSmallIcon(R.drawable.logo);
        Intent intent=new Intent(this , AcceptServiceActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("subcategory",dataMap.get("subcategory") );
        bundle.putString("customerName", dataMap.get("customer_name") );
        bundle.putString("customerAddress", dataMap.get("customer_address"));
        bundle.putString("task_id", task_id);
        bundle.putLong("requestTime", new Date().getTime());
        bundle.putString("customerMobile", dataMap.get("customer_mobile"));
        intent.putExtras(bundle);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1, intent,  PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("Service Request");
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentText("You have a request from a customer !");
        Notification notification=builder.build();
        notification.flags=NotificationCompat.FLAG_AUTO_CANCEL;
        manager.notify(0, notification);*/
}
}