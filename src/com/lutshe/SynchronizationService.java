package com.lutshe;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.Scanner;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.lutshe.store.Store;


public class SynchronizationService extends IntentService {

	private static final int PRELOADED_MESSAGES_NUM = 3;
	private static final long SYNC_DELAY = 1000 /*ms*/ * 60 /*sec*/ * 60 /*min*/ * 12 /*hours*/;
	private static final long RETRY_TIME = 1000 /*ms*/ * 60 /*sec*/ * 60 /*min*/ * 1 /*hours*/;
	private NotificationProvider notificationProvider;
	private Store store;
	
	public SynchronizationService() {
		super("sync service");
	}

	private void init() {
		notificationProvider = NotificationProvider.getInstance(getResources());
		store = new Store(getApplicationContext());
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		init();
		Log.i("sgsync", "starting synchronization");
		
		int lastNotificationNumber = store.getLastNotificationNumber();
		long lastSyncTime = store.getLastSyncTime();
		int availableNotifications = notificationProvider.getAvailableNotificationsCount();
	
		boolean needToUpdate = availableNotifications - PRELOADED_MESSAGES_NUM < lastNotificationNumber && System.currentTimeMillis() - lastSyncTime > RETRY_TIME;
		boolean shouldUpdate = System.currentTimeMillis() - lastSyncTime > SYNC_DELAY;

		if (needToUpdate || shouldUpdate) {
			
			int availableUpdates = getAvailableForUpdate(getResources());
			for(int i = availableNotifications + 1; i <= availableUpdates; i++) {
				Log.i("update", "downloading day " + i);
				String update = downloadNotificaion(getResources(), i);
				Log.i("update", "downloaded: " + update);
				
				if (update != null && !"".equals(update.trim())) {
					//save it to arsen's db
				}
			}
			
			store.registerSync();
			notificationProvider.reload(getResources());
		}
	}

	private String downloadNotificaion(Resources resources, int id) {
		Scanner scanner = null;
		String result = null;
		
		String locale = resources.getString(R.string.locale);
		int version = getVersion();
		
		try {
			scanner = new Scanner(new BufferedInputStream(new URL("http://updates-survivalguide.rhcloud.com/updates/v"+version+"/"+locale+"/message/"+id).openStream()));
			if (scanner.hasNext()) {
				result = scanner.nextLine();
			}
		} catch (Exception e) {
			BugSenseHandler.sendException(e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return result;
	}

	private int getVersion() {
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pInfo.versionCode;
		} catch (Exception e) {
			BugSenseHandler.sendException(e);
			return 0;
		}
	}
	
	private int getAvailableForUpdate(Resources resources) {
		Scanner scanner = null;
		int result = 0;
		
		String locale = resources.getString(R.string.locale);
		int version = getVersion();
		
		try {
			scanner = new Scanner(new BufferedInputStream(new URL("http://updates-survivalguide.rhcloud.com/updates/v"+version+"/"+locale).openStream()));
			if (scanner.hasNext()) {
				result = scanner.nextInt();
			}
		} catch (Exception e) {
			BugSenseHandler.sendException(e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return result;
	}
}
