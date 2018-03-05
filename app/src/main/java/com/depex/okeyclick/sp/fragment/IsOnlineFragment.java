package com.depex.okeyclick.sp.fragment;

import android.Manifest;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.appscreens.LoginScreenActivity;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;

import com.depex.okeyclick.sp.services.CurrentLocationService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class IsOnlineFragment extends Fragment implements OnMapReadyCallback, RadioGroup.OnCheckedChangeListener, OnSuccessListener<Location> {
    GoogleMap googleMap;
    SharedPreferences preferences;
    ProjectAPI projectAPI;
    FusedLocationProviderClient mFusedLocationclient;
    FirebaseJobDispatcher jobDispatcher;
    Job locationJob;
    LocationManager manager;
    Context context;
    Toolbar toolbar;
    Marker marker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //  manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        toolbar = getActivity().getWindow().getDecorView().findViewById(R.id.toolbar);
        toolbar.setTitle("SERVICE STATUS");
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //  manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,this);


        LocationRequest request = new LocationRequest();

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(500);

        mFusedLocationclient = LocationServices.getFusedLocationProviderClient(getActivity());


        mFusedLocationclient.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    changeLocation(location);
                }
            }
        }, null);


        View view = inflater.inflate(R.layout.content_home_fragment, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        RadioGroup radioGroup = view.findViewById(R.id.on_off_group);
        radioGroup.setOnCheckedChangeListener(this);


        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));

        locationJob = jobDispatcher.newJobBuilder()
                .setService(CurrentLocationService.class)
                .setTag("location-job")
                .setRecurring(false)
                .setTrigger(Trigger.NOW)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.FOREVER)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(Utils.SITE_URL);
        builder.addConverterFactory(new StringConvertFactory());
        Retrofit retrofit = builder.build();
        projectAPI = retrofit.create(ProjectAPI.class);
        // mFusedLocationclient=LocationServices.getFusedLocationProviderClient(getActivity());

        preferences = getActivity().getSharedPreferences("service_pref", Context.MODE_PRIVATE);
        boolean isOnline = preferences.getBoolean("isOnline", false);
        RadioButton onlineButton = view.findViewById(R.id.online_radio);
        RadioButton offlineButton = view.findViewById(R.id.off_line_radio);

        if (isOnline) {
            onlineButton.setChecked(true);
            offlineButton.setChecked(false);
        } else {
            onlineButton.setChecked(false);
            offlineButton.setChecked(true);
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

                            if (ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(getActivity(),
                                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(getActivity(), new String[]{
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION}, 1 );

                            }

        mFusedLocationclient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                String fullname=preferences.getString("fullname", "0");
                LatLng latLng=new LatLng(location.getLatitude(), location.getLongitude());

                String snippetAddress="";
                if(IsOnlineFragment.this.googleMap!=null)
                    marker=IsOnlineFragment.this.googleMap.addMarker(new MarkerOptions().position(latLng).title(fullname).snippet(snippetAddress).visible(true));

                CameraPosition.Builder builder=new CameraPosition.Builder();
                builder.target(latLng);
                builder.zoom(15);
                builder.tilt(90);
                CameraPosition cameraPosition=builder.build();
                //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                if(IsOnlineFragment.this.googleMap!=null)
                    IsOnlineFragment.this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

            }
        });
      //  mFusedLocationclient.getLastLocation().addOnSuccessListener(IsOnlineFragment.this);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.online_radio:
                goStatus("on");
                break;
            case R.id.off_line_radio:
                goStatus("off");
                //Toast.makeText(getActivity(), "Offline", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void goStatus(String status){
        String user_id=preferences.getString("user_id", "0");
        JSONObject jsonObject=new JSONObject();
        JSONObject object=new JSONObject();
        try {
            jsonObject.put("apikey", getString(R.string.apikey));
            jsonObject.put("v_code", getString(R.string.v_code));
            jsonObject.put("online_status", status);
            jsonObject.put("userToken", preferences.getString("userToken","0"));
            jsonObject.put("user_id", user_id);

            object.put("RequestData", jsonObject);
            Call<String> statusCall= projectAPI.setStatus(object.toString());
            statusCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.i("responseData", response.body());
                    try {
                        JSONObject res=new JSONObject(response.body());
                        boolean success=res.getBoolean("successBool");
                        if(success){
                            JSONObject object1=res.getJSONObject("response");
                            String status=object1.getString("isonline");
                            if(status.equals("on")){
                                preferences.edit().putBoolean("isOnline" ,true).apply();
                                toolbar.setTitle("ONLINE");

                                /**
                                 * start job service .......
                                 */

                                jobDispatcher.schedule(locationJob);


                            }else if(status.equals("off")){
                                preferences.edit().putBoolean("isOnline", false).apply();
                                jobDispatcher.schedule(locationJob);
                                toolbar.setTitle("OFFLINE");
                            }
                        }else{
                            JSONObject errorObject=res.getJSONObject("ErrorObj");
                            String errorCode=errorObject.getString("ErrorCode");
                            String Msg="Session Timedout please login again ";
                            Toast.makeText(context, Msg, Toast.LENGTH_LONG).show();
                            Intent intent =new Intent(context, LoginScreenActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("responseError", t.toString());
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSuccess(Location location) {
           /* double lat=location.getLatitude();
            String fullname=preferences.getString("fullname","");
            double lng=location.getLongitude();
        LatLng latLng=new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(latLng).title(fullname).snippet("Current Location").visible(true));

        CameraPosition.Builder builder=new CameraPosition.Builder();
        builder.target(latLng);
        builder.zoom(10);
        builder.tilt(90);
        CameraPosition cameraPosition=builder.build();
        //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);*/
    }


    public void changeLocation(Location location) {
        double lat=location.getLatitude();
        String fullname=preferences.getString("fullname","");
        double lng=location.getLongitude();
        LatLng latLng=new LatLng(lat, lng);
        if(marker!=null){
            marker.remove();
        }
        Address address=null;
        if(context==null) return;
        Geocoder geocoder=new Geocoder(context);

        try {
           List<Address> list= geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
           if(list.size()>0)
               address=list.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String snippetAddress="";
        if(address!=null){
            if(address.getMaxAddressLineIndex()>0)
            snippetAddress=address.getAddressLine(0);
        }
        if(googleMap!=null)
        marker=googleMap.addMarker(new MarkerOptions().position(latLng).title(fullname).snippet(snippetAddress).visible(true));

        CameraPosition.Builder builder=new CameraPosition.Builder();
        builder.target(latLng);
        builder.zoom(15);
        builder.tilt(90);
        CameraPosition cameraPosition=builder.build();
        //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if(googleMap!=null)
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
    /*  @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/
}