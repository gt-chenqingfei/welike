package com.redefine.richtext.processor;

import com.redefine.richtext.RichEditText;
import com.redefine.richtext.RichTextView;

/**
 * Created by MR on 2018/1/23.
 */

public class RichTextProcessorFactory {

    public static IRichEditTextProcessor getEditTextProcessor(RichEditText editText) {
        return new RichEditTextProcessor(editText);
    }

    public static IRichTextProcessor getTextViewProcessor(RichTextView view) {
        return new RichTextProcessor(view);
    }
}
