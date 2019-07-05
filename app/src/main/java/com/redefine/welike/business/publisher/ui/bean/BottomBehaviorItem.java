package com.redefine.welike.business.publisher.ui.bean;

public class BottomBehaviorItem {
    public static final int BEHAVIOR_POST_ITEM_TYPE = 1;
    public static final int BEHAVIOR_ALUMB_ITEM_TYPE = 2;
    public static final int BEHAVIOR_VIDEO_ITEM_TYPE = 3;
    public static final int BEHAVIOR_SNAPSHOT_ITEM_TYPE = 4;
    public static final int BEHAVIOR_TOPIC_ITEM_TYPE = 5;
    public static final int BEHAVIOR_POLL_ITEM_TYPE = 6;
    public static final int BEHAVIOR_EASY_POST = 7;
    public static final int BEHAVIOR_EMOJI_TYPE = 8;
    public static final int BEHAVIOR_AT_TYPE = 9;
    public static final int BEHAVIOR_LINK_TYPE = 10;
    public static final int BEHAVIOR_ARTICLE_POST = 11;

    public int type;
    public int resource;
    public String name;
    public boolean enabled = true;
    public boolean isSelected = true;
    public int position = -1;

    public BottomBehaviorItem(int type, int entranceResource, String entranceName) {
        this.type = type;
        this.resource = entranceResource;
        this.name = entranceName;
    }
}
