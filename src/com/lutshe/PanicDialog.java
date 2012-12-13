package com.lutshe;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bugsense.trace.BugSenseHandler;

public class PanicDialog {

    private Activity activity;
    private String text;
    private String btnTxt;
    private View dialogView;
    private TextView dialogText;
    private Button dialogButton;


    public PanicDialog(Activity activity, String text, String btnTxt) {
        this.activity = activity;
        BugSenseHandler.initAndStartSession(activity, "3d42042b");
        this.btnTxt = btnTxt;
        this.text = text;
        dialogView = View.inflate(activity, R.layout.dialog, null);
        dialogText = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);


    }

    public void load() {
        final Dialog d = new Dialog(activity, R.style.apocalipseDialogStyle);
        dialogButton.setText(btnTxt);
        dialogText.setText(text);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.cancel();
            }
        });

        d.setContentView(dialogView);
        d.show();
    }
}
