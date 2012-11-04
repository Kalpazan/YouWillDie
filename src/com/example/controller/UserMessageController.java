package com.example.controller;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.MainActivity;
import com.example.NotificationsListAdapter;
import com.example.R;

public class UserMessageController {
	private static final int FADE_DELAY = 1500;
	private View msgView;
	private TextView textView;
	private MainActivity activity;
	private boolean isViewVisible;
	
	public UserMessageController(MainActivity activity) {
		this.activity = activity;
	}
	
	public synchronized void showMessage(String message) {
		LinearLayout topLayout = (LinearLayout) activity.findViewById(R.id.top_layout);
		topLayout.addView(getView(message));
		isViewVisible = true;
		msgView.postDelayed(new Runnable() {
			@Override
			public void run() {
				hideMessageView();
			}
		}, 1500);
	}
	
	public synchronized void hideMessageView() {
		Animation fadeout = new AlphaAnimation(1.f, 0.f);
		fadeout.setDuration(FADE_DELAY);
		msgView.startAnimation(fadeout);
		msgView.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	((LinearLayout) activity.findViewById(R.id.top_layout)).removeView(getView(null));
		    }
		}, FADE_DELAY);
		
		isViewVisible = false;
	}

	public void rateClicked() {
		hideMessageView();
	}
	
	private View getView(String msgText) {
		if (msgView == null) {
			msgView = NotificationsListAdapter.inflater.inflate(R.layout.top_message, null);	
			msgView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			msgView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					hideMessageView();
				}
			});
			textView = (TextView) msgView.findViewById(R.id.user_message_text);
		}
		
		if (msgText != null) {
			textView.setText(msgText);
		}
		
		return msgView;
	}

	public synchronized boolean isVisible() {
		return isViewVisible;
	}
}
