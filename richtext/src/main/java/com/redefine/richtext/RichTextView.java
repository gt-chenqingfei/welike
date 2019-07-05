package com.redefine.richtext;

import android.content.Context;
import android.support.text.emoji.widget.EmojiTextView;
import android.util.AttributeSet;
import android.os.Build;

import com.redefine.richtext.movemethod.RichMoveMethod;
import com.redefine.richtext.processor.IRichTextProcessor;
import com.redefine.richtext.processor.RichTextProcessorFactory;

/**
 * Created by MR on 2018/1/23.
 */

public class RichTextView extends EmojiTextView {

    private IRichTextProcessor mRichProcessor;

    public RichTextView(Context context) {
        super(context);
        initRichTextProcessor();
    }

    public RichTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRichTextProcessor();
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRichTextProcessor();
    }

    public IRichTextProcessor getRichProcessor() {
        return mRichProcessor;
    }


    protected void initRichTextProcessor() {
        mRichProcessor = RichTextProcessorFactory.getTextViewProcessor(this);
        RichMoveMethod moveMethod = new RichMoveMethod();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            setOnTouchListener(moveMethod);
        }
        setMovementMethod(moveMethod);
        setFocusable(false);
        setClickable(false);
        setLongClickable(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Exception e ) {
            // do nothing
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }
}
