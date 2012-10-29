package com.example;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class PortraitViewFlipper extends ViewFlipper{

    public PortraitViewFlipper(Context context) {
        super(context);
    }
    public PortraitViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        }
        catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }
}
