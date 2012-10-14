package com.example;

import android.os.CountDownTimer;

public class FinalCountdown extends CountDownTimer {
	private static final int MINUTE = 1000 * 60;
	private static final int HOUR = MINUTE * 60;
	
	private long bigBoomTime;
	private MyActivity activity;
	
	public FinalCountdown(long millisInFuture, long countDownInterval, MyActivity activity) {
		super(millisInFuture, countDownInterval);
		this.bigBoomTime = millisInFuture;
		this.activity = activity;
	}

	@Override
	public void onFinish() {
		activity.updateTimerText("BOOM!");
	}

	@Override
	public void onTick(long timeLeft) {
    	timeLeft = bigBoomTime - System.currentTimeMillis();
    	
    	long daysLeft = timeLeft / (HOUR * 24);
        long hourLeft = (timeLeft / HOUR) - (24 * daysLeft);
        long minutesLeft = (timeLeft % HOUR) / MINUTE;
        long secondsLeft = (timeLeft % MINUTE) / 1000;
        long millisecLeft = timeLeft % 1000;
        
        String left ="%s days %s h %s m  " +
        		"%s.%03d sec";
        activity.updateTimerText(String.format(left, daysLeft, hourLeft, minutesLeft, secondsLeft, millisecLeft));
    }

}
