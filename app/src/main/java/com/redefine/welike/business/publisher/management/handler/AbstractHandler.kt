package com.redefine.welike.business.publisher.management.handler

import com.redefine.foundation.utils.NetWorkUtil
import com.redefine.welike.MyApplication
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.w

open class AbstractHandler constructor(val draft: IPublishDraft, var minProgress: Int = 0, var maxProgress: Int = 1, var next: IHandler?) : IHandler {

    constructor(draft: IPublishDraft) : this(draft, 0, 1, null)

    var isCanceled: Boolean = false
    var uid: String = ""

    override fun start(callback: (IHandler, PublishMessage) -> Unit): Boolean {
        w("PublishHandler", this.javaClass.simpleName + ":start")
        if (!NetWorkUtil.isNetWorkConnected(MyApplication.getAppContext())) {
            callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.ERROR,
                    PublishMessage.ErrorCode.NETWORK_DIS_CONNECT))
            return false
        }

        val account = AccountManager.getInstance().account
        if (account == null) {
            callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.ERROR,
                    PublishMessage.ErrorCode.ACCOUNT_INVALID))
            return false
        }
        uid = account.uid
        return true
    }

    fun next(callback: (IHandler, PublishMessage) -> Unit) {
        synchronized(this) {
            if (next == null) {
                when {
                    isLast() -> callback(this,
                            PublishMessage(maxProgress, PublishMessage.PublishState.SUCCESS))
                    isCanceled -> callback(this,
                            PublishMessage(maxProgress, PublishMessage.PublishState.CANCELED))
                    else -> callback(this,
                            PublishMessage(maxProgress, PublishMessage.PublishState.ERROR))
                }
            } else {
                next?.start(callback)
            }
        }
    }

    override fun cancel() {
        synchronized(this) {
            next = null
            isCanceled = true
        }
    }

    override fun isLast(): Boolean {
        return false
    }


}