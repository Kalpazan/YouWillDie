package com.example;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.FinalCountdown.HOUR;
import static com.example.FinalCountdown.MINUTE;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
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
		inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
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

		TextView title = (TextView) view.findViewById(R.id.title); 
		TextView artist = (TextView) view.findViewById(R.id.artist); 
																	
		TextView notificationTime = (TextView) view.findViewById(R.id.time); 
		ImageView thumb_image = (ImageView) view.findViewById(R.id.list_image);
		final NotificationTemplate notification = provider.getPreviousNotifications().get(position);

		title.setText(notification.getTitle());
		artist.setText(notification.getMainText());
		
		notificationTime.setText(formatDate(provider.getNotofocationTime(position)));
		thumb_image.setImageDrawable(activity.getResources().getDrawable(notification.getIcon()));

		view.setOnLongClickListener(new OnLongClickListener() {

            public boolean onLongClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Чувак охуенная прога. Написала: "+ notification.getMainText());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent chooserIntent = Intent.createChooser(intent, "Отправить другу");
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.getApplicationContext().startActivity(chooserIntent);
                return true;
            }
        });

		return view;
	}

	private String formatDate(long time) {
		long diff = System.currentTimeMillis() - time;

		if (diff < 5 * MINUTE) {
			return "только что";
		} 
		
		if (diff < 24 * HOUR) {
			return "сегодня";
		} 
		
		if (diff < 24 * HOUR * 2) {
			return "вчера";
		} 
		
		long days = diff / (24 * HOUR);
		return days == 2 ? days + " дня назад" : days + " дней назад";
	}
	
}
