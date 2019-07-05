package com.redefine.welike.business.startup.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import com.redefine.welike.R;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Created by nianguowang on 2018/4/20
 */
public class WelikeIndiaTextView extends AppCompatTextView {
    public WelikeIndiaTextView(Context context) {
        super(context);
        init();
    }

    public WelikeIndiaTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WelikeIndiaTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setText(getWelikeIndiaSpannable());
    }

    private Spannable getWelikeIndiaSpannable() {
        String welike = getResources().getString(R.string.app_name);
        String india = getResources().getString(R.string.india);
        Spannable welikeIndia = new SpannableString(welike + "   " + india + "  ");
        Drawable likeDrawable = getResources().getDrawable(R.drawable.ic_like);
        likeDrawable.setBounds(0,0, DensityUtil.dp2px(14), DensityUtil.dp2px(12));
        ImageSpan likeSpan = new VerticalImageSpan(likeDrawable);
        Drawable indiaDrawable = getResources().getDrawable(R.drawable.ic_india);
        indiaDrawable.setBounds(0,0,DensityUtil.dp2px(17), DensityUtil.dp2px(12));
        ImageSpan indiaSpan = new VerticalImageSpan(indiaDrawable);
        welikeIndia.setSpan(likeSpan, welike.length() + 1,welike.length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        welikeIndia.setSpan(indiaSpan, welikeIndia.length() - 1,welikeIndia.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return welikeIndia;
    }

    public class VerticalImageSpan extends ImageSpan {

        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fontMetricsInt.ascent = -bottom;
                fontMetricsInt.top = -bottom;
                fontMetricsInt.bottom = top;
                fontMetricsInt.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            int transY = 0;
            //获得将要显示的文本高度-图片高度除2等居中位置+top(换行情况)
            transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }
}
