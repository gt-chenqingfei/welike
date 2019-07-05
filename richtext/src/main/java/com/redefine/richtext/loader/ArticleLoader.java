package com.redefine.richtext.loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.R;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.helper.RichTextHelper;
import com.redefine.richtext.span.CenterAlignImageSpan;
import com.redefine.richtext.span.OnRichItemClickListener;
import com.redefine.richtext.span.RichClickableSpan;

/**
 * Name: ArticleLoader
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-22 14:37
 */
public class ArticleLoader {

    private OnRichItemClickListener mListener;

    public ArticleLoader() {

    }

    public ArticleLoader(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mListener = listener;
    }

    public Spannable parseRichContent(Context context, Block b) {
        if (b == null) {
            return new SpannableStringBuilder();
        }
        if (TextUtils.isEmpty(b.blockText) || b.richItem == null || !b.richItem.isArticleItem()) {
            return new SpannableStringBuilder(b.blockText);
        }
        String target = TextUtils.isEmpty(b.richItem.display) ? b.richItem.source: b.richItem.display;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);
        String linkStartFlag = RichTextHelper.article_;

        if (target.startsWith(linkStartFlag)) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.rich_item_article_icon);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable);
            spannableStringBuilder.setSpan(imageSpan, 0, linkStartFlag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        RichClickableSpan span = new RichClickableSpan(b.copy(), mListener);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}
