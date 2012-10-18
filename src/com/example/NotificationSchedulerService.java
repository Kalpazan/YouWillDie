package com.example;

import static android.app.Notification.FLAG_SHOW_LIGHTS;
import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

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
	private NotificationProvider notificationProvider;

	@Override
	public void onCreate() {
		BugSenseHandler.initAndStartSession(getApplicationContext(), "f48c5119");
		notificationProvider = new NotificationProvider(getApplicationContext());
		
		scheduleNextNotification(10000);
	}

	public void scheduleNextNotification(long delay) {
		final int lastNumber = notificationProvider.getLastNotificationNumber();
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				int nextNotificationNumber = lastNumber + 1;
				
				createInfoNotification(notificationProvider.getNotification(nextNotificationNumber));
				notificationProvider.updateLastNotificationNumber(nextNotificationNumber); 
				
//				Calendar calendar = GregorianCalendar.getInstance();
//				calendar.setTimeInMillis(System.currentTimeMillis());
//				calendar.add(1, Calendar.DATE);
//				int randomHoursNumber = new Random().nextInt(5);
//				calendar.set(Calendar.HOUR_OF_DAY, 11 + randomHoursNumber);
//				
//				scheduleNextNotification(calendar.getTimeInMillis() - System.currentTimeMillis());
				
				scheduleNextNotification(20000);
			}
		};
		
		new Timer().schedule(task, delay);
	}
	
	public void createInfoNotification(NotificationTemplate template) {
		Context context = getApplicationContext();
		manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
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
	}

	static void startService(Context context) {
		context.startService(new Intent(context,
				NotificationSchedulerService.class));
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
