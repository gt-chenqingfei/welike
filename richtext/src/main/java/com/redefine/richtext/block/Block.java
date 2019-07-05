package com.redefine.richtext.block;

import com.redefine.richtext.RichItem;

import java.io.Serializable;

/**
 * Created by MR on 2018/1/23.
 */

public class Block implements Serializable {

    private static final long serialVersionUID = 2318253359900515036L;
    public String blockText;
    public RichItem richItem;

    public Block copy() {
        Block block = new Block();
        block.blockText = blockText;
        block.richItem = richItem.copyRichItem();
        return block;
    }
}
