package com.redefine.richtext;

import android.content.Context;
import android.util.AttributeSet;

import com.redefine.richtext.processor.IRichEditTextProcessor;
import com.redefine.richtext.processor.RichTextProcessorFactory;

/**
 * Created by MR on 2018/1/23.
 */

public class RichEditText extends android.support.text.emoji.widget.EmojiEditText {

    private IRichEditTextProcessor mRichProcessor;

    public RichEditText(Context context) {
        super(context);
        initRichTextProcessor();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRichTextProcessor();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRichTextProcessor();
    }

    public IRichEditTextProcessor getRichProcessor() {
        return mRichProcessor;
    }

    protected void initRichTextProcessor() {
        if (mRichProcessor != null) {
            return ;
        }
        mRichProcessor = RichTextProcessorFactory.getEditTextProcessor(this);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mRichProcessor == null) {
            initRichTextProcessor();
        }
        mRichProcessor.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (mRichProcessor == null) {
            initRichTextProcessor();
        }
        return mRichProcessor.onTextContextMenuItem(id) || super.onTextContextMenuItem(id);
    }
}
