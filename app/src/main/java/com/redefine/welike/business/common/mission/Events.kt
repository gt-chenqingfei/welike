package com.redefine.welike.business.common.mission

enum class MissionType(val value: Int, var scheme: String) {
    LIKE(1, "welike://com.redefine.welike/main/home?page_name=main_home"),// 送出一个赞
    DISCOVERY_TAB(2, "welike://com.redefine.welike/main/home?page_name=main_discover"),//  探索发现页
    HOT_FEED(3, "welike://com.redefine.welike/main/home?page_name=main_discover"),// 查看热门微博
    RANK(4, "welike://com.redefine.welike/main/home?page_name=main_discover"),// 查看排行榜
    BROWSE_OFFICIAL_ACTIVE(5, "welike://com.redefine.welike/main/home?page_name=main_discover"),// 浏览平台活动
    AVATAR(6, "welike://com.redefine.welike/main/home?page_name=edit_user_info&entry_type=assignment"),// 完善头像
    INTRODUCTION(7, "welike://com.redefine.welike/main/home?page_name=edit_user_info&entry_type=assignment"),// 完善个人签名
    TOPIC_NEW_WELIKER(8, "welike://com.redefine.welike/main/home?page_name=publish&entry_type=assignment&hash_tag=%23New%20Weliker&hash_type=TOPIC&hash_id=NEW_WELIKER"),//  发布一条#New Weliker 的post
    TASK_LIST(9, "welike://com.redefine.welike/main/home?page_name=main_mine"),//  查看任务列表
    COMMENT_NEW_WELIKER(10, "welike://com.redefine.welike/main/home?page_name=topic_landing&entry_type=assignment&topic_name=%23New%20Weliker&topic_id=NEW_WELIKER"),// 在#New Weliker中评论1个post
    LIKE_NEW_WELIKER(11, "welike://com.redefine.welike/main/home?page_name=topic_landing&entry_type=assignment&topic_name=%23New%20Weliker&topic_id=NEW_WELIKER"),//  在#New Weilike中送出1个赞
    FOLLOW_NEW_WELIKER(12, "welike://com.redefine.welike/main/home?page_name=topic_landing&entry_type=assignment&topic_name=%23New%20Weliker&topic_id=NEW_WELIKER"),//  在#New Weliker中关注1个用户
    COMMENT(13, "welike://com.redefine.welike/main/home?page_name=main_discover"),// 在热门微博下，发布一条评论
    SHARE_PROFILE(14, "welike://com.redefine.welike/main/home?page_name=share&from=4&entry_type=assignment"),// 邀请你的好友加入welike吧
    HAVE_FRIEND(15, "welike://com.redefine.welike/main/home?page_name=follow&tab_name=follower&entry_type=assignment"),// 拥有1个朋友
    TOTAL_SIGN(16, ""),//  再登录1天（累计签到2天）
    PHONE_BOOK(17, ""),//  -
    SHARE_POST(18, "welike://com.redefine.welike/main/home?page_name=main_discover"),//  分享1条热门post到其他平台
    FOLLOW(19, "welike://com.redefine.welike/main/home?page_name=main_discover"),//  当前关注量超过10人
    FORWARD(20, "welike://com.redefine.welike/main/home?page_name=main_discover"),//  转发
    FANS(21, "welike://com.redefine.welike/main/home?page_name=follow&tab_name=follower&entry_type=assignment"),//  累计粉丝量超过20人
    INVITE(22, "welike://com.redefine.welike/main/home?page_name=share&from=2&entry_type=assignment"),//   邀请成功人数超过5人
    KEEP_SIGN(23, "")// 今天起连续签到3天
}

enum class TipType{
    HOME_VOTE_STUFF,ME_POST,DISCOVERY_SHARE,EDITOR_SHOW
}