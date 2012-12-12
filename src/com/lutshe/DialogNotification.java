package com.lutshe;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bugsense.trace.BugSenseHandler;

public class DialogNotification {

    private Activity activity;
    private String text;
    private String btnTxt;
    private View dialogView;
    private TextView dialogText;
    private Button dialogButton;


    public DialogNotification(Activity activity, String btnTxt, String text) {
        this.activity = activity;
        BugSenseHandler.initAndStartSession(activity, "3d42042b");
        this.btnTxt = btnTxt;
        this.text = text;
        dialogView = View.inflate(activity, R.layout.dialog, null);
        dialogText = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogButton = (Button) activity.findViewById(R.id.dialog_button);


    }

    public void load() {
         try {
        dialogButton.setText(btnTxt); } catch (Exception e){
             BugSenseHandler.sendException(e);
         }
        dialogText.setText(text);
        Dialog d = new Dialog(activity, R.style.shareDialogStyle);
        d.setContentView(dialogView);
        d.show();
    }
}
