package com.depex.okeyclick.sp.appscreens;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.depex.okeyclick.sp.constants.UtilsMethods.getRatingText;

public class PaymentConfirmActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    @BindView(R.id.state_progress)
    StateProgressBar stateProgressBar;

    @BindView(R.id.sp_image)
    RoundedImageView spImage;

    @BindView(R.id.customer_image)
    RoundedImageView customerImage;

    @BindView(R.id.submit_btn)
    Button submitBtn;
    AlertDialog dialog ;
    SharedPreferences preferences;


    RatingBar ratingBarBottomSheet;


    TextView rateTextView;

    Button submitRatingBtn;

    EditText rateCommentEdit;

    String reciverId;

    String task_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_payment_confirm);
        ButterKnife.bind(this);
        String[]arr=new String[]{"Service\nAccepted", "On the \nWay", "Arrived to \nLocation", "Start the \nJob", "Finish \nJob"};
        stateProgressBar.setStateDescriptionData(arr);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
        getSupportActionBar().setTitle("Payment Confirmation");
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        task_id=preferences.getString("task_id", "0");
        submitBtn.setOnClickListener(this);

        initserviceProvider();

    }

    private void initserviceProvider() {

        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();

        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("user_id", preferences.getString("user_id", "0"));
            data.put("task_id", task_id);
            requestData.put("RequestData", data);
            Log.i("requestData", "Payment Confirmation : "+requestData.toString());
        } catch (JSONException e) {
            Log.e("responseDataError", "Payment Confirmation : "+e.toString());
        }


        new Retrofit.Builder()
                .addConverterFactory(new StringConvertFactory())
                .baseUrl(Utils.SITE_URL)
                .build()
                .create(ProjectAPI.class)
                .checkSpStatus(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData", "Payment Confirmation : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                JSONObject spData=resObj.getJSONObject("sp_Data");
                                String url=spData.getString("user_images");
                                GlideApp.with(PaymentConfirmActivity.this).load(url).placeholder(R.drawable.user_dp_place_holder).into(spImage);

                                JSONObject csData=resObj.getJSONObject("cs_Data");
                                String url2=csData.getString("user_images");
                                reciverId=csData.getString("id");
                                GlideApp.with(PaymentConfirmActivity.this).load(url).placeholder(R.drawable.user_dp_place_holder).into(customerImage);

                            }

                        } catch (JSONException e) {
                            Log.e("responseDataError", "Payment Confirmation : "+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("responseDataError", "Payment Confirmation Error : "+t.toString());
                            initserviceProvider();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit_btn:
                    confirmPayment();
                break;
            case R.id.submit_rating_btn:
                    submitRating();
                break;
        }

    }

    private void submitRating() {

        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("sender_id", preferences.getString("user_id","0"));
            data.put("receiver_id", reciverId);
            data.put("task_id", task_id);
            data.put("rate", ratingBarBottomSheet.getRating());
            data.put("comment", rateCommentEdit.getText().toString());
            requestData.put("RequestData", data);
            Log.i("requestData", "Rating API : "+requestData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .rating(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData", "Rating Api : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                String msg=resObj.getString("msg");
                                showConfirmRatingDialog(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    AlertDialog confirmRatingDialog;

    private void showConfirmRatingDialog(String msg) {
        confirmRatingDialog =new AlertDialog.Builder(this)
                .setMessage(msg)
                .setTitle("Confirm Rating")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startHomeActivity();
                        confirmRatingDialog.dismiss();
                    }
                }).create();
        confirmRatingDialog.show();
    }

    private void startHomeActivity() {
        Intent homeIntent=new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }



    private void confirmPayment() {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("user_id", preferences.getString("user_id", "0"));
            data.put("task_id", task_id);
            requestData.put("RequestData", data);
            Log.i("requestData", "confirm complete Data : "+requestData.toString());
        } catch (JSONException e) {
            Log.e("responseDataError", "Confirm Complete Error : "+e.toString());
        }
        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .confirmComplete(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString=response.body();
                        Log.i("responseData", "Confirm Complete : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                showConfirmDialog();
                            }
                        } catch (JSONException e) {
                           Log.e("responseDataError", "Confirm Complete Error : "+e.toString());
                        }
                    }


                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("responseDataError", "Confirm Complete : "+t.toString());
                        confirmPayment();
                    }
                });
    }



    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Task Information");
        builder.setMessage("Task Complete Successfully !");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                showReviewRatingSheet();

            }
        });
        dialog=builder.create();
        dialog.show();
    }


    private void showReviewRatingSheet() {
       // Toast.makeText(this, "Review Rating screen sheet " , Toast.LENGTH_LONG).show();
        View view= LayoutInflater.from(this).inflate(R.layout.content_review_rating_action_sheet, null, false);
        BottomSheetDialog dialog=new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
        ratingBarBottomSheet=view.findViewById(R.id.rating_bar_review_bottom_sheet);
        rateTextView=view.findViewById(R.id.rate_txt);
        submitRatingBtn=view.findViewById(R.id.submit_rating_btn);
        submitRatingBtn.setOnClickListener(this);
        rateTextView.setText(getRatingText(ratingBarBottomSheet.getRating()));
        ratingBarBottomSheet.setOnRatingBarChangeListener(this);
        rateCommentEdit=view.findViewById(R.id.write_review_edit);
    }


    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        rateTextView.setText(getRatingText(v));
    }
}