package com.example;

import static android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;
import static android.app.Notification.FLAG_SHOW_LIGHTS;
import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.example.store.Store;

public class NotificationServiceThatJustWorks extends IntentService {

	public static final String IS_NOTIFICATION_INTENT = "isNotificationIntent";
	private static final String IS_START_INTENT = "isStartIntent";

	public NotificationServiceThatJustWorks() {
		super("blabla");
	}

	public static final String EXTRA_NAME = "com.example.msgId";
	private NotificationManager manager;
	private NotificationProvider notificationProvider;
	private Store store;

	public void init() {
		Log.d("notification", "init called");
		BugSenseHandler.initAndStartSession(getApplicationContext(), "f48c5119");
		notificationProvider = new NotificationProvider();
		store = new Store(getApplicationContext());
		manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void finishProcessing() {
		BugSenseHandler.flush(getApplicationContext());
	}
	
	private long getNextNotificationTime(long lastNotificationTime) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(lastNotificationTime);
//		calendar.add(Calendar.DATE, 1);
//
//		int randomHoursNumber = new Random().nextInt(10);
//		calendar.set(Calendar.HOUR_OF_DAY, 11 + randomHoursNumber);
//
//		int randomMinsNumber = new Random().nextInt(60);
//		calendar.set(Calendar.MINUTE, randomMinsNumber);

		calendar.add(Calendar.MINUTE, 5);
		
		return calendar.getTimeInMillis();
	}

	public Notification createInfoNotification(NotificationTemplate template, int notificationId) {
		Context context = getApplicationContext();

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra(EXTRA_NAME, notificationId);

		NotificationCompat.Builder nb = new NotificationCompat.Builder(context).setSmallIcon(template.getIcon()).setAutoCancel(true).setTicker(template.getStatusBarText())
				.setContentText(template.getMainText()).setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, FLAG_CANCEL_CURRENT)).setContentTitle(template.getTitle())
				.setDefaults(FLAG_SHOW_LIGHTS);

		return nb.build();
	}

	private void scheduleNextNotification(long when) {
		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmListener.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 412341234, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
		Log.d("notification", "alarm was set");
	}

	static void startService(Context context) {
		Intent intent = new Intent(context, NotificationServiceThatJustWorks.class);
		intent.putExtra(context.getPackageName() + IS_START_INTENT, true);
		context.startService(intent);
		Log.d("notification", "starting service");
	}

	public static class BootListener extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BugSenseHandler.initAndStartSession(context, "f48c5119");
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				Log.d("notification", "got boot action - starting intent");
				startService(context);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		init();
		
		Log.d("notification", "now is " + new Date(System.currentTimeMillis()).toGMTString());
		Log.d("notification", "handling intent");
		boolean isStartIntent = intent.getBooleanExtra(getPackageName() + IS_START_INTENT, false);
		boolean isNotificationIntent = intent.getBooleanExtra(getPackageName() + IS_NOTIFICATION_INTENT, false);
		
		if (!isStartIntent && !isNotificationIntent) {
			Log.wtf("notification", "some bullshit intent!" + intent.describeContents());
			return;
		}

		long when = 0;
		int lastNotificationNumber = store.getLastNotificationNumber();
		int currentNum = lastNotificationNumber + 1;

		Log.d("notification", "last notification number = " + lastNotificationNumber);
		
		if (!isStartIntent) {
			Log.d("notification", "this was real intent to send smth");
			NotificationTemplate template = notificationProvider.getNotification(currentNum);
			Notification notification = createInfoNotification(template, currentNum);

			Log.d("notification", "so sending");
			manager.notify(template.getTitle().hashCode(), notification);

			Log.d("notification", "saving notId " + currentNum + " at " + new Date(System.currentTimeMillis()).toGMTString());
			store.saveNotificationTime(currentNum, System.currentTimeMillis());
			store.updateLastNotificationNumber(currentNum);
		}
		
		if (notificationProvider.hasNotificationWithNumber(currentNum + 1)) {
			if (lastNotificationNumber == -1) {
				when = System.currentTimeMillis() + 3 * 60 * 1000;
			} else {
				long lastNotificationTime = store.getNotofocationTime(lastNotificationNumber);
				when = getNextNotificationTime(lastNotificationTime);
			}
			
			Log.d("notification", "time of next notification: " + new Date(when).toGMTString());
			scheduleNextNotification(when);
		}
		
		finishProcessing();
	}
}
