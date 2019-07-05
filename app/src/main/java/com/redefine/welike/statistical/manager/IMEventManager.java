package com.redefine.welike.statistical.manager;

import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/6/13
 */
public enum IMEventManager {
    INSTANCE;

    private int mention_num;
    private int comment_num;
    private int like_num;
    private int stranger_num;

    public void setMention_num(int mention_num) {
        this.mention_num = mention_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public void setStranger_num(int stranger_num) {
        this.stranger_num = stranger_num;
    }

    public void report1() {
        EventLog.IM.report1(mention_num, comment_num, like_num, stranger_num);
    }

    public void report2() {
        EventLog.IM.report2(mention_num, comment_num, like_num, stranger_num);
    }

    public void report3() {
        EventLog.IM.report3(mention_num, comment_num, like_num, stranger_num);
    }

    public void report4() {
        EventLog.IM.report4(mention_num, comment_num, like_num, stranger_num);
    }
    public void report5() {
        EventLog.IM.report5(mention_num, comment_num, like_num, stranger_num);
    }

    public void report6(int chat_num, String chat_uid,
                        int chat_source, int chat_state) {
        EventLog.IM.report6(chat_num, chat_uid, chat_source, chat_state);
    }

    public void report7() {
        EventLog.IM.report7();
    }

    public void report8(String pushId) {
        EventLog.IM.report8(pushId);
    }

    public void report9(String last_message, int last_message_type, int message_type,
                        int emoji_num, String pre_uid, int word_num, int chat_state,
                        int chat_locate, int photo_num, int photo_source) {
        EventLog.IM.report9(last_message, last_message_type, message_type, emoji_num,
                pre_uid, word_num, chat_state, chat_locate, photo_num, photo_source);
    }
}
