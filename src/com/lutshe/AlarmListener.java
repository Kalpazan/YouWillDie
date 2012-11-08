package com.lutshe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent i) {
		Log.d("notification", "received an intent " + i.describeContents());
		Intent intent = new Intent(context, NotificationServiceThatJustWorks.class);
		intent.putExtra(context.getPackageName() + NotificationServiceThatJustWorks.IS_NOTIFICATION_INTENT, true);
		context.startService(intent);
	}
}
