package com.lutshe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.lutshe.controller.MessageDisplayController;
import com.lutshe.controller.PanicController;
import com.lutshe.controller.RateViewController;
import com.lutshe.controller.UserMessageController;
import com.lutshe.points.PointsController;
import com.lutshe.store.Store;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;

public class MainActivity extends Activity {

    private static final Random RANDOM = new Random();

    private FinalCountdown timer;

    private NotificationsListAdapter listAdapter;
    private RateViewController rateViewController;
    private UserMessageController userMessageController;
    private PointsController pointsController;
    private Store store;

    private MessageDisplayController messagesController;
    private NotificationProvider provider;

    public static MainActivity instance;

    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	setIntent(intent);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Intent intent = getIntent();
    	if (intent != null) {
    		handleIntent(intent);
    		setIntent(null);
    	}
        initGooglePlayServices();
    }
    
    @Override
    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            Crashlytics.start(this);
            MainActivity.instance = this;

//	        mainView = getMainView();
            setContentView(R.layout.main);
            overScrollSetting();

            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
            store = new Store(getApplicationContext());
            
            pointsController = new PointsController(this);
            provider = NotificationProvider.getInstance(getResources());
            messagesController = new MessageDisplayController(provider, this);
            messagesController.init();

            setupHistoryList(provider, messagesController);

            findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendToChooser sendToChooser = new SendToChooser(MainActivity.this,
                            messagesController.getCurrentMessage().getMainText() +"\n\nhttps://play.google.com/store/apps/details?id=com.lutshe", pointsController);
                    sendToChooser.sendViaCustomChooser();
                }
            });

            findViewById(R.id.messageIcon).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int lastNotificationNumber = store.getLastNotificationNumber();
                    if (lastNotificationNumber > 1) {
                        int next = RANDOM.nextInt(lastNotificationNumber) + 1;
                        NotificationTemplate notification = provider.getNotification(next);
                        messagesController.setCurrentMessage(notification);
                    }
                }
            });

            findViewById(R.id.survivor_btn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (store.hasApocalypseFinished()) {
                        new ApocalypseWindow(MainActivity.this).load();
                    } else {
                        if (getRateViewController().isVisible()) {
                            getRateViewController().hideRateView();
                        } else {
                            getRateViewController().showRateView(false);
                        }
                    }
                }
            });

            Button.OnClickListener helpListener = new Button.OnClickListener() {
                public void onClick(View view) {
                    messagesController.flipViews();
                }
            };
            findViewById(R.id.help_button_container).setOnClickListener(helpListener);
            findViewById(R.id.help_button).setOnClickListener(helpListener);

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void initGooglePlayServices() {
        try {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
            if (resultCode == ConnectionResult.SUCCESS) {
                return;
            }
            else if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, this.getClass().getPackage().hashCode());
                dialog.show();
            }
        }
        catch (Exception ex) {
            Crashlytics.log("Google play services not available");
        }
    }


    //Method removes the stupid horizontal highlighting in ScrollView
    private void overScrollSetting() {
        // find scroll view
        ScrollView messageScroll = (ScrollView) findViewById(R.id.message_scroll_view);
        ListView listView = (ListView) findViewById(R.id.content);
        ScrollView helpScroll = (ScrollView) findViewById(R.id.help_view_container);
        try {
            // look for setOverScrollMode method
            Method setOverScroll = messageScroll.getClass().getMethod("setOverScrollMode", new Class[]{Integer.TYPE});
            if (setOverScroll != null) {
                try {
                    // if found call it (OVER_SCROLL_NEVER == 2)
                    setOverScroll.invoke(messageScroll, 2);
                    setOverScroll.invoke(helpScroll, 2);
                    listView.setOverScrollMode(2);

                } catch (Exception ite) {
                    Log.d("scrollView", "set overScroll failed");
                }
            }
        } catch (NoSuchMethodException nsme) {
            Log.d("scrollView", "searching method failed");
        }
    }

    private boolean isFirstLaunch() {
        return !store.wasLaunchedBefore();
    }

    private boolean isFirstLaunchToday() {
        Calendar today = new GregorianCalendar();
        
        Calendar lastLaunch = new GregorianCalendar();
        lastLaunch.setTimeInMillis(store.getLastLaunchTime());
        
        return lastLaunch.get(DATE) != today.get(DATE) || lastLaunch.get(MONTH) != today.get(MONTH);
    }

    private void setupHistoryList(final NotificationProvider provider, final MessageDisplayController messageController) {
        ListView historyListView = (ListView) findViewById(R.id.content);
        listAdapter = new NotificationsListAdapter(this, provider);
        historyListView.setAdapter(listAdapter);

        @SuppressWarnings("deprecation")
        final SlidingDrawer slider = (SlidingDrawer) findViewById(R.id.drawer);

        historyListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int realPosition = listAdapter.flipPosition(position);
                if (position == 0 && store.hasApocalypseFinished()) {
                    showStoryMessage();
                    slider.close();
                    return;
                }

                if (realPosition == 0) {
                    messageController.showGreetingView();
                    return;
                }

                NotificationTemplate notification = provider.getNotifications()[realPosition];
                messageController.setCurrentMessage(notification);
                messageController.showMessageView();
                slider.animateClose();
                if (realPosition == 0 && !store.wasPointsAddedOnMsgView()) {
                    pointsController.addPoints(6);
                    getUserMessageController().showMessage(getString(R.string.review_bonus_text));
                    store.registerPointsAddingOnMsgView();
                }
            }
        });
    }

    public void startCountDown() {
        stopCountdown();
        if (store.hasApocalypseFinished()) {
            timer = FinalCountdown.getInstance(0, 100, this);
            timer.cancel();
        	timer.showTimeLeft(store.getCountdownTime() - store.getApocalypseFinishingTime());
        	findViewById(R.id.stamp).setVisibility(View.VISIBLE);
        } else {
	        timer = FinalCountdown.getInstance(store.getCountdownTime() - System.currentTimeMillis(), 51, this);
	        timer.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCountdown();
        EasyTracker.getInstance(this).activityStop(this);
    }

    private void stopCountdown() {
        if (timer != null) {
            timer.cancel();
            timer.clean();
            timer = null;
        }
    }

    @Override
    protected void onPause() {
        MainActivity.instance = null;
        super.onPause();
        stopCountdown();

        final SlidingDrawer slider = (SlidingDrawer) findViewById(R.id.drawer);
        slider.close();
    }

//    @Override
    protected void handleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            instance = this;

            boolean isItJustPanic = extras.getBoolean(PanicNotificationsService.IS_PANIC_MESSAGE_EXTRA, false);
            if (isItJustPanic) {
                Log.d("panic", "isJustPanic");
                int messageId = extras.getInt(PanicController.PANIC_MESSAGE_ID_EXTRA);

                if (messageId == PanicController.STORY_MESSAGE_ID) {
                	showStoryMessage();
                } else {
	                String[] messages = getApplicationContext().getResources().getStringArray(R.array.panic_messages);
	                String[] buttonTexts = getApplicationContext().getResources().getStringArray(R.array.panic_messages_btn_text);
	                String message = messages[messageId];
	                String buttonText = buttonTexts[messageId];
	                showPanicMessage(message, buttonText);
                    Log.d("panic", "panic msg " + messageId +" shown");
                }
            } else {
                int id = extras.getInt(NotificationServiceThatJustWorks.EXTRA_NAME, -1);
                if (id < 0) {
                	return;
                }
                NotificationTemplate notification = provider.getNotification(id);
                pointsController.addPoints(4);
                getUserMessageController().showMessage(getString(R.string.notification_bonus_text));
                messagesController.setCurrentMessage(notification);
                messagesController.showMessageView();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            MainActivity.instance = this;
            
            if (isFirstLaunch()) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        NotificationServiceThatJustWorks.startService(getApplicationContext());
                        return null;
                    }
                }.execute((Void) null);

                store.registerFirstLaunch();
                pointsController.addPoints(12);
                store.registerPointsAddingOnCreate();
                store.registerLaunch();
            } else if (isFirstLaunchToday()) {
                pointsController.addPoints(7);
                getUserMessageController().showMessage(getString(R.string.welcome_back_bonus_text));
                store.registerLaunch();
            }

