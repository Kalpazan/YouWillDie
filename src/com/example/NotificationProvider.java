package com.example;

import java.util.Arrays;
import java.util.List;

public class NotificationProvider {

	private NotificationTemplate[] notifications = {
			new NotificationTemplate("Click to see tits!", "(*)(.)", "c====3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "���� 61 �������� � ������� �����, ���� ��� ����� ���������� ��� ���������������� �����������", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "�� ������ ���� ������� ����� ������� � �������� � ����. ����� ������ ������� (��� ������ �� �������)", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "������� ���������, ��� ������ ���� �����. ����� ��� �� ���� ���� � ��� ���", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "������� ������ ���������� �������������� � ��������� ����, ������� �� ������ ������ �� ����� �������. (��� ����� ��������� �����-�� ������ �����������, ���� evernote, <a href='drive.google.com'>googleDoc</a>, or Dropbox.com)", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "���� �� ��� �� ������ �������� - ��������� ����! (��� �� ����� ��������� ������ �� ������ � �����, ������������� �������� �������� ��� ������ �� �������) ", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "���� ����� ������ ����� - ����� �� ����� ���-�� ������������ ����� � �������� ", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "������ ��� � ������� ������ - ���� ��� ������ � �������������� ��������� �����."+
	"� �����: ��� ������ ����� � ���� �����, ��� ������ ����� ������ �������� ���� �����!", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "���� �������� ������! ���� ��� �����������, ����� ���-�� ���������� � ����������.", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "��������� ���-��� ��� ������� � �������� ��������", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "�� ������� �� ��� � ���-����. ����� ����� �����. �� ������� ���������, ��������� � ��� �� ����", "c=3", R.drawable.icon_tongue),
			new NotificationTemplate("Hi man!", "���� �� ������, ����� � ���� ���� �������� ��������, ������ ��������� � ������", "c=3", R.drawable.icon_tongue),
	};
	
	public NotificationTemplate[] getNotifications() {
		return notifications;
	}

	public int getPreviousNotificationsCount() {
		return notifications.length;
	}


	public List<NotificationTemplate> getPreviousNotifications() {
		return Arrays.asList(notifications);
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
