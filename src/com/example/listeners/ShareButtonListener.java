package com.example.listeners;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.MainActivity;
import com.example.controller.MessageDisplayController;
import com.example.points.PointsController;

public class ShareButtonListener implements OnClickListener {

	private MessageDisplayController messageDisplayController;
	private PointsController pointsController;
	
	public ShareButtonListener(MessageDisplayController messageDisplayController, PointsController pointsController) {
		this.messageDisplayController = messageDisplayController;
		this.pointsController = pointsController;
	}

	public void onClick(View v) {
		pointsController.addPoints(20);
        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.setAction(ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(EXTRA_TEXT, messageDisplayController.getCurrentMessage().getMainText());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        Intent chooserIntent = Intent.createChooser(intent, "Отправить другу");
        chooserIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        v.getContext().startActivity(chooserIntent);
	}
	
}
