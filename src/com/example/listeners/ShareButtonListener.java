package com.example.listeners;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.SendToChooser;
import com.example.controller.MessageDisplayController;
import com.example.points.PointsController;

public class ShareButtonListener implements OnClickListener {

	private MessageDisplayController messageDisplayController;
	private PointsController pointsController;
    private Activity activity;
	
	public ShareButtonListener(Activity activity, MessageDisplayController messageDisplayController, PointsController pointsController) {
		this.messageDisplayController = messageDisplayController;
		this.pointsController = pointsController;
        this.activity = activity;
	}

	public void onClick(View v) {
		pointsController.addPoints(20);

        SendToChooser sendToChooser = new SendToChooser(activity, messageDisplayController.getCurrentMessage().getMainText());
         sendToChooser.sendViaCustomChooser();
//        Intent intent = new Intent(v.getContext(), MainActivity.class);
//        intent.setAction(ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(EXTRA_TEXT, messageDisplayController.getCurrentMessage().getMainText());
//        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//        Intent chooserIntent = Intent.createChooser(intent, "��������� �����");
//        chooserIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//        v.getContext().startActivity(chooserIntent);
	}
	
}