//            MessagesDeliveryMonitoringService.startService(getApplicationContext());
            PanicController.schedulePanic(getApplicationContext(), store);

            startCountDown();
            
            if (store.containsOldVersionData()) {
            	store.cleanupOldVersionData();
            	new PanicDialog(this, getResources().getString(R.string.sorryDialogText), getResources().getString(R.string.sorryDialogButtonText)).load();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    public void showMessage(String string) {
        getUserMessageController().showMessage(string);
    }

    public Store getStore() {
        return store;
    }

    public PointsController getPointsController() {
        return pointsController;
    }

    public synchronized void setCurrentNotification(final NotificationTemplate template, final int id) {
        runOnUiThread(new Runnable() {
			@Override
			public void run() {
				store.updateLastNotificationNumber(id);
	            listAdapter.notifyDataSetChanged();

	            messagesController.setCurrentMessage(template);
	            messagesController.showMessageView();

	            pointsController.addPoints(4);
	            getUserMessageController().showMessage(getString(R.string.notification_bonus_text));
			}
		});
    }

    public void showStoryMessage() {
        final MainActivity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopCountdown();
                new ApocalypseWindow(activity).load();
                if (!store.hasApocalypseFinished()) {
                	store.registerApocalypseFinishing();
                	listAdapter.notifyDataSetChanged();
                	store.saveApocalypseFinishingTime();
                	stopCountdown();
                }
            }
        });
    }

    public void showPanicMessage(final String message, final String buttonText) {
        final MainActivity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PanicDialog(activity, message, buttonText).load();
            }
        });
    }

    public RateViewController getRateViewController() {
        if (rateViewController == null) {
            rateViewController = new RateViewController(this, pointsController, store);
        }
        return rateViewController;
    }

    public UserMessageController getUserMessageController() {
        if (userMessageController == null) {
            userMessageController = new UserMessageController(this);
        }
        return userMessageController;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.mainLayout));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

}