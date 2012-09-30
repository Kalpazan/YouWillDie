package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        buttonCreate();
        countdown();
    }

    public void countdown() {
        final TextView textTime = (TextView) findViewById(R.id.textTime);
        final Calendar finalDate = Calendar.getInstance();
        finalDate.set(2012, 11, 21, 0, 0);
        new CountDownTimer(finalDate.getTimeInMillis(), 1) {
            @Override
            public void onTick(long l) {
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(new Date());
                long timeLeft = finalDate.getTimeInMillis() - startDate.getTimeInMillis();
                long daysLeft = timeLeft / (1000 * 60 * 60 * 24);
                long hourLeft = (timeLeft / (1000 * 60 * 60)) - (24 * daysLeft);
                long minutesLeft = (timeLeft / (1000 * 60)) - (24 * 60 * daysLeft) - (60 * hourLeft);
                long secondsLeft = (timeLeft / (1000)) - (24 * 60 * 60 * daysLeft) - (60 * 60 * hourLeft) - (60 * minutesLeft);
                long millisecLeft = timeLeft - (24 * 60 * 60 * 1000 * daysLeft) - (60 * 60 * 1000 * hourLeft) - (60 * 1000 * minutesLeft) - (1000 * secondsLeft);
                String left = "You will die in...\n wait... wait...\n" + daysLeft + " days\n" + hourLeft + " hours\n" +
                        minutesLeft + " min\n" + secondsLeft + " sec\n" + millisecLeft + "\nmilliseconds";
                textTime.setText(left);
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
        final Toast toast = Toast.makeText(getApplicationContext(),
                string, Toast.LENGTH_SHORT);
        toast.setGravity(0, 0, 0);
        toast.show();
    }
}

