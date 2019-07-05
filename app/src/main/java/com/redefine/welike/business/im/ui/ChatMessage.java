package com.redefine.welike.business.im.ui;

import com.redefine.im.room.MESSAGE;
import com.redefine.welike.business.im.model.CardInfo;

/**
 * Created by nianguowang on 2018/6/11
 */
public class ChatMessage {

    public static final int MESSAGE_TYPE_UNKNOWN = 0;
    public static final int MESSAGE_TYPE_TXT = 1;
    public static final int MESSAGE_TYPE_PIC = 2;
    public static final int MESSAGE_TYPE_AUDIO = 3;
    public static final int MESSAGE_TYPE_VIDEO = 4;
    public static final int MESSAGE_TYPE_TIME = 5;
    public static final int MESSAGE_TYPE_SYSTEM = 6;
    public static final int MESSAGE_TYPE_CARD_POST = 7;
    public static final int MESSAGE_TYPE_CARD_TOPIC = 8;
    public static final int MESSAGE_TYPE_CARD_PROFILE = 9;
    public static final int MESSAGE_TYPE_CARD_APP = 10;
    public static final int MESSAGE_TYPE_CARD_LINK = 11;

    private int type;
    private int progress;
    private MESSAGE message;
    private CardInfo cardInfo;

    public ChatMessage(MESSAGE message, int type) {
        this.message = message;
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public MESSAGE getMessage() {
        return message;
    }

    public void setMessage(MESSAGE message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
