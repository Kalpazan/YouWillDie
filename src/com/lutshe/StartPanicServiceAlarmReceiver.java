package com.lutshe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.lutshe.controller.PanicController.PANIC_MESSAGE_ID_EXTRA;

public class StartPanicServiceAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent i) {
		Log.d("notification", "StartPanicServiceAlarmReceiver received an intent " + i.describeContents());
		Intent intent = new Intent(context, PanicNotificationsService.class);
		int messageId = i.getIntExtra(PANIC_MESSAGE_ID_EXTRA, -1);
		Log.d("panic", messageId+"");
		intent.putExtra(PANIC_MESSAGE_ID_EXTRA, messageId);
		context.startService(intent);
	}
}