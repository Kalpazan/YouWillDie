package com.example;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.points.PointsController;

public class SendToChooser {


    private Activity activity;
    private String messageText;
    private PointsController pointsController;
    private Dialog d;

    public SendToChooser(Activity activity, String messageText, PointsController pointsController) {
        this.pointsController=pointsController;
        this.messageText = messageText;
        this.activity = activity;
    }

    public void sendViaCustomChooser() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");

        PackageManager pm = activity.getPackageManager();

        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

        infos =  resolveInfoFilter(infos);

        View dialogView = View.inflate(activity, R.layout.custom_chooser, null);

        final ListView listView = (ListView)dialogView.findViewById(R.id.custom_chooser);
        AppAdapter adapter = new AppAdapter(pm, infos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                d.hide();
                pointsController.addPoints(20);
                ResolveInfo ri = (ResolveInfo) listView.getAdapter().getItem(position);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sample subject");
                intent.putExtra(Intent.EXTRA_TEXT, messageText);

                activity.startActivity(intent);

            }
        });

        d = new Dialog(activity, R.style.shareDialogStyle);
        d.setTitle(activity.getResources().getString(R.string.chooser_title));
        d.setContentView(dialogView);
        d.show();

    }

    private List<ResolveInfo> resolveInfoFilter(List<ResolveInfo> infos) {
        List<ResolveInfo> filteredInfos = new ArrayList<ResolveInfo>();
        for (ResolveInfo resolveInfo : infos){
            String pckgName = resolveInfo.activityInfo.packageName;
              if (pckgName.contains("mms") || pckgName.contains("sms") ||pckgName.contains("twi")
                      || pckgName.contains("face") || pckgName.contains("vkont")|| pckgName.contains("gm")
                      || pckgName.contains("mail")|| pckgName.contains("skype")|| pckgName.contains("icq")){
                  filteredInfos.add(resolveInfo);
              }
        }
        return filteredInfos;
    }

    private class AppAdapter extends BaseAdapter {
        List<ResolveInfo> mInfos;
        PackageManager mPm;


        private AppAdapter(PackageManager pm, List<ResolveInfo> infos) {
            this.mInfos = infos;
            this.mPm = pm;

        }

        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ResolveInfo ri = mInfos.get(position);

            if (convertView == null) {
                convertView = View.inflate(activity, R.layout.sendto_list, null);
            }

            ViewGroup container = (ViewGroup) convertView;

            ImageView image = (ImageView) container.findViewById(R.id.app_image);
            image.setImageDrawable(ri.activityInfo.loadIcon(mPm));

            TextView title = (TextView) container.findViewById(R.id.app_title);
            if (ri.activityInfo.packageName.contains("vkontakt")) {
                title.setText("Vkontakte");
            }  else {
            title.setText(ri.activityInfo.loadLabel(mPm));
            }
            return container;
        }
    }
}
