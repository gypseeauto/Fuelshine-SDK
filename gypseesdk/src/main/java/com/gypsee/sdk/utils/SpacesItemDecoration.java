package com.gypsee.sdk.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {


        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) / 2 == 0) {
            outRect.right = space;
        } else {
            outRect.right = 0;
        }
    }
}
