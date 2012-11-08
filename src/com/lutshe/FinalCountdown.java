package com.lutshe;

import android.os.CountDownTimer;
import android.widget.TextView;

public class FinalCountdown extends CountDownTimer {
	public static final int MINUTE = 1000 * 60;
	public static final int HOUR = MINUTE * 60;
	
	private MainActivity activity;
    //private TextView textDaysLeft;
    private int counter;
    private String formatter;
    private TextView textTimeDay;
    private TextView textTimeHour;
    private TextView textTimeMin;
    private TextView textTimeSec;
    private TextView textTimeMilisec;


	public FinalCountdown(long millisInFuture, long countDownInterval, MainActivity activity) {
		super(millisInFuture, countDownInterval);
		this.activity = activity;

        formatter = activity.getResources().getString(R.string.millisecFormatter);

        //textDaysLeft = (TextView) activity.findViewById(R.id.days_left);

        textTimeDay = (TextView) activity.findViewById(R.id.textTimeDay);
        textTimeHour = (TextView) activity.findViewById(R.id.textTimeHour);
        textTimeMin = (TextView) activity.findViewById(R.id.textTimeMin);
        textTimeSec = (TextView) activity.findViewById(R.id.textTimeSec);
        textTimeMilisec = (TextView) activity.findViewById(R.id.textTimeMilisec);

//        Typeface type = Typeface.createFromAsset(activity.getAssets(), "Acme 9 Regular.ttf");
//        textTimeDay.setTypeface(type);
//        textTimeHour.setTypeface(type);
//        textTimeMin.setTypeface(type);
//        textTimeSec.setTypeface(type);
//        textTimeMilisec.setTypeface(type);
	}

	@Override
	public void onFinish() {
		//activity.updateTimerText("BOOM!");
	}

	@Override
	public void onTick(long timeLeft) {

		long daysLeft = timeLeft / (HOUR * 24);
        textTimeDay.setText(String.valueOf(daysLeft));
        long hourLeft = (timeLeft / HOUR) - (24 * daysLeft);
        textTimeHour.setText(String.valueOf(hourLeft));
        long minutesLeft = (timeLeft % HOUR) / MINUTE;
		
        long secondsLeft = (timeLeft % MINUTE) / 1000;
        textTimeSec.setText(String.valueOf(secondsLeft));
        long millisecLeft = timeLeft % 1000;
        textTimeMilisec.setText(String.format(formatter,(int)millisecLeft));


        counter %= 100;
        
        if (counter == 0) {
        	activity.checkForUpdates();
        	
            textTimeMin.setText(String.valueOf(minutesLeft));
            //textDaysLeft.setText(String.valueOf(daysLeft)+" " + activity.getResources().getString(R.string.days_left));
        }
        counter++;
    }

}
