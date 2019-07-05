package com.redefine.welike.base.constant;

/**
 * Created by liwenbo on 2018/2/1.
 */

public class EventIdConstant {

    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    /**
     * pagestack的添加删除消息
     */
    public static final int LAUNCH_DISCOVER_PAGE = generateID();
    public static final int LAUNCH_FEED_DETAIL_EVENT = generateID();
    public static final int LAUNCH_COMMENT_DETAIL_EVENT = generateID();
    public static final int LAUNCH_MESSAGE_LIST_EVENT = generateID();
    public static final int LAUNCH_SETTING_EVENT = generateID();
    public static final int LAUNCH_USER_HOST_EVENT = generateID();
    public static final int LAUNCH_USER_FOLLOW_EVENT = generateID();
    public static final int LAUNCH_CHAT_EVENT = generateID();
    public static final int LAUNCH_LOGOUT_EVENT = generateID();
    public static final int LAUNCH_SEARCH_RESULT_EVENT = generateID();
    public static final int LAUNCH_SETTING_LANGUAGE = generateID();
    public static final int LAUNCH_SETTING_PUSH = generateID();
    public static final int LAUNCH_MINE_MY_LIKE = generateID();
    public static final int LAUNCH_MINE_USER_PERSONAL_INFO = generateID();
    public static final int LAUNCH_MINE_USER_PERSONAL_EDIT_NAME = generateID();
    public static final int LAUNCH_MINE_USER_PERSONAL_EDIT_BRIEF = generateID();
    public static final int LAUNCH_MINE_USER_PERSONAL_EDIT_SEX = generateID();
    public static final int CLEAR_STACK_LAUNCH_MAIN_MINE = generateID();
    public static final int LAUNCH_SEARCH_SUG_EVENT = generateID();
    public static final int LAUNCH_ASSIGNMENT_PAGE = generateID();
    public static final int LAUNCH_MAIN_HOME = generateID();
    public static final int LAUNCH_CHOICE_INTEREST_PAGE = generateID();
    public static final int LAUNCH_COMMON_FEED_LIST = generateID();
    public static final int LAUNCH_SHARE_PAGE = generateID();
    public static final int LAUNCH_PUBLISH_PAGE = generateID();
    public static final int LAUNCH_PASSER_BY_PAGE = generateID();
    public static final int LAUNCH_LOCATION_NEAR_BY_PAGE = generateID();
    public static final int LAUNCH_WEB_VIEW = generateID();
    public static final int LAUNCH_TOPIC_USER_PAGE = generateID();
    public static final int LAUNCH_TOPIC_LANDING_PAGE = generateID();
    public static final int LAUNCH_INTEREST_CATEGORY_PAGE = generateID();
    public static final int LAUNCH_REPOER_PAGE = generateID();
    public static final int LAUNCH_IM_STRANGER_PAGE = generateID();
    public static final int LAUNCH_MAIN_MESSAGE = generateID();
    public static final int LAUNCH_MAIN_MINE = generateID();
    public static final int LAUNCH_PUBLISH_SHORT_CUT_ENTRANCE_PAGE = generateID();
    public static final int LAUNCH_BLOCK_PAGE = generateID();
    public static final int LAUNCH_BLOCK_FOLLOWING_PAGE = generateID();
    public static final int LAUNCH_BLOCK_SEARCH_PAGE = generateID();
    public static final int POP_TOP_PAGE = generateID();
    public static final int LAUNCH_YOUTUBE_PLAYER = generateID();
    public static final int LAUNCH_PAGE_IM = generateID();
    public static final int LAUNCH_PAGE_TOPIC_LIST = generateID();
    public static final int LAUNCH_PAGE_REPORT_DESC = generateID();
    public static final int LAUNCH_DEACTIVATE_PAGE = generateID();
    public static final int LAUNCH_DEACTIVATE_RECALL_PAGE = generateID();
    public static final int LAUNCH_DEACTIVATE_CONFIRM_PAGE = generateID();
    public static final int LAUNCH_SETTING_PRIVACY_PAGE = generateID();
    public static final int LAUNCH_DEACTIVATE_NOTIFICATION_PAGE = generateID();
    public static final int LAUNCH_QUITE_HOURS_PAGE = generateID();
    public static final int LAUNCH_CONTACT = generateID();
    public static final int LAUNCH_BROWSE_DISCOVER = generateID();
    public static final int LAUNCH_BROWSE_DISCOVER_FINISH = generateID();

    public static final int LAUNCH_PROFILE_BIND_SOCIAL = generateID();

    public static final int LAUNCH_USER_INTEREST = generateID();
    public static final int LAUNCH_RECOMMEND_USER_PAGE = generateID();
    public static final int LAUNCH_POST_ARTICLE_PAGE = generateID();
    public static final int CLEAR_STACK_LAUNCH_MAIN_HOME = generateID();
    public static final int LAUNCH_SUPER_TOPIC_PAGE = generateID();

    public static final int CLEAR_ACTIVITY_STACK_4_LOGOUT = generateID();
    public static final int CLEAR_ACTIVITY_STACK_4_LOGIN = generateID();


    public static final int LAUNCH_BROWSE_LATEST_CAMPAIGN = generateID();


    public static final int LAUNCH_REGISTER_ACTIVITY = generateID();
    public static final int LAUNCH_PROFILE_PHOTO_PREVIEW = generateID();
    public static final int LAUNCH_VERIFY_PAGE = generateID();
    public static final int LAUNCH_VERIFY_DIALOG = generateID();
    public static final int LAUNCH_VERIFY_PHONE_PAGE = generateID();

    public static final int LAUNCH_SIGN_LOGIN_PAGE = generateID();
    public static final int LAUNCH_LOGIN_PAGE = generateID();

    public static final int LAUNCH_RECOMMEND_USER_ACTIVITY = generateID();
    public static final int LAUNCH_HOME_TO_FORYOU = generateID();


}
