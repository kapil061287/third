package com.depex.okeyclick.sp.services;
import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

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

import okhttp3.internal.Util;
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
                Location location=locationResult.getLastLocation();
                //for(Location location : locationResult.getLocations()){
                    changeLocation(location);
                    if(preferences.getBoolean(Utils.JOB_IN_PROGRESS, false)) {
                        Intent intent = new Intent(Utils.CURRENT_LOCATION);
                        Bundle bundle=new Bundle();
                        bundle.putDouble("lat", location.getLatitude());
                        bundle.putDouble("lng", location.getLongitude());
                        intent.putExtras(bundle);
                        LocalBroadcastManager.getInstance(CurrentLocationService.this).sendBroadcast(intent);
                    }
                //}
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


    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i("jobDispatcher", "Location Job is closed !");

        return true;
    }


    public void changeLocation(final Location location) {
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
                        Log.i("responseData","Current Loction Service : "+response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        changeLocation(location);
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
}