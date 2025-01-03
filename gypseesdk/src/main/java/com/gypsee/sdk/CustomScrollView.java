package com.gypsee.sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Prevent scrolling when touching the map
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isTouchOnMap(ev)) {
                return false; // Don't intercept touch events on the map
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isTouchOnMap(MotionEvent ev) {
        // Check if the touch event is within the bounds of the map
        View mapView = findViewById(R.id.map);
        if (mapView != null) {
            int[] location = new int[2];
            mapView.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            return ev.getRawX() >= x && ev.getRawX() <= (x + mapView.getWidth()) &&
                    ev.getRawY() >= y && ev.getRawY() <= (y + mapView.getHeight());
        }
        return false;
    }
}


