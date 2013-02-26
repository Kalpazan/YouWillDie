package com.lutshe.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import com.lutshe.StartPanicServiceAlarmReceiver;
import com.lutshe.store.Store;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.util.Calendar.*;

public class PanicController {

	public static final int STORY_MESSAGE_ID = 100;
	public static final String PANIC_MESSAGE_ID_EXTRA = "com.lutshe.panicMessageId";

	public static void schedulePanic(Context context, Store store) {

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);

//        Calendar calendar = new GregorianCalendar();
//        calendar.setTimeInMillis(store.getCountdownTime());
//        calendar.set(HOUR, 12);
//
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
        calendar.setTimeInMillis(store.getCountdownTime());
        calendar.set(HOUR_OF_DAY, 11);
        calendar.set(MINUTE, 55);
		schedulePanicMessage(store, alarmManager, context, calendar.getTimeInMillis(), 0); // Apocalypse in ~4 hours

		calendar.add(GregorianCalendar.MINUTE, 3);
		schedulePanicMessage(store, alarmManager, context, calendar.getTimeInMillis(), 1); // 2 hours to Earth!

		calendar.add(GregorianCalendar.MINUTE, 3);
		schedulePanicMessage(store, alarmManager, context, calendar.getTimeInMillis(), 2); // Small Earth..

		calendar.add(GregorianCalendar.MINUTE, 2);
		schedulePanicMessage(store, alarmManager, context, calendar.getTimeInMillis(), 3); // Error003

		calendar.add(GregorianCalendar.MINUTE, 2);
		schedulePanicMessage(store, alarmManager, context, calendar.getTimeInMillis(), 4); // New plan...

		calendar.add(GregorianCalendar.MINUTE, 2);
		schedulePanicMessage(store, alarmManager, context, calendar.getTimeInMillis(), STORY_MESSAGE_ID); // WHOLE STORY!

	}

	private static void schedulePanicMessage(Store store, AlarmManager alarmManager, Context context,  long when, int messageId) {
        if (store.isPanicMessageDelivered(messageId)) {
            return;
        }

		Intent intent = new Intent(context, StartPanicServiceAlarmReceiver.class);
		intent.putExtra(PANIC_MESSAGE_ID_EXTRA, messageId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, messageId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
	}

}