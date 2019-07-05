package com.redefine.welike.statistical.manager;

import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/7/30
 */
public enum ShareEventManager {
    INSTANCE;

    private int channel;
    private int source;
    private String frompage;
    private int channel_page;
    private String feedname;
    private int shareType;

    public void setFrompage(String frompage) {
        this.frompage = frompage;
    }

    public void setChannel_page(int channel_page) {
        this.channel_page = channel_page;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public void setFeedname(String feedname) {
        this.feedname = feedname;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public void report1() {
        EventLog.Share.report1(source);
    }

    public void report2() {
        if (source == ShareModel.SHARE_MODEL_TYPE_POST) {
            EventLog.Share.report2(channel, source, channel_page);
        } else {
            EventLog.Share.report2(channel, source, 0);
        }

    }

    public void report3() {
        EventLog.Share.report3(frompage, channel_page, feedname, shareType);
    }

    public void report4(int shareType, int source) {
        EventLog.Share.report4(shareType, source);
    }
}
