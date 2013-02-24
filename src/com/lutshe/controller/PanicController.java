package com.lutshe.controller;

import java.util.GregorianCalendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import com.lutshe.StartPanicServiceAlarmReceiver;
import com.lutshe.store.Store;

public class PanicController {

	public static final int STORY_MESSAGE_ID = 100;
	public static final String PANIC_MESSAGE_ID_EXTRA = "com.lutshe.panicMessageId";

	public static void shedulePanic(Context context, Store store) {
		if (store.hasAlreadyScheduledPanic()) {
			return;
		}

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);

//		GregorianCalendar calendar = new GregorianCalendar(2013, FEBRUARY, 21, 11, 0);
//		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 0); // Apocalypse in ~4 hours
//
//		calendar.add(MINUTE, 30);
//		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 1); // 2 hours to Earth!
//
//		calendar.add(MINUTE, 30);
//		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 2); // Small Earth..
//
//		calendar.add(MINUTE, 30);
//		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 3); // Error003
//
//		calendar.add(MINUTE, 30);
//		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 4); // New plan...
//
//		calendar.add(MINUTE, 7);
//		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), STORY_MESSAGE_ID); // WHOLE STORY!

		GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(store.getCountdownTime() - 6*60*1000);
		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 0); // Apocalypse in ~4 hours

		calendar.add(GregorianCalendar.SECOND, 5);
		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 1); // 2 hours to Earth!

		calendar.add(GregorianCalendar.SECOND, 5);
		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 2); // Small Earth..

		calendar.add(GregorianCalendar.SECOND, 5);
		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 3); // Error003

		calendar.add(GregorianCalendar.SECOND, 5);
		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), 4); // New plan...

		calendar.add(GregorianCalendar.SECOND, 7);
		schedulePanicMessage(alarmManager, context, calendar.getTimeInMillis(), STORY_MESSAGE_ID); // WHOLE STORY!

		store.registerPanicMessagesScheduled();
	}

	private static void schedulePanicMessage(AlarmManager alarmManager, Context context,  long when, int messageId) {
		Intent intent = new Intent(context, StartPanicServiceAlarmReceiver.class);
		intent.putExtra(PANIC_MESSAGE_ID_EXTRA, messageId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, messageId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
	}

}