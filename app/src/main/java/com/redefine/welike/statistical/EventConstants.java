package com.redefine.welike.statistical;

/**
 * Created by nianguowang on 2018/7/21
 */
public interface EventConstants {

    String KEY_OS = "os";
    String KEY_OS_VER = "os_ver";
    String KEY_SDK_VER = "sdk_ver";
    String KEY_TZ = "tz";
    String KEY_CHANNEL = "channel";
    String KEY_MODEL = "model";
    String KEY_VENDOR = "vendor";
    String KEY_APP_KEY = "appkey";
    String KEY_IDFA = "idfa";
    String KEY_MARKET_SOURCE = "market_source";
    String KEY_APPFLYERS_SOURCE = "appflyers_source";
    String KEY_UID_SOURCE = "uid_source";
    String KEY_CHANNEL_SOURCE = "channel_source";
    String KEY_IP = "ip";
    String KEY_OPEN_SOURCE = "open_source";
    String KEY_TEST_AREA = "test_area";
    String KEY_LOG_EXTRA = "log_extra";
    String KEY_IS_LOGIN = "isLogin";
    String KEY_LA = "la";
    String KEY_LAC = "lac";
    String KEY_LAD = "lad";
    String KEY_GAID = "gaid";
    String KEY_ABT = "abt";
    String KEY_DEVICE_ID = "deviceId";
    String KEY_UTDID = "utdid";
    String KEY_VERSION_NAME = "versionName";
    String KEY_VERSION_CODE = "versionCode";
    String KEY_ISP = "isp";
    String KEY_LOCALE = "locale";
    String KEY_COUNTRY = "country";
    String KEY_RESOLUTION = "resolution";
    String KEY_DPI = "dpi";
    String KEY_NET = "net";
    String KEY_MAC = "mac";
    String KEY_UID = "uid";
    String KEY_NICK_NAME = "nick_name";
    String KEY_LNG = "lng";
    String KEY_LAT = "lat";
    String KEY_SESSION_ID = "session_id";
    String KEY_CTIME = "ctime";
    String KEY_VIDMATE_CHANNEL_TAG = "vidmate_channel_tag";
    String KEY_GOOGLE_FRAME = "google_frame";

    int COMMON_RESULT_SUCCESS = 1;
    int COMMON_RESULT_FAIL = 2;

    ///////////Feed Refresh action//////////
    int PULL_DOWN_REFRESH = 1;
    int PULL_UP_REFRESH = 2;
    int AUTO_REFRESH = 3;
    int CLICK_BUTTON_REFRESH = 4;

    ///////////Feed Refresh type//////////
    int FEED_HOME = 1;
    int FEED_DISCOVER_HOT = 2;
    int FEED_DISCOVER_LATEST = 3;
    int FEED_SEARCH_LATEST = 4;
    int FEED_SEARCH_POSTS = 5;
    int FEED_LOCATION = 6;
    int FEED_PROFILE_POST = 7;
    int FEED_PROFILE_LIKE = 8;
    int FEED_ME_MYLIKE = 9;
    int FEED_TOPIC_HOT = 10;
    int FEED_TOPIC_LATEST = 11;
    int FEED_LOCATION_HOT = 12;
    int FEED_LOCATION_LATEST = 13;

    String LABEL_FOR_YOU = "0";
    String LABEL_VIDEO = "a";
    String LABEL_OTHER = "other";
    String FEED_REFRESH_TYPE_VIDMATE = "14_";
    String FEED_REFRESH_TYPE_DISCOVER = "2_";

    ////////////LOGIN////////////
    int LOGIN_SOURCE_PHONE = 1;
    int LOGIN_SOURCE_FACEBOOK = 2;
    int LOGIN_SOURCE_GOOGLE = 3;
    int LOGIN_SOURCE_TRUECALLER = 4;

    int LOGIN_RETURN_RESULT_FAIL = 0;
    int LOGIN_RETURN_RESULT_SUCCESS = 1;
    int LOGIN_RETURN_RESULT_CANCEL = 2;

    int LOGIN_NEW_USER = 0;
    int LOGIN_OLD_USER = 1;

    int LOGIN_LANGUAGE_EN = 1;
    int LOGIN_LANGUAGE_HI = 2;

    int LOGIN_VERIFY_TYPE_SMS = 0;
    int LOGIN_VERIFY_TYPE_VOICE = 1;

    int LOGIN_PAGE_STATUS_SMS = 0;
    int LOGIN_PAGE_STATUS_VOICE = 1;

    /////////////SHARE///////////
    String SHARE_FROM_PAGE_HOME = "1";
    String SHARE_FROM_PAGE_PROFILE = "2";
    String SHARE_FROM_PAGE_DISCOVER = "3_";
    String SHARE_FROM_PAGE_TOPIC = "4";
    String SHARE_FROM_PAGE_OTHER = "5";

    int SHARE_CHANNEL_PAGE_HOME = 1;
    int SHARE_CHANNEL_PAGE_PROFILE = 2;
    int SHARE_CHANNEL_PAGE_DISCOVER = 3;
    int SHARE_CHANNEL_PAGE_TOPIC = 4;
    int SHARE_CHANNEL_PAGE_OTHER = 5;

    int SHARE_TYPE_LINK = 1;
    int SHARE_TYPE_VIDEO = 2;

    ///////////POST_STATUS///////

    interface POST_STATUS {
        int FROM_LOTTIE = 1;
        int FROM_PUBLISHER = 2;
        int BUTTON_TYPE_CHANGE_TEXT = 1;
        int BUTTON_TYPE_CHANGE_IMAGE = 2;
        int BUTTON_TYPE_EDIT_TEXT = 3;
    }

    //////////Interest and Recommend card////////
    int INTEREST_CARD_BUTTON_TYPE_FOLLOW = 1;
    int INTEREST_CARD_BUTTON_TYPE_UNFOLLOW = 2;
    int INTEREST_CARD_BUTTON_TYPE_PORTRAIT = 3;
    int INTEREST_CARD_BUTTON_TYPE_MORE = 4;
    int INTEREST_CARD_BUTTON_TYPE_CANCEL = 5;

    int INTEREST_CARD_FROM_PAGE_UNLOGIN = 1;
    int INTEREST_CARD_FROM_PAGE_DISCOVER = 2;

    int INTEREST_CARD_SEX_FEMALE = 1;
    int INTEREST_CARD_SEX_MALE = 2;

    ////////////////Search//////////////

    int SEARCH_FROM_PAGE_ROUTE = 1;
    int SEARCH_FROM_PAGE_DISCOVER_TOPIC = 2;
    int SEARCH_FROM_PAGE_SEARCH_TOPIC = 3;
    int SEARCH_FROM_PAGE_TOPIC_HOT_LIST = 4;
    int SEARCH_FROM_PAGE_FEED = 5;

    ////////////////Feed Page////////////
    String FEED_PAGE_HOME = "1";
    String FEED_PAGE_DISCOVER = "2_";
    String FEED_PAGE_DISCOVER_FOR_YOU = "2_0";
    String FEED_PAGE_DISCOVER_VIDEO = "2_a";
    String FEED_PAGE_DISCOVER_VIDEO_AFTER = "2_b";
    String FEED_PAGE_DISCOVER_LATEST = "3";
    String FEED_PAGE_SEARCH_LATEST = "4";
    String FEED_PAGE_SEARCH_POSTS = "5";
    String FEED_PAGE_LOCATION_HOT = "6";
    String FEED_PAGE_LOCATION_LATEST = "7";
    String FEED_PAGE_TOPIC_HOT = "8";
    String FEED_PAGE_TOPIC_LATEST = "9";
    String FEED_PAGE_PROFILE = "10";
    String FEED_PAGE_PROFILE_LIKE_OWNER = "11";
    String FEED_PAGE_PROFILE_LIKE_VISIT = "11";
    String FEED_PAGE_PROFILE_POST_OWNER = "12";
    String FEED_PAGE_PROFILE_POST_VISIT = "12";
    String FEED_PAGE_ME_LIKE = "15";
    String FEED_PAGE_HOT_24_ASSIGNMENT = "16";
    String FEED_PAGE_TOPIC_TOP = "17";
    String FEED_PAGE_UNLOGIN = "18_";
    String FEED_PAGE_UNLOGIN_FOR_YOU = "18_0";
    String FEED_PAGE_UNLOGIN_VIDEO = "18_a";
    String FEED_PAGE_UNLOGIN_VIDEO_AFTER = "18_b";
    String FEED_PAGE_UNLOGIN_LATEST = "18_interest_latest";
    String FEED_PAGE_SUPER_TOPIC_INFO = "21";
    String FEED_PAGE_SUPER_TOPIC_HOT = "22";
    String FEED_PAGE_SUPER_TOPIC_LATEST = "23";
    String FEED_PAGE_SEARCH_USERS = "24";
    ///////////////////////////////////////////////
    String FEED_PAGE_POST_DETAIL = "50";
    String FEED_PAGE_POST_DETAIL_COMMENT = "51";
    String FEED_PAGE_COMMENT_DETAIL = "52";
    String FEED_PAGE_VIDEO_PLAYER = "53";
    String FEED_PAGE_NOTIFICATION = "54";
    String FEED_PAGE_POST_DETAIL_BOTTOM_BUTTON = "55";
    String FEED_PAGE_COMMENT_DETAIL_BOTTOM_BUTTON = "56";
    String FEED_PAGE_HOME_CARD_RECOMMEND = "57";
    String FEED_PAGE_HOME_FULL_SCREEN_RECOMMEND = "58";
    String FEED_PAGE_DISCOVER_CARD_RECOMMEND = "59";
    String FEED_PAGE_CONTACT = "60";
    String FEED_PAGE_FOLLOWING = "61";
    String FEED_PAGE_FOLLOWER = "62";
    String FEED_PAGE_RECOMMENT_ALL_FOLLOW = "63";
    String FEED_PAGE_ME = "64";
    String FEED_PAGE_SETTING_BLOCK = "65";
    String FEED_PAGE_IM_MESSAGE = "66";
    String FEED_PAGE_FOLLOW_BIG_CARD = "67";
    String FEED_PAGE_LOCATION_PASSERBY = "70";
    String FEED_PAGE_TOPIC_USER = "71";

    /////////////////////////For old event start/////////////////////////
    String FEED_PAGE_OLD_HOME = "1";
    String FEED_PAGE_OLD_DISCOVER_HOT = "2";
    String FEED_PAGE_OLD_DISCOVER_LATEST = "3";
    String FEED_PAGE_OLD_SEARCH_LATEST = "4";
    String FEED_PAGE_OLD_SEARCH_POSTS = "5";
    String FEED_PAGE_OLD_LOCATION = "6";
    String FEED_PAGE_OLD_PROFILE_POST_OWNER = "7";
    String FEED_PAGE_OLD_PROFILE_POST_VISIT = "7";
    String FEED_PAGE_OLD_PROFILE_LIKE_OWNER = "8";
    String FEED_PAGE_OLD_PROFILE_LIKE_VISIT = "8";
    String FEED_PAGE_OLD_ME_LIKE = "9";
    String FEED_PAGE_OLD_TOPIC_HOT = "10";
    String FEED_PAGE_OLD_TOPIC_LATEST = "11";
    String FEED_PAGE_OLD_LOCATION_HOT = "12";
    String FEED_PAGE_OLD_LOCATION_LATEST = "13";
    String FEED_PAGE_OLD_SUPER_TOPIC_INFO = "16";
    String FEED_PAGE_OLD_SUPER_TOPIC_HOT = "17";
    String FEED_PAGE_OLD_SUPER_TOPIC_LATEST = "18";

    String FEED_PAGE_OLD_DISCOVER = "2_";
    String FEED_PAGE_OLD_DISCOVER_FOR_YOU = "2_0";
    String FEED_PAGE_OLD_DISCOVER_VIDEO = "2_a";
    String FEED_PAGE_OLD_DISCOVER_VIDEO_AFTER = "2_b";

