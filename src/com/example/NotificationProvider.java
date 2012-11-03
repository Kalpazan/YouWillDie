package com.example;

import java.util.Arrays;
import java.util.List;

import android.content.res.Resources;
import android.content.res.TypedArray;

public class NotificationProvider {

//	private NotificationTemplate[] notifications = {
//			new NotificationTemplate("���", "�������� � ����������: �� � ��� �����. �������� - ����� ����", "������", R.drawable.icon_tongue),
//			new NotificationTemplate("����!", "����� ������ ������� ������ � �������� �� ��������. ���������� ��������� ���� ������� ����� �������. ������ ���� ������� ��������� ���������� �� ������", "�����", R.drawable.icon_tongue),
//			new NotificationTemplate("�������", "�����������... �� ���� ���� �� ����� �����. ��������: ���� ���������� �� ��� �� ��������", ":)", R.drawable.icon_tongue),
//			new NotificationTemplate("Click to see tits!", "(*)(.)", "c====3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "���� 61 �������� � ������� �����, ���� ��� ����� ���������� ��� ���������������� �����������", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "�� ������ ���� ������� ����� ������� � �������� � ����. ����� ������ ������� (��� ������ �� �������)", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "������� ���������, ��� ������ ���� �����. ����� ��� �� ���� ���� � ��� ���", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "������� ������ ���������� �������������� � ��������� ����, ������� �� ������ ������ �� ����� �������. (��� ����� ��������� �����-�� ������ �����������, ���� evernote, <a href='drive.google.com'>googleDoc</a>, or Dropbox.com)", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "���� �� ��� �� ������ �������� - ��������� ����! (��� �� ����� ��������� ������ �� ������ � �����, ������������� �������� �������� ��� ������ �� �������) ", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "���� ����� ������ ����� - ����� �� ����� ���-�� ������������ ����� � �������� ", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "������ ��� � ������� ������ - ���� ��� ������ � �������������� ��������� �����. � �����: ��� ������ ����� � ���� �����, ��� ������ ����� ������ �������� ���� �����!", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "���� �������� ������! ���� ��� �����������, ����� ���-�� ���������� � ����������.", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "��������� ���-��� ��� ������� � �������� ��������", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "�� ������� �� ��� � ���-����. ����� ����� �����. �� ������� ���������, ��������� � ��� �� ����", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "���� �� ������, ����� � ���� ���� �������� ��������, ������ ��������� � ������", "c=3", R.drawable.icon_tongue),
//	};
	
	private NotificationTemplate[] notifications;
	
	public NotificationProvider(Resources resources) {
		String[] messages = resources.getStringArray(R.array.messages);
		TypedArray imgs = resources.obtainTypedArray(R.array.icons);
		
		notifications = new NotificationTemplate[messages.length];
		
		for (int i = 0; i < messages.length; i++) {
			String[] strings = messages[i].split("\\|");
			int icon = imgs.getResourceId(i, -1);
			if (strings.length == 2) {
				notifications[i] = new NotificationTemplate(strings[0], strings[1], "instruction for today", icon);
			} else {
				notifications[i] = new NotificationTemplate(strings[0].split(" ")[0], strings[0], "instruction for today", icon);
			}
		}
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
