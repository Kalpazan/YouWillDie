package com.example.controller;

import android.content.res.Resources;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.MainActivity;
import com.example.NotificationProvider;
import com.example.NotificationTemplate;
import com.example.R;
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
	private WebView helpText;
	
	private ScrollView messageScrollView;
	
	private NotificationProvider provider;
	
	private String header;
	
	
	public MessageDisplayController(NotificationProvider notificationProvider, MainActivity activity) {
		msgText = (TextView) activity.findViewById(R.id.notification_text);
		icon = (ImageView) activity.findViewById(R.id.messageIcon);
		resources = activity.getResources();
		store = activity.getStore();
		viewFlipper = (ViewFlipper) activity.findViewById(R.id.view_flipper);
		helpButton = (Button) activity.findViewById(R.id.help_button);
		slidingDrawer = (SlidingDrawer) activity.findViewById(R.id.drawer);
		header = activity.getResources().getString(R.string.html_header);
		messageScrollView = (ScrollView) activity.findViewById(R.id.message_scroll_view);
		
		helpText = (WebView) activity.findViewById(R.id.help_text);
		helpText.setVerticalScrollBarEnabled(false);
//		helpText.setBackgroundColor(Color.TRANSPARENT);
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
		messageScrollView.scrollTo(0, 0);
		icon.setImageDrawable(resources.getDrawable(currentMessage.getIcon()));
	}

	public void showHelpView() {
		showWebView(resources.getString(R.string.help_text));
	}
	
	public void showGreetingView() {
    	helpText.loadDataWithBaseURL(null, header + resources.getString(R.string.greeting_text), "text/html", "UTF-8", null);
    		
		if (slidingDrawer.isOpened()) {
			slidingDrawer.animateClose();
		}
	}
	
	private void showWebView(String htmlText) {
		if (isMessageViewActive()) {
    		helpText.loadDataWithBaseURL(null, header + htmlText, "text/html", "UTF-8", null);
    		
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
    		showGreetingView();
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
