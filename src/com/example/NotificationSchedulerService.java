package com.example;

import static android.app.Notification.FLAG_SHOW_LIGHTS;
import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static java.util.Calendar.DATE;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import com.example.store.Store;

public class NotificationSchedulerService extends Service {

	public static final String EXTRA_NAME = "com.example.msgId";
	private NotificationManager manager;
	private NotificationProvider notificationProvider;
	private Store store;

	private static boolean alreadySchedled = false;

	private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
	
	@Override
	public void onCreate() {
		BugSenseHandler.initAndStartSession(getApplicationContext(), "f48c5119");
		notificationProvider = new NotificationProvider();
		store = new Store(getApplicationContext());
		
		if (!alreadySchedled) {
			if (store.getLastNotificationNumber() == -1) {
				scheduleNextNotification(60 * 3 * 1000);
			} else {
				long lastNotificationTime = store.getNotofocationTime(store.getLastNotificationNumber());
				long nextNotificationTime = getNextNotificationTime(lastNotificationTime);
				long timeToNextCall = nextNotificationTime - System.currentTimeMillis();
				scheduleNextNotification(timeToNextCall);
			}
		}
	}

	public void scheduleNextNotification(long delay) {
		final int lastNumber = store.getLastNotificationNumber();
		if (delay < 0) delay = 0;
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				int currentNotificationNumber = lastNumber + 1;

				if (notificationProvider.hasNotificationWithNumber(currentNotificationNumber)) {

					createInfoNotification(notificationProvider.getNotification(currentNotificationNumber), currentNotificationNumber);
					
					store.updateLastNotificationNumber(currentNotificationNumber);
					long currentTime = System.currentTimeMillis();
					store.saveNotificationTime(currentNotificationNumber, currentTime);
					
					long nextNotificationTime = getNextNotificationTime(currentTime);
					long timeToNextCall = nextNotificationTime - currentTime;
					scheduleNextNotification(timeToNextCall);
				}
			}
		};

		scheduledThreadPoolExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
		alreadySchedled = true;
	}

	private long getNextNotificationTime(long lastNotificationTime) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(lastNotificationTime);
		calendar.add(DATE, 1);
		
		int randomHoursNumber = new Random().nextInt(10);
		calendar.set(HOUR_OF_DAY, 11 + randomHoursNumber);
	
		int randomMinsNumber = new Random().nextInt(60);
		calendar.set(MINUTE, randomMinsNumber);
		
//		calendar.add(Calendar.MINUTE, 15);
		
		return calendar.getTimeInMillis();
	}
	
	public void createInfoNotification(NotificationTemplate template, int notificationId) {
		Context context = getApplicationContext();
		manager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra(EXTRA_NAME, notificationId);

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
	}

	static void startService(Context context) {
		context.startService(new Intent(context, NotificationSchedulerService.class));
	}

	public static class BootListener extends BroadcastReceiver {
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
