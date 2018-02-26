package com.depex.okeyclick.sp.appscreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.adapters.PackgeSpinnerAdpater;
import com.depex.okeyclick.sp.adapters.ServiceSpinnerAdapter;
import com.depex.okeyclick.sp.adapters.SubServiceMultiSpinnerAdapter;
import com.depex.okeyclick.sp.api.APICallback;
import com.depex.okeyclick.sp.api.ApiListener;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.modal.Package;
import com.depex.okeyclick.sp.modal.Service;
import com.depex.okeyclick.sp.modal.SubService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lamudi.phonefield.PhoneEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupScreenActivitySecondFace extends AppCompatActivity implements ApiListener<JsonObject>, AdapterView.OnItemSelectedListener,  View.OnClickListener {

    @BindView(R.id.sub_category_multispinner)
    MultiSelectSpinner subCategorySpinner;
    @BindView(R.id.service_spinner)
    Spinner serviceSpinner;
    @BindView(R.id.package_spinner)
    Spinner packageSpinner;
    @BindView(R.id.distance_spinner)
    Spinner distanceSpinner;
    @BindView(R.id.otp_signpu)
    EditText otp_txt;
    /*@BindView(R.id.mobile_signup)
    EditText mobile_txt;*/

    @BindView(R.id.mobile_signup)
    PhoneEditText mobileSignup;

    @BindView(R.id.send_otp_btn)
    Button send_otp_btn;
    @BindView(R.id.register_btn_signup)
    Button registerBtn;

    String otpNumber;
    boolean isVerifyOTP=false;
   // boolean isSendOtp=false;
    SharedPreferences preferences;
    ProjectAPI api;
    APICallback apiCallback;

    @BindView(R.id.term_condition_checkbox)
    CheckBox termConditionCheckbox;

    StringBuilder subcategoriesforform=new StringBuilder();
    String tag=this.getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen_second_face);
        ButterKnife.bind(this);
        mobileSignup.getEditText().setBackgroundResource(R.drawable.input_field_back);
        mobileSignup.getEditText().getLayoutParams().height=getResources().getDimensionPixelSize(R.dimen.height_mobile_box);
        mobileSignup.getEditText().setPadding(getResources().getDimensionPixelSize(R.dimen.input_field_paddingLeft),0,0,0);
        mobileSignup.setHint(R.string.mobile);
        mobileSignup.getSpinner().setBackgroundResource(R.drawable.input_field_back);
       // mobileSignup.getSpinner().setPadding(10, 0, 0, 0);

        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) mobileSignup.getSpinner().getLayoutParams();
        params.setMargins(0, 0, 5, 0);
        params.height=getResources().getDimensionPixelSize(R.dimen.input_field_height);
        mobileSignup.getSpinner().setLayoutParams(params);
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        initSpinner();
        Retrofit.Builder builder=new Retrofit.Builder();
        builder.baseUrl(Utils.SITE_URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit=builder.build();
        api=retrofit.create(ProjectAPI.class);
        Call<JsonObject> services=api.getServices(getString(R.string.apikey));
        apiCallback=new APICallback(this);
        services.enqueue(apiCallback);
        Call<JsonObject> package_service=api.getPackages(getString(R.string.apikey));
        package_service.enqueue(apiCallback);
        serviceSpinner.setOnItemSelectedListener(this);
        subCategorySpinner.setOnItemSelectedListener(this);
        send_otp_btn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }


    private void initSpinner() {
            ArrayList<Service> services=new ArrayList<>();
            Service service=new Service();
            service.setServiceName("Category");
            services.add(service);


            ArrayList<SubService> subServices=new ArrayList<>();
            SubService subService=new SubService();
            subService.setServiceName("Sub Service");


            ArrayList<Package> packages=new ArrayList<>();
            Package pack=new Package();
            pack.setPackageName("Package");
            packages.add(pack);


        final ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add(0, "Select Sub Service");
            ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, arrayList);
            subCategorySpinner.setListAdapter(arrayAdapter1).setSelectAll(false).setMinSelectedItems(1);
            subCategorySpinner.setAllUncheckedText("Sub Services");
            subCategorySpinner.setAllCheckedText("All Selected");

            ServiceSpinnerAdapter serviceSpinnerAdapter=new ServiceSpinnerAdapter(this, services);
            serviceSpinner.setAdapter(serviceSpinnerAdapter);


            PackgeSpinnerAdpater spinnerAdpater=new PackgeSpinnerAdpater(this, packages);
            packageSpinner.setAdapter(spinnerAdpater);


            ArrayList<String> distanceList=new ArrayList<>();
            distanceList.add("Distance in Km");
            distanceList.add("5-10");
            distanceList.add("11-15");
            distanceList.add("16-20");
            distanceList.add("21-25");

        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, distanceList);
        distanceSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public void success(Call<JsonObject> call, JsonObject response, Object... objects) {
        if(response==null)
            return;
        Log.i(tag , response.toString());
        boolean success=response.get("successBool").getAsBoolean();
        if(success){
            String responseType=response.get("responseType").getAsString();
            JsonObject responseObj=response.getAsJsonObject("response");
            Gson gson=new Gson();
            switch (responseType){
                case "get_category":
                    JsonArray catetory_list=responseObj.getAsJsonArray("List");
                    Service[]servicesArr=gson.fromJson(catetory_list, Service[].class);
                    ArrayList<Service> services=new ArrayList<>(Arrays.asList(servicesArr));
                    Service service=new Service();
                    service.setServiceName("Service");
                    services.add(0,service);
                    Log.i(tag, services.toString() );
                    ServiceSpinnerAdapter adapter=new ServiceSpinnerAdapter(this, services);
                    serviceSpinner.setAdapter(adapter);
                    break;

                case "get_subcategory":
                    JsonArray subService_list=responseObj.getAsJsonArray("List");
                    SubService[]subServices_arr=gson.fromJson(subService_list, SubService[].class);
                    final ArrayList<SubService> subServices=new ArrayList<>(Arrays.asList(subServices_arr));
                    //final SubService subService=new SubService();
                    //subService.setServiceName("Sub Service");
                   // subServices.add(0, subService);

                    final ArrayList<String> arrayList=new ArrayList<>();
                    for(SubService subService1 : subServices){
                        arrayList.add(subService1.getServiceName());
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_multiple_choice, arrayList);

                   subCategorySpinner.setListAdapter(arrayAdapter).setSelectAll(false).setMinSelectedItems(1);
                   subCategorySpinner.setSelection(0, true);


                    subCategorySpinner.setListener(new BaseMultiSelectSpinner.MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] selected) {
                            boolean first=true;
                            boolean last=false;
                            subcategoriesforform=new StringBuilder();
                            for(int i=0;i<selected.length;i++){
                                if(selected[i]){

                                    if(first) {
                                        subcategoriesforform.append(subServices.get(i).getId());
                                        first=false;
                                    }else {
                                        subcategoriesforform.append(","+subServices.get(i).getId());
                                    }
                                }
                            }
                            Log.i(tag, subcategoriesforform.toString());
                        }
                    });

                    break;
                case "package_list":
                    JsonArray package_list=responseObj.getAsJsonArray("List");
                    Package[]packages_arr=gson.fromJson(package_list, Package[].class);
                    ArrayList<Package> packages=new ArrayList<>(Arrays.asList(packages_arr));
                    Log.i(tag, packages.toString());
                    PackgeSpinnerAdpater spinnerAdpater=new PackgeSpinnerAdpater(this, packages);
                    packageSpinner.setAdapter(spinnerAdpater);
                    break;
                case "send_otp":
                    otp_txt.setVisibility(View.VISIBLE);
                    send_otp_btn.setText("Resend OTP");
                    otpNumber=responseObj.get("otp").getAsString();
                    //isSendOtp=true;
                    break;
                case "user_register":

                    break;
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           switch (parent.getId()){
               case R.id.service_spinner:
                   ServiceSpinnerAdapter adapter= (ServiceSpinnerAdapter) parent.getAdapter();
                   Service service= (Service) adapter.getItem(position);
                   String service_id=service.getId();
                   Call<JsonObject> subServices=api.getSubServices(getString(R.string.apikey), service_id);
                   subServices.enqueue(apiCallback);
                   break;
           }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_otp_btn:
           // if(!isSendOtp){
                Call<JsonObject> otpCall=api.getOtp(getString(R.string.apikey), mobileSignup.getPhoneNumber());
                otpCall.enqueue(apiCallback);
            /*}else {
                if(otp_txt.getText().toString().equals(otpNumber)){
                    Toast.makeText(this, "Successfully Verify OTP", Toast.LENGTH_LONG).show();
                    isVerifyOTP=true;
                    registerBtn.setEnabled(true);
                    otp_txt.setVisibility(View.GONE);
                    send_otp_btn.setVisibility(View.GONE);

                }else {
                    Toast.makeText(this, "Invalid OTP!", Toast.LENGTH_LONG).show();
                    send_otp_btn.setText("Resend OTP");
                }
            }*/
            break;

            case R.id.register_btn_signup:
               // Toast.makeText(this, "Button Clicked ", Toast.LENGTH_LONG).show();
                if(otp_txt.getText().toString().equals(otpNumber)){
                   // Toast.makeText(this, "Successfully Verify OTP", Toast.LENGTH_LONG).show();
                    isVerifyOTP=true;
                    registerBtn.setEnabled(true);
                   // otp_txt.setVisibility(View.GONE);
                   // send_otp_btn.setVisibility(View.GONE);
                }else {
                    Toast.makeText(this, "Invalid OTP!", Toast.LENGTH_LONG).show();
                    //send_otp_btn.setText("Resend OTP");
                }
                if(!termConditionCheckbox.isChecked()){
                    Toast.makeText(this, "Please Check term and condition box !", Toast.LENGTH_LONG).show();
                    return;
                }
                if(isVerifyOTP){
                    String mobile=mobileSignup.getPhoneNumber();
                    String service=((Service)serviceSpinner.getSelectedItem()).getId();
                    String subservices=subcategoriesforform.toString();
                    String packageID=((Package)packageSpinner.getSelectedItem()).getId();
                    String distance=distanceSpinner.getSelectedItem().toString();
                    String firstFace=preferences.getString("first_", "0");
                    if(!firstFace.equals("0")){
                        try {
                            JSONObject requestData=new JSONObject(firstFace);
                            JSONObject data=requestData.getJSONObject("requestData");
                            Log.i(tag, data.toString());
                            data.put("deviceType", "android");
                            data.put("deviceID", "82150528-23LG-4622-B303-68B4572F9305");
                            data.put("accessType", "False");
                            data.put("accessName", "Normal");
                            data.put("mobile", mobile);
                            data.put("category", service);
                            data.put("subcategory", subservices);
                            data.put("package", packageID);
                            String dist=distance.split("-")[1];
                            data.put("distance", dist);
/**
 * Code for register user only other responses in success method of this class of APIListener.
  */
                            JSONObject requestData1=new JSONObject();
                            requestData1.put("RequestData", data);
                            StringConvertFactory factory=new StringConvertFactory();
                            Retrofit.Builder builder=new Retrofit.Builder()
                                    .baseUrl(Utils.SITE_URL)
                                    .addConverterFactory(factory);
                            Retrofit retrofit=builder.build();
                            //JsonObject jsonObject=new JsonObject();

                            Call<String> call=retrofit.create(ProjectAPI.class).signUp(requestData1.toString());
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    String body=response.body();
                                    Log.i("responseData", "Registration Response : "+body);
                                    try {
                                        JSONObject jsonObject1=new JSONObject(body);
                                        boolean b=jsonObject1.getBoolean("successBool");
                                        if(b){
                                            preferences.edit().clear().apply();
                                            Toast.makeText(SignupScreenActivitySecondFace.this, "Successfully Register !", Toast.LENGTH_LONG ).show();
                                            Intent intent=new Intent(SignupScreenActivitySecondFace.this, LoginScreenActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }else{
                                            JSONObject object=jsonObject1.getJSONObject("ErrorObj");
                                            String errorCode=object.getString("ErrorCode");
                                            if(errorCode.equals("104")){
                                                String errorMsg=object.getString("ErrorMsg");
                                                Toast.makeText(SignupScreenActivitySecondFace.this, errorMsg,Toast.LENGTH_LONG).show();
                                                preferences.edit().remove("first_").apply();
                                                Intent intent=new Intent(SignupScreenActivitySecondFace.this, SignupScreenActivityFirstFace.class);
                                                startActivity(intent);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        Log.e("responseData", "Exception Error : "+e.toString());
                                    }


                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.i("responseError", t+"");
                                }
                            });
                            Log.i("requestData", requestData1.toString());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.i(tag, "Preferences not found !");
                    }
                }else {
                    Log.i(tag, "Otp is not verified !");
                }

                break;
        }
    }
}