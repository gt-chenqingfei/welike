package com.redefine.welike.commonui.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

/**
 * Created by liwenbo on 2018/3/2.
 */

public class FeedClassicsHeader extends ClassicsHeader {
    private int mRefreshCount;

    public FeedClassicsHeader(Context context) {
        super(context);
    }

    public FeedClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FeedClassicsHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);
        REFRESH_HEADER_LOADING = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "refresh_header_refreshing");
        REFRESH_HEADER_FINISH = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_refresh_header_finish_new_posts");
        REFRESH_HEADER_ERROR = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "refresh_header_failed");
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
        } else {
            Drawable drawable = mProgressView.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).stop();
            } else {
                mProgressView.animate().rotation(0).setDuration(300);
            }
        }
        mProgressView.setVisibility(GONE);
        if (success) {
            mResultView.setVisibility(VISIBLE);
            mResultView.setImageResource(R.drawable.feed_refresh_header_success);
            if (mRefreshCount > 0) {
                mTitleText.setText(mRefreshCount + " " + REFRESH_HEADER_FINISH);
            } else {
                mTitleText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_refresh_header_finish_none"));
            }
        } else {
            mResultView.setVisibility(VISIBLE);
            mResultView.setImageResource(R.drawable.feed_refresh_header_failed);
            mTitleText.setText(REFRESH_HEADER_ERROR);
        }
        return mFinishDuration;//延迟500毫秒之后再弹回
    }

    public void setRefreshCount(int size) {
        mRefreshCount = size;
    }
}
