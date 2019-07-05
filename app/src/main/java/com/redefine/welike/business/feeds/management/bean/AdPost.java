package com.redefine.welike.business.feeds.management.bean;

/**
 * @author redefine honlin
 * @Date on 2018/11/5
 * @Description
 */
public class AdPost extends PostBase {


    private static final long serialVersionUID = -5393494033546967611L;

    public AdPost() {
        type = POST_TYPE_AD;
    }

    private HeaderAttachment headerAttachment;

    private AdAttachment adAttachment;

    public HeaderAttachment getHeaderAttachment() {
        return headerAttachment;
    }

    public void setHeaderAttachment(HeaderAttachment headerAttachment) {
        this.headerAttachment = headerAttachment;
    }

    public AdAttachment getAdAttachment() {
        return adAttachment;
    }

    public void setAdAttachment(AdAttachment adAttachment) {
        this.adAttachment = adAttachment;
    }
}
