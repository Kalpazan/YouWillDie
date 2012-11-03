package com.example.controller;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
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
	private SlidingDrawer slidingDrawer;
	
	private ViewFlipper viewFlipper;
	private Button helpButton;
	
	private NotificationProvider provider;
	
	String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><style>   body{    text-align:justify;}  </style><html><body>" +
			"";
	
	
	public MessageDisplayController(NotificationProvider notificationProvider, MainActivity activity) {
		msgText = (TextView) activity.findViewById(R.id.notification_text);
		icon = (ImageView) activity.findViewById(R.id.messageIcon);
		resources = activity.getResources();
		store = activity.getStore();
		viewFlipper = (ViewFlipper) activity.findViewById(R.id.view_flipper);
		helpButton = (Button) activity.findViewById(R.id.help_button);
		slidingDrawer = (SlidingDrawer) activity.findViewById(R.id.drawer);
		
		WebView helpText = (WebView) activity.findViewById(R.id.help_text);
		helpText.loadData(header +
				"<img src=\"file:///android_assets/icon_hipster.png\" />"+
				"<img src=\"file:///android_assets/Resources/icon_hipster.png\" />"+
				"<img src=\"file:/android_res/drawable/icon_drug.png\" />"+
				"<img src=\"file:/res/drawable/icon_drug.png\" />"+
				"<img src=\"res/drawable/icon_bat.png\" />"+
				"C ���� �� ����� ���������������� ������� �� ����������� ����� ���� (����� ������) "+
        
		"��� ��������� ������� �������� ���� ������ ������������. <img src=\"file:///android_res/drawable/icon_hipster.png\"/> ����� ������ �������� ���� ������ ��� ������ ��������� ����..	<br>����� ��������� ������ ���� �� ������ ��������� ���� ���������� ��������. ��-�� �������������� �������������� ������ �� �� ����� ���������� �� ���� ������� ����� 1�� ��������� � ����. ������ ��� ������! � �������� ��������� �� ��� �������. ���� ������� ��� � ����. ������������ 1� �������� ������ ����� ����������� ����� ����� � ������������ ������ ������� � ���������, ����� ������.", "text/html", null);
		helpText.setBackgroundColor(Color.TRANSPARENT);
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
	    	if (slidingDrawer.isOpened()) {
	    		slidingDrawer.animateClose();
	    	}
    	}
	}
	
    public void showMessageView() {
    	if (!isMessageViewActive() && canShowMessageView()) {
    		helpButton.setText("?");
	    	viewFlipper.showNext();
	    	if (slidingDrawer.isOpened()) {
	    		slidingDrawer.animateClose();
	    	}
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
