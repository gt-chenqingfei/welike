package com.redefine.welike.commonui.event.model

import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1

/**
 * @author qingfei.chen
 * @date 2018/12/3
 * Copyright (C) 2018 redefine , Inc.
 */
class PublishEventProxy(val eventModel: PublishEventModel) {

    fun report1() {
        EventLog1.Publish.report1(eventModel.source, eventModel.mainSource, eventModel.pageType)

        EventLog.Publish.report1(PublisherEventManager.INSTANCE.source,
                PublisherEventManager.INSTANCE.main_source,
                PublisherEventManager.INSTANCE.page_type)
    }

    fun report2() {
        EventLog1.Publish.report2(eventModel.source, eventModel.mainSource)
    }

    fun report3() {
        EventLog1.Publish.report3(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report4() {
        EventLog1.Publish.report4(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report5() {
        AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_POSTER_LOCATION)
        EventLog.Publish.report5(PublisherEventManager.INSTANCE.source,
                PublisherEventManager.INSTANCE.main_source,
                PublisherEventManager.INSTANCE.page_type)
        EventLog1.Publish.report5(eventModel.source, eventModel.mainSource)
    }

    fun report6() {
        EventLog1.Publish.report6(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report7() {
        EventLog1.Publish.report7(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report8() {
        EventLog1.Publish.report8(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }


    fun report9() {
        EventLog1.Publish.report9(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report10() {
        EventLog1.Publish.report10(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report11() {
        EventLog1.Publish.report11(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report12() {
        EventLog1.Publish.report12(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report13() {
        EventLog1.Publish.report13(eventModel.source, eventModel.mainSource, eventModel.pageType, eventModel.existState, eventModel.wordNum, eventModel.pictureNum, eventModel.pictureSize, eventModel.pictureUploadTime, eventModel.videoNum, eventModel.videoSize, eventModel.videoConvertTime, eventModel.videoUploadTime, eventModel.linkNum, eventModel.poolType, eventModel.pollNum, eventModel.pollTime, eventModel.emojiNum, eventModel.atNum, eventModel.topicNum, eventModel.topicSource, eventModel.alsoRepost, eventModel.alsoComment, eventModel.postId, eventModel.repostId, eventModel.topicId, eventModel.community, eventModel.draftId)
    }

    fun report14() {
        EventLog1.Publish.report14(eventModel.source, eventModel.mainSource, eventModel.pageType, eventModel.existState, eventModel.wordNum, eventModel.pictureNum, eventModel.pictureSize, eventModel.pictureUploadTime, eventModel.videoNum, eventModel.videoSize, eventModel.videoConvertTime, eventModel.videoUploadTime, eventModel.linkNum, eventModel.poolType, eventModel.pollNum, eventModel.pollTime, eventModel.emojiNum, eventModel.atNum, eventModel.topicNum, eventModel.topicSource, eventModel.alsoRepost, eventModel.alsoComment, eventModel.postId, eventModel.repostId, eventModel.topicId, eventModel.community)
    }

    fun report15() {
        EventLog1.Publish.report15(eventModel.source, eventModel.mainSource, eventModel.pageType,
                eventModel.existState, eventModel.wordNum, eventModel.pictureNum, eventModel.pictureSize,
                eventModel.pictureUploadTime, eventModel.videoNum, eventModel.videoSize,
                eventModel.videoConvertTime, eventModel.videoUploadTime, eventModel.linkNum,
                eventModel.poolType, eventModel.pollNum, eventModel.pollTime, eventModel.emojiNum,
                eventModel.atNum, eventModel.topicNum, eventModel.topicSource, eventModel.alsoRepost,
                eventModel.alsoComment, eventModel.postId, eventModel.repostId, eventModel.topicId,
                eventModel.community, eventModel.postLa, eventModel.postTags, eventModel.contentUid,
                PublishEventModel.uploadType, eventModel.sequenceId, eventModel.retryTimes, eventModel.draftId)
    }

    fun report16() {
        EventLog1.Publish.report16(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report17() {
        EventLog1.Publish.report17(eventModel.source, eventModel.mainSource, eventModel.pageType)
        EventLog.Publish.report24(PublisherEventManager.INSTANCE.source,
                PublisherEventManager.INSTANCE.main_source,
                PublisherEventManager.INSTANCE.page_type,
                PublisherEventManager.INSTANCE.at_source)
    }

    fun report18() {
        EventLog1.Publish.report18(eventModel.source, eventModel.mainSource, eventModel.pageType, eventModel.topicSource)
    }

    fun report19() {
        EventLog1.Publish.report19(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report20() {
        EventLog1.Publish.report20(eventModel.source, eventModel.mainSource, eventModel.pageType, eventModel.topicSource, eventModel.topicId)
    }

    fun report21() {
        EventLog1.Publish.report21(eventModel.source, eventModel.mainSource, eventModel.pageType)
    }

    fun report22() {
        EventLog1.Publish.report22(eventModel.source, eventModel.mainSource, eventModel.pageType,
                eventModel.existState, eventModel.wordNum, eventModel.pictureNum, eventModel.pictureSize,
                eventModel.pictureUploadTime, eventModel.videoNum, eventModel.videoSize, eventModel.videoConvertTime,
                eventModel.videoUploadTime, eventModel.linkNum, eventModel.poolType, eventModel.pollNum,
                eventModel.pollTime, eventModel.emojiNum, eventModel.atNum, eventModel.topicNum,
                eventModel.topicSource, eventModel.alsoRepost, eventModel.alsoComment, eventModel.postId,
                eventModel.repostId, eventModel.topicId, eventModel.community, PublishEventModel.uploadType,
                eventModel.sequenceId, eventModel.errorCode, eventModel.errorMessage, eventModel.retryTimes, eventModel.draftId)
    }

    fun report34() {
        EventLog1.Publish.report34(eventModel.draftId, eventModel.reSendFrom)
    }

    fun report35() {
        EventLog1.Publish.report35(eventModel.draftId)
    }


}