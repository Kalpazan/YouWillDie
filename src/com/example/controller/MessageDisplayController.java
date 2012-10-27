package com.example.controller;

import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.MainActivity;
import com.example.NotificationProvider;
import com.example.NotificationTemplate;
import com.example.R;

public class MessageDisplayController {
	
	private NotificationTemplate currentMessage;
	private TextView msgText;
	private ImageView icon;
	private Resources resources;
	
	
	public MessageDisplayController(NotificationProvider notificationProvider, MainActivity activity) {
		msgText = (TextView) activity.findViewById(R.id.notification_text);
		icon = (ImageView) activity.findViewById(R.id.messageIcon);
		resources = activity.getResources();
	}

	public void setCurrentMessage(NotificationTemplate message) {
		currentMessage = message;
		updateView();
	}
	
	public NotificationTemplate getCurrentMessage() {
		return currentMessage;
	}
	
	public boolean hasMessage() {
		return currentMessage == null;
	}
	
	private void updateView() {
//		msgText.loadData(currentMessage.getMainText(), "text/html", "UTF-8");
		msgText.setText(currentMessage.getMainText());
		icon.setImageDrawable(resources.getDrawable(currentMessage.getIcon()));
	}

}
