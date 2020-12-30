package com.km.cameraapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class NotificationsUtil extends ContextWrapper {

    private static final String TAG = "MyNotifications";
    public static final String CHANNEL_ID="my_default_channel_id";

    private int notifId;
    private String title;


    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Notification notification;
    Context context;

    public NotificationsUtil(Context context, String title, int notifid) {
        super(context);
        notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context=context;
        this.title=title;
        this.notifId=notifid;
        Log.d(TAG, "MyNotifications: notifid="+notifId);
    }

    private void createNotification(){
/*

*/
        builder=new NotificationCompat.Builder(context,CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                //.setContentIntent(pendingIntent)
                ;

    }


    public void updateNotification(String subtitle, boolean ongoing){
        updateNotification(subtitle,ongoing,false);

    }

    public void updateNotification(String subtitle, boolean ongoing, boolean error){
        if(builder==null){
            createNotification();
        }
        builder.setContentText(subtitle);
        builder.setOngoing(ongoing);
        if(error){
            builder.setSmallIcon(R.drawable.ic_error);
        }else if(ongoing){
            builder.setSmallIcon(android.R.drawable.stat_sys_upload);
        }else{
            builder.setSmallIcon(R.drawable.ic_check_white);
        }

        notification=builder.build();
        notificationManager.notify(notifId,notification);

    }


    public void hideNotification(){
        notificationManager.cancel(notifId);
    }

    public int getNotifId() {
        return notifId;
    }

    public Notification getNotification() {
        return notification;
    }



    public static void createNotificationChannel(Context context) {
        Log.d(TAG, "createNotificationChannel: ");
        //https://developer.android.com/training/notify-user/channels
        //IntentsUtils.openNotificationChannelSettings(context);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "my channel";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Uri customTone= Uri.parse("android.resource://"+context.getApplicationContext().getPackageName()+"/"+R.raw.notify);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            //mChannel.enableLights(true);
            //mChannel.enableVibration(true);
            channel.setSound(customTone, attributes); // This is IMPORTANT
            //channel.setLightColor(Color.GREEN);
            //channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if(notificationManager!=null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
