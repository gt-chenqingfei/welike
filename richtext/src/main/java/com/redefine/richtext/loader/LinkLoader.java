package com.redefine.richtext.loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.R;
import com.redefine.richtext.span.CenterAlignImageSpan;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.richtext.span.RadiusBackgroundSpan;
import com.redefine.richtext.span.RichClickableSpan;

/**
 * Created by MR on 2018/1/23.
 */

public class LinkLoader {

    private OnRichItemClickListener mListener;
    private boolean isRoundRectLink = false;

    public LinkLoader() {

    }

    public LinkLoader(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public Spannable parseRichContent(Context context, Block b) {
        if (b == null) {
            return new SpannableStringBuilder();
        }
        if (TextUtils.isEmpty(b.blockText) || b.richItem == null || !b.richItem.isLinkItem()) {
            return new SpannableStringBuilder(b.blockText);
        }
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source : b.richItem.display;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);

        if (isRoundRectLink) {
            String linkStartFlag = context.getResources().getString(R.string.web_links_dot);
            if (target.startsWith(linkStartFlag)) {
                target = target.substring(1);
                b.richItem.display = target;
                b.blockText = target;
                if (target.endsWith(" ")) {
                    b.richItem.length = target.length() - 1;
                } else {
                    b.richItem.length = target.length();
                }
                spannableStringBuilder = new SpannableStringBuilder(target);
            }
            Drawable drawable = context.getResources().getDrawable(R.drawable.white_weblink_icon);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            RadiusBackgroundSpan span = new RadiusBackgroundSpan(drawable, ScreenUtils.dip2Px(4), b.copy(), mListener);
            spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            String linkStartFlag = context.getResources().getString(R.string.web_links_dot);
            if (target.startsWith(linkStartFlag)) {
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_link_link);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable);
                spannableStringBuilder.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            RichClickableSpan span = new RichClickableSpan(b.copy(), mListener);
            spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }


    public Spannable parseRichContentWidthoutIcon(Context context, Block b) {
        if (b == null) {
            return new SpannableStringBuilder();
        }
        if (TextUtils.isEmpty(b.blockText) || b.richItem == null || !b.richItem.isLinkItem()) {
            return new SpannableStringBuilder(b.blockText);
        }
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source : b.richItem.display;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);

        RichClickableSpan span = new RichClickableSpan(b.copy(), mListener);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    public void setRoundRectLink(boolean b) {
        isRoundRectLink = b;
    }
}
