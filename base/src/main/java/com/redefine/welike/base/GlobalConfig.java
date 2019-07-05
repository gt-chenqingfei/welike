package com.redefine.welike.base;

import com.redefine.welike.base.util.TimeUtil;

/**
 * Created by liubin on 2018/1/15.
 */

public class GlobalConfig {
    public static final int LOOP_GET_NEW = 1;
    public static final int POST_PIC_MAX_COUNT = 9;
    public static final int POSTS_NUM_ONE_PAGE = 10;
    public static final int COMMENTS_NUM_ONE_PAGE = 20;
    public static final int USERS_NUM_ONE_PAGE = 20;
    public static final int USER_ALBUMS_NUM_ONE_PAGE = 15;
    public static final int IM_MESSAGES_ONE_PAGE = 15;
    public static final int INTRESTS_NUM_ONE_PAGE = 8;
    public static final int SUG_EMPTY_HIS_SHOW_NUM = 5;
    public static final int SUG_HIS_SHOW_NUM = 2;
    public static final int SUG_HIS_CACHE_NUM = 20;
    public static final int TOPIC_SUG_HIS_CACHE_NUM = 5;
    public static final int SUG_SEARCH_ONE_PAGE_NUM = 20;
    public static final int TOPIC_SUG_SEARCH_ONE_PAGE_NUM = 20;
    public static final int SUG_USERS_NUM_ONE_PAGE = 20;
    public static final int SEARCH_POSTS_NUMBER_ONE_PAGE = 15;
    public static final int SEARCH_USERS_NUMBER_ONE_PAGE = 10;
    public static final int PROFILE_PHOTO_NUMBER_ONE_PAGE = 60;
    public static final int HOME_POSTS_MAX_CACHE_COUNT = 200;
    public static final int HOT_POSTS_MAX_CACHE_COUNT = 200;
    public static final int SUMMARY_LIMIT = 275;
    public static final int DEFAULT_HEADS_LIST_COUNT = 2;
    public static final int DRAFT_MAX_COUNT = 20;
    public static final int LBS_NEAR_REVIEW_USERS_COUNT = 6;
    public static final int INTEREST_USER_FIRST_PAGE = 15;
    public static final int INTEREST_USER_MORE_PAGE = 15;
    public static final int SEARCH_HOT_TOPIC_NUM = 8;
    public static final int DISCOVER_TOPICS = 10;

    public static final int LIST_ACTION_NONE = 0;
    public static final int LIST_ACTION_REFRESH = 1;
    public static final int LIST_ACTION_HIS = 2;

    public static final String ATTACHMENT_PIC_TYPE = "IMAGE";
    public static final String ATTACHMENT_VIDEO_TYPE = "VIDEO";
    public static final String ATTACHMENT_POLL_TYPE = "POLL";
    public static final String ATTACHMENT_TOPIC_CARD = "TOPICCARD";


    public static final String ATTACHMENT_AD_CARD = "AD";
    public static final String ATTACHMENT_HEADER_CARD = "HEADER";

    public static final String ADDITION_THUMB_TYPE = "THUMB";

    public static final String PUBLISH_PIC_SUFFIX = ".tmp";
    public static final String PUBLISH_MP4 = ".mp4";
    public static final String AT = "@";
    public static final String TOPIC = "#";
    public static final String NEW_LINE = "\n";
    public static final String AND_SO_ON = "...";
    public static final int FEED_MAX_LENGTH = 1000;
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String USER_SERVICE = "http://www.welike.in/privacy/protocol.html";
    public static final int FEED_DETAIL_COMMENT_MAX_SHOW = 2;
    public static final int MIN_TOPIC_SEARCH_TEXT = 4;
    public static final int FEED_MAX_TOPIC = 3;
    public static final int MAX_TOPIC_LANDING_USER_COUNT = 6;
    public static final int MAX_TOPIC_USER_COUNT = 20;
    public static final String REPLY_TEXT = "reply";
    public static final String params = "hjd";

    public static final int LOOP_ACTION_HOME = 1;
    public static final int LOOP_ACTION_DISTORY = 2;

    public static final int MIN_POLL_ITEM_SIZE = 2;
    public static final int MAX_POLL_ITEM_SIZE = 4;
    public static final long DEFAULT_POLL_EXPIRED_TIME = 3 * TimeUtil.DAY_1;
    public static final int MIN_FEED_TOPIC_COUNT = 4;


    public static final int FEED_MAX_SUPER_TOPIC = 1;
    public static final int PUBLISH_POST_INPUT_TEXT_MAX_OVER_LIMIT = 1000;
    public static final int PUBLISH_POST_INPUT_TEXT_MAX_WARN_LIMIT = 900;
    public static final int PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT = 275;
    public static final int PUBLISH_COMMENT_INPUT_TEXT_MAX_WARN_LIMIT = 225;
}
