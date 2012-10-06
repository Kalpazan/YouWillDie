package com.example;

public class NotificationProvider {

	private NotificationTemplate[] notifications = {
			new NotificationTemplate("Hi man!", "Bla Bla blabla bla", "c=3", R.drawable.icon_1),
			new NotificationTemplate("New have a new message, mister!", "This is a very good program", "c==3", R.drawable.icon_2),
			new NotificationTemplate("Noooooooooooooooo...", "...oooooooooooo!!!11", "c===3", R.drawable.icon_1),
			new NotificationTemplate("Good advice:", "Be a nice boy today", "c====3", R.drawable.icon_2),
			new NotificationTemplate("Click to see tits!", "(*)(.)", "c====3", R.drawable.icon_2),
	};
	
	
	public NotificationTemplate[] getNotifications() {
		return notifications;
	}
	
}
