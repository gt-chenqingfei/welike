package com.redefine.welike.business.startup.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;

public class TextProgress extends ProgressBar {

    private Context mContext;
    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;
    private float mProgress;
    private int mState;

    // IconTextProgressBar的状态
    private static final int STATE_DEFAULT = 101;
    private static final int STATE_DOWNLOADING = 102;
    private static final int STATE_PAUSE = 103;
    private static final int STATE_DOWNLOAD_FINISH = 104;
    // IconTextProgressBar的文字大小(sp)
    private static final float TEXT_SIZE_SP = 18f;

    public TextProgress(Context context) {
        super(context, null, android.R.attr.progressBarStyleHorizontal);
        mContext = context;
        init();
    }

    public TextProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * 设置下载状态
     */
    public synchronized void setState(int state) {
        mState = state;
        invalidate();
    }

    /**
     * 设置下载进度
     */
    public synchronized void setProgress(float progress) {
        super.setProgress((int) progress);
        mProgress = progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mState) {
            case STATE_DEFAULT:
                drawIconAndText(canvas, STATE_DEFAULT, false);
                break;

            case STATE_DOWNLOADING:
                drawIconAndText(canvas, STATE_DOWNLOADING, false);
                break;

            case STATE_PAUSE:
                drawIconAndText(canvas, STATE_PAUSE, false);
                break;

            case STATE_DOWNLOAD_FINISH:
                drawIconAndText(canvas, STATE_DOWNLOAD_FINISH, true);
                break;

            default:
                drawIconAndText(canvas, STATE_DEFAULT, false);
                break;
        }
    }

    private void init() {
        setIndeterminate(false);
        setIndeterminateDrawable(ContextCompat.getDrawable(mContext,
                android.R.drawable.progress_indeterminate_horizontal));
        setProgressDrawable(ContextCompat.getDrawable(mContext,
                R.drawable.regist_loading_background));
        setMax(100);

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(ScreenUtils.sp2px(mContext, TEXT_SIZE_SP));
        mPaint.setTypeface(Typeface.MONOSPACE);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    private void initForState(int state) {
        switch (state) {
            case STATE_DEFAULT:
                setProgress(100);
                mPaint.setColor(Color.WHITE);
                break;

            case STATE_DOWNLOADING:
                mPaint.setColor(ContextCompat.getColor(mContext, R.color.regist_nick_name_next));
                break;

            case STATE_PAUSE:
                mPaint.setColor(ContextCompat.getColor(mContext, R.color.regist_nick_name_next));
                break;

            case STATE_DOWNLOAD_FINISH:
                setProgress(100);
                mPaint.setColor(Color.WHITE);
                break;

            default:
                setProgress(100);
                mPaint.setColor(Color.WHITE);
                break;
        }
    }

    private void drawIconAndText(Canvas canvas, int state, boolean onlyText) {
        initForState(state);

        String text = getText(state);
        Rect textRect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textRect);

        if (onlyText) {
            // 仅绘制文字
            float textX = (getWidth() / 2) - textRect.centerX();
            float textY = (getHeight() / 2) - textRect.centerY();
            canvas.drawText(text, textX, textY, mPaint);
        } else {
            // 绘制图标和文字
            Bitmap icon = getIcon(state);

            float textX = getWidth() / 2 - textRect.centerX();

            float textY = (getHeight() / 2) - textRect.centerY();
            canvas.drawText(text, textX, textY, mPaint);

            if (state == STATE_DEFAULT) return;

            Bitmap bufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bufferCanvas = new Canvas(bufferBitmap);
            bufferCanvas.drawText(text, textX, textY, mPaint);
            // 设置混合模式
            mPaint.setXfermode(mPorterDuffXfermode);
            mPaint.setColor(getResources().getColor(R.color.regist_yellow));
            RectF rectF = new RectF(0, 0, getWidth() * mProgress / 100, getHeight());
            // 绘制源图形
            bufferCanvas.drawRect(rectF, mPaint);
            // 绘制目标图
            canvas.drawBitmap(bufferBitmap, 0, 0, null);
            // 清除混合模式
            mPaint.setXfermode(null);

            if (!icon.isRecycled()) {
                icon.isRecycled();
            }
            if (!bufferBitmap.isRecycled()) {
                bufferBitmap.recycle();
            }
        }
    }

    private Bitmap getIcon(int state) {
        Bitmap icon;
        switch (state) {
            case STATE_DEFAULT:
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit);
                break;

            case STATE_DOWNLOADING:
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit);

                break;

            case STATE_PAUSE:
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit);
                break;

            default:
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit);
                break;
        }
        return icon;
    }

    private String getText(int state) {
        return "";
    }

}
