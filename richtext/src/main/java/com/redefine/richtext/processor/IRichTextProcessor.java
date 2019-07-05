package com.redefine.richtext.processor;

import android.text.Spanned;

import com.redefine.richtext.RichItem;
import com.redefine.richtext.helper.RichTextLoader;
import com.redefine.richtext.span.OnRichItemClickListener;

import java.util.List;

/**
 * Created by MR on 2018/1/23.
 */

public interface IRichTextProcessor {

    void setRichContent(String text, List<RichItem> items);

    void setRichContent(String text, List<RichItem> items, boolean isSupportInnerImage);

    void setRichContent(String text, List<RichItem> items, boolean isSupportInnerImage, boolean isFilterEnterChar);

    void setRichContent(String text, List<RichItem> items, RichTextLoader.IRichItemFilter filter, boolean isFilterEnterChar);

    void insertCharSequence(int i, Spanned htmlText);

    /**
     * 需要在setRichContent之前调用
     * @param listener
     */
    void setOnRichItemClickListener(OnRichItemClickListener listener);

    void parseCommonLinks();

    void showMoreBtn(String display);
    void showLessBtn(String display);

    void setRoundRectLink(boolean b);
}
