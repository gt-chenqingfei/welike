package com.redefine.welike.business.publisher.management.task

import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.handler.HandlerQueueBuilder
import com.redefine.welike.business.publisher.management.handler.PublishCommentHandler
import com.redefine.welike.business.publisher.management.handler.PublishMessage

class PublishCommentTask<T : IPublishDraft>(postDraft: T, callback: (IPublishTask, PublishMessage) -> Unit)
    : AbstractTask<T>(postDraft, callback) {
    override fun start() {
        handlerQueue = HandlerQueueBuilder.buildQueue(this, postDraft, PublishCommentHandler(postDraft))
        handlerQueue?.start(callback)
    }
}