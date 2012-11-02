package com.example.points;

import static android.graphics.Color.TRANSPARENT;

import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.MainActivity;
import com.example.R;
import com.example.store.Store;

import java.util.Date;

public class PointsController {

    private static final int ANIMATION_DURATION = 2200;
    private TextView bonusText;
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView rank;

    private Store store;

    private static Level[] levels = {
            new Level("жертва", 0),
            new Level("житель", 21),
            new Level("стажер", 50),
            new Level("спасатель", 103),
            new Level("крепкий орешек", 170),
            new Level("герой", 210),
            new Level("агент МПСЖФ", 500)};

    public PointsController(MainActivity activity) {
        store = activity.getStore();

        progressText = (TextView) activity.findViewById(R.id.progressText);
        rank = (TextView) activity.findViewById(R.id.rank);
        bonusText = (TextView) activity.findViewById(R.id.bonus_points);
        bonusText.setTextColor(TRANSPARENT);

        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);

        if (store.wasPointsAddedOnCreate()) {
            addPoints(2);
            store.registerPointsAddingOnCreate();
        }
        update();
    }

    private void update() {
        int points = getCurrentPoints();

        int currentLevel = getCurrentLevel(points);
        int nextLevelPoints = maxLevel(currentLevel) ? points : levels[currentLevel + 1].pointsNeeded;

        progressText.setText(points + "/" + nextLevelPoints);

        // сам не понял
        points -= maxLevel(currentLevel) ? 0 : levels[currentLevel].pointsNeeded;
        nextLevelPoints -= maxLevel(currentLevel) ? 0 : levels[currentLevel].pointsNeeded;
        progressBar.setProgress((points * 100) / nextLevelPoints);

        rank.setText("вы " + levels[currentLevel].name + "!");
    }

    private boolean maxLevel(int currentLevel) {
        return currentLevel == levels.length - 1;
    }

    private int getCurrentPoints() {
        return store.getCurrentPoints();
    }

    public void addPoints(int bonus) {
        if (store.getCurrentPoints() < levels[levels.length - 1].pointsNeeded) {
            bonusText.setText("+" + bonus);
            bonusText.setTextColor(0xff00ff00);

            AlphaAnimation animation = new AlphaAnimation((float) 1, (float) 0);
            animation.setDuration(ANIMATION_DURATION);
            animation.setFillAfter(true);

//		ScaleAnimation scaleAnimation = new ScaleAnimation((float)1, (float)1.3, (float)1, (float)1.3, RELATIVE_TO_SELF, (float).5, RELATIVE_TO_SELF, (float).5);
//		scaleAnimation.setDuration(ANIMATION_DURATION);
//		scaleAnimation.setFillAfter(false);
//		
//		AnimationSet animationSet = new AnimationSet(true);
//		animationSet.addAnimation(animation);
//		animationSet.addAnimation(scaleAnimation);

            bonusText.startAnimation(animation);

            int points = getCurrentPoints();
            store.updatePoints(points + bonus);
            update();
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
