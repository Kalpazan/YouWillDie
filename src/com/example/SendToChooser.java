package com.example;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.points.PointsController;

import java.util.ArrayList;
import java.util.List;

public class SendToChooser {


    private Activity activity;
    private String messageText;
    private PointsController pointsController;

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

        Dialog d = new Dialog(activity, R.style.shareDialogStyle);
        d.setTitle("+20 to your chans to live");
        d.setContentView(dialogView);
        d.show();
    }

    private List<ResolveInfo> resolveInfoFilter(List<ResolveInfo> infos) {
        List<ResolveInfo> filteredInfos = new ArrayList<ResolveInfo>();
        for (ResolveInfo resolveInfo : infos){
            String pckgName = resolveInfo.activityInfo.packageName;
              if (pckgName.contains("mms") || pckgName.contains("sms") ||pckgName.contains("twi")
                      || pckgName.contains("face") || pckgName.contains("vkont")|| pckgName.contains("gm")
                      || pckgName.contains("mail")|| pckgName.contains("skype")|| pckgName.contains("icq")
                      || pckgName.contains("mail")){
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
            title.setText(ri.activityInfo.loadLabel(mPm));

            return container;
        }
    }
}
