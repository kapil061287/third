package com.depex.okeyclick.sp.appscreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.api.ApiListener;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener, ApiListener<JsonObject> {

    @BindView(R.id.signup_button)
    Button signup_btn;

    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.email_login)
    TextInputLayout email_txt;
    @BindView(R.id.password_login)
    TextInputLayout passwordLogin;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        ButterKnife.bind(this);

        //set onclick listener
        setOnClickListener(signup_btn, loginBtn);
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        prefEditor=preferences.edit();

    }

    private void setOnClickListener(View... views) {
        for(View view : views){
            view.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button:
                Intent signupIntent = new Intent(this, SignupScreenActivityFirstFace.class);
                startActivity(signupIntent);
                finish();

            break;
            case R.id.login_btn:
                String email=email_txt.getEditText().getText().toString();
                String password=passwordLogin.getEditText().getText().toString();

                try{
                    JSONObject requestData=new JSONObject();
                    JSONObject data=new JSONObject();
                    data.put("apikey", getString(R.string.apikey));
                    data.put("v_code", getString(R.string.v_code));
                    data.put("deviceType", "android");
                    data.put("deviceID",
                            "82150528-23LG-4622-B303-68B4572F9305");
                    data.put("username", email);
                    data.put("password", password);
                    requestData.put("RequestData", data);

                    Retrofit.Builder builder=new Retrofit.Builder();
                    builder.baseUrl(Utils.SITE_URL);
                    builder.addConverterFactory(new StringConvertFactory());
                    ProjectAPI api=builder.build().create(ProjectAPI.class);
                    Call<String> loginCall=api.login(requestData.toString());
                    loginCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response.body());
                                Log.i("responseLogin", jsonObject.toString());
                                boolean success=jsonObject.getBoolean("successBool");
                                if(success){
                                    JSONObject responseData=jsonObject.getJSONObject("response");
                                    prefEditor.putBoolean("isOnline", false);
                                    prefEditor.putBoolean("isLogin", true);
                                    prefEditor.putString("user_id", responseData.getString("user_id"));
                                    prefEditor.putString("fullname", responseData.getString("fullname"));
                                    prefEditor.putString("userToken", responseData.getString("userToken"));
                                    prefEditor.commit();
                                    Intent intent=new Intent(LoginScreenActivity.this, HomeActivity.class);
                                    startActivity(intent);

                                }else{
                                    JSONObject object=jsonObject.getJSONObject("ErrorObj");
                                    String errorCode=object.getString("ErrorCode");
                                    if(errorCode.equals("109")) {
                                        String errorMsg = object.getString("ErrorMsg");
                                        Toast.makeText(LoginScreenActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                                t.printStackTrace();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }
    }
    @Override
    public void success(Call<JsonObject> call, JsonObject response, Object... objects) {
        Log.i("retrofit2Json", response.toString());
    }
}