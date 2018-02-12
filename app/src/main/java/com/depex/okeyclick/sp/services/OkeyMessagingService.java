package com.depex.okeyclick.sp.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.appscreens.AcceptServiceActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Date;
import java.util.Map;

public class OkeyMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String address=remoteMessage.getFrom();
        Log.i("remoteMessage","From : " +address);

            Log.i("remoteMessage", "Message Data Payload : "+remoteMessage.getData());
           // String title=remoteMessage.getNotification().getTitle();

          //  Log.i("remoteMessage", "Notification Title : "+ title);

          //  Log.i("remoteMessage", "Notification msg : "+remoteMessage.getNotification().getBody());
       /* NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelid="notify_chennel1";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel(channelid, "NotifyChannel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }


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
        manager.notify(0, notification);*/
    }
}