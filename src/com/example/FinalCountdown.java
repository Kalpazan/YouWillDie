package com.example;

import android.os.CountDownTimer;

public class FinalCountdown extends CountDownTimer {
	public static final int MINUTE = 1000 * 60;
	public static final int HOUR = MINUTE * 60;
	
	private MainActivity activity;
    private long daysLeft;
    private int counter;

    public long getDaysLeft() {
        return daysLeft;
    }

	public FinalCountdown(long millisInFuture, long countDownInterval, MainActivity activity) {
		super(millisInFuture, countDownInterval);
		this.activity = activity;
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

        String left ="%s ä %s ÷ %s ì %s.%03d";
        activity.updateTimerText(String.format(left, daysLeft, hourLeft, minutesLeft, secondsLeft, millisecLeft));
        
        counter++;
        counter %= 50;
        
        if (counter == 0) {
        	activity.checkForUpdates();
        }
    }

}
