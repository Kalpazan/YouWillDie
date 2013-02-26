package com.lutshe.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;

import java.util.Calendar;
import java.util.Date;

public class Store {

	private static final String WAS_LAUNCHED_PROPERTY = "wasLaunched";
	private static final String APOCALYPSE_TIME = "apocalyposeTime";
	private static final String POINTS = "points";
	private static final String TIME_PREFIX = "time_of_";
	private static final String LAST_NOTIFICATION_NUM = "lastNotificationNum";
	private static final String STORE_NAME = "huj_vozrozhdennij";
	private static final String OLD_STORE_NAME = "huj";
	
	private SparseArray<Long> notificationTimesCache = new SparseArray<Long>();
	
	private Context context;
	
	public Store(Context context) {
		this.context = context;
        registerCountdownTime();
	}

	public boolean containsOldVersionData() {
		SharedPreferences settings = context.getSharedPreferences(OLD_STORE_NAME, 0);
		return settings.contains(WAS_LAUNCHED_PROPERTY);
	}
	
	public void cleanupOldVersionData() {
		SharedPreferences settings = context.getSharedPreferences(OLD_STORE_NAME, 0);
		settings.edit().clear().commit();
	}
	
	public void saveNotificationTime(int notificationNumber, long time) {
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putLong(TIME_PREFIX + notificationNumber, time);
	    notificationTimesCache.put(notificationNumber, time);

	    editor.commit();
	}
	
	public long getNotofocationTime(int notificationNumber) {
		Long cachedValue = notificationTimesCache.get(notificationNumber);
		if (cachedValue != null) {
			return cachedValue;
		}
		
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    long value = settings.getLong(TIME_PREFIX + notificationNumber, -1);
		notificationTimesCache.put(notificationNumber, value);
	    
	    return value;
	}
	
	public synchronized void updateLastNotificationNumber(int number) {
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt(LAST_NOTIFICATION_NUM, number);

	    editor.commit();
	}
	
	public synchronized int getLastNotificationNumber() {
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    return settings.getInt(LAST_NOTIFICATION_NUM, -1);
	}

	public int getCurrentPoints() {
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    return settings.getInt(POINTS, 0);
	}
	
	public void updatePoints(int points) {
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt(POINTS, points);

	    editor.commit();
	}

	public void registerFirstLaunch() {
		context.getSharedPreferences(STORE_NAME, 0).edit().putBoolean(WAS_LAUNCHED_PROPERTY, true).commit();
	}

	public boolean wasLaunchedBefore() {
		return context.getSharedPreferences(STORE_NAME, 0).getBoolean(WAS_LAUNCHED_PROPERTY, false);
	}

    public void registerPointsAddingOnCreate() {
        context.getSharedPreferences(STORE_NAME, 0).edit().putLong("pointsOnCreate", new Date().getTime()).commit();
    }

    public boolean wasPointsAddedOnCreate() {
        SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
        Date date = new Date(settings.getLong("pointsOnCreate", 0));
        if (date.getDay() == new Date().getDay() & date.getMonth() == new Date().getMonth()) return false;
        return true;
    }

    public void registerPointsAddingOnMsgView() {
        context.getSharedPreferences(STORE_NAME, 0).edit().putLong("pointsOnMsgView", new Date().getTime()).commit();
    }

    public boolean wasPointsAddedOnMsgView() {
        SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
        Date date = new Date(settings.getLong("pointsOnMsgView", 0));
        Date today = new Date();
        
		if (date.getDay() == today.getDay() && date.getMonth() == today.getMonth()) return false;
        return true;
    }

	public void registerRate() {
		context.getSharedPreferences(STORE_NAME, 0).edit().putBoolean("hasRated", true).commit();
	}
	
	public boolean hasAlreadyRated() {
		return context.getSharedPreferences(STORE_NAME, 0).getBoolean("hasRated", false);
	}

	public long getLastLaunchTime() {
		return context.getSharedPreferences(STORE_NAME, 0).getLong("ll", 0);
	}
	
	public void registerLaunch() {
		context.getSharedPreferences(STORE_NAME, 0).edit().putLong("ll", new Date().getTime()).commit();
	}

	public void saveNextNotificationTime(long when) {
		context.getSharedPreferences(STORE_NAME, 0).edit().putLong("nextNotificationTime", when).commit();
	}
	
	public long getNextNotificationTime() {
		return context.getSharedPreferences(STORE_NAME, 0).getLong("nextNotificationTime", 0);
	}
	
	public void registerPanicMessagesScheduled() {
		context.getSharedPreferences(STORE_NAME, 0).edit().putBoolean("panicScheduled", true).commit();
	}
	
	public boolean hasAlreadyScheduledPanic() {
		return context.getSharedPreferences(STORE_NAME, 0).getBoolean("panicScheduled", false);
	}
	
	public void registerApocalypseFinishing() {
		context.getSharedPreferences(STORE_NAME, 0).edit().putBoolean("apocalypseFinished", true).commit();
	}
	
	public boolean hasApocalypseFinished() {
		return context.getSharedPreferences(STORE_NAME, 0).getBoolean("apocalypseFinished", false);
	}

	public void saveApocalypseFinishingTime() {
		context.getSharedPreferences(STORE_NAME, 0).edit().putLong(APOCALYPSE_TIME, System.currentTimeMillis()).commit();
	}
	
	public long getApocalypseFinishingTime() {
		return context.getSharedPreferences(STORE_NAME, 0).getLong(APOCALYPSE_TIME, Long.MAX_VALUE);
	}

    public void registerCountdownTime(){
        if (!wasLaunchedBefore()){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 36);
            calendar.set(Calendar.HOUR_OF_DAY, 15);
            calendar.set(Calendar.MINUTE, 0);
            context.getSharedPreferences(STORE_NAME, 0).edit().putLong("finalApocalypseTime", calendar.getTimeInMillis()).commit();
    }     }

    public long getCountdownTime(){
        return context.getSharedPreferences(STORE_NAME, 0).getLong("finalApocalypseTime", 1000);
    }

    public void registerPanicMessage(int messageId) {
        context.getSharedPreferences(STORE_NAME, 0).edit().putBoolean("panic_" + messageId, true).commit();
        Log.d("panic", "panic_"+messageId + " true registered");
    }

    public boolean isPanicMessageDelivered(int messageId) {
        Log.d("panic", "panic_"+messageId + " readding");
        return context.getSharedPreferences(STORE_NAME, 0).getBoolean("panic_"+messageId, false);
    }
}
