package com.redefine.welike.business.feeds.management.bean;

import java.io.Serializable;

public class PollItemInfo implements Serializable {
    private static final long serialVersionUID = 6968432989374209530L;

    public String pollItemPic;
    public String pollItemText;
    public int width;
    public int height;
    public String id;
    public int choiceCount;
    public boolean isSelected;

    public PollItemInfo() {
    }

    public PollItemInfo(String id) {
        this.id = id;
    }
}
