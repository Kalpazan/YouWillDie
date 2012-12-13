package com.lutshe;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static java.util.Calendar.DECEMBER;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
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

import com.bugsense.trace.BugSenseHandler;
import com.lutshe.controller.InternetController;
import com.lutshe.controller.MessageDisplayController;
import com.lutshe.controller.PanicController;
import com.lutshe.controller.RateViewController;
import com.lutshe.controller.UserMessageController;
import com.lutshe.points.PointsController;
import com.lutshe.store.Store;

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

//    private View mainView;
//    
//    private View getMainView(){
//    	LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        return inflater.inflate(R.layout.main, null);
//    }

//    @Override
//    public View findViewById(int id) {
//    	return mainView.findViewById(id);
//    }

    @Override
    public void onCreate(Bundle bundle) {
        try {

            super.onCreate(bundle);
            MainActivity.instance = this;

            BugSenseHandler.initAndStartSession(MainActivity.this, "3d42042b");

//	        mainView = getMainView();
            setContentView(R.layout.main);
            overScrollSetting();

            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
            store = new Store(getApplicationContext());
            pointsController = new PointsController(this);

            if (InternetController.isNetworkAvailable(getApplicationContext())) {
                SynchronizationService.start(getApplicationContext());
            }
            provider = NotificationProvider.getInstance(getResources(), getApplicationContext());
            messagesController = new MessageDisplayController(provider, this);
            messagesController.init();

            setupHistoryList(provider, messagesController);

            findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendToChooser sendToChooser = new SendToChooser(MainActivity.this, messagesController.getCurrentMessage().getMainText(), pointsController);
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
                    if (getRateViewController().isVisible()) {
                        getRateViewController().hideRateView();
                    } else {
                        getRateViewController().showRateView(false);
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
            BugSenseHandler.sendException(e);
        }
    }

    private void overScrollSetting() {
        // find scroll view
        Log.d("scrollView", "start point");
        ScrollView messageScroll = (ScrollView) findViewById(R.id.message_scroll_view);
        ListView listView = (ListView) findViewById(R.id.content);
        ScrollView helpScroll = (ScrollView) findViewById(R.id.help_view_container);
        Log.d("scrollView", "scroll init");
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

    @SuppressWarnings("deprecation")
	private boolean isFirstLaunchToday() {
        Date today = new Date();
        Date lastLaunch = new Date(store.getLastLaunchTime());
        return lastLaunch.getDate() != today.getDate() || lastLaunch.getMonth() != today.getMonth();
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
        final Calendar finalDate = Calendar.getInstance();
        finalDate.clear();
        finalDate.set(2012, DECEMBER, 21, 0, 0);

        timer = new FinalCountdown(PanicController.APPOCALYPSE_TIME - System.currentTimeMillis(), 51, this);
        timer.start();

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.exit:
//                showMessage("All the same, this does not stop. It is waiting for you.");
//                System.exit(0);
//                return true;
//            case R.id.help:
//                showMessage("Who will help you?");
//                showMessage("No one!");
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCountdown();
    }

    private void stopCountdown() {
        if (timer != null) {
            timer.cancel();
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

        BugSenseHandler.flush(MainActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            instance = this;
            
            boolean isItJustPanic = extras.getBoolean(PanicNotificationsService.IS_PANIC_MESSAGE_EXTRA, false);
            if (isItJustPanic) {
            	int messageId = extras.getInt(PanicController.PANIC_MESSAGE_ID_EXTRA);
            	String[] messages = getApplicationContext().getResources().getStringArray(R.array.panic_messages);
            	String[] buttonTexts = getApplicationContext().getResources().getStringArray(R.array.panic_messages_btn_text);
        		String message = messages[messageId];
        		String buttonText = buttonTexts[messageId];
            	showPanicMessage(message, buttonText);
            } else {
	            int id = extras.getInt(NotificationServiceThatJustWorks.EXTRA_NAME);
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
                pointsController.addPoints(10);
                store.registerPointsAddingOnCreate();
                store.registerLaunch();
            } else if (isFirstLaunchToday()) {
                pointsController.addPoints(7);
                getUserMessageController().showMessage(getString(R.string.welcome_back_bonus_text));
                store.registerLaunch();
            }
            
//            MessagesDeliveryMonitoringService.startService(getApplicationContext());
            PanicController.shedulePanic(getApplicationContext(), store);
            
            startCountDown();
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
//        final Toast toast = Toast.makeText(getApplicationContext(), string, LENGTH_LONG);
//        toast.setGravity(0, 0, 0);
//        toast.show();
    }

    public Store getStore() {
        return store;
    }

    public PointsController getPointsController() {
        return pointsController;
    }

    private NotificationTemplate nextNotification;
    private int notificationId;

    public synchronized void setCurrentNotification(NotificationTemplate template, int id) {
        nextNotification = template;
        notificationId = id;
    }

    public void showStoryMessage() {
    	final MainActivity activity = this;
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new ApocalipseWindow(activity).load();
				store.registerApocalypse();
				listAdapter.notifyDataSetChanged();
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
    
    public synchronized void checkForUpdates() {
        if (nextNotification != null) {
            store.updateLastNotificationNumber(notificationId);
            listAdapter.notifyDataSetChanged();

            messagesController.setCurrentMessage(nextNotification);
            messagesController.showMessageView();

            pointsController.addPoints(4);
            getUserMessageController().showMessage(getString(R.string.notification_bonus_text));

            nextNotification = null;
            notificationId = -1;
        }
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