package com.redefine.welike.base.constant;

import com.redefine.foundation.framework.Event;

/**
 * Created by liwenbo on 2018/3/5.
 */

public class MessageIdConstant {

    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    /**
     * page页面的message消息
     */
    public static final int MESSAGE_DELETE_POST = generateID();
    public static final int MESSAGE_COMMENT_PUBLISH = generateID();
    public static final int MESSAGE_FORWARD_POST_PUBLISH = generateID();
    public static final int MESSAGE_NOTIFY_ASSIGNMENT = generateID();
    public static final int MESSAGE_EDIT_NICK_NAME_SUCCESS = generateID();
    public static final int MESSAGE_EDIT_SEX_SUCCESS = generateID();
    public static final int MESSAGE_EDIT_INTRODUCTION_SUCCESS = generateID();
    public static final int MESSAGE_DELETE_COMMENT = generateID();
    public static final int MESSAGE_DELETE_COMMENT_SUB = generateID();
    public static final int MESSAGE_SYNC_ACCOUNT_PROFILE = generateID();
    public static final int MESSAGE_SYNC_NEW_HOME_FEED = generateID();
    public static final int MESSAGE_SYNC_NEW_LEAST_FEED = generateID();
    public static final int MESSAGE_SYNC_BLOCK_USER = generateID();
    public static final int MESSAGE_SHOW_HOME_REFRESH = generateID();
    public static final int MESSAGE_HIDE_HOME_REFRESH = generateID();
    public static final int MESSAGE_LAUNCH_DISCOVER_PAGE = generateID();
    public static final int MESSAGE_LAUNCH_HOME_PAGE = generateID();
    public static final int MESSAGE_LAUNCH_MINE_PAGE = generateID();
    public static final int MESSAGE_LAUNCH_MESSAGE_PAGE = generateID();
    public static final int MESSAGE_LAUNCH_FORYOU_SUB = generateID();


}
