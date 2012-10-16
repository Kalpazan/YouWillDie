package com.example;

import static android.app.Notification.FLAG_SHOW_LIGHTS;
import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.bugsense.trace.BugSenseHandler;

public class NotificationSchedulerService extends Service {

	private NotificationManager manager;
	private HashMap<Integer, Notification> notifications = new HashMap<Integer, Notification>();

	@Override
	public void onCreate() {
		BugSenseHandler
				.initAndStartSession(getApplicationContext(), "f48c5119");
		NotificationTemplate[] notifications = new NotificationProvider()
				.getNotifications();
		for (final NotificationTemplate template : notifications) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					createInfoNotification(template);
				}
			};
			new Timer().schedule(task, new Random().nextInt(60 * 5) * 1000);
		}
	}

	public void createInfoNotification(NotificationTemplate template) {
		Context context = getApplicationContext();
		manager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(context, MyActivity.class);
		notificationIntent.putExtra("msg", template.getMainText());

		NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
				.setSmallIcon(template.getIcon())
				.setAutoCancel(true)
				.setTicker(template.getStatusBarText())
				.setContentText(template.getMainText())
				.setContentIntent(
						PendingIntent.getActivity(context, 0,
								notificationIntent, FLAG_CANCEL_CURRENT))
				.setContentTitle(template.getTitle())
				.setDefaults(FLAG_SHOW_LIGHTS);

		Notification notification = nb.build();
		manager.notify(template.getTitle().hashCode(), notification);
		notifications.put(template.getTitle().hashCode(), notification);
	}

	static void startService(Context context) {
		context.startService(new Intent(context,
				NotificationSchedulerService.class));
	}

	static class BootListener extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BugSenseHandler.initAndStartSession(context, "f48c5119");
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				startService(context);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
