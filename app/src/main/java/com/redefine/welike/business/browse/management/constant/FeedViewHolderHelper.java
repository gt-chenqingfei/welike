package com.redefine.welike.business.browse.management.constant;

import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PollInfo;
import com.redefine.welike.business.feeds.management.bean.PollPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;

/**
 * Created by honglin on 2018/7/23.
 */

public class FeedViewHolderHelper {


    public static final int FEED_CARD_TYPE_UNKNOWN = 0;
    public static final int FEED_CARD_TYPE_TEXT = 1;
    public static final int FEED_CARD_TYPE_PIC = 2;
    public static final int FEED_CARD_TYPE_VIDEO = 3;
    public static final int FEED_CARD_TYPE_LINK = 4;
    public static final int FEED_CARD_TYPE_FORWARD_TEXT = 5;
    public static final int FEED_CARD_TYPE_FORWARD_PIC = 6;
    public static final int FEED_CARD_TYPE_FORWARD_VIDEO = 7;
    public static final int FEED_CARD_TYPE_FORWARD_LINK = 8;
    public static final int FEED_CARD_TYPE_FORWARD_DELETE = 9;
    public static final int FEED_CARD_TYPE_VOTE_PIC = 10;
    public static final int FEED_CARD_TYPE_VOTE_TEXT = 11;
    public static final int FEED_CARD_TYPE_FORWARD_VOTE_PIC = 12;
    public static final int FEED_CARD_TYPE_FORWARD_VOTE_TEXT = 13;

    public static final int FEED_CARD_TYPE_ALL_VIDEO = 16;

    public static final int FEED_CARD_TYPE_INTEREST = 14;
    public static final int FEED_CARD_TYPE_FOLLOW = 15;

    public static final int FEED_CARD_TYPE_ART = 17;
    public static final int FEED_CARD_TYPE_FORWARD_ART = 18;
    public static final int FEED_CARD_TYPE_GP_SCORE = 20;


    public static int getFeedViewHolderType(PostBase postBase) {

        if (postBase == null) return FEED_CARD_TYPE_UNKNOWN;
        int feedType = postBase.getType();
        if (feedType != PostBase.POST_TYPE_FORWARD) {//
            switch (feedType) {
                case PostBase.POST_TYPE_TEXT:
                    return FEED_CARD_TYPE_TEXT;
                case PostBase.POST_TYPE_PIC:
                    return FEED_CARD_TYPE_PIC;
                case PostBase.POST_TYPE_VIDEO:
                    return FEED_CARD_TYPE_VIDEO;
                case PostBase.POST_TYPE_LINK:
                    return FEED_CARD_TYPE_LINK;
                case PostBase.POST_TYPE_POLL: {

                    if (((PollPost) postBase).mPollInfo.poolType == PollInfo.FEED_POLL_TYPE_TEXT)
                        return FEED_CARD_TYPE_VOTE_TEXT;
                    else
                        return FEED_CARD_TYPE_VOTE_PIC;
                }

                case PostBase.POST_TYPE_INTEREST:
                    return FEED_CARD_TYPE_INTEREST;
                case PostBase.POST_TYPE_FOLLOW:
                    return FEED_CARD_TYPE_FOLLOW;
                case PostBase.POST_TYPE_GP_SCORE:
                    return FEED_CARD_TYPE_GP_SCORE;
                case PostBase.POST_TYPE_ART:
                    return FEED_CARD_TYPE_ART;
                default:
                    return FEED_CARD_TYPE_UNKNOWN;
            }
        } else {
            ForwardPost forwardPost = (ForwardPost) postBase;
            if (forwardPost.isForwardDeleted() || forwardPost.getRootPost() == null) {
                return FEED_CARD_TYPE_FORWARD_DELETE;
            }
            int forwardType = forwardPost.getRootPost().getType();
            switch (forwardType) {
                case PostBase.POST_TYPE_TEXT:
                    return FEED_CARD_TYPE_FORWARD_TEXT;
                case PostBase.POST_TYPE_PIC:
                    return FEED_CARD_TYPE_FORWARD_PIC;
                case PostBase.POST_TYPE_VIDEO:
                    return FEED_CARD_TYPE_FORWARD_VIDEO;
                case PostBase.POST_TYPE_LINK:
                    return FEED_CARD_TYPE_FORWARD_LINK;
                case PostBase.POST_TYPE_ART:
                    return FEED_CARD_TYPE_FORWARD_ART;
                case PostBase.POST_TYPE_POLL: {
                    if (((PollPost) forwardPost.getRootPost()).mPollInfo.poolType == PollInfo.FEED_POLL_TYPE_TEXT)
                        return FEED_CARD_TYPE_FORWARD_VOTE_TEXT;
                    else
                        return FEED_CARD_TYPE_FORWARD_VOTE_PIC;
                }
                default:
                    return FEED_CARD_TYPE_UNKNOWN;
            }
        }

    }


}
