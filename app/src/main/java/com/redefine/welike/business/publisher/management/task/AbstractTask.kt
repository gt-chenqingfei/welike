package com.redefine.welike.business.publisher.management.task

import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.handler.HandlerQueue
import com.redefine.welike.business.publisher.management.handler.PublishMessage

/**
 *
 * Name: PublishPostTask
 * Author: liwenbo
 * Email:
 * Comment: //post发布器Task，用于抽象每个发布任务
 * Date: 2018-07-10 18:09
 *
 */
abstract class AbstractTask<T : IPublishDraft> constructor(val postDraft: T, val callback: (IPublishTask, PublishMessage) -> Unit) : IPublishTask {

    var handlerQueue: HandlerQueue? = null

    override fun getState(): PublishMessage.PublishState {
        return handlerQueue?.getPublishState() ?: PublishMessage.PublishState.INIT
    }

    override fun getTaskId(): String {
        return postDraft.getDraftId()
    }

    override fun cancel(): Boolean {
        return handlerQueue?.cancel() ?: false
    }

    override fun getPublishDraft(): IPublishDraft {
        return postDraft
    }

}