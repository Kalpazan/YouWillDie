package com.lutshe;


public class NotificationTemplate {
	
	private String statusBarText;
	private String mainText;
	private String title;
    private String link;
	private int icon = R.drawable.icon_tongue;

	public NotificationTemplate(String link, String title, String mainText, int icon) {
        this.link = link;
		this.statusBarText = title;
		this.mainText = mainText;
		this.title = title;
		this.icon = icon;
	}

	public String getStatusBarText() {
		return statusBarText;
	}

    public String getLink() {
        return link;
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
