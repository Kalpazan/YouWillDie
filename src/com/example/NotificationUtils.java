package com.example;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;

public class NotificationUtils {

    //copypasted from http://habrahabr.ru/post/140928/

    private static final String TAG = NotificationUtils.class.getSimpleName();

    private static NotificationUtils instance;

    private static Context context;
    private NotificationManager manager;
    private int lastId = 0;
    private HashMap<Integer, Notification> notifications;

    private NotificationUtils(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications = new HashMap<Integer, Notification>();
    }

    public static NotificationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationUtils(context);
        } else {
            instance.context = context;
        }
        return instance;
    }

    public int createInfoNotification(String message) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
                //NotificationCompat.Builder nb = new NotificationBuilder(context) //for ver. Android > 3.0
                .setSmallIcon(R.drawable.icon_tongue)
                .setAutoCancel(true)
                .setTicker(message)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("AppName")
                .setDefaults(Notification.FLAG_SHOW_LIGHTS);

        Notification notification = nb.build();
        manager.notify(lastId, notification);
        notifications.put(lastId, notification);
        return lastId++;
    }
}
