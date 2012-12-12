package com.lutshe;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import com.bugsense.trace.BugSenseHandler;
import com.lutshe.R;

public class ApocalipseWindow {

    private Activity activity;
    private View apocalipseView;
    private WebView apocalipseText;
    private Button apocalipseButton;


    public ApocalipseWindow(Activity activity) {
        this.activity = activity;
        apocalipseView = View.inflate(activity, R.layout.apocalipse_window, null);
        apocalipseText = (WebView) apocalipseView.findViewById(R.id.apocalipse_text);
        apocalipseButton = (Button) apocalipseView.findViewById(R.id.apocalipse_button);
    }

    public void load() {
        final Dialog d = new Dialog(activity, R.style.shareDialogStyle);
        apocalipseText.loadData("Вот конец света", "text/html", "UTF-8");
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
