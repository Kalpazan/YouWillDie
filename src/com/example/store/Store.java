package com.example.store;

import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

public class Store {

	private static final String POINTS = "points";
	private static final String TIME_PREFIX = "time_of_";
	private static final String LAST_NOTIFICATION_NUM = "lastNotificationNum";
	private static final String STORE_NAME = "huj";
	
	private HashMap<Integer, Long> notificationTimesCache = new HashMap<Integer, Long>();
	
	private Context context;
	
	public Store(Context context) {
		this.context = context;
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
		context.getSharedPreferences(STORE_NAME, 0).edit().putBoolean("wasLaunched", true).commit();
	}

	public boolean wasLaunchedBefore() {
		return context.getSharedPreferences(STORE_NAME, 0).getBoolean("wasLaunched", false);
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
        if (date.getDay() == new Date().getDay() & date.getMonth() == new Date().getMonth()) return false;
        return true;
    }
}
