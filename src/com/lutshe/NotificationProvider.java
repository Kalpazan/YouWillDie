package com.lutshe;

import android.content.res.Resources;
import android.content.res.TypedArray;

public class NotificationProvider {

    private static NotificationProvider instance;

    private NotificationTemplate[] notifications;

    private NotificationProvider(Resources resources) {
        loadNotifications(resources);
    }

    public synchronized static NotificationProvider getInstance(Resources resources) {
        if (instance == null) {
            instance = new NotificationProvider(resources);
        }

        return instance;
    }

    private synchronized void loadNotifications(Resources resources) {
        String[] messages = resources.getStringArray(R.array.messages);
        TypedArray imgs = resources.obtainTypedArray(R.array.icons);
        notifications = new NotificationTemplate[messages.length];
        for (int i = 0; i < messages.length; i++) {
            String[] strings = messages[i].split("\\|");
            int iconNumber = Integer.valueOf(strings[3]);
            int icon = imgs.getResourceId(iconNumber, -1);
            notifications[i] = new NotificationTemplate(strings[0], strings[1], strings[2], icon);
        }
    }

    public synchronized NotificationTemplate[] getNotifications() {
        return notifications;
    }

    public synchronized boolean hasNotificationWithNumber(int number) {
        return number < notifications.length && number >= 0;
    }

    public synchronized NotificationTemplate getNotification(int number) {
        if (number >= 0 && number < notifications.length) {
            return notifications[number];
        }
        return null;
    }

    public synchronized int getAvailableNotificationsCount() {
        return notifications.length;
    }

    public synchronized void reload(Resources resources) {
        loadNotifications(resources);
    }
}
