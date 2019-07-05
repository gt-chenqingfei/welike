package com.redefine.welike.business.publisher.management.task

import com.redefine.foundation.utils.NetWorkUtil
import com.redefine.welike.MyApplication
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.handler.HandlerQueueBuilder
import com.redefine.welike.business.publisher.management.handler.PublishMessage
import com.redefine.welike.business.publisher.management.handler.PublishPostHandler

/**
 *
 * Name: PublishPostTask
 * Author: liwenbo
 * Email:
 * Comment: //post发布器Task，用于抽象每个发布任务
 * Date: 2018-07-10 18:09
 *
 */
class PublishPostTask<T : IPublishDraft>(postDraft: T, callback: (IPublishTask, PublishMessage) -> Unit)
    : AbstractTask<T>(postDraft, callback) {

    private val MAX_RETRY_TIMES = 3
    private var retryTimes = MAX_RETRY_TIMES

    override fun start() {

        val callbackInterrupt: (IPublishTask, PublishMessage) -> Unit = { publishTask: IPublishTask, publishMessage: PublishMessage ->
            val isNetworkConnected = NetWorkUtil.isNetWorkConnected(MyApplication.getAppContext())
            PublishAnalyticsManager.getInstance().setRetryTimes(retryTimes, postDraft.getDraftId())
            if (publishMessage.state == PublishMessage.PublishState.ERROR && isNetworkConnected) {
                when {
                    publishMessage.errorCode == PublishMessage.ErrorCode.EXCEPTION ||
                            publishMessage.errorCode == PublishMessage.ErrorCode.ACCOUNT_INVALID ->

                        callback(publishTask, publishMessage)
                    retryTimes > MAX_RETRY_TIMES ->
                        callback(publishTask, publishMessage)
                    else -> {
                        retryTimes++
                        Thread.sleep((1000 * (10 - retryTimes)).toLong())
                        start()
                    }
                }
            } else {
                callback(publishTask, publishMessage)
            }
        }
        if (handlerQueue == null) {
            handlerQueue = HandlerQueueBuilder.buildQueue(this, postDraft, PublishPostHandler(postDraft))
        }
        handlerQueue?.start(callbackInterrupt)
    }
}