package com.redefine.welike.business.feeds.management.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PollInfo implements Serializable {


    public static int FEED_POLL_TYPE_PIC = 0;
    public static int FEED_POLL_TYPE_TEXT = 1;

    private static final long serialVersionUID = 7128663261104941476L;
    public long createTime;   // 创建时间
    public long expiredTime;  // 相对值剩余过期时间
    public String pollId;
    public int totalCount = 0;
    public List<PollItemInfo> pollItemInfoList;
    public long expired;   // 绝对值有效期
    public boolean expirePoll;  //是否已经过期

    //    public UserBase userBase;//发布投票人用户信息
    public String checkOption;// 点击选项控制
    public String visibilityOption;// 可见性控制
    public String pollDesc;// 投票描述
    public int poolType = FEED_POLL_TYPE_PIC;
    public boolean isPoll;//是否已经投票
    public boolean hotPoll = false;
    public List<PollParticipants> pollParticipants = new ArrayList<>();

}
