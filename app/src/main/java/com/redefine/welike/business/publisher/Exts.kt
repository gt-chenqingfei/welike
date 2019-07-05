package com.redefine.welike.business.publisher

import android.util.Log
import com.redefine.im.BuildConfig
import com.redefine.richtext.RichContent
import com.redefine.welike.business.common.mission.MissionManager
import com.redefine.welike.business.common.mission.MissionType
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.commonui.event.model.PublishEventModel

/**
 * Copyright (C) 2018 redefine , Inc.
 * @author qingfei.chen
 * @date 2018-10-30
 */

fun d(tag: String? = "Publisher", content: String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, content)
    }
}

fun w(tag: String? = "Publisher", content: String) {
    if (BuildConfig.DEBUG) {
        Log.w(tag, content)
    }
}

fun e(tag: String? = "Publisher", content: String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, content)
    }
}

fun calculateEventData(richContent: RichContent, publishEventModel: PublishEventModel) {
    try {
        PublisherEventManager.INSTANCE.words_num = richContent.text.toByteArray().size
        var linkNum = 0
        var atNum = 0
        var topicNum = 0
        val richItemList = richContent.richItemList
        for (richItem in richItemList) {
            if (richItem.isLinkItem) {
                linkNum++
            } else if (richItem.isAtItem) {
                atNum++
            } else if (richItem.isTopicItem) {
                topicNum++
                if (richItem.display != null && richItem.display.equals("#New Weliker", ignoreCase = true)) {
                    MissionManager.notifyEvent(MissionType.TOPIC_NEW_WELIKER)
                }
            }
        }


        publishEventModel.linkNum = linkNum
        publishEventModel.atNum = atNum
        publishEventModel.topicNum = topicNum
        publishEventModel.wordNum = richContent.text.toByteArray().size

        PublisherEventManager.INSTANCE.web_link = linkNum
        PublisherEventManager.INSTANCE.at_num = atNum
        PublisherEventManager.INSTANCE.topic_num = topicNum
    } catch (e: Exception) {
        e.printStackTrace()
    }


}