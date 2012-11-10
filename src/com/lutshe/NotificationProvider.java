package com.lutshe;

import android.content.res.Resources;
import android.content.res.TypedArray;

public class NotificationProvider {

	private static NotificationProvider instance;
	
	private NotificationTemplate[] notifications;
	
	private NotificationProvider(Resources resources) {
		String[] messages = resources.getStringArray(R.array.messages);
		TypedArray imgs = resources.obtainTypedArray(R.array.icons);
		
		notifications = new NotificationTemplate[messages.length];
		
		for (int i = 0; i < messages.length; i++) {
			String[] strings = messages[i].split("\\|");
			int icon = imgs.getResourceId(i, -1);
				notifications[i] = new NotificationTemplate(strings[0], strings[1], strings[2], icon);
		}
	}
	
	public synchronized static NotificationProvider getInstance(Resources resources) {
		if (instance == null) {
			instance = new NotificationProvider(resources);
		}
		
		return instance;
	}
	
	public NotificationTemplate[] getNotifications() {
		return notifications;
	}

	public int getPreviousNotificationsCount() {
		return notifications.length;
	}

	public boolean hasNotificationWithNumber(int number) {
		return number < notifications.length && number >= 0;
	}
	
	public NotificationTemplate getNotification(int number) {
		if (number < notifications.length) {
			return notifications[number];
		}
		return null;
	}
	
}
