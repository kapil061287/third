package com.depex.okeyclick.sp.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.appscreens.AcceptServiceActivity;
import com.depex.okeyclick.sp.appscreens.PaymentConfirmActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Date;
import java.util.Map;

public class OkeyMessagingService extends FirebaseMessagingService {
    private static final int CONFIRM_PAYMENT_REQUEST_CODE = 2;
    RemoteMessage remoteMessage;
    SharedPreferences preferences;
    NotificationManager manager;
    String channelid;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String address=remoteMessage.getFrom();

        manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        channelid="notify_chennel1";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel(channelid, "NotifyChannel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        Log.i("remoteMessage","From : " +address);
            preferences=getSharedPreferences("service_pref", MODE_PRIVATE);
            Log.i("remoteMessage", "Message Data Payload : "+remoteMessage.getData());
            this.remoteMessage=remoteMessage;
            if("create_request".equalsIgnoreCase(remoteMessage.getData().get("notification_type"))){
                if(preferences.getBoolean("isInHome", false)){
                    Intent intent=createIntentForAcceptRequest(remoteMessage.getData());
                    startActivity(intent);
                }else if (!preferences.getBoolean("spOnJob", false)){
                    sendNotificationForAcceptRequest();
                }
                //sendNotificationForAcceptRequest();
            }else if ("payment_process".equalsIgnoreCase(remoteMessage.getData().get("notification_type"))){
                Bundle bundle=createBundleFromMap(remoteMessage.getData());
                Intent paymentConfirmIntent =createIntentFromBundle(bundle, PaymentConfirmActivity.class);

                if(preferences.getBoolean("isInInvoice", false)){
                    startActivity(paymentConfirmIntent);
                }else{
                    sendNotificationForConfirmPayment(paymentConfirmIntent);
                }
            }



           // String title=remoteMessage.getNotification().getTitle();

          //  Log.i("remoteMessage", "Notification Title : "+ title);

          //  Log.i("remoteMessage", "Notification msg : "+remoteMessage.getNotification().getBody());

    }

    public Intent createIntentForAcceptRequest(Map<String, String> dataMap){
        Intent intent=new Intent(this , AcceptServiceActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("subcategory",dataMap.get("subcategory") );
        bundle.putString("customerName", dataMap.get("customer_name") );
        bundle.putString("customerAddress", dataMap.get("customer_address"));
        bundle.putString("task_id", dataMap.get("task_id"));
        bundle.putLong("requestTime", new Date().getTime());
        bundle.putString("customerMobile", dataMap.get("customer_mobile"));
        intent.putExtras(bundle);
        return intent;
    }

    private void sendNotificationForConfirmPayment(Intent forPendingIntent) {
        Notification notification=new NotificationCompat.Builder(this, channelid)
                .setContentIntent(PendingIntent.getActivity(this, CONFIRM_PAYMENT_REQUEST_CODE, forPendingIntent, PendingIntent.FLAG_ONE_SHOT))
                .setContentTitle("Payment Confirmation")
                .setSmallIcon(R.drawable.logo)
                .setContentText("You recieved a payment from the task ")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();
        manager.notify(1, notification);
    }

    private Bundle createBundleFromMap(Map<String, String> data) {

        Bundle bundle=new Bundle();
        for(Map.Entry<String, String> entry : data.entrySet()){
            String key=entry.getKey();
            String value=entry.getValue();
            bundle.putString(key, value);
        }
        return bundle;
    }

    public Intent createIntentFromBundle(Bundle bundle, Class classObj){
        Intent intent=new Intent(this, classObj);
        if(bundle!=null)
        intent.putExtras(bundle);
        return  intent;
    }

    public void sendNotificationForAcceptRequest(){

        Map<String, String> dataMap =remoteMessage.getData();
        String task=dataMap.get("task_id");
        String msg=null;
        if(task!=null){
            msg="You have a request from a customer !";
        }else{
            msg="Another Notification";
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this, channelid);
        builder.setSmallIcon(R.drawable.logo);
        Intent intent=new Intent(this , AcceptServiceActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("subcategory",dataMap.get("subcategory") );
        bundle.putString("customerName", dataMap.get("customer_name") );
        bundle.putString("customerAddress", dataMap.get("customer_address"));
        bundle.putString("task_id", dataMap.get("task_id"));
        bundle.putLong("requestTime", new Date().getTime());
        bundle.putString("customerMobile", dataMap.get("customer_mobile"));
        intent.putExtras(bundle);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1, intent,  PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("Service Request");
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentText("You have a request from a customer !");
        Notification notification=builder.build();
        notification.flags=NotificationCompat.FLAG_AUTO_CANCEL;
        manager.notify(0, notification);
    }
}