package com.depex.okeyclick.sp.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.depex.okeyclick.sp.services.CurrentLocationService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class MyBroadcastReciever extends BroadcastReceiver {
    FirebaseJobDispatcher jobDispatcher;
    SharedPreferences preferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        preferences=context.getSharedPreferences("service_pref", Context.MODE_PRIVATE);
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            boolean status=preferences.getBoolean("isOnline", false);
            if(status){
                    jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(context));
                Toast.makeText(context, "Start Service for android ", Toast.LENGTH_LONG).show();
                Job locationJob=jobDispatcher
                        .newJobBuilder()
                        .setTag("location-job")
                        .setService(CurrentLocationService.class)
                        .setRecurring(false)
                        .setReplaceCurrent(true)
                        .setTrigger(Trigger.NOW)
                        .setLifetime(Lifetime.FOREVER)
                        .build();
                    jobDispatcher.schedule(locationJob);
            }
        }
    }
}