package com.example;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.widget.Toast.LENGTH_SHORT;
import static java.util.Calendar.DECEMBER;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity {
    private static final int MINUTE = 1000 * 60;
	private static final int HOUR = MINUTE * 60;

	/**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        buttonCreate();
        countdown();
    }

    public void countdown() {
        final TextView textTime = (TextView) findViewById(R.id.textTime);
        final Calendar finalDate = Calendar.getInstance();
        finalDate.set(2012, DECEMBER, 21, 0, 0);
        new CountDownTimer(finalDate.getTimeInMillis(), 1) {
            @Override
            public void onTick(long timeLeft) {
            	timeLeft = finalDate.getTimeInMillis() - System.currentTimeMillis();
            	
            	long daysLeft = timeLeft / (HOUR * 24);
                long hourLeft = (timeLeft / HOUR) - (24 * daysLeft);
                long minutesLeft = (timeLeft % HOUR) / MINUTE;
                long secondsLeft = (timeLeft % MINUTE) / 1000;
                long millisecLeft = timeLeft % 1000;
                
                String left = "You will die in...\n wait... wait...\n " +
                		"%s days\n " +
                		"%s hours\n " +
                		"%s min\n " +
                		"%s sec\n " +
                		"%s \nmilliseconds";
                textTime.setText(String.format(left, daysLeft, hourLeft, minutesLeft, secondsLeft, millisecLeft));
            }

            @Override
            public void onFinish() {
                textTime.setText("BOOM!");
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                showMessage("All the same, this does not stop. I'll wait for you");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void buttonCreate() {
        final Toast toast = Toast.makeText(getApplicationContext(),
                "is not available for you, sorry :(", Toast.LENGTH_SHORT);
        toast.setGravity(0, 0, 0);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                toast.show();
            }
        });
    }

    public void showMessage(String string) {
        final Toast toast = Toast.makeText(getApplicationContext(), string, LENGTH_SHORT);
        toast.setGravity(0, 0, 0);
        toast.show();
    }
}

