package com.redefine.welike.business.message.management.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by MR on 2018/1/26.
 */

public class NotificationCount {

    private static final String MENTION = "MENTION";
    private static final String COMMENT = "COMMENT";
    private static final String LIKE = "LIKE";
    private static final String PUSH = "OPERATE";

    public int mention = 0;
    public int comment = 0;
    public int like = 0;
    public int push = 0;


    public static NotificationCount parse(JSONObject result) {
        try {
            NotificationCount count = new NotificationCount();
            count.mention = result.getIntValue(MENTION);
            count.comment = result.getIntValue(COMMENT);
            count.like = result.getIntValue(LIKE);
            count.push = result.getIntValue(PUSH);
            return count;
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }
}
