package com.redefine.richtext.span;

import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;

/**
 * Name: RichSpan
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-20 03:06
 */
public interface RichSpan extends IClickableSpan {
    Block getBlock();

    RichItem getRichItem();
}
