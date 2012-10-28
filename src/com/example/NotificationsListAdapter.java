package com.example;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.FinalCountdown.HOUR;

import java.util.Calendar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.store.Store;

public class NotificationsListAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	private NotificationProvider provider;
	private Store store;
	
	public NotificationsListAdapter(MainActivity a, NotificationProvider provider) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
		this.provider = provider;
		this.store = a.getStore();
	}

	public int getCount() {
		return store.getLastNotificationNumber() + 1;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		position = flipPosition(position);
		View view = convertView;
		if (convertView == null)
			view = inflater.inflate(R.layout.list_row, null);

		TextView title = (TextView) view.findViewById(R.id.title); 
		TextView artist = (TextView) view.findViewById(R.id.artist); 
																	
		TextView notificationTime = (TextView) view.findViewById(R.id.time); 
		ImageView thumb_image = (ImageView) view.findViewById(R.id.list_image);
		final NotificationTemplate notification = provider.getPreviousNotifications().get(position);

		title.setText(notification.getTitle());
		artist.setText(notification.getMainText());
		
		notificationTime.setText(formatDate(store.getNotofocationTime(position)));
		thumb_image.setImageDrawable(activity.getResources().getDrawable(notification.getIcon()));

		return view;
	}

	public int flipPosition(int position) {
		return store.getLastNotificationNumber() - position;
	}
	
	private String formatDate(long time) {
		Calendar someTime = Calendar.getInstance();
		
		Calendar messageTime = Calendar.getInstance();
		messageTime.setTimeInMillis(time);

		someTime.add(Calendar.MINUTE, -5);
		if (someTime.before(messageTime)) return "Только что";
		
		someTime.set(Calendar.HOUR_OF_DAY, 0);
		long startOfToday = someTime.getTimeInMillis();
		if(someTime.before(messageTime)) return "сегодня";

		someTime.add(Calendar.HOUR_OF_DAY, -24);
		if(someTime.before(messageTime)) return "вчера";
		
		someTime.add(Calendar.DAY_OF_MONTH, -2);
		if(someTime.before(messageTime)) return "недавно";
		
		long days = (startOfToday - time) / (24 * HOUR);
		
		boolean stupidNumber = days % 10  < 5;
		
		return days + (stupidNumber ? " дня назад" : " дней назад");
	}
	
}
