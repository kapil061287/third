package com.depex.okeyclick.sp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.annotation.GlideModule;
import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.view.OkeyClickTextView;
import com.depex.okeyclick.sp.view.SubCatRadioButton;
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



public class PublicProfileFragment extends Fragment {


    private Context context;
    SharedPreferences preferences;

    @BindView(R.id.profile_pic_public_p)
    RoundedImageView publicProfilePic;

    @BindView(R.id.public_profile_name)
    TextView publicProfileName;

    @BindView(R.id.public_profile_per_hour_rate)
    TextView publicProfileRate;

    @BindView(R.id.public_profile_exp)
    TextView publicProfileExp;

    @BindView(R.id.public_profile_package)
    TextView publicProfilePackage;

    @BindView(R.id.public_profile_category)
    TextView publicProfileCategory;

    @BindView(R.id.public_profile_sub_category_linear_layout)
    LinearLayout publicProfileSubCategoryLinearLayout;

    @BindView(R.id.public_profile_about)
    TextView publicProfileAbout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_public_profile_fragment, container, false);

        Toolbar toolbar=getActivity().getWindow().getDecorView().findViewById(R.id.toolbar);
        toolbar.setTitle("Public Profile");
        preferences=context.getSharedPreferences("service_pref", Context.MODE_PRIVATE);
        ButterKnife.bind(this, view);
        initPublicProfile();


        return view;
    }



    private void initPublicProfile() {
        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .getPublicProfile(getString(R.string.apikey), preferences.getString("user_id", "0"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String resString=response.body();
                        Log.i("responseData", "Public Profile Response : "+resString);
                        try {
                            JSONObject resJson=new JSONObject(resString);
                            boolean success=resJson.getBoolean("successBool");
                            if(success){
                                JSONObject res=resJson.getJSONObject("response");
                                JSONArray list=res.getJSONArray("List");
                                JSONObject jsonObject=list.getJSONObject(0);

                                String hourRate=jsonObject.getString("pac_price_per_hr");
                                publicProfileRate.setText(hourRate);
                                String name=jsonObject.getString("first_name");
                                publicProfileName.setText("Mr. "+name);

                                String exp=jsonObject.getString( "exp");
                                publicProfileExp.setText(exp);
                                String pack=jsonObject.getString("pac_name");
                                publicProfilePackage.setText(pack);
                                publicProfileCategory.setText(jsonObject.getString("category"));
                                GlideApp.with(context).load(jsonObject.getString("user_images")).into(publicProfilePic);
                                String about=jsonObject.getString("about_company");
                                publicProfileAbout.setText(about);


                                JSONArray subCategories=jsonObject.getJSONArray("subcategory");
                                for(int i=0;i<subCategories.length();i++){
                                    JSONObject jsonObject1=subCategories.getJSONObject(i);
                                    String id=jsonObject1.getString("id");
                                    String subName=jsonObject1.getString("service_name");
                                    OkeyClickTextView okeyClickTextView=new OkeyClickTextView(context, R.layout.okey_click_text_view);
                                    TextView OkeyClickTextView1= (TextView) okeyClickTextView.getView(publicProfileSubCategoryLinearLayout, subName);
                                    publicProfileSubCategoryLinearLayout.addView(OkeyClickTextView1);


                                }




                            }
                        } catch (JSONException e) {
                            Log.e("responseDataError", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                            Log.e("responseData", t.toString());
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
}
