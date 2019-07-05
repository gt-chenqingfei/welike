package com.redefine.richtext.emoji.bean;

/**
 * Created by MR on 2018/1/24.
 */

public abstract class BaseEmojiBean {

    public String emoji;
    public int resource;

    public BaseEmojiBean(String emoji, int resource) {
        this.emoji = emoji;
        this.resource = resource;
    }
}
