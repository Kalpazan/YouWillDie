package com.example;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.widget.Toast.LENGTH_LONG;
import static java.util.Calendar.DECEMBER;

import java.util.Calendar;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.example.controller.MessageDisplayController;
import com.example.controller.RateViewController;
import com.example.controller.UserMessageController;
import com.example.points.PointsController;
import com.example.store.Store;

public class MainActivity extends Activity {

    private FinalCountdown timer;

//    private TextView textDaysLeft;
    private MediaPlayer mediaPlayer;
	private NotificationsListAdapter listAdapter;
	private RateViewController rateViewController;
	private UserMessageController userMessageController;
	private PointsController pointsController;
    private Store store;
    
    private MessageDisplayController messagesController;
    private NotificationProvider provider;
    
    public static MainActivity instance;
    
    @Override
    public void onCreate(Bundle bundle) {
//    	Debug.startMethodTracing("SurvivalGuide");
        try {
	    	super.onCreate(bundle);
	        
	        setContentView(R.layout.main);
	        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
	        store = new Store(getApplicationContext());
	        pointsController = new PointsController(this);
	        
	        provider = NotificationProvider.getInstance(getResources());
	        messagesController = new MessageDisplayController(provider, this);
	
	        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	                SendToChooser sendToChooser = new SendToChooser(MainActivity.this, messagesController.getCurrentMessage().getMainText(), pointsController);
	                sendToChooser.sendViaCustomChooser();
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
	        
	        Button helpButton = (Button) findViewById(R.id.help_button);
	        
			helpButton.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View view) {
					messagesController.flipViews();
	            }
	        });
			
	        setupHistoryList(provider, messagesController);
	      
			playSound();
	//		Debug.stopMethodTracing();
        } catch (Exception e) {
        	BugSenseHandler.initAndStartSession(MainActivity.this, "f48c5119");
        	BugSenseHandler.sendException(e);
        }
    }

    private boolean isFirstLaunch() {
		return !store.wasLaunchedBefore();
	}

	private void setupHistoryList(final NotificationProvider provider, final MessageDisplayController messageController) {
        ListView historyListView = (ListView) findViewById(R.id.content);
		listAdapter = new NotificationsListAdapter(this, provider);
		historyListView.setAdapter(listAdapter);

        final SlidingDrawer slider = (SlidingDrawer) findViewById(R.id.drawer);

        historyListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                NotificationTemplate notification = provider.getNotifications()[listAdapter.flipPosition(position)];
                messageController.setCurrentMessage(notification);
                messageController.showMessageView();
                slider.animateClose();
                if (store.wasPointsAddedOnMsgView()){
                    pointsController.addPoints(6);
                    getUserMessageController().showMessage("Перечитываешь? Молодец!");
                    store.registerPointsAddingOnMsgView();
                }
            }
        });
    }

    public void playSound() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.stopwatch);
        mediaPlayer.setLooping(true);
        
        int maxVolume = 11;
		float log1=(float)(Math.log(maxVolume-2)/Math.log(maxVolume));
        mediaPlayer.setVolume(1 - log1, 1 - log1);
        mediaPlayer.start();
    }

    public void startCountDown() {
        final Calendar finalDate = Calendar.getInstance();
        finalDate.clear();
        finalDate.set(2012, DECEMBER, 21, 0, 0);

        timer = new FinalCountdown(finalDate.getTimeInMillis() - System.currentTimeMillis(), 51, this);
        timer.start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                showMessage("All the same, this does not stop. It is waiting for you.");
                System.exit(0);
                return true;
            case R.id.help:
                showMessage("Who will help you?");
                showMessage("No one!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
    protected void onPause() {
		MainActivity.instance = null;
        super.onPause();
        timer.cancel();
        mediaPlayer.pause();
        
        final SlidingDrawer slider = (SlidingDrawer) findViewById(R.id.drawer);
        slider.close();
        
        BugSenseHandler.flush(MainActivity.this); //How to use it. See https://www.bugsense.com/docs
        Debug.stopMethodTracing();
    }

    @Override
    protected void onResume() {
    	Log.d("time", "onResume!");
        super.onResume();
        if (isFirstLaunch()) {
        	new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					NotificationServiceThatJustWorks.startService(getApplicationContext());
					return null;
				}
			}.execute((Void)null);
			
        	store.registerFirstLaunch();
            pointsController.addPoints(10);
            getUserMessageController().showMessage("Твой первый запуск? +10 очков!");
            store.registerPointsAddingOnCreate();
        }
        
        startCountDown();
        mediaPlayer.start();
        messagesController.init();
        
        MainActivity.instance = this;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
	
	public synchronized void checkForUpdates() {
		if (nextNotification != null) {
			messagesController.setCurrentMessage(nextNotification);
			messagesController.showMessageView();
			if (store.wasPointsAddedOnMsgView()){
                pointsController.addPoints(4);
                getUserMessageController().showMessage("новое сообщение: +4 очка!");
                store.registerPointsAddingOnMsgView();
            }

			store.updateLastNotificationNumber(notificationId);
			listAdapter.notifyDataSetChanged();
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
	
}

