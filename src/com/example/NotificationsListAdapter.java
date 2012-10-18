package com.example;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationsListAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	private NotificationProvider provider;

	public NotificationsListAdapter(Activity a, NotificationProvider provider) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.provider = provider;
	}

	public int getCount() {
		return provider.getLastNotificationNumber() + 1;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null)
			view = inflater.inflate(R.layout.list_row, null);

		TextView title = (TextView) view.findViewById(R.id.title); // title
		TextView artist = (TextView) view.findViewById(R.id.artist); // artist
																	// name
		TextView duration = (TextView) view.findViewById(R.id.duration); // duration
		ImageView thumb_image = (ImageView) view.findViewById(R.id.list_image); // thumb
																				// image
		NotificationTemplate notification = provider.getPreviousNotifications().get(position);

		// Setting all values in listview
		title.setText(notification.getTitle());
		artist.setText(notification.getMainText());
		duration.setText("1:11");
		thumb_image.setImageDrawable(activity.getResources().getDrawable(notification.getIcon()));
		
		return view;
	}

}
