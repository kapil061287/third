package com.depex.okeyclick.sp.appscreens;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.fragment.AcceptServiceFragment;
import com.depex.okeyclick.sp.fragment.InviteAndEarnFragment;
import com.depex.okeyclick.sp.fragment.IsOnlineFragment;
import com.depex.okeyclick.sp.fragment.PaymentHistoryFragment;
import com.depex.okeyclick.sp.fragment.PublicProfileFragment;
import com.depex.okeyclick.sp.fragment.ViewProfileFragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_online);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_color));
        setSupportActionBar(toolbar);

        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        preferences.edit().putBoolean("isInHome", true).apply();
        String token=preferences.getString("deviceToken", "0");
        String tokenString=FirebaseInstanceId.getInstance().getToken();
        Log.i("tokenR", tokenString);
        sendTokenToserver(tokenString);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        ViewProfileFragment fragment=new ViewProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragement, fragment).addToBackStack(null).commit();

        FirebaseMessaging firebaseMessaging=FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic("spOffers");

        String fullname =preferences.getString("fullname", "Not Login !");
        int count=navigationView.getHeaderCount();
        if(count>0){
            View view=navigationView.getHeaderView(0);
            TextView name=view.findViewById(R.id.user_name_header_nav);
            name.setText(fullname);
            TextView textView=view.findViewById(R.id.textView);
            ImageView view1=view.findViewById(R.id.imageView);
            GlideApp.with(HomeActivity.this).load("").placeholder(R.drawable.user_dp_place_holder).into(view1);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewProfileFragment fragment=new ViewProfileFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragement, fragment).addToBackStack(null).commit();
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        FragmentManager manager=getSupportFragmentManager();
        int count=manager.getBackStackEntryCount();
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(count==1) {
            finish();
        }
          else{
                super.onBackPressed();
           }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.is_online, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
       switch (id){
           case R.id.notification_menu_home:

               break;
       }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.home_menu:
                ViewProfileFragment viewProfileFragment=new ViewProfileFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_fragement, viewProfileFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.change_status_menu:
                IsOnlineFragment fragment=new IsOnlineFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_fragement, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.logout_menu:
                preferences.edit().clear().apply();
                Toast.makeText(this, "Successfully Logout", Toast.LENGTH_LONG).show();
                Intent loginIntent=new Intent(this, LoginScreenActivity.class);
                startActivity(loginIntent);
                finish();
                break;
            case R.id.public_profile_menu:
                PublicProfileFragment publicProfileFragment=new PublicProfileFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_fragement, publicProfileFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.offers_menu:
              /*  getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_fragement, new AcceptServiceFragment())
                        .addToBackStack(null)
                        .commit();*/
                break;
            case R.id.service_history_menu:
                startServiceHistoryActivity();
                break;
            case R.id.share_with_others_menu:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_fragement, new InviteAndEarnFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.term_and_condition_menu:
                startWebView(getString(R.string.term_and_condition_url), "Term And Condition");
                break;
            case R.id.payment_history_menu:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_fragement, new PaymentHistoryFragment())
                        .addToBackStack(null)
                        .commit();
                break;
        }


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startWebView(String url, String title){
        Intent intent=new Intent(this, WebInnerViewActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("title", title);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void startServiceHistoryActivity() {
        Intent intent=new Intent(this, ServiceHistoryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("isBackground", "App is in background");
        //getSharedPreferences("app_background", MODE_PRIVATE).edit().putBoolean("isBackground", true).commit();
    }

    public void sendTokenToserver(String token){

        if(!preferences.getBoolean("isLogin", false)){
            Log.i("tokenRefresh", "Usernot login");
            return;
        }

        JSONObject requestData=new JSONObject();
        try {
            String userToken=preferences.getString("userToken", "0");
            JSONObject data=new JSONObject();
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("deviceType", "android");
            data.put("userToken", userToken);
            data.put("user_id", preferences.getString("user_id", "0"));
            data.put("DeviceToken", token);
            requestData.put("RequestData" , data);
            Log.i("requestDataDevice","Device Token Requst : "+ requestData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Retrofit.Builder builder=new Retrofit.Builder();
        builder.baseUrl(Utils.SITE_URL);
        builder.addConverterFactory(new StringConvertFactory());
        Retrofit retrofit=builder.build();
        ProjectAPI projectAPI=retrofit.create(ProjectAPI.class);
        Log.i("requestData", "Null Check updateFcmToken : "+requestData);
        Call<String> updateCall=projectAPI.updateFcmToken(requestData.toString());
        updateCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response==null) return;
                Log.i("responseData", response.body()+"");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                    Log.i("responseError", t.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.edit().putBoolean("isInHome", false).apply();
        preferences.edit().putBoolean("spOnJob", false).apply();
    }
}