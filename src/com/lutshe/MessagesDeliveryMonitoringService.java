package com.lutshe;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.lutshe.store.Store;

/**
 * Checks wit a given interval if there were any problems with notifications delivery (next notification time is in the past) and starts {@link NotificationServiceThatJustWorks} if needed
 */
public class MessagesDeliveryMonitoringService extends IntentService {

	private static final int POSSIBLE_DELAY = 10 * 1000 * 60; // 10 mins
	private static final int CHECK_INTERVAL = 12 * 1000 * 60 * 60; // 12 hours
	private Store store;

	public void init() {
		store = new Store(getApplicationContext());
	}
	
	public MessagesDeliveryMonitoringService() {
		super("monitor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			
			init();
			long now = System.currentTimeMillis();
			long nextNotificationTime = store.getNextNotificationTime();
	
			if (now > nextNotificationTime + POSSIBLE_DELAY) {
				NotificationServiceThatJustWorks.startService(getApplicationContext());
			}
		
		} finally {
			scheduleNextCheck();
		}
	}
	
	private void scheduleNextCheck() {
		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Service.ALARM_SERVICE);
		
		Intent intent = new Intent(getApplicationContext(), MessagesDeliveryMonitoringService.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 412341234, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		long when = System.currentTimeMillis() + CHECK_INTERVAL;
		alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);

	}
	
	static void startService(Context context) {
		Intent intent = new Intent(context, MessagesDeliveryMonitoringService.class);
		context.startService(intent);
	}
}
