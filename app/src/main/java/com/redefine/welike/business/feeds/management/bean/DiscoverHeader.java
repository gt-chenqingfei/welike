package com.redefine.welike.business.feeds.management.bean;

public class DiscoverHeader extends PostBase {
    /**
     * "id": 8,
     * "topicId": "GIRL",
     * "topicName": "girl",
     * "lanCode": "en",
     * "hot": 549,
     * "weight": 8,
     * "offlineTime": null,
     * "iconUrl": "https://img.welike.in/6d9cbfcc6937444e9f21634bf08f6327.png",
     * "rmdWords": "happening",
     * "status": 1,
     * "operator": "admin",
     * "createTime": 1544461153000,
     * "updateTime": 1545841446000,
     * "postCount": 0,
     */
    public static final int TYPE = -1000;

    public int id;
    public String topicId;
    public String topicName;
    public String lanCode;
    public int hot;
    public int weight;
    public String iconUrl;
    public String rmdWords;
    public int status;
    public String operator;
    public long createTime;
    public long updateTime;
    public int postCount;

    public int backgroudAt = 0;

}