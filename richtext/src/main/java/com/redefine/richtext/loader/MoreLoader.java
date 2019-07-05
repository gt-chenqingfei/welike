package com.redefine.richtext.loader;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.block.Block;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.richtext.span.RichClickableSpan;

/**
 * Created by liwenbo on 2018/3/6.
 */

public class MoreLoader {

    private OnRichItemClickListener mListener;

    public MoreLoader() {

    }

    public MoreLoader(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public Spannable parseRichContent(Block b) {
        if (b == null || TextUtils.isEmpty(b.blockText) || b.richItem == null || !b.richItem.isMoreItem()) {
            return new SpannableStringBuilder();
        }
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source: b.richItem.display;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);
        RichClickableSpan span = new RichClickableSpan(b, mListener);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.insert(0, "...");
        return spannableStringBuilder;
    }
}
