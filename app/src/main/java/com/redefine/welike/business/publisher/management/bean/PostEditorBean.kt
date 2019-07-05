package com.redefine.welike.business.publisher.management.bean


/**
 * @author qingfei.chen
 * @date 2018/11/13
 * Copyright (C) 2018 redefine , Inc.
 */
data class TopicWrapper(val bean: TopicSearchSugBean.TopicBean, val index: Int = -1, val withOutFlag: Boolean)

data class MentionWrapper(var mention: String?, var uid: String?, var withOutFlag: Boolean)

data class CheckState(val checked: Boolean, val isClick: Boolean)
open class MenuState() {
    var sendBtnEnable: Boolean = false
    var topicCount: Int = 0
    var superTopicCount: Int = 0
    var hasFocus: Boolean = false

    constructor(hasFocus: Boolean, sendBtnEnable: Boolean, topicCount: Int, superTopicCount: Int) : this() {
        this.hasFocus = hasFocus
        this.sendBtnEnable = sendBtnEnable
        this.topicCount = topicCount
        this.superTopicCount = superTopicCount
    }
}

class PostMenuState() : MenuState() {
    var hasVideo: Boolean = false
    var hasPic: Boolean = false
    var hasPoll: Boolean = false

    constructor(hasVideo: Boolean, hasPic: Boolean, hasPoll: Boolean,
                hasFocus: Boolean, sendBtnEnable: Boolean, topicCount: Int, superTopicCount: Int) : this() {

        this.hasVideo = hasVideo
        this.hasPic = hasPic
        this.hasPoll = hasPoll
        this.hasFocus = hasFocus
        this.sendBtnEnable = sendBtnEnable
        this.topicCount = topicCount
        this.superTopicCount = superTopicCount
    }

    constructor(hasPoll: Boolean) : this() {
        this.hasPoll = hasPoll
    }
}