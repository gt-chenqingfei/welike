package com.redefine.welike.business.publisher.management.task

import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.handler.PublishMessage

/**
 *
 * Name: IPublishTask
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 18:29
 *
 */
interface IPublishTask {
    fun start()
    fun cancel(): Boolean
    fun getState(): PublishMessage.PublishState
    fun getTaskId(): String
    fun getPublishDraft(): IPublishDraft
}