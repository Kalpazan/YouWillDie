package com.example;

import android.os.CountDownTimer;

public class FinalCountdown extends CountDownTimer {
	public static final int MINUTE = 1000 * 60;
	public static final int HOUR = MINUTE * 60;
	
	private MainActivity activity;
    private long daysLeft;
    private int counter;

    private String timeLeftStr;
    
    public long getDaysLeft() {
        return daysLeft;
    }

	public FinalCountdown(long millisInFuture, long countDownInterval, MainActivity activity) {
		super(millisInFuture, countDownInterval);
		this.activity = activity;
		timeLeftStr = activity.getResources().getString(R.string.time_left);
	}

	@Override
	public void onFinish() {
		activity.updateTimerText("BOOM!");
	}

	@Override
	public void onTick(long timeLeft) {
    	daysLeft = timeLeft / (HOUR * 24);
        long hourLeft = (timeLeft / HOUR) - (24 * daysLeft);
        long minutesLeft = (timeLeft % HOUR) / MINUTE;
        long secondsLeft = (timeLeft % MINUTE) / 1000;
        long millisecLeft = timeLeft % 1000;

        activity.updateTimerText(String.format(timeLeftStr, daysLeft, hourLeft, minutesLeft, secondsLeft, millisecLeft));
        
        counter++;
        counter %= 50;
        
        if (counter == 0) {
        	activity.checkForUpdates();
        }
    }

}
