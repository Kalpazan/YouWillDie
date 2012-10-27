package com.example;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.widget.Toast.LENGTH_SHORT;
import static java.util.Calendar.DECEMBER;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
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
import android.widget.ViewFlipper;

import com.bugsense.trace.BugSenseHandler;
import com.example.points.PointsController;
import com.example.store.Store;

public class MainActivity extends Activity {

    private CountDownTimer timer;
    private TextView textTime;
    private MediaPlayer mediaPlayer;
	private NotificationsListAdapter listAdapter;
    private ViewFlipper flipper;
    
    private PointsController pointsController;
    private Store store;
    
    
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        BugSenseHandler.initAndStartSession(MainActivity.this, "f48c5119");
        setContentView(R.layout.main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        buttonCreate();
        store = new Store(getApplicationContext());
        pointsController = new PointsController(this);
        
        flipper = (ViewFlipper)findViewById(R.id.view_flipper);
        findViewById(R.id.help_button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.showNext();
            }
        });

        setupHistoryList();
        
        NotificationSchedulerService.startService(getApplicationContext());
        
        listAdapter.notifyDataSetChanged();
        
        TimerTask task = new TimerTask() {
			@Override
			public void run() {
				listAdapter.notifyDataSetChanged();
			}
		};
		new Timer().schedule(task, 0);

		startCountDown();
		playSound();
    }

    private void setupHistoryList() {
        ListView historyListView = (ListView) findViewById(R.id.content);
        final NotificationProvider provider = new NotificationProvider();
		listAdapter = new NotificationsListAdapter(this, provider);
		historyListView.setAdapter(listAdapter);

        final TextView notificationText = (TextView) findViewById(R.id.notification_text);
        final SlidingDrawer slider = (SlidingDrawer) findViewById(R.id.drawer);

        historyListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                NotificationTemplate notification = provider.getNotifications()[position];
                notificationText.setText(notification.getMainText());
                slider.animateClose();
                pointsController.addPoints(5);
            }
        });
    }

    public void playSound() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.stopwatch);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void startCountDown() {
        textTime = (TextView) findViewById(R.id.textTime);
        Typeface type = Typeface.createFromAsset(getAssets(), "TEXASLED.TTF");
        textTime.setTypeface(type);
//        textTime.setTextSize(35);
        final Calendar finalDate = Calendar.getInstance();
        finalDate.set(2012, DECEMBER, 21, 0, 0);
        timer = new FinalCountdown(finalDate.getTimeInMillis(), 17, this).start();
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
            String notificationTitle = extras.getString("msg");
            showMessage(notificationTitle);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void buttonCreate() {
//        final Toast toast = Toast.makeText(getApplicationContext(), "is not available for you, sorry :(", LENGTH_SHORT);
//        toast.setGravity(0, 0, 0);
//
//        View button = findViewById(R.id.slow_down_button);
//        button.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View view) {
//                toast.show();
//                pointsController.addPoints(1);
//            }
//        });
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

