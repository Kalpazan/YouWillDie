package com.example;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

public class NotificationProvider {

	private static final String TIME_PREFIX = "time_of_";
	private static final String LAST_NOTIFICATION_NUM = "lastNotificationNum";
	private static final String STORE_NAME = "huj";
	
	private NotificationTemplate[] notifications = {
			new NotificationTemplate("Click to see tits!", "(*)(.)", "c====3", R.drawable.icon_2),
			new NotificationTemplate("Hi man!", "���� 61 �������� � ������� �����, ���� ��� ����� ���������� ��� ���������������� �����������", "c=3", R.drawable.icon_3),
			new NotificationTemplate("Hi man!", "�� ������ ���� ������� ����� ������� � �������� � ����. ����� ������ ������� (��� ������ �� �������)", "c=3", R.drawable.icon_4),
			new NotificationTemplate("Hi man!", "������� ���������, ��� ������ ���� �����. ����� ��� �� ���� ���� � ��� ���", "c=3", R.drawable.icon_5),
			new NotificationTemplate("Hi man!", "������� ������ ���������� �������������� � ��������� ����, ������� �� ������ ������ �� ����� �������. (��� ����� ��������� �����-�� ������ �����������, ���� evernote, <a href='drive.google.com'>googleDoc</a>, or Dropbox.com)", "c=3", R.drawable.icon_1),
			new NotificationTemplate("Hi man!", "���� �� ��� �� ������ �������� - ��������� ����! (��� �� ����� ��������� ������ �� ������ � �����, ������������� �������� �������� ��� ������ �� �������) ", "c=3", R.drawable.icon_2),
			new NotificationTemplate("Hi man!", "���� ����� ������ ����� - ����� �� ����� ���-�� ������������ ����� � �������� ", "c=3", R.drawable.icon_3),
			new NotificationTemplate("Hi man!", "������ ��� � ������� ������ - ���� ��� ������ � �������������� ��������� �����."+
	"� �����: ��� ������ ����� � ���� �����, ��� ������ ����� ������ �������� ���� �����!", "c=3", R.drawable.icon_4),
			new NotificationTemplate("Hi man!", "���� �������� ������! ���� ��� �����������, ����� ���-�� ���������� � ����������.", "c=3", R.drawable.icon_5),
			new NotificationTemplate("Hi man!", "��������� ���-��� ��� ������� � �������� ��������", "c=3", R.drawable.icon_1),
			new NotificationTemplate("Hi man!", "�� ������� �� ��� � ���-����. ����� ����� �����. �� ������� ���������, ��������� � ��� �� ����", "c=3", R.drawable.icon_2),
			new NotificationTemplate("Hi man!", "���� �� ������, ����� � ���� ���� �������� ��������, ������ ��������� � ������", "c=3", R.drawable.icon_3),
	};
	
	private Context context;
	
	
	public NotificationProvider(Context context) {
		this.context = context;
	}

	public NotificationTemplate[] getNotifications() {
		return notifications;
	}

	public int getPreviousNotificationsCount() {
		return notifications.length;
	}


	public List<NotificationTemplate> getPreviousNotifications() {
		return Arrays.asList(notifications);
	}

	public void saveNotificationTime(int notificationNumber, long time) {
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putLong(TIME_PREFIX + notificationNumber, time);

	    editor.commit();
	}
	
	public long getNotofocationTime(int notificationNumber) {
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    return settings.getLong(TIME_PREFIX + notificationNumber, -1);
	}
	
	public synchronized void updateLastNotificationNumber(int number) {
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt(LAST_NOTIFICATION_NUM, number);

	    editor.commit();
	}
	
	public synchronized int getLastNotificationNumber() {
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    return settings.getInt(LAST_NOTIFICATION_NUM, -1);
	}

	public boolean hasNotificationWithNumber(int number) {
		return number < notifications.length && number >= 0;
	}
	
	public NotificationTemplate getNotification(int number) {
		if (number < notifications.length) {
			return notifications[number];
		}
		return null;
	}
	
}
