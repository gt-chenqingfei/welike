package com.redefine.commonui.widget.shadowlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by liwenbo on 2018/2/22.
 */

public class ShadowLinearLayout extends LinearLayout {

    private ShadowDrawDelegate mDrawShadowDelegate;

    public ShadowLinearLayout(Context context) {
        super(context);
        init(null);
    }

    public ShadowLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ShadowLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ShadowLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    private void init(@Nullable AttributeSet attrs) {
        mDrawShadowDelegate = new ShadowDrawDelegate(getContext(), attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mDrawShadowDelegate.draw(this, canvas);
    }
}
