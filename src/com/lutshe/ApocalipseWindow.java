package com.lutshe;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ApocalipseWindow {

    private Activity activity;
    private View apocalipseView;
    private TextView apocalipseText1;
    private ImageView apocalipseImg1;
    private Button apocalipseButton;


    public ApocalipseWindow(Activity activity) {
        this.activity = activity;
        apocalipseView = View.inflate(activity, R.layout.apocalipse_window, null);
        apocalipseText1 = (TextView) apocalipseView.findViewById(R.id.apocalipse_text1);
        apocalipseButton = (Button) apocalipseView.findViewById(R.id.apocalipse_button);
    }

    public void load() {
        final Dialog d = new Dialog(activity, R.style.apocalipseDialogStyle);
        apocalipseText1.setText("Dот конец света как бы");
        apocalipseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.cancel();
            }
        });

        d.setContentView(apocalipseView);
        d.show();
    }
}
