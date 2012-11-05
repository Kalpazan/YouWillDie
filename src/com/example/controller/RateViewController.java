package com.example.controller;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.bugsense.trace.BugSenseHandler;
import com.example.MainActivity;
import com.example.NotificationsListAdapter;
import com.example.R;
import com.example.points.PointsController;
import com.example.store.Store;

public class RateViewController {

	private View rateView;
	private PointsController pointsController;
	private Store store;
	private MainActivity activity;
	private boolean isViewVisible;
	private InternetController internetController;

	public RateViewController(MainActivity activity, PointsController pointsController, Store store) {
		this.pointsController = pointsController;
		this.store = store;
		this.activity = activity;
	}
	
	public synchronized void showRateView(boolean checkIfAlreadyRated) {
		if (checkIfAlreadyRated && store.hasAlreadyRated()) {
			return;
		} 
		LinearLayout topLayout = (LinearLayout) activity.findViewById(R.id.top_layout);
		topLayout.addView(getRateView());
		isViewVisible = true;
	}
	
	public synchronized void hideRateView() {
		((LinearLayout) activity.findViewById(R.id.top_layout)).removeView(getRateView());
		isViewVisible = false;
	}

	public void rateClicked() {
		hideRateView();
        internetController = new InternetController(activity);
		store.registerRate();
		try {
            if (internetController.isNetworkAvailable()) {

                pointsController.addPoints(50);
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));

            } else {
                activity.showMessage("You are not online!!!!");
            }

		} catch (Exception e) {
			activity.showMessage("Seems like you don't have Google Play installed..");
			BugSenseHandler.sendException(e);
		}
	}
	
	private View getRateView() {
		if (rateView == null) {
			rateView = NotificationsListAdapter.inflater.inflate(R.layout.rate_app, null);	
			rateView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			OnClickListener clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					rateClicked();
				}
			};
			rateView.setClickable(true);
			rateView.setOnClickListener(clickListener);
			rateView.findViewById(R.id.rate_button).setOnClickListener(clickListener);
		}
		return rateView;
	}

	public synchronized boolean isVisible() {
		return isViewVisible;
	}
}
