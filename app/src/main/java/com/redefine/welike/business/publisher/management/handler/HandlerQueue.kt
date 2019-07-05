package com.redefine.welike.business.publisher.management.handler

import com.redefine.welike.business.publisher.management.task.IPublishTask

/**
 *
 * Name: HandlerQueue
 * Author: liwenbo
 * Email:
 * Comment: //责任链Queue，用于存放责任链的每个责任链实体
 * Date: 2018-07-10 18:07
 *
 */
class HandlerQueue constructor(val publishTask: IPublishTask, private val firstHandler: IHandler) {
    private var publishMessage: PublishMessage = PublishMessage()
    private var currentHandle: IHandler = firstHandler

    fun start(callback: (IPublishTask, PublishMessage) -> Unit) {
        firstHandler.start { iHandler: IHandler, publishMessage: PublishMessage ->
            this.currentHandle = iHandler
            this.publishMessage = publishMessage
            callback(publishTask, publishMessage)
        }
    }

    fun cancel(): Boolean {
        return false
    }

    fun getPublishState(): PublishMessage.PublishState {
        return publishMessage.state
    }

}