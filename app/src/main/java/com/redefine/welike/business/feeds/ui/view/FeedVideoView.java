package com.redefine.welike.business.feeds.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by gongguan on 2018/1/30.
 */

public class FeedVideoView extends VideoView {
    public FeedVideoView(Context context) {
        super(context);
    }

    public FeedVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
