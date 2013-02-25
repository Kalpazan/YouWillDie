package com.lutshe;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lutshe.store.Store;

import java.util.Calendar;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.lutshe.FinalCountdown.HOUR;

public class NotificationsListAdapter extends BaseAdapter {

	private Activity activity;
	public static LayoutInflater inflater = null;
	private NotificationProvider provider;
	private Store store;
	private boolean hasApocalypseStory = false;
	
	private int previousSize = -1;
	
	public NotificationsListAdapter(MainActivity a, NotificationProvider provider) {
		activity = a;
		inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
		this.provider = provider;
		this.store = a.getStore();
	}

	public int getCount() {
		int size = store.getLastNotificationNumber() + 1;
		if (store.hasApocalypseFinished()) {
			size ++;
			hasApocalypseStory = true;
		}
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

	public View getView(int position, View view, ViewGroup parent) {
		Log.d("lview", "getView");
		position = flipPosition(position);
		
		if (view == null) {
			view = inflater.inflate(R.layout.list_row, null);
			view.setTag(new ViewHolder(view));
		} 
		
		ViewHolder row = (ViewHolder) view.getTag();
		
		final NotificationTemplate notification; 
		if (hasApocalypseStory && flipPosition(position) == 0) {
			notification = new NotificationTemplate(null, "The end", activity.getString(R.string.apocalypse_window_text1), R.drawable.icon_gentelman);
			row.notificationTime.setText("Day X "); 
		} else {
			notification = provider.getNotification(position);
			row.notificationTime.setText(formatDate(store.getNotofocationTime(position)));
		}
		row.title.setText(notification.getTitle());
		row.artist.setText(notification.getMainText());
		
		row.icon.setImageDrawable(activity.getResources().getDrawable(notification.getIcon()));

		return view;
	}

	private class ViewHolder {
		TextView title;
		TextView artist;
		TextView notificationTime;
		ImageView icon;
		
		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.title); 
			artist = (TextView) view.findViewById(R.id.artist); 
																		
			notificationTime = (TextView) view.findViewById(R.id.time); 
			icon = (ImageView) view.findViewById(R.id.list_image);
		}
	}
	
	public int flipPosition(int position) {
		return (previousSize - 1 ) - position;
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
		
		return days + " " + (stupidNumber ? res.getString(R.string.days_ago_ru1) : res.getString(R.string.days_ago_ru2));
	}
	
}
