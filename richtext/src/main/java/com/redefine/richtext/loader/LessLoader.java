package com.redefine.richtext.loader;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.block.Block;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.richtext.span.RichClickableSpan;

/**
 * Created by mengnan on 2018/5/28.
 **/
public class LessLoader {
    private OnRichItemClickListener mListener;

    public LessLoader() {

    }

    public LessLoader(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public Spannable parseRichContent(Block b) {
        if (b == null || TextUtils.isEmpty(b.blockText) || b.richItem == null || !b.richItem.isLessItem()) {
            return new SpannableStringBuilder();
        }
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source: b.richItem.display;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);
        RichClickableSpan span = new RichClickableSpan(b, mListener);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}

