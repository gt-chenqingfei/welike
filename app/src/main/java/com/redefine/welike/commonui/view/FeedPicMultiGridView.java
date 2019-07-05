package com.redefine.welike.commonui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.redefine.commonui.fresco.loader.SinglePicCrop;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.picturelooker.loader.SinglePicUrlLoader;
import com.redefine.welike.R;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;

/**
 * Created by liwenbo on 2018/3/17.
 */

public class FeedPicMultiGridView<T extends PicBaseAdapter> extends MultiGridView<T> {
    protected boolean mIsFourGridStyle = true;

    public FeedPicMultiGridView(Context context) {
        super(context);
    }

    public FeedPicMultiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedPicMultiGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FeedPicMultiGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int realWidth = 0;
        int realHeight = 0;
        if (mAdapter == null || mAdapter.getCount() == 0 || mRowChildCount == 0 || mMaxRowCount == 0) {
            setMeasuredDimension(realWidth, realHeight);
            return;
        }

        if (mAdapter.getCount() == 1) {
            // 处理单图显示
            realWidth = mAdapter.getItemWidth(0);
            realHeight = mAdapter.getItemHeight(0);
            SinglePicCrop.PicCropInfo rect;
            switch (ABTest.INSTANCE.check(ABKeys.TEST_IMAGE_DISPLAY)) {
                case 1:
                    rect = SinglePicCrop.getSinglePicShowRectTest(getContext(), realWidth, realHeight);
                    break;
                case 0:
                default:
                    rect = SinglePicCrop.getSinglePicShowRect(getContext(), realWidth, realHeight);
            }


            if (rect == null || rect.rectF.height() == 0 || rect.rectF.width() == 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            int tmpWidth = (int) rect.rectF.width() - marginTotalWidth;
            int tmpHeight = (int) rect.rectF.height();

            realWidth = tmpWidth - (getPaddingLeft() + getPaddingRight());

            realHeight = realWidth * tmpHeight / tmpWidth;

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.max(0, realWidth), MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.max(0, realHeight), MeasureSpec.EXACTLY);
            View view = getChildAt(0);
            view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            setMeasuredDimension(realWidth + getPaddingLeft() + getPaddingRight(), realHeight + getPaddingTop() + getPaddingBottom());
        } else if (mAdapter.getCount() == 2 || mAdapter.getCount() == 4) {
            if (mAdapter == null || mAdapter.getCount() == 0 || mRowChildCount == 0 || mMaxRowCount == 0) {
                setMeasuredDimension(realWidth, realHeight);
                return;
            }
            int urlSize = mAdapter.getCount();
            int size = Math.min(urlSize, getChildCount());
            int specWidthSize = ScreenUtils.getSreenWidth(getContext()) - marginTotalWidth;
            int itemSize = (specWidthSize - getPaddingLeft() - getPaddingRight() - mChildMargin) / 2 + 1;
            View view;
            realWidth = specWidthSize;
            int lineCount = ((size + mRowChildCount - 1) / mRowChildCount);
            realHeight = itemSize * lineCount + mChildMargin * (lineCount - 1) + getPaddingTop() + getPaddingBottom();
            int childMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.max(0, itemSize), MeasureSpec.EXACTLY);
            for (int i = 0; i < size; i++) {
                view = getChildAt(i);
                view.measure(childMeasureSpec, childMeasureSpec);
            }
            setMeasuredDimension(realWidth, realHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setFourGridStyle(boolean b) {
        mIsFourGridStyle = b;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter == null || mAdapter.getCount() == 0 || mRowChildCount == 0 || mMaxRowCount == 0) {
            return;
        }
        int urlSize = mAdapter.getCount();
        int size = Math.min(urlSize, getChildCount());


        // 针对于4个的并排显示
        if (size == 4 && mIsFourGridStyle) {
            layoutChildren(size, 2);
            return;
        }
        // 其余的顺序layout
        for (int i = 0; i < size; i++) {
            layoutChildren(size, mRowChildCount);
        }
    }
}
