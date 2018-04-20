package com.depex.okeyclick.sp.services;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.constants.UtilsMethods;
import com.depex.okeyclick.sp.factory.StringConvertFactory;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IsTaskCancelService extends JobService {

    SharedPreferences preferences;
    NotificationManager manager;

    String channelId;
    @Override
    public boolean onStartJob(JobParameters job) {
        channelId="notify_chennel1";
        manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this, channelId);

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentText("Customer canceled the corrent task  Sorry for Inconvenience !");
        builder.setContentTitle("Cancel Current Task ");
        // builder.setContentIntent()

        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
        checkServiceProviderRunningStatus(job);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        return true;
    }

    private void checkServiceProviderRunningStatus(final JobParameters job) {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("v_code", getString(R.string.v_code));
            data.put("apikey", getString(R.string.apikey));
            data.put("user_id", preferences.getString("user_id", "0"));
            data.put("task_id", preferences.getString("task_id", "0"));
            requestData.put("RequestData", data);
        } catch (JSONException e) {
            Log.e("responseDataError", "Is task cancel Service : "+e.toString());
        }



        new Retrofit.Builder()
                .baseUrl(Utils.SITE_URL)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectAPI.class)
                .checkSpStatus(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString =response.body();
                        if(responseString==null){
                            return;
                        }
                        Log.i("responseDataRunning", "isTask Cancel Service "+responseString+"" );
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                int task_status=resObj.getInt("task_status");

                                JSONObject spData=resObj.getJSONObject("sp_Data");
                                String firstName=spData.getString("first_name");

                                if(task_status!=9 ){
                                    if(preferences.getBoolean("canCancel", false)) {
                                        checkServiceProviderRunningStatus(job);
                                    }else {
                                        jobFinished(job, false);
                                    }
                                }else {
                                    //preferences.edit().putBoolean("isCancelTask", true).apply();=
                                     LocalBroadcastManager manager=LocalBroadcastManager.getInstance(IsTaskCancelService.this);
                                    Intent intent=new Intent(Utils.CANCEL_ACTION);
                                    manager.sendBroadcast(intent);
                                    jobFinished(job, false);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("responseDataError", e.toString());
                        }
                    }


                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if(UtilsMethods.isInternetAvailable(IsTaskCancelService.this)){
                            checkServiceProviderRunningStatus(job);
                        }
                        Log.i("responseDataError", t.toString());
                    }
                });
    }
}