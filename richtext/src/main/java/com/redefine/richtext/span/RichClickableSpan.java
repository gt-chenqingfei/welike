package com.redefine.richtext.span;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.constant.RichConstant;

/**
 * Created by MR on 2018/1/24.
 */

public class RichClickableSpan extends ClickableSpan implements RichSpan {


    private final Block mBlock;
    protected OnRichItemClickListener mListener;
    private boolean isPressed = false;

    public RichClickableSpan(Block block) {
        mBlock = block;
    }

    public RichClickableSpan(Block b, OnRichItemClickListener l) {
        this(b);
        this.mListener = l;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(RichConstant.richTextColor);
        ds.bgColor = isPressed ? RichConstant.richPressedColor : Color.TRANSPARENT;
    }

    @Override
    public void onClick(View widget) {
        if (mBlock == null || mBlock.richItem == null) {
            return;
        }
        if (mListener != null) {
            mListener.onRichItemClick(mBlock.richItem);
        }
    }

    public RichItem getRichItem() {
        return mBlock.richItem;
    }

    public Block getBlock() {
        return mBlock;
    }

    public void setPressed(boolean b) {
        isPressed = b;
    }
}
