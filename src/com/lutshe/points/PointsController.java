package com.lutshe.points;

import static android.graphics.Color.TRANSPARENT;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lutshe.MainActivity;
import com.lutshe.R;
import com.lutshe.controller.RateViewController;
import com.lutshe.store.Store;

public class PointsController {

    private static final int ANIMATION_DURATION = 2200;
    private TextView bonusText;
    private ProgressBar progressBar;
    private TextView progressText;
    private MainActivity activity;
    private ImageView rank;

    private Store store;

    private static Level[] levels = {
            new Level(R.drawable.ranc_1, 0),
            new Level(R.drawable.ranc_2, 25),
            new Level(R.drawable.ranc_3, 80),
            new Level(R.drawable.ranc_4, 150),
            new Level(R.drawable.ranc_5, 220),
            new Level(R.drawable.ranc_7, 310),
            new Level(R.drawable.ranc_6, 400),
            new Level(R.drawable.ranc_8, 500)
            };

    public PointsController(MainActivity activity) {
        store = activity.getStore();

        this.activity = activity;
        progressText = (TextView) activity.findViewById(R.id.progressText);
        bonusText = (TextView) activity.findViewById(R.id.bonus_points);
        bonusText.setTextColor(TRANSPARENT);
        rank = (ImageView) activity.findViewById(R.id.pagon);
        
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);

        update();
    }

    private void update() {
        int points = getCurrentPoints();

        int currentLevel = getCurrentLevel(points);
        int nextLevelPoints = maxLevel(currentLevel) ? points : levels[currentLevel + 1].pointsNeeded;

        progressText.setText(points + "/" + nextLevelPoints);

        points -= maxLevel(currentLevel) ? 0 : levels[currentLevel].pointsNeeded;
        nextLevelPoints -= maxLevel(currentLevel) ? 0 : levels[currentLevel].pointsNeeded;
        progressBar.setProgress((points * 100) / nextLevelPoints);
        rank.setImageDrawable(activity.getResources().getDrawable(levels[currentLevel].imageId));
    }

    private boolean maxLevel(int currentLevel) {
        return currentLevel == levels.length - 1;
    }

    private int getCurrentPoints() {
        return store.getCurrentPoints();
    }

    public void addPoints(int bonus) {
        int currentPoints = store.getCurrentPoints();
        int lastLevel = levels.length-1;

        if (currentPoints < levels[lastLevel].pointsNeeded) {
            bonusText.setText("+" + bonus);
            bonusText.setTextColor(0xffFAF398);

            AlphaAnimation animation = new AlphaAnimation((float) 1, (float) 0);
            animation.setDuration(ANIMATION_DURATION);
            animation.setFillAfter(true);

            bonusText.startAnimation(animation);

            int points = getCurrentPoints();
            store.updatePoints(points + bonus);
            update();
        } 
        
        int currentLevel = getCurrentLevel(currentPoints);
		
        if (currentLevel != lastLevel && currentPoints + bonus > levels[currentLevel+1].pointsNeeded) {
        	switch (currentLevel+1) {
			case 2:
			case 3:
			case 4:
				RateViewController rateController = activity.getRateViewController();
				rateController.showRateView(true);
			}
        }
    }

    private int getCurrentLevel(int points) {
        for (int i = 1; i < levels.length; i++) {
            if (points < levels[i].pointsNeeded) {
                return i - 1;
            }
        }
        return levels.length - 1;
    }
}
