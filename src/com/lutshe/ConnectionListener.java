package com.lutshe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionListener extends BroadcastReceiver {

	private static final String LOGTAG = ConnectionListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(LOGTAG, "Action: " + intent.getAction());
		
    	if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
		    NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		    String typeName = info.getTypeName();
		    String subtypeName = info.getSubtypeName();
		    boolean available = info.isAvailable();
		    
		    Log.i(LOGTAG, "Network Type: " + typeName 
				+ ", subtype: " + subtypeName
				+ ", available: " + available);	
		    
		    if (available) {
		    	SynchronizationService.start(context);
		    }
    	}
	}
	
	

}
