package com.example.controller;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.*;
import com.example.store.Store;

public class MessageDisplayController {
	
	private static final int MESSAGE_VIEW_ID = 1;
	private NotificationTemplate currentMessage;
	private TextView msgText;
	private ImageView icon;
	private Resources resources;
	private Store store;
	
	private ViewFlipper viewFlipper;
	private Button helpButton;
	
	private NotificationProvider provider;
	
	
	public MessageDisplayController(NotificationProvider notificationProvider, MainActivity activity) {
		msgText = (TextView) activity.findViewById(R.id.notification_text);
		icon = (ImageView) activity.findViewById(R.id.messageIcon);
		resources = activity.getResources();
		store = activity.getStore();
		viewFlipper = (ViewFlipper) activity.findViewById(R.id.view_flipper);
		helpButton = (Button) activity.findViewById(R.id.help_button);
		provider = notificationProvider;
	}

	public void setCurrentMessage(NotificationTemplate message) {
		helpButton.setVisibility(View.VISIBLE);
		currentMessage = message;
		updateView();
	}
	
	public NotificationTemplate getCurrentMessage() {
		return currentMessage;
	}
	
	public boolean hasMessage() {
		return currentMessage != null;
	}
	
	private void updateView() {
		msgText.setText(currentMessage.getMainText());
		icon.setImageDrawable(resources.getDrawable(currentMessage.getIcon()));
	}

	public void showHelpView() {
    	if (isMessageViewActive()) {
    		helpButton.setText("Ok");
	    	viewFlipper.showNext();
    	}
	}
	
    public void showMessageView() {
    	if (!isMessageViewActive() && canShowMessageView()) {
    		helpButton.setText("?");
	    	viewFlipper.showNext();
    	}
	}
    
    private boolean canShowMessageView() {
    	return store.getLastNotificationNumber() != -1;
	}
    
    private boolean isMessageViewActive() {
    	return viewFlipper.getDisplayedChild() == MESSAGE_VIEW_ID;
	}
	
    public void init() {
    	if(canShowMessageView()) {
    		if (!hasMessage()) {
    			int id = store.getLastNotificationNumber();
    			setCurrentMessage(provider.getNotification(id));
    		} 
    		
    		showMessageView();
    	} else {
    		helpButton.setVisibility(View.INVISIBLE);
    	}
	}
    
    public void flipViews() {
    	if (isMessageViewActive()) {
    		showHelpView();
    	} else {
    		showMessageView();
    	}
    }
}
