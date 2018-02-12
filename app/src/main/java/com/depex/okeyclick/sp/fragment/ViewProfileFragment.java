package com.depex.okeyclick.sp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import com.depex.okeyclick.sp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hedgehog.ratingbar.RatingBar;


public class ViewProfileFragment extends Fragment implements OnMapReadyCallback {

    SharedPreferences preferences;
    FusedLocationProviderClient client;
    Context context;
    GoogleMap googleMap;
    Marker marker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_profile_view_fragment, container, false);
        Toolbar toolbar=getActivity().getWindow().getDecorView().findViewById(R.id.toolbar);
        toolbar.setTitle("HOME");
        preferences = context.getSharedPreferences("service_pref", Context.MODE_PRIVATE);
        TextView username_text = view.findViewById(R.id.username_view_profile_fr);
        username_text.setText("Mr. " + preferences.getString("fullname", "SP"));
        RatingBar ratingBar = view.findViewById(R.id.star_view_profile);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.user_location_view_profile);
        mapFragment.getMapAsync(this);
        ratingBar.setStar(4);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        client = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
                    , 1);


            return;
        }

        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location==null)return;
                MarkerOptions options=new MarkerOptions();
                LatLng latLng=new LatLng(location.getLatitude(), location.getLongitude());

                options.position(latLng);
                options.visible(true);
                options.title(preferences.getString("fullname", "You"));

                marker=ViewProfileFragment.this.googleMap.addMarker(options);
                CircleOptions circleOptions=new CircleOptions();
                circleOptions.center(latLng);
                circleOptions.clickable(true);
                circleOptions.radius(5000);
                circleOptions.visible(true);
                circleOptions.strokeColor(Color.RED);

                ViewProfileFragment.this.googleMap.addCircle(circleOptions);
                CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLng(latLng);
                ViewProfileFragment.this.googleMap.moveCamera(cameraUpdate);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1 :
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_DENIED){



                }else {

                }
                break;
        }
    }
}
