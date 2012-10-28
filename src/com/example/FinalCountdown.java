package com.example;

import android.os.CountDownTimer;

public class FinalCountdown extends CountDownTimer {
	public static final int MINUTE = 1000 * 60;
	public static final int HOUR = MINUTE * 60;
	
	private long bigBoomTime;
	private MainActivity activity;
    private long daysLeft;

    public long getDaysLeft() {
        return daysLeft;
    }

    public FinalCountdown(long millisInFuture, long countDownInterval, MainActivity activity) {
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
    	
    	daysLeft = timeLeft / (HOUR * 24);
        long hourLeft = (timeLeft / HOUR) - (24 * daysLeft);
        long minutesLeft = (timeLeft % HOUR) / MINUTE;
        long secondsLeft = (timeLeft % MINUTE) / 1000;
        long millisecLeft = timeLeft % 1000;

        String left ="%s � %s � %s � %s.%03d";
        activity.updateTimerText(String.format(left, daysLeft, hourLeft, minutesLeft, secondsLeft, millisecLeft));
    }

}
