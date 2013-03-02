package com.lutshe.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lutshe.MainActivity;
import com.lutshe.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class UserMessageController {
	private static final int FADE_DELAY = 1350;
	
	private final LinearLayout topLayout;
	private final LayoutInflater layoutInflater;
	
	public UserMessageController(MainActivity activity) {
		topLayout = (LinearLayout) activity.findViewById(R.id.top_layout);
		layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
	}
	
	public void showMessage(String message) {
		final View msgView = createMessageView(message);
		topLayout.addView(msgView);
        Runnable hideTask = new Runnable() {
            @Override
            public void run() {
                hideMessageView(msgView);
            }
        };
		msgView.postDelayed(hideTask, 1500);
        msgView.setTag(hideTask);
	}
	
	private void hideMessageView(final View msgView) {
//		Animation fadeout = new AlphaAnimation(1.f, 0.f);
//
//		fadeout.setDuration(FADE_DELAY);
//		fadeout.setAnimationListener(new Animation.AnimationListener() {
//			@Override public void onAnimationStart(Animation animation) {}
//			@Override public void onAnimationRepeat(Animation animation) {}
//
//			@Override public void onAnimationEnd(Animation animation) {
				topLayout.removeView(msgView);
//			}
//		});
//
//		msgView.startAnimation(fadeout);
	}

	private View createMessageView(String msgText) {
		View msgView = layoutInflater.inflate(R.layout.top_message, null);
		msgView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		msgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                v.removeCallbacks((Runnable) v.getTag());
				hideMessageView(v);
                v.setClickable(false);
                v.setOnClickListener(null);
			}
		});
		
		TextView textView = (TextView) msgView.findViewById(R.id.user_message_text);
		textView.setText(msgText);
		
		return msgView;
	}

}
