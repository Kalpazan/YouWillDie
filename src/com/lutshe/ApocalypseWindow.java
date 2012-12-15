package com.lutshe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import java.lang.reflect.Method;

public class ApocalypseWindow {

    private Activity activity;
    private View apocalypseView;
    private Button closeApocalypseButton;
    private Button mailApocalypseButton;


    public ApocalypseWindow(Activity activity) {
        this.activity = activity;
        apocalypseView = View.inflate(activity, R.layout.apocalypse_window, null);
        closeApocalypseButton = (Button) apocalypseView.findViewById(R.id.close_apocalypse_button);
        mailApocalypseButton = (Button) apocalypseView.findViewById(R.id.mail_apocalypse_button);
    }

    public void load() {
        final Dialog d = new Dialog(activity, R.style.apocalypseDialogStyle);
        closeApocalypseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.cancel();
            }
        });
        mailApocalypseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"lutshe.studio@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.mail_subject));
                try {
                    activity.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    ((MainActivity) activity).showMessage(activity.getString(R.string.no_mail_client));
                }

            }
        });
        d.setContentView(apocalypseView);
        overScrollSetting();
        d.show();
    }

    private void overScrollSetting() {
        // find scroll view
        Log.d("scrollView", "start point");
        ScrollView messageScroll = (ScrollView) apocalypseView.findViewById(R.id.apocalipse_scroll_view);

        Log.d("scrollView", "scroll init");
        try {
            // look for setOverScrollMode method
            Method setOverScroll = messageScroll.getClass().getMethod("setOverScrollMode", new Class[]{Integer.TYPE});

            if (setOverScroll != null) {
                try {
                    // if found call it (OVER_SCROLL_NEVER == 2)
                    setOverScroll.invoke(messageScroll, 2);

                } catch (Exception ite) {
                    Log.d("scrollView", "set overScroll failed");
                }
            } else Log.d("scrollView", "set overScroll ==null");
        } catch (NoSuchMethodException nsme) {
            Log.d("scrollView", "searching method failed");
        }
    }
}
