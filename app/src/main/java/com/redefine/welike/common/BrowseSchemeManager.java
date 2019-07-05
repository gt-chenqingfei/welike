package com.redefine.welike.common;

import java.net.URLEncoder;

public class BrowseSchemeManager {


    public static final String POST_DETAIL = "welike://com.redefine.welike/main/home?page_name=post_detail&pid=%s";
    public static final String USER_PROFILE = "welike://com.redefine.welike/main/home?page_name=user_profile&uid=%s&toast=%s";
    public static final String TOPIC_LANDING = "welike://com.redefine.welike/main/home?page_name=topic_landing&topic_id=%s&topic_name=%s";
    public static final String PUBLISHER = "welike://com.redefine.welike/main/home?page_name=publish&data=%s";


    private static BrowseSchemeManager browseSchemeManager;


    public static BrowseSchemeManager getInstance() {

        if (browseSchemeManager == null) browseSchemeManager = new BrowseSchemeManager();

        return browseSchemeManager;

    }

    private String schemeParam;

    public String getSchemeParam() {
        return schemeParam;
    }

    public void setPostDetail(String pid) {
        schemeParam = String.format(POST_DETAIL, pid);
    }

    public void setUserProfile(String uid) {
        schemeParam = String.format(USER_PROFILE, uid, "");
    }

    public void setTopicLanding(String tId, String tName) {
        schemeParam = String.format(TOPIC_LANDING, tId, URLEncoder.encode(tName));
    }

    public void setPublishData(String data) {
        schemeParam = String.format(PUBLISHER, data);
    }

    public void clear() {
        schemeParam = null;
    }

}
