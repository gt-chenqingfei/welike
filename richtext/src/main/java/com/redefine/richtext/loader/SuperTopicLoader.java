package com.redefine.richtext.loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.R;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.span.IconLeftSpan;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.richtext.span.RichClickableSpan;

/**
 * Created by MR on 2018/1/23.
 */

public class SuperTopicLoader {

    private OnRichItemClickListener mListener;

    public SuperTopicLoader() {

    }

    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public Spannable parseRichContent(Context context, Block b) {
        if (b == null) {
            return new SpannableStringBuilder();
        }
        if (TextUtils.isEmpty(b.blockText) || b.richItem == null || !b.richItem.isSuperTopicItem()) {
            return new SpannableStringBuilder(b.blockText);
        }
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source: b.richItem.display;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);
        Drawable drawable = context.getResources().getDrawable(R.drawable.super_topic_rich_icon);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        IconLeftSpan span = new IconLeftSpan(drawable, b.copy(), mListener);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}
