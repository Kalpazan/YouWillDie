package com.lutshe;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.lutshe.controller.PanicController;
import com.lutshe.store.Store;

import static com.lutshe.controller.PanicController.PANIC_MESSAGE_ID_EXTRA;

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
            Log.d("panic", "panic msg " + messageId+ " try to register");
            new Store(getApplicationContext()).registerPanicMessage(messageId);

			if (activity == null) {
				Notification notification = createPanicNotification(messageId);
				((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(2012+messageId, notification);
			} else {
				if (messageId == PanicController.STORY_MESSAGE_ID) {
					activity.showStoryMessage();
				} else {
					String[] messages = getApplicationContext().getResources().getStringArray(R.array.panic_messages);
					String[] buttonTexts = getApplicationContext().getResources().getStringArray(R.array.panic_messages_btn_text);
	        		String message = messages[messageId];
	        		String buttonText = buttonTexts[messageId];
					activity.showPanicMessage(message, buttonText);
				}
			}
		} catch (Exception e) {
			Crashlytics.logException(e);
		}
	}

	public Notification createPanicNotification(int messageId) {
		Context context = getApplicationContext();
		String message = null;
		
		if (messageId == PanicController.STORY_MESSAGE_ID) {
			message = context.getResources().getString(R.string.apocalypse_window_text1);
		} else {
			String[] messages = context.getResources().getStringArray(R.array.panic_messages);
			message = messages[messageId];
		}
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra(PANIC_MESSAGE_ID_EXTRA, messageId);
		notificationIntent.putExtra(IS_PANIC_MESSAGE_EXTRA, true);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, messageId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_hair),80,100,false);

		NotificationCompat.Builder nb = 
				new NotificationCompat.Builder(context).setSmallIcon(R.drawable.icon_hair).setLargeIcon(icon).setAutoCancel(true).setTicker(message)
				.setContentText(message).setContentIntent(pendingIntent)/*.setContentTitle(getString(R.string.panic_message_title))*/
				.setDefaults(Notification.FLAG_SHOW_LIGHTS);

		return nb.build();
	}

}
