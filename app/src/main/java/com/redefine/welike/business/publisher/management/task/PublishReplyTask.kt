package com.redefine.welike.business.publisher.management.task

import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.handler.HandlerQueueBuilder
import com.redefine.welike.business.publisher.management.handler.PublishMessage
import com.redefine.welike.business.publisher.management.handler.PublishReplyHandler

class PublishReplyTask<T : IPublishDraft>(postDraft: T, callback: (IPublishTask, PublishMessage) -> Unit)
    : AbstractTask<T>(postDraft, callback) {

    override fun start() {
        handlerQueue = HandlerQueueBuilder.buildQueue(this, postDraft, PublishReplyHandler(postDraft))
        handlerQueue?.start(callback)
    }
}