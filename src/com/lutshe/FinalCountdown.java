package com.lutshe;

import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.widget.TextView;

public class FinalCountdown extends CountDownTimer {
	public static final int MINUTE = 1000 * 60;
	public static final int HOUR = MINUTE * 60;
	
	private MainActivity activity;
    //private TextView textDaysLeft;
    private int counter;
    private String decimalFormatter;
    private String formatter;
    private TextView textTimeDay;
    private TextView textTimeHour;
    private TextView textTimeMin;
    private TextView textTimeSec;
    private TextView textTimeMilisec;

    private long previousMinute = -1;
    
    long minutesLeft;
    long secondsLeft;
    long millisecLeft;
    long daysLeft;
    long hourLeft;

    
	public FinalCountdown(long millisInFuture, long countDownInterval, MainActivity activity) {
		super(millisInFuture, countDownInterval);
		this.activity = activity;

		decimalFormatter = activity.getResources().getString(R.string.millisecFormatter);
		formatter = activity.getResources().getString(R.string.datesFormatter);

		Typeface type = Typeface.createFromAsset(activity.getAssets(), "MyriadPro-Semibold.otf");
        textTimeDay.setTypeface(type);
        textTimeHour.setTypeface(type);
        textTimeMin.setTypeface(type);
        textTimeSec.setTypeface(type);
        textTimeMilisec.setTypeface(type);
		
        textTimeDay = (TextView) activity.findViewById(R.id.textTimeDay);
        textTimeHour = (TextView) activity.findViewById(R.id.textTimeHour);
        textTimeMin = (TextView) activity.findViewById(R.id.textTimeMin);
        textTimeSec = (TextView) activity.findViewById(R.id.textTimeSec);
        textTimeMilisec = (TextView) activity.findViewById(R.id.textTimeMilisec);
        
	}

	@Override
	public void onFinish() {
		//activity.updateTimerText("BOOM!");
	}

	@Override
	public void onTick(long timeLeft) {

        minutesLeft = (timeLeft % HOUR) / MINUTE;
        secondsLeft = (timeLeft % MINUTE) / 1000;
        millisecLeft = timeLeft % 1000;
        
        if (previousMinute != minutesLeft) {
        	previousMinute = minutesLeft;
        	
        	daysLeft = timeLeft / (HOUR * 24);
            hourLeft = (timeLeft / HOUR) - (24 * daysLeft);
            textTimeDay.setText(String.format(formatter, daysLeft));
            textTimeHour.setText(String.format(formatter, hourLeft));
            textTimeMin.setText(String.format(formatter, minutesLeft));
        }

        
        textTimeSec.setText(String.format(formatter, secondsLeft));
        textTimeMilisec.setText(String.format(decimalFormatter,(int)millisecLeft));

        counter %= 100;
        
        if (counter == 0) {
        	activity.checkForUpdates();
        }
        counter++;
    }

}
