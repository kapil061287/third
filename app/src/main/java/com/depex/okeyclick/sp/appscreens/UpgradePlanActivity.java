package com.depex.okeyclick.sp.appscreens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.constants.UtilsMethods;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.fragment.MembershipFragment;
import com.depex.okeyclick.sp.modal.Membership;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpgradePlanActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.premium_pack_tabs)
    TabLayout tabLayout;

    @BindView(R.id.premium_pack_view_pager)
    ViewPager viewPager;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_plan);
        ButterKnife.bind(this);
        preferences=getSharedPreferences(Utils.SITE_PREF, MODE_PRIVATE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        initMemberShipPlan();
    }

    private void initMemberShipPlan() {
        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .getMemberShipPlan(getString(R.string.apikey))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData","Upgrage Plan : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                JSONArray memberShipArrObj=resObj.getJSONArray("List");
                                Gson gson=new Gson();
                                Membership[]membershipArr=gson.fromJson(memberShipArrObj.toString(), Membership[].class);
                                List<Membership> memberships=new ArrayList<>(Arrays.asList(membershipArr));
                                MembershipPagerAdapter adapter =new MembershipPagerAdapter(getSupportFragmentManager(), memberships);
                                viewPager.setAdapter(adapter);
                                Log.i("responseData", "Membership Array : "+ Arrays.toString(membershipArr));
                            }

                        } catch (JSONException e) {
                            Log.e("responseDataError","Upgrade Plan : "+ e.toString());
                        }
                    }


                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if(UtilsMethods.isInternetAvailable(UpgradePlanActivity.this)){
                            initMemberShipPlan();
                        }
                            Log.e("responseDataError", "Upgrade Plan : "+t.toString());
                    }
                });
    }


    class MembershipPagerAdapter extends FragmentPagerAdapter{
        private List<Membership> memberships;
        public MembershipPagerAdapter(FragmentManager fm, List<Membership> memberships) {
            super(fm);
            Collections.sort(memberships,new  Membership.MemberComparator());
            this.memberships=memberships;
        }


        @Override
        public Fragment getItem(int position) {
            return MembershipFragment.getInstance(memberships.get(position));
        }


        @Override
        public int getCount() {
            return memberships.size();
        }
    }
}