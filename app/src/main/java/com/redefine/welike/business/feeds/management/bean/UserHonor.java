package com.redefine.welike.business.feeds.management.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserHonor implements Serializable {
    public static final int TYPE_TASK = 1;//任务勋章
    public static final int TYPE_EVENT = 2;//世界杯勋章

    @SerializedName("type")
    public int type = TYPE_EVENT;

    @SerializedName("honorPic")
    public String honorPic;

    @SerializedName("forwardUrl")
    public String forwardUrl;
}
