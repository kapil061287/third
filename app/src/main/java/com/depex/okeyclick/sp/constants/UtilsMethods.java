package com.depex.okeyclick.sp.constants;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.appscreens.AcceptServiceActivity;
import com.depex.okeyclick.sp.appscreens.StartJobActivity;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.depex.okeyclick.sp.modal.ServiceHistory;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UtilsMethods {

    public static final int genrateASCIIforHashCode(String str){
        int result=0;
        if(str!=null){
            for(int i =0;i<str.length();i++){
                result=str.charAt(i);
            }
            return result;
        }
        return result;
    }


    public static String getRatingText(float rate){
        String rateText="Excellent";
        if(rate<=1){
            rateText="Poor";
        }
        else if(rate>1 && rate<=2){
            rateText="Fair";
        }
        else if(rate>2 && rate<=3){
            rateText="Average";
        }
        else if(rate>3 && rate<=4){
            rateText="Good";
        }
        else if (rate>4 && rate<=5){
            rateText="Excellent";
        }
        return rateText;
    }


    public static boolean isInternetAvailable(Context context){
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        if(info!=null) {
            if (info.isConnectedOrConnecting()) {
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }


    public static void viewTaskProcess(final Context context, final ServiceHistory serviceHistory){
        final ServiceHistory serviceHistory1=serviceHistory;
        SharedPreferences preferences=context.getSharedPreferences(Utils.SITE_PREF, Context.MODE_PRIVATE);
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", context.getString(R.string.v_code));
            data.put("apikey", context.getString(R.string.apikey));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("user_id",preferences.getString("user_id","0"));
            data.put("task_id", serviceHistory.getId());
            requestData.put("RequestData", data);

            new Retrofit.Builder()
                    .addConverterFactory(new StringConvertFactory())
                    .baseUrl(Utils.SITE_URL)
                    .build()
                    .create(ProjectAPI.class)
                    .taskDetails(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String responseString=response.body();
                            Log.i("responseData","Task Details API : "+responseString);
                            String status=serviceHistory1.getStatus();
                            try {
                                JSONObject res=new JSONObject(responseString);
                                boolean success=res.getBoolean("successBool");
                                Bundle bundle=null;
                                Intent intent=null;
                                if(success){

                                    JSONObject jsonObject=res.getJSONObject("response");

                                    switch (status){
                                        case "1":
                                            break;
                                        case "2":
                                          /*  bundle=new Bundle();
                                            bundle.putString("taskDetailsJson", jsonObject.toString());
                                            intent=new Intent(context, AcceptServiceActivity.class);
                                            intent.putExtras(bundle);
                                            context.startActivity(intent);*/
                                            break;
                                        case "3":
                                            /*bundle=new Bundle();
                                            bundle.putString("taskDetailsJson", jsonObject.toString());
                                            intent=new Intent(context, AcceptServiceActivity.class);
                                            intent.putExtras(bundle);
                                            context.startActivity(intent);*/
                                            break;
                                        case "4":
                                            /*bundle=new Bundle();
                                            bundle.putString("taskDetailsJson", jsonObject.toString());
                                            intent=new Intent(context, StartJobActivity.class);
                                            intent.putExtras(bundle);
                                            context.startActivity(intent);*/
                                            break;
                                        case "5":
                                            /*bundle=new Bundle();
                                            bundle.putString("taskDetailsJson", jsonObject.toString());
                                            intent=new Intent(context, StartJobActivity.class);
                                            intent.putExtras(bundle);
                                            context.startActivity(intent);*/
                                            break;
                                        case "6":
                                            break;
                                        case "7":
                                            break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("responseDataError","Task Details API Error : "+t.toString());
                        }
                    });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void startJobJourney(Bundle bundle) {

    }
}