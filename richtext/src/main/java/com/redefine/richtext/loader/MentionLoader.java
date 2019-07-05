package com.redefine.richtext.loader;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.block.Block;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.richtext.span.RichClickableSpan;

/**
 * Created by MR on 2018/1/23.
 */

public class MentionLoader {
    private OnRichItemClickListener mListener;

    public MentionLoader() {

    }

    public MentionLoader(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public Spannable parseRichContent(Block b) {
        return parseRichContent(b, true);
    }

    public Spannable parseRichContent(Block b, boolean isAddSpace) {
        if (b == null || TextUtils.isEmpty(b.blockText) || b.richItem == null || !b.richItem.isAtItem()) {
            return new SpannableStringBuilder();
        }
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source: b.richItem.display;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);
        RichClickableSpan span = new RichClickableSpan(b.copy(), mListener);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (isAddSpace) {
            spannableStringBuilder.append(" ");
        }
        return spannableStringBuilder;
    }
}