    String FEED_PAGE_OLD_UNLOGIN = "14_";
    String FEED_PAGE_OLD_UNLOGIN_FOR_YOU = "14_0";
    String FEED_PAGE_OLD_UNLOGIN_VIDEO = "14_a";
    String FEED_PAGE_OLD_UNLOGIN_VIDEO_AFTER = "18_b";
    String FEED_PAGE_OLD_UNLOGIN_LATEST = "15";
    /////////////////////////For old event end///////////////////////////


    ////////////////Search//////////////

    int UNLOGIN_DISCOVERACTION_SEARCH = 1;
    int UNLOGIN_DISCOVERACTION_USER_ICON = 2;
    int UNLOGIN_DISCOVERACTION_USER_MORE = 3;
    int UNLOGIN_DISCOVERACTION_TREND_TOPIC = 4;
    int UNLOGIN_DISCOVERACTION_TREND_TOPIC_MORE = 5;
    int UNLOGIN_DISCOVERACTION_LATEST_CAMPAIGN = 6;
    int UNLOGIN_DISCOVERACTION_HOT_WORDS = 7;

    ///////////////Profile////////////

    int PROFILE_ICON_TYPE_FACEBOOK = 1;
    int PROFILE_ICON_TYPE_INSTAGRAM = 2;
    int PROFILE_ICON_TYPE_YOUTUBE = 3;

    int PROFILE_FROM_PAGE_ME = 1;
    int PROFILE_FROM_PAGE_OTHER = 2;

    int PROFILE_MORE_TYPE_SHARE = 1;
    int PROFILE_MORE_TYPE_OTHER = 2;

    int PROFILE_USER_TYPE_SELF = 1;
    int PROFILE_USER_TYPE_VISIT = 2;

    //////////////VIDEO///////////////
    int VIDEO_BUTTON_TYPE_AVATAR = 1;
    int VIDEO_BUTTON_TYPE_TEXT = 2;
    int VIDEO_BUTTON_TYPE_ROTATE = 3;
    int VIDEO_BUTTON_TYPE_CLOSE = 4;

    int VIDEO_MUTE_TYPE_VOICE_OFF = 1;
    int VIDEO_MUTE_TYPE_VOICE_ON = 2;

    //////////////GP Score//////////////
    int GP_ACTION_TYPE_REFRESH = 1;
    int GP_ACTION_TYPE_SHARE = 2;
    int GP_ACTION_TYPE_POST = 3;

    int GP_RETURN_RESULT_FAIL = 0;
    int GP_RETURN_RESULT_SUCCESS = 1;
    int GP_RETURN_RESULT_NONE = 2;

    ///////////Un login///////////
    int UNLOGIN_FROM_PAGE_OTHER = 0;
    int UNLOGIN_FROM_PAGE_HOME = 1;
    int UNLOGIN_FROM_PAGE_PROFILE = 2;
    int UNLOGIN_FROM_PAGE_POST_DETAIL = 3;
    int UNLOGIN_FROM_PAGE_TOPIC = 4;
    int UNLOGIN_FROM_PAGE_SEARCH = 5;

    ///////////Publish///////////
    int PUBLISH_ADD_TOPIC_RECOMMOND = 1;
    int PUBLISH_ADD_TOPIC_SEARCH = 2;

    //////////////New Share/////////
    int NEW_SHARE_CHANNLE_WHATSAPP = 4;
    int NEW_SHARE_CHANNLE_FACEBOOK = 1;
    int NEW_SHARE_CHANNLE_INSTAGRAM = 3;
    int NEW_SHARE_CHANNLE_COPYLINK = 5;
    int NEW_SHARE_CHANNLE_OTHER = 2;

    int NEW_SHARE_CONTENT_TYPE_POST = 1;
    int NEW_SHARE_CONTENT_TYPE_PROFILE = 4;
    int NEW_SHARE_CONTENT_TYPE_APP = 2;
    int NEW_SHARE_CONTENT_TYPE_TOPIC = 5;
    int NEW_SHARE_CONTENT_TYPE_H5 = 7;

    int NEW_SHARE_POST_TYPE_VIDEO = 3;
    int NEW_SHARE_POST_TYPE_PIC = 2;
    int NEW_SHARE_POST_TYPE_TEXT = 1;
    int NEW_SHARE_POST_TYPE_ARTICAL = 9;
    int NEW_SHARE_POST_TYPE_POLL = 6;
    int NEW_SHARE_POST_TYPE_POST_STATUS = 5;
    int NEW_SHARE_POST_TYPE_OTHER = 7;

    int NEW_SHARE_VIDEO_POST_TYPE_VIDEO = 1;
    int NEW_SHARE_VIDEO_POST_TYPE_LINK = 2;

    int NEW_SHARE_SHARE_FROM_CARD = 1;
    int NEW_SHARE_SHARE_FROM_VIDEO = 2;
    int NEW_SHARE_SHARE_FROM_POST_DETAIL = 3;
    int NEW_SHARE_SHARE_FROM_OTHER = 4;
    int NEW_SHARE_SHARE_FROM_ARTICAL = 5;
    int NEW_SHARE_SHARE_FROM_PHOTO = 6;

    int NEW_SHARE_POP_FROM_POST_DETAIL = 1;
    int NEW_SHARE_POP_FROM_POST_CARD = 2;
    int NEW_SHARE_POP_FROM_PROFILE = 3;
    int NEW_SHARE_POP_FROM_APP = 4;
    int NEW_SHARE_POP_FROM_TOPIC = 5;
    int NEW_SHARE_POP_FROM_H5 = 6;

    int NEW_SHARE_RESULT_SUCCESS = 1;
    int NEW_SHARE_RESULT_FAIL = 2;
    int NEW_SHARE_RESULT_UNKNOW = 3;

    //------------------------boom divider --------------------------------------//
    //------------------------FEED SOURCE  --------------------------------------//
    String FEED_SOURCE_HOME = "1";
    String FEED_SOURCE_DISCOVER = "2_";
    String FEED_SOURCE_DISCOVER_FOR_YOU = "2_0";
    String FEED_SOURCE_DISCOVER_VIDEO = "2_a";
    String FEED_SOURCE_DISCOVER_VIDEO_AFTER = "2_b";
    String FEED_SOURCE_DISCOVER_LATEST = "3";
    String FEED_SOURCE_SEARCH_LATEST = "4";
    String FEED_SOURCE_SEARCH_POSTS = "5";
    String FEED_SOURCE_LOCATION = "6";
    String FEED_SOURCE_PROFILE_POST = "7";
    String FEED_SOURCE_PROFILE_LIKE = "8";
    String FEED_SOURCE_ME_LIKES = "9";
    String FEED_SOURCE_TOPIC_HOT = "10";
    String FEED_SOURCE_TOPIC_LATEST = "11";
    String FEED_SOURCE_LOCATION_HOT = "12";
    String FEED_SOURCE_LOCATION_LATEST = "13";

    String FEED_SOURCE_UNLOGIN = "14_";
    String FEED_SOURCE_UNLOGIN_FOR_YOU = "14_0";
    String FEED_SOURCE_UNLOGIN_VIDEO = "14_a";
    String FEED_SOURCE_UNLOGIN_VIDEO_AFTER = "14_b";
    String FEED_SOURCE_UNLOGIN_LATEST = "15";
    String FEED_SOURCE_SUPER_TOPIC_HOT = "16";
    String FEED_SOURCE_SUPER_TOPIC_LATRST = "17";
    String FEED_SOURCE_VIDEO_AFTER = "18";


    int CLICK_AREA_HEAD = 1;
    int CLICK_AREA_MORE = 2;
    int CLICK_AREA_TEXT = 3;
    int CLICK_AREA_PIC = 4;
    int CLICK_AREA_VIDEO = 5;
    int CLICK_AREA_POLL = 6;

    int UNLOGIN_SELECT_TYPE_NO = 0;
    int UNLOGIN_SELECT_TYPE_YES = 1;



}
