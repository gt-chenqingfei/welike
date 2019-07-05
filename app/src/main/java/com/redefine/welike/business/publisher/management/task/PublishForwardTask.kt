package com.redefine.welike.business.publisher.management.task

import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.handler.HandlerQueueBuilder
import com.redefine.welike.business.publisher.management.handler.PublishForwardHandler
import com.redefine.welike.business.publisher.management.handler.PublishMessage

class PublishForwardTask<T : IPublishDraft>(postDraft: T, callback: (IPublishTask, PublishMessage) -> Unit)
    : AbstractTask<T>(postDraft, callback) {

    override fun start() {
        handlerQueue = HandlerQueueBuilder.buildQueue(this, postDraft, PublishForwardHandler(postDraft))
        handlerQueue?.start(callback)
    }
}