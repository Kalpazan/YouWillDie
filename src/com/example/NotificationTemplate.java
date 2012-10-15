package com.example;

public class NotificationTemplate {
	
	private String statusBarText;
	private String mainText;
	private String title;
	private int icon = R.drawable.icon_1;
	
	public NotificationTemplate(String title, String mainText, String statusBarText, int icon) {
		this.statusBarText = statusBarText;
		this.mainText = mainText;
		this.title = title;
		this.icon = icon;
	}

	public String getStatusBarText() {
		return statusBarText;
	}

	public String getMainText() {
		return mainText;
	}

	public String getTitle() {
		return title;
	}

	public int getIcon() {
		return icon;
	}

}
