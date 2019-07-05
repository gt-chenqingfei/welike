package com.redefine.welike.business.feeds.management.bean;

public class PollPost extends PostBase {
    private static final long serialVersionUID = -6475677798317225601L;
    public PollInfo mPollInfo;

    public PollPost() {
        type = POST_TYPE_POLL;
    }
}
