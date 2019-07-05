package com.redefine.welike.business.publisher.management.handler

import com.redefine.welike.business.publisher.management.draft.IPublishDraft

/**
 *
 * Name: StartHandler
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 21:25
 *
 */
class StartHandler constructor(draft: IPublishDraft, minProgress: Int = 0, maxProgress: Int = 1, next: IHandler?) : AbstractHandler(draft, minProgress, maxProgress, next) {

    override fun start(callback: (IHandler, PublishMessage) -> Unit): Boolean {
        if (!super.start(callback)) {
            return false
        }
        callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.START))
        next(callback)

        return true
    }

}