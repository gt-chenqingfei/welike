package com.redefine.multimedia.player.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 *   MODEL =ML-AL00 , HUAWEI P20
 *
 *   此机型在onPause 时三次调用onMeasure方法,最后一次measure的mMeasureHeight和mMeasureWidth是正确值
 *   ，onResume 时只调用一次 onMeasure，measure的mMeasureHeight和mMeasureWidth的值是屏幕宽高，这样会导致
 *   视频被拉伸 ，根据以上情况的规律得出的解决方案是把onPause最后一次 mMeasureWidth和
 *   mMeasureHeight 记录下来待onResume时候使用
 *   这并不是最优解决方案，如找到更好的方案，可替换
 */
public class VideoTextureView4HuaWei extends TextureView implements ITextureStrategy {

    boolean isPause;
    private int mVideoWidth;
    private int mVideoHeight;
    int mPausedMeasureTimes = 0;
    int mMeasureWidth = 0;
    int mMeasureHeight = 0;


    public VideoTextureView4HuaWei(Context context) {
        super(context);
    }

    public VideoTextureView4HuaWei(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoTextureView4HuaWei(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoTextureView4HuaWei(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onPause() {
        isPause = true;
    }

    @Override
    public void onResume() {
        isPause = false;
    }

    @Override
    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mVideoWidth == 0 || mVideoHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        onMeasureKeepAspectRatio(widthMeasureSpec, heightMeasureSpec);
    }

    private void onMeasureKeepAspectRatio(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = heightSpecSize;
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                }
            } else {
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        }

        if (mMeasureWidth > 0 && mMeasureHeight > 0 && !isPause) {
            width = mMeasureWidth;
            height = mMeasureHeight;

            mMeasureWidth = 0;
            mMeasureHeight = 0;
            mPausedMeasureTimes = 0;
        }

        if (isPause) {
            mPausedMeasureTimes++;
            if (mPausedMeasureTimes == 3) {
                mMeasureWidth = width;
                mMeasureHeight = height;
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public TextureView getTextureView() {
        return this;
    }


}
