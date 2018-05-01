package com.depex.okeyclick.sp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.modal.ServiceProviderModal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hedgehog.ratingbar.RatingBar;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PublicProfileFragment extends Fragment implements View.OnClickListener{
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int PICK_IMAGE = 1;
    @BindView(R.id.view_pager_profile)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.user_image_provider_profile)
    ImageView roundedImageView;

    @BindView(R.id.upload_pic_click)
    ImageView uploadPic;


    @BindView(R.id.service_provider_name)
    TextView serviceProviderName;

    @BindView(R.id.per_hour_price)
    TextView perHourPrice;

    @BindView(R.id.star_view_profile)
    RatingBar ratingBar;


    @BindView(R.id.book_now_btn)
    Button bookNow;
    private Context context;

    @BindView(R.id.parant_layout)
    ConstraintLayout parantLayout;

    SharedPreferences preferences;
    private BottomSheetDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_service_provider_profile_fragment, container, false);
        ButterKnife.bind(this, view);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        preferences=context.getSharedPreferences(Utils.SITE_PREF, Context.MODE_PRIVATE);
        uploadPic.setOnClickListener(this);
       /* String json=getArguments().getString("json");
        preferences=context.getSharedPreferences(Utils.SITE_PREF, Context.MODE_PRIVATE);
        serviceProviderModal = ServiceProviderModal.fromJson(json);
        getProfile(serviceProviderModal);*/
       initScreen();
        return view;
    }

    private void initScreen() {
        new Retrofit.Builder()
                .addConverterFactory(new StringConvertFactory())
                .baseUrl(Utils.SITE_URL)
                .build()
                .create(ProjectAPI.class)
                .getPublicProfile(getString(R.string.apikey), preferences.getString("user_id", "0"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData", "Public profile :  "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                Gson gson=new GsonBuilder().setDateFormat(getString(R.string.date_time_format_from_web)).create();
                                JSONObject resObj=res.getJSONObject("response");
                                JSONObject userObj=resObj.getJSONObject("List");
                                ServiceProviderModal modal=gson.fromJson(userObj.toString(), ServiceProviderModal.class);
                                Log.i("responseData", "public profile service provider modal : "+modal.toJson());
                                getProfile(modal);

                            }
                        } catch (JSONException e) {
                            Log.e("responseDataError", "Public Profile Error : "+e.toString());
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("responseDataError", "Public Profile Error : "+t.toString());
                    }
                });
    }


    private void getProfile(ServiceProviderModal serviceProviderModal) {
        serviceProviderName.setText(serviceProviderModal.getName()+" "+ serviceProviderModal.getLastName());
        ratingBar.setStar(3);
        perHourPrice.setText(serviceProviderModal.getPricePerHour());
        GlideApp.with(context).load(serviceProviderModal.getProfilePic()).placeholder(R.drawable.user_dp_place_holder).circleCrop().into(roundedImageView);
        ReviewsServiceProviderFragment reviewsServiceProviderFragment= ReviewsServiceProviderFragment.getInstance(serviceProviderModal);
        DetailsServiceProviderFragment detailsServiceProviderFragment=DetailsServiceProviderFragment.getInstance(serviceProviderModal);
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(reviewsServiceProviderFragment);
        fragments.add(detailsServiceProviderFragment);
        SectionPagerAdapter adapter=new SectionPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        //DetailsSpProfileFragment detailsSpProfileFragment=DetailsSpProfileFragment.getInstance(serviceProviderModal);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_pic_click:
                showBottomSheet();
                break;
            case R.id.camera_btn:
                pickFromCamera();
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
            case R.id.gallery_btn:
                pickFromGallery();
                if (dialog != null) {
                    dialog.dismiss();
                }

        }
    }


    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void pickFromCamera() {

        Intent takingPhotoIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takingPhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takingPhotoIntent, CAMERA_REQUEST_CODE);
        }

    }




    private void showBottomSheet() {
        dialog=new BottomSheetDialog(context);
        View view1=LayoutInflater.from(context).inflate(R.layout.content_bottom_sheet_layout_profile_fragment, null, false);
        Button camera=view1.findViewById(R.id.camera_btn);
        Button gallery=view1.findViewById(R.id.gallery_btn);
        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        dialog.setContentView(view1);
        dialog.show();
    }



    private class SectionPagerAdapter  extends FragmentPagerAdapter {
        List<Fragment> list;
        public SectionPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list=list;
        }


        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }



    public static PublicProfileFragment getInstance(ServiceProviderModal serviceProviderModal, String json){
        PublicProfileFragment fragment=new PublicProfileFragment();
        Bundle bundle=new Bundle();
        bundle.putString("json", serviceProviderModal.toJson());
        //json 2 for package category subcategory
        bundle.putString("json2", json);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK && requestCode==PICK_IMAGE){

            Uri selectedImage = data.getData();
            GlideApp.with(context).load(selectedImage).circleCrop().into(roundedImageView);
            GlideApp.with(context).asFile().load(selectedImage).into(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                    Log.i("responseData", "Profile Fragment File Path : "+resource.getPath());
                }
            });
        }
        if(resultCode== Activity.RESULT_OK && requestCode==CAMERA_REQUEST_CODE){
            Bitmap bitmap=(Bitmap)data.getExtras().get("data");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                FileOutputStream fou=new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fou);
                GlideApp.with(context).load(image).circleCrop().into(roundedImageView);
                createFileUploadInRetrofit2(image);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void createFileUploadInRetrofit2(File file) {
        String contentType= MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        String mimeType=MimeTypeMap.getSingleton().getMimeTypeFromExtension(contentType);
        RequestBody requestBody =RequestBody.create(MediaType.parse(mimeType), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image",file.getName(), requestBody);
        RequestBody v_code=RequestBody.create(MultipartBody.FORM, getString(R.string.v_code));
        RequestBody apikey=RequestBody.create(MultipartBody.FORM, getString(R.string.apikey));
        RequestBody userToken=RequestBody.create(MultipartBody.FORM, preferences.getString("userToken", "0"));
        RequestBody user_id=RequestBody.create(MultipartBody.FORM, preferences.getString("user_id", "0"));
        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProjectAPI.class)
                .upload(v_code, apikey, image, userToken, user_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody=response.body();
                        try {
                            InputStream io=responseBody.byteStream();
                            BufferedReader reader=new BufferedReader(new InputStreamReader(io));
                            String line;
                            while ((line=reader.readLine())!=null){
                                Log.i("inputStreamReader", line);
                            }
                        } catch (Exception e) {
                            Log.e("responseDataError", "CommentActivity : "+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseDataError", "Comment Activity : "+ t.toString());
                    }
                });
    }


}