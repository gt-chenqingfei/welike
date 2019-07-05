package com.redefine.richtext.loader;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.block.Block;
import com.redefine.richtext.span.DraweeSpan;
import com.redefine.richtext.span.OnRichItemClickListener;

/**
 * Name: InnerImageLoader
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-11 21:08
 */
public class InnerImageLoader {

    private OnRichItemClickListener mListener;

    private final int DEFAULT_MARGIN_WIDTH = dip2Px(12);
    private boolean isEnable = true;

    public InnerImageLoader() {

    }

    public InnerImageLoader(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public Spannable parseRichContent(Context context, Block b) {
        if (b == null || !isEnable) {
            return new SpannableStringBuilder();
        }
        if (TextUtils.isEmpty(b.blockText) || b.richItem == null || !b.richItem.isInnerImageItem()) {
            return new SpannableStringBuilder(b.blockText);
        }
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source: b.richItem.display;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);

        if (b.richItem.width == 0 || b.richItem.height == 0) {
            b.richItem.width = dip2Px(200);
            b.richItem.height = dip2Px(200);
        }
        DraweeSpan span = new DraweeSpan(context, b,getScreenWidth(context) - DEFAULT_MARGIN_WIDTH * 2, mListener);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2Px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void enable(boolean b) {
        isEnable = b;
    }
}
