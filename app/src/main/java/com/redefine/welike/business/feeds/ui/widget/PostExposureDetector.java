package com.redefine.welike.business.feeds.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by nianguowang on 2018/10/18
 */
public class PostExposureDetector extends RecyclerView.OnScrollListener {


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        calculate(recyclerView);
    }

    private void calculate(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            for (int i = firstVisibleItemPosition ; i <= lastVisibleItemPosition ; i++) {
                View indexView = layoutManager.getChildAt(i - firstVisibleItemPosition);
                if (indexView == null) {
                    continue;
                }
                int height = indexView.getHeight();
                Rect rect = new Rect();
                indexView.getLocalVisibleRect(rect);
                if (rect.bottom > 0) {
                    int showHeight = rect.bottom - rect.top;
                    if (showHeight >= (height * 2) / 3) {

                    }
                }
            }
        }
    }
}
