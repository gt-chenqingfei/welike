package com.redefine.richtext.loader;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.R;
import com.redefine.richtext.block.Block;

/**
 * Created by MR on 2018/1/25.
 */

public class UnSupportLoader {
    public Spannable parseRichContent(Context context, Block b) {

        if (b == null || b.richItem == null) {
            return new SpannableStringBuilder();
        }
//        if (TextUtils.isEmpty(b.blockText) || b.richItem == null) {
//            return new SpannableStringBuilder(b.blockText);
//        }
//        String unSrpportContent = context.getString(R.string.no_support);
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(unSrpportContent);
//        RichClickableSpan span = new RichClickableSpan(b);
//        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spannableStringBuilder;
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source: b.richItem.display;
        return new SpannableStringBuilder(target);
    }
}
