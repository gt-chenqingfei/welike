package com.redefine.richtext.processor;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.redefine.richtext.RichItem;
import com.redefine.richtext.RichTextView;
import com.redefine.richtext.helper.RichTextLoader;
import com.redefine.richtext.span.OnRichItemClickListener;

import java.util.List;

/**
 * Created by MR on 2018/1/23.
 */

public class RichTextProcessor implements IRichTextProcessor {

    private final RichTextView mRichTextView;
    private final RichTextLoader mRichTextLoader;

    public RichTextProcessor(RichTextView view) {
        mRichTextView = view;
        mRichTextLoader = new RichTextLoader(view.getContext());
    }

    @Override
    public void setRichContent(String text, List<RichItem> items) {
        mRichTextView.setText(mRichTextLoader.parseRichContent(text, items));
    }

    @Override
    public void setRichContent(String text, List<RichItem> items, boolean isSupportInnerImage) {
        setRichContent(text, items, isSupportInnerImage, false);
    }

    @Override
    public void setRichContent(String text, List<RichItem> items, boolean isSupportInnerImage, boolean isFilterEnterChar) {
        mRichTextView.setText(mRichTextLoader.parseRichContent(text, items
                , isSupportInnerImage ? null : new RichTextLoader.IRichItemFilter() {

                    @Override
                    public Spannable filter(RichItem richItem) {
                        if (richItem.isInnerImageItem()) {
                            return new SpannableStringBuilder();
                        } else {
                            return null;
                        }
                    }
                }
                , isFilterEnterChar ? new RichTextLoader.ICharItemFilter() {

                    @Override
                    public String filter(String chars) {
                        return chars.replaceAll("\n", "");
                    }
                } : null));
    }

    @Override
    public void setRichContent(String text, List<RichItem> items, RichTextLoader.IRichItemFilter filter, boolean isFilterEnterChar) {
        mRichTextView.setText(mRichTextLoader.parseRichContent(text, items
                , filter
                , isFilterEnterChar ? new RichTextLoader.ICharItemFilter() {

                    @Override
                    public String filter(String chars) {
                        return chars.replaceAll("\n", "");
                    }
                } : null));
    }

    @Override
    public void insertCharSequence(int i, Spanned htmlText) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(mRichTextView.getText());
        spannableStringBuilder.insert(i, htmlText);
        mRichTextView.setText(spannableStringBuilder);
    }

    @Override
    public void setOnRichItemClickListener(OnRichItemClickListener listener) {
        mRichTextLoader.setOnRichItemClickListener(listener);
    }

    @Override
    public void parseCommonLinks() {
        mRichTextView.setText(mRichTextLoader.parseCommonLinks(mRichTextView.getText()));
    }

    @Override
    public void showMoreBtn(String display) {
        Spannable spannable = mRichTextLoader.parseMore(display);
        insertCharSequence(mRichTextView.getText().length(), spannable);
    }

    @Override
    public void showLessBtn(String display) {
        Spannable spannable = mRichTextLoader.parseLess(display);
        insertCharSequence(mRichTextView.getText().length(), spannable);
    }

    @Override
    public void setRoundRectLink(boolean b) {
        mRichTextLoader.setRoundRectLink(b);
    }
}
