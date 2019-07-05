package com.redefine.richtext.loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.emoji.EmojiManager;
import com.redefine.richtext.R;
import com.redefine.richtext.emoji.bean.EmojiBean;
import com.redefine.richtext.span.EmojiSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MR on 2018/1/23.
 */

public class EmojiLoader {

    /**
     * 从服务端获取的数据，还未经过解析，则统一处理
     *
     * @param context
     * @param spannable
     * @return
     */
    public Spannable parseEmojiContent(Context context, SpannableStringBuilder spannable) {
        if (TextUtils.isEmpty(spannable)) {
            return spannable;
        }
        Pattern pattern = EmojiManager.getDefaultEmojiPattern();
        Matcher matcher = pattern.matcher(spannable);
        String emoji;
        Drawable drawable;
        int size = context.getResources().getDimensionPixelOffset(R.dimen.default_emoji_size1);
        EmojiSpan span;
        while (matcher.find()) {
            emoji = matcher.group();
            if (TextUtils.isEmpty(emoji)) {
                continue;
            }
            drawable = EmojiManager.getInstance().getEmojiDrawable(context, emoji);
            if (drawable == null) {
                continue;
            }
            drawable.setBounds(0, 0, size, size);
            span = new EmojiSpan(EmojiManager.getInstance().getEmojiBean(context, emoji), drawable);
            spannable.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public Spannable parseEmojiContent(Context context, EmojiBean emojiBean) {
        if (emojiBean == null) {
            return new SpannableStringBuilder();
        }

        if (emojiBean.resource <= 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append(emojiBean.emoji);
            return spannableStringBuilder;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(emojiBean.emoji);
        int size = context.getResources().getDimensionPixelOffset(R.dimen.default_emoji_size1);
        Drawable drawable = context.getResources().getDrawable(emojiBean.resource);
        if (drawable == null) {
            return new SpannableStringBuilder();
        }
        drawable.setBounds(0, 0, size, size);
        EmojiSpan span = new EmojiSpan(emojiBean, drawable);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}
