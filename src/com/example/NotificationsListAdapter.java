package com.example;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.FinalCountdown.HOUR;

import java.util.Calendar;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.store.Store;

public class NotificationsListAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	private NotificationProvider provider;
	private Store store;
	
	private int previousSize;
	
	public NotificationsListAdapter(MainActivity a, NotificationProvider provider) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
		this.provider = provider;
		this.store = a.getStore();
	}

	public int getCount() {
		int size = store.getLastNotificationNumber() + 1;
		if (size != previousSize) {
			previousSize = size;
			notifyDataSetChanged();
		}
		previousSize = size;
		return size;
	}

	public Object getItem(int position) {
		return flipPosition(position);
	}

	public long getItemId(int position) {
		return flipPosition(position);
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
		Resources res = activity.getResources();
		Calendar someTime = Calendar.getInstance();
		
		Calendar messageTime = Calendar.getInstance();
		messageTime.setTimeInMillis(time);

		someTime.add(Calendar.MINUTE, -5);
		if (someTime.before(messageTime)) return res.getString(R.string.few_mins_ago);
		
		someTime.set(Calendar.HOUR_OF_DAY, 0);
		long startOfToday = someTime.getTimeInMillis();
		if(someTime.before(messageTime)) return res.getString(R.string.today);

		someTime.add(Calendar.HOUR_OF_DAY, -24);
		if(someTime.before(messageTime)) return res.getString(R.string.yesterday);
		
		someTime.add(Calendar.DAY_OF_MONTH, -2);
		if(someTime.before(messageTime)) return res.getString(R.string.recently);
		
		long days = (startOfToday - time) / (24 * HOUR);
		
		boolean stupidNumber = days % 10  < 5;
		
		return days + (stupidNumber ? res.getString(R.string.days_ago_ru1) : res.getString(R.string.days_ago_ru2));
	}
	
}
