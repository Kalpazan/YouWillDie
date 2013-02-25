package com.lutshe;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.lutshe.store.Store;

public class NotificationServiceThatJustWorks extends IntentService {

	public static final String IS_NOTIFICATION_INTENT = "isNotificationIntent";
	private static final String IS_START_INTENT = "isStartIntent";

	public NotificationServiceThatJustWorks() {
		super("blabla");
	}

	public static final String EXTRA_NAME = "com.lutshe.msgId";
	private NotificationManager manager;
	private NotificationProvider notificationProvider;
	private Store store;

	public void init() {
		Log.d("notification", "init called");
		notificationProvider = NotificationProvider.getInstance(getResources());
		store = new Store(getApplicationContext());
		manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void finishProcessing() {
		BugSenseHandler.flush(getApplicationContext());
	}
	
	private long getNextNotificationTime(long lastNotificationTime) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(lastNotificationTime);

//		if(calendar.get(Calendar.HOUR_OF_DAY) > 5) {
//			calendar.add(Calendar.DATE, 1);
//		}
//
//		int randomHoursNumber = new Random().nextInt(10);
//		calendar.set(Calendar.HOUR_OF_DAY, 11 + randomHoursNumber);
//
//		int randomMinsNumber = new Random().nextInt(60);
//		calendar.set(Calendar.MINUTE, randomMinsNumber);

//		calendar.add(Calendar.SECOND, 20);
		calendar.add(Calendar.MINUTE, 20);

		return calendar.getTimeInMillis();
	}

	public Notification createInfoNotification(NotificationTemplate template, int notificationId) {
		Context context = getApplicationContext();

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra(EXTRA_NAME, notificationId);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, FLAG_CANCEL_CURRENT);

        Bitmap icon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), template.getIcon()),80,100,false);

		NotificationCompat.Builder nb = 
				new NotificationCompat.Builder(context).setSmallIcon(template.getIcon()).setLargeIcon(icon).setAutoCancel(true).setTicker(template.getStatusBarText())
				.setContentText(template.getMainText()).setContentIntent(pendingIntent).setContentTitle(template.getTitle())
				.setDefaults(Notification.FLAG_SHOW_LIGHTS);

		return nb.build();
	}

	private void scheduleNextNotification(long when) {
		store.saveNextNotificationTime(when);
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
			BugSenseHandler.initAndStartSession(context, "3d42042b");
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				Log.d("notification", "got boot action - starting intent");
				startService(context);
				MessagesDeliveryMonitoringService.startService(context);
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

		Log.d("notification", "last notification number = " + lastNotificationNumber);
		
		if (!isStartIntent) {
			lastNotificationNumber++;
			
			Log.d("notification", "this was real intent to send smth");
			Log.d("notification", "about to send. id = "+lastNotificationNumber);
			
			NotificationTemplate template = notificationProvider.getNotification(lastNotificationNumber);

			if (MainActivity.instance != null) {
				MainActivity.instance.setCurrentNotification(template, lastNotificationNumber);
			} else {
				Notification notification = createInfoNotification(template, lastNotificationNumber);
	
				Log.d("notification", "so sending");
				manager.notify(template.getTitle().hashCode(), notification);
				store.updateLastNotificationNumber(lastNotificationNumber);
			}

			Log.d("notification", "saving notId " + lastNotificationNumber + " at " + new Date(System.currentTimeMillis()).toGMTString());
			store.saveNotificationTime(lastNotificationNumber, System.currentTimeMillis());
		} else {
			BugSenseHandler.initAndStartSession(getApplicationContext(), "3d42042b");
		}
		
		if (!store.hasApocalypseFinished()) {
			if (notificationProvider.hasNotificationWithNumber(lastNotificationNumber + 1)) {
				if (lastNotificationNumber == -1) {
					store.updateLastNotificationNumber(0);
					store.saveNotificationTime(0, System.currentTimeMillis());
					when = System.currentTimeMillis() + 2 * 60 * 1000;
//					when = System.currentTimeMillis() + 10000;
				} else {
					long lastNotificationTime = store.getNotofocationTime(lastNotificationNumber);
					when = getNextNotificationTime(lastNotificationTime);
				}
				
				Log.d("notification", "time of next notification: " + new Date(when).toGMTString());
				scheduleNextNotification(when);
			} else {
				long lastNotificationTime = store.getNotofocationTime(lastNotificationNumber);
				when = getNextNotificationTime(lastNotificationTime);
				scheduleCheck(when);
			}
		}
		
		finishProcessing();
	}

	private void scheduleCheck(long when) {

		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Service.ALARM_SERVICE);
		
		Intent intent = new Intent(getApplicationContext(), NotificationServiceThatJustWorks.class);
		intent.putExtra(getApplicationContext().getPackageName() + IS_START_INTENT, true);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 412341234, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
		Log.d("notification", "check alarm was set");
		
	}
}
