package com.redefine.im.bean;

public class IMSystemMessage extends IMMessageBase {
    private String text;

    public IMSystemMessage() {
        setType(MESSAGE_TYPE_SYSTEM);
    }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

}
