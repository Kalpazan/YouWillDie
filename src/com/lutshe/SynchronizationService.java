package com.lutshe;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.Scanner;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.lutshe.controller.InternetController;
import com.lutshe.store.Store;


public class SynchronizationService extends IntentService {

	private static final int PRELOADED_MESSAGES_NUM = 3;
	private static final long SYNC_DELAY = 1000 /*ms*/ * 60 /*sec*/ * 60 /*min*/ * 12 /*hours*/;
	private static final long RETRY_TIME = 1000 /*ms*/ * 60 /*sec*/ * 60 /*min*/ * 1 /*hours*/;
	private NotificationProvider notificationProvider;
	private Store store;
	private Database database;
	
	public SynchronizationService() {
		super("sync service");
	}

	private void init() {
		notificationProvider = NotificationProvider.getInstance(getResources(), getApplicationContext());
		store = new Store(getApplicationContext());
		database = Database.getDb(getApplicationContext());
	}
	
	private void sync() {
		Log.i("sgsync", "starting synchronization");
		
		int lastNotificationNumber = store.getLastNotificationNumber();
		Log.i("sgsync", "lastNotNum: " + lastNotificationNumber);
		long lastSyncTime = store.getLastSyncTime();
		Log.i("sgsync", "lastSyncTime: " + lastSyncTime);
		int availableNotifications = notificationProvider.getAvailableNotificationsCount();
		Log.i("sgsync", "availableNotifcations: " + availableNotifications);
	
		boolean needToUpdate = availableNotifications - PRELOADED_MESSAGES_NUM < lastNotificationNumber && System.currentTimeMillis() - lastSyncTime > RETRY_TIME;
		boolean shouldUpdate = System.currentTimeMillis() - lastSyncTime > SYNC_DELAY;

		Log.i("sgsync", needToUpdate + " " + shouldUpdate);
		if (needToUpdate || shouldUpdate) {
			boolean wasUpdated = false;
			int availableUpdates = getAvailableForUpdate(getResources());
			Log.i("update", lastNotificationNumber +","+availableNotifications+","+availableUpdates);
			
			for(int i = availableNotifications + 1; i <= availableUpdates; i++) {
				Log.i("update", "downloading day " + i);
				String update = downloadNotificaion(getResources(), i).trim();
				Log.i("update", "downloaded: " + update);
				
				if (update != null && !"".equals(update)) {
					//save it to arsen's db
					Log.i("update", "saving as day " + i);
					
					String[] notification = update.split("\\|");
					Log.i("update", notification[0]);
					Log.i("update", notification[1]);
					Log.i("update", notification[2]);
					Log.i("update", notification[3]);
					database.addItem(i, notification[0], notification[1], notification[2], Integer.valueOf(notification[3]));
                    wasUpdated = true;
				}
			}

            if (wasUpdated) {
                store.registerSync();
			    notificationProvider.reload(getResources());
            }
		}
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			init();
			sync();
		} catch (Exception e) {
			Log.e("sgsync", e.getMessage(), e);
			BugSenseHandler.sendExceptionMessage("update failed", getResources().getString(R.string.locale), e);
		}
	}

	private String downloadNotificaion(Resources resources, int id) {
		Scanner scanner = null;
		StringBuilder result = new StringBuilder();
		
		String locale = resources.getString(R.string.locale);
		
		try {
			scanner = new Scanner(new BufferedInputStream(new URL("http://updates-survivalguide.rhcloud.com/updates/v1/"+locale+"/message/"+id).openStream()));
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				Log.d("update", line);
				result.append(line).append("\n");
			}
		} catch (Exception e) {
			BugSenseHandler.sendException(e);
			Log.e("update", e.getMessage(), e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return result.toString();
	}

	private int getAvailableForUpdate(Resources resources) {
		Scanner scanner = null;
		int result = 0;
		
		String locale = resources.getString(R.string.locale);
		String url = "http://updates-survivalguide.rhcloud.com/updates/v1/" + locale;
		
		Log.d("sgsync", url);
		try {
			
			scanner = new Scanner(new BufferedInputStream(new URL(url).openStream()));
			if (scanner.hasNext()) {
				String line = scanner.nextLine();
				Log.d("sgsync", line);		
				result = Integer.valueOf(line);
			}
		} catch (Exception e) {
			Log.e("update", e.getMessage(), e);
			BugSenseHandler.sendException(e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return result;
	}
	
	static void start(Context context) {
		Intent syncIntent = new Intent(context, SynchronizationService.class);
		context.startService(syncIntent);
		Log.d("sgsync", "starting sync service");
	}
}
