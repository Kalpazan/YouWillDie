package com.lutshe;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static com.lutshe.controller.PanicController.PANIC_MESSAGE_ID_EXTRA;

import com.bugsense.trace.BugSenseHandler;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PanicNotificationsService extends IntentService {

	public static final String IS_PANIC_MESSAGE_EXTRA = "isPanicMessage";

	public PanicNotificationsService() {
		super("oh no! the end is near");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			Log.i("service", "was called");
			MainActivity activity = MainActivity.instance;
			int messageId = intent.getIntExtra(PANIC_MESSAGE_ID_EXTRA, -1);

			if (activity == null) {
				Notification notification = createPanicNotification(messageId);
				((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(2012+messageId, notification);
			} else {
				String[] messages = getApplicationContext().getResources().getStringArray(R.array.panic_messages);
				String message = messages[messageId];
				activity.showPanicMessage(message, "Ok");
			}
		} catch (Exception e) {
			BugSenseHandler.sendException(e);
		}
	}

	public Notification createPanicNotification(int messageId) {
		Context context = getApplicationContext();
		String[] messages = context.getResources().getStringArray(R.array.panic_messages);
		String message = messages[messageId];
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra(PANIC_MESSAGE_ID_EXTRA, messageId);
		notificationIntent.putExtra(IS_PANIC_MESSAGE_EXTRA, true);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, messageId, notificationIntent, FLAG_CANCEL_CURRENT);
		
		NotificationCompat.Builder nb = 
				new NotificationCompat.Builder(context).setSmallIcon(R.drawable.icon_hair).setAutoCancel(true).setTicker(message)
				.setContentText(message).setContentIntent(pendingIntent)/*.setContentTitle(getString(R.string.panic_message_title))*/
				.setDefaults(Notification.FLAG_SHOW_LIGHTS);

		return nb.build();
	}

}
