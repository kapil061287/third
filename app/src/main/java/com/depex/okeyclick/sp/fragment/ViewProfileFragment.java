package com.depex.okeyclick.sp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.appscreens.UpgradePlanActivity;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
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
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ViewProfileFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    SharedPreferences preferences;
    FusedLocationProviderClient client;
    Context context;
    GoogleMap googleMap;
    Marker marker;

    @BindView(R.id.profile_pic)
    ImageView profilePic;

    @BindView(R.id.username_view_profile_fr)
    TextView userNameViewProfile;

    @BindView(R.id.mobile_view_profile)
    TextView mobileViewProfile;

    @BindView(R.id.star_view_profile)
    RatingBar starViewProfile;

    @BindView(R.id.price_per_hour)
    TextView pricePerHour;

    @BindView(R.id.service_view_profile)
    TextView serviceViewProfile;

    @BindView(R.id.net_earning)
    TextView netEarning;

    @BindView(R.id.service_1_view_profile)
    TextView service1ViewProfile;

    @BindView(R.id.service_2_view_profile)
    TextView service2ViewProfile;

    @BindView(R.id.total_earning_view_profile)
    TextView totalEarningViewProfile;

    @BindView(R.id.no_online_view_profile)
    TextView noOnlineViewProfile;

    @BindView(R.id.no_service_view_profile)
    TextView noServiceViewProfile;

    @BindView(R.id.estimated_net_view_profile)
    TextView estimatedNetViewProfile;

    @BindView(R.id.download_excel)
    Button downloadExcel;

    @BindView(R.id.download_pdf)
    Button downloadPdf;

    @BindView(R.id.membership)
    TextView memberShipPlan;

    @BindView(R.id.change_status)
    Button changeStatusBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_profile_view_fragment, container, false);
        ButterKnife.bind(this, view);
        changeStatusBtn.setOnClickListener(this);

        client = LocationServices.getFusedLocationProviderClient(context);
        Toolbar toolbar = getActivity().getWindow().getDecorView().findViewById(R.id.toolbar);
        toolbar.setTitle("HOME");
        preferences = context.getSharedPreferences(Utils.SITE_PREF, Context.MODE_PRIVATE);
        if(preferences.getBoolean("isOnline", false)){
            changeStatusBtn.setText("Off");
        }else {
            changeStatusBtn.setText("On");
        }
        TextView username_text = view.findViewById(R.id.username_view_profile_fr);
        username_text.setText(preferences.getString("fullname", "SP"));
        RatingBar ratingBar = view.findViewById(R.id.star_view_profile);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.user_location_view_profile);
        mapFragment.getMapAsync(this);
        ratingBar.setStar(4);
        memberShipPlan.setOnClickListener(this);

        initProfile();
        return view;
    }

    private void setMemberShipPlan(String plan) {
        switch (plan) {
            case "Basic":
                memberShipPlan.setText("Basic (Upgrade)");
                break;
            case "Premium":
                memberShipPlan.setText(plan + " (Upgrade)");
                memberShipPlan.setTextColor(context.getResources().getColor(R.color.success_color));
                break;
            case "Advance":
                memberShipPlan.setText(plan);
                memberShipPlan.setTextColor(context.getResources().getColor(R.color.success_color));
        }
    }

    private void initProfile() {
        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .getPublicProfile(getString(R.string.apikey), preferences.getString("user_id", "0"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString = response.body();
                        Log.i("responseData", "View Profile : " + responseString + "");
                        if (responseString == null)
                            return;

                        try {
                            JSONObject res = new JSONObject(responseString);
                            boolean success = res.getBoolean("successBool");
                            if (success) {
                                JSONObject resObj = res.getJSONObject("response");
                                JSONObject spData = resObj.getJSONObject("List");
                                String totalEarning = spData.getString("total_earning");
                                String lastEarning = spData.getString("last_earning");

                                if (totalEarning != null)
                                    totalEarningViewProfile.setText(getString(R.string.uro)+totalEarning);
                                else
                                    totalEarningViewProfile.setText(getString(R.string.uro)+"0.00");
                                if (netEarning != null)
                                    netEarning.setText(getString(R.string.uro)+lastEarning);
                                else netEarning.setText(getString(R.string.uro)+"0.00");
                                GlideApp.with(context).load(spData.getString("user_images")).circleCrop().into(profilePic);
                                //serviceViewProfile.setText(spData.getString("category"));
                                mobileViewProfile.setText(spData.getString("mobile"));

                                setMemberShipPlan(spData.getString("premium_membership"));
                                String memberShipId = spData.getString("member_ship_id");
                                preferences.edit().putString("membership", spData.getString("premium_membership"))
                                        .putString("membershipId", memberShipId).apply();
                                userNameViewProfile.setText(new StringBuilder().append(spData.getString("first_name")).append(" ").append(spData.getString("last_name")).toString());
                                pricePerHour.setText(new StringBuilder().append(spData.getString("pac_price_per_hr")).append("$ / hour").toString());
                                float rating = (float) spData.getDouble("rating");
                                starViewProfile.setStar(3.4f);
                            }
                        } catch (JSONException e) {
                            Log.e("responseDataError", "View Profile Error: " + e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        initProfile();
                        Log.e("responseDataError", "View Profile Error : " + t.toString());
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
                    , 1);

            return;
        }
        setLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                } else {
                    setLocation();
                }
                break;
        }
    }

    void setLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) return;
                MarkerOptions options = new MarkerOptions();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                options.position(latLng);
                options.visible(true);
                options.title(preferences.getString("fullname", "You"));

                marker = ViewProfileFragment.this.googleMap.addMarker(options);
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latLng);
                circleOptions.clickable(true);
                circleOptions.radius(500);
                circleOptions.visible(true);
                circleOptions.strokeColor(Color.RED);


                ViewProfileFragment.this.googleMap.addCircle(circleOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                ViewProfileFragment.this.googleMap.moveCamera(cameraUpdate);

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.membership:
                   // startUpgradeActivity();
                break;
            case R.id.change_status:
                    getFragmentManager().beginTransaction().replace(R.id.container_fragement, new IsOnlineFragment()).addToBackStack(null).commit();
                break;
        }
    }

    private void startUpgradeActivity() {
        Intent intent=new Intent(context, UpgradePlanActivity.class);
        startActivity(intent);
    }
}
