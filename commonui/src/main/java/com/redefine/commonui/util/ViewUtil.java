package com.redefine.commonui.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by liwenbo on 2018/4/12.
 */

public class ViewUtil {

    /**
     * 满屏检测，存在一次数据不满一屏的case暂时不使用
     * @return
     */
    public static boolean canScroll(RecyclerView mRecycler) {
        if (mRecycler == null) {
            return false;
        }
        int childCount = mRecycler.getChildCount();
        if (childCount == 0) {
            return false;
        }
        View lastChildView = mRecycler.getChildAt(childCount - 1);
        View firstChildView = mRecycler.getChildAt(0);
        int top = firstChildView.getTop();
        int bottom = lastChildView.getBottom();
        int bottomEdge = mRecycler.getHeight() - mRecycler.getPaddingBottom();
        int topEdge = mRecycler.getPaddingTop();
        if (bottom >= bottomEdge && top <= topEdge) {
            return true;
        }
        return false;
    }
}
