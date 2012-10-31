package com.example;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.widget.Toast.LENGTH_SHORT;
import static java.util.Calendar.DECEMBER;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.example.controller.MessageDisplayController;
import com.example.points.PointsController;
import com.example.store.Store;

public class MainActivity extends Activity {

    private FinalCountdown timer;
    private TextView textTime;
    private TextView textDaysLeft;
    private MediaPlayer mediaPlayer;
	private NotificationsListAdapter listAdapter;
    
    private PointsController pointsController;
    private Store store;
    
    private MessageDisplayController messagesController;
    private NotificationProvider provider = new NotificationProvider();
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        BugSenseHandler.initAndStartSession(MainActivity.this, "f48c5119");
        setContentView(R.layout.main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        store = new Store(getApplicationContext());
        pointsController = new PointsController(this);
        
        provider = new NotificationProvider();
        messagesController = new MessageDisplayController(provider, this);

        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToChooser sendToChooser = new SendToChooser(MainActivity.this, messagesController.getCurrentMessage().getMainText(), pointsController);
                sendToChooser.sendViaCustomChooser();
            }
        });
        
        Button helpButton = (Button) findViewById(R.id.help_button);
        
        messagesController.init();
        
		helpButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
				messagesController.flipViews();
            }
        });

        setupHistoryList(provider, messagesController);
      
        if (isFirstLaunch()) {
        	NotificationServiceThatJustWorks.startService(getApplicationContext());
        	store.registerFirstLaunch();
        }
        
        listAdapter.notifyDataSetChanged();

		startCountDown();
		playSound();
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
                pointsController.addPoints(5);
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
        textTime = (TextView) findViewById(R.id.textTime);
        textDaysLeft = (TextView) findViewById(R.id.days_left);
        Typeface type = Typeface.createFromAsset(getAssets(), "TEXASLED.TTF");
        textTime.setTypeface(type);
        final Calendar finalDate = Calendar.getInstance();
        finalDate.set(2012, DECEMBER, 21, 0, 0);
        timer = new FinalCountdown(finalDate.getTimeInMillis(), 17, this);
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
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int id = extras.getInt(NotificationServiceThatJustWorks.EXTRA_NAME);
            NotificationTemplate notification = provider.getNotification(id);
            messagesController.setCurrentMessage(notification);
            messagesController.showMessageView();
        }
    }

	@Override
    protected void onPause() {

        super.onPause();
        timer.cancel();
        mediaPlayer.pause();
        
        final SlidingDrawer slider = (SlidingDrawer) findViewById(R.id.drawer);
        slider.close();
        
        BugSenseHandler.flush(MainActivity.this); //How to use it. See https://www.bugsense.com/docs
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.start();
        mediaPlayer.start();



    }

    public void updateTimerText(String timeString) {
        textTime.setText(timeString);
        textDaysLeft.setText(timer.getDaysLeft() + " days left");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void showMessage(String string) {
        final Toast toast = Toast.makeText(getApplicationContext(), string, LENGTH_SHORT);
        toast.setGravity(0, 0, 0);
        toast.show();
    }

    public Store getStore() {
		return store;
	}
    
    public PointsController getPointsController() {
		return pointsController;
	}
}

