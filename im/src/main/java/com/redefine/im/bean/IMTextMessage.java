package com.redefine.im.bean;

/**
 * Created by liubin on 2018/2/2.
 */

public class IMTextMessage extends IMMessageBase {
    private String text;

    public IMTextMessage() { type = MESSAGE_TYPE_TXT; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }
}
