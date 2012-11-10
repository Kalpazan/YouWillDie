package com.lutshe.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.lutshe.MainActivity;
import com.lutshe.NotificationProvider;
import com.lutshe.NotificationTemplate;
import com.lutshe.R;
import com.lutshe.store.Store;

public class MessageDisplayController {
	
	private static final int MESSAGE_VIEW_ID = 1;

	private static final int MESSAGE = 1;
	private static final int HELP = 2;
	private static final int GREETING = 3;
	
	private NotificationTemplate currentMessage;
	private TextView msgText;
	private ImageView icon;
	private Resources resources;
	private Store store;
	private SlidingDrawer slidingDrawer;
	
	private int currentView = 0;
	
	private ViewFlipper viewFlipper;
	private Button helpButton;
	private WebView helpText;
	private Button linkBtn;
	private ScrollView messageScrollView;
	
	private NotificationProvider provider;
	private Activity activity;
	private String header;
	private boolean helpLoaded;
	
	
	private Drawable questionMarkBg;
	private Drawable closeBg;
	
	public MessageDisplayController(NotificationProvider notificationProvider, MainActivity activity) {
        this.activity = activity;
		msgText = (TextView) activity.findViewById(R.id.notification_text);
		icon = (ImageView) activity.findViewById(R.id.messageIcon);
		resources = activity.getResources();
		store = activity.getStore();
		viewFlipper = (ViewFlipper) activity.findViewById(R.id.view_flipper);
		helpButton = (Button) activity.findViewById(R.id.help_button);
		slidingDrawer = (SlidingDrawer) activity.findViewById(R.id.drawer);
		header = activity.getResources().getString(R.string.html_header);
		messageScrollView = (ScrollView) activity.findViewById(R.id.message_scroll_view);
		linkBtn = (Button)activity.findViewById(R.id.link_button);
        linkBtn.setVisibility(View.INVISIBLE);
		helpText = (WebView) activity.findViewById(R.id.help_text);
		helpText.setVerticalScrollBarEnabled(false);
		provider = notificationProvider;
		
		questionMarkBg = resources.getDrawable(R.drawable.vopros);
		closeBg = resources.getDrawable(R.drawable.x);
	}

	public void setCurrentMessage(final NotificationTemplate message) {
        if (message.getLink().equals("")){
            linkBtn.setVisibility(View.INVISIBLE);
        } else {
            linkBtn.setVisibility(View.VISIBLE);
            linkBtn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getLink()));
                    activity.startActivity(browserIntent);
                }
            });
        }
		helpButton.setVisibility(View.VISIBLE);
		currentMessage = message;
		updateView();
		if (!helpLoaded) {
			loadHelp(resources.getString(R.string.help_text));
			helpLoaded = true;
		}
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
		currentView = HELP;
	}
	
	public void showGreetingView() {
		helpText.loadDataWithBaseURL(null, header + resources.getString(R.string.greeting_text), "text/html", "UTF-8", null);
		helpLoaded = false;
		if (currentView == MESSAGE) {
			viewFlipper.showNext();
		}
		helpButton.setBackgroundDrawable(closeBg);
		currentView = GREETING;
    	if (slidingDrawer.isOpened()) {
    		slidingDrawer.animateClose();
    	}
	}
	
	private void showWebView(String htmlText) {
		if (isMessageViewActive() || currentView == GREETING) {
			if (!helpLoaded) {
				loadHelp(htmlText);
				helpLoaded = true;
			}
			helpButton.setBackgroundDrawable(closeBg);			
    		if (currentView == MESSAGE) {
    			viewFlipper.showNext();
    		}
	    	if (slidingDrawer.isOpened()) {
	    		slidingDrawer.animateClose();
	    	}
    	}
	}

	private void loadHelp(String htmlText) {
		helpText.loadDataWithBaseURL(null, header + htmlText, "text/html", "UTF-8", null);
		helpLoaded = true;
	}
	
    public void showMessageView() {
    	if (!isMessageViewActive() && canShowMessageView()) {
    		helpButton.setBackgroundDrawable(questionMarkBg);
    		viewFlipper.showNext();
	    	if (slidingDrawer.isOpened()) {
	    		slidingDrawer.animateClose();
	    	}

	    	if (!helpLoaded) {
	    		loadHelp(resources.getString(R.string.help_text));
	    	}
	    	currentView = MESSAGE;
    	}
	}
    
    private boolean canShowMessageView() {
    	return store.getLastNotificationNumber() > 0;
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
    	if (currentView == GREETING) {
    		if (canShowMessageView()) {
    			showMessageView();
    		} 
    	} else if (isMessageViewActive()){
    		showHelpView();
    	} else {
    		showMessageView();
    	}
    }
}
