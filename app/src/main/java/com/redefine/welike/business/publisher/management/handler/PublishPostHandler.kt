package com.redefine.welike.business.publisher.management.handler

import com.redefine.welike.base.ErrorCode
import com.redefine.welike.business.publisher.api.PublishApiService
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.draft.PublishPostDraft
import com.redefine.welike.net.RetrofitManager
import com.redefine.welike.net.subscribeExt
import io.reactivex.schedulers.Schedulers

/**
 *
 * Name: StartHandler
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 21:25
 *
 */
class PublishPostHandler constructor(draft: IPublishDraft) : AbstractHandler(draft) {


    private val mPostService = RetrofitManager.getInstance().retrofit.create(PublishApiService::class.java)

    override fun start(callback: (IHandler, PublishMessage) -> Unit): Boolean {
        if (!super.start(callback)) {
            return false
        }

        e("PublishPostHandler", "from:${draft.getDraftSerializable().from}")
        callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.RUNNING))

        val publishPostBean = PostRequestBeanFactory.buildPublishBean(draft as PublishPostDraft)

        mPostService.post(uid, publishPostBean)
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribeExt({
                    if (it.code == ErrorCode.ERROR_NETWORK_SUCCESS) {
                        next(callback)
                        PublishAnalyticsManager.getInstance().postAnalyticsComplete(it.result, draft.getDraftId())
                    } else {
                        val errorMessage = "code:${it.code} ,msg:${it.resultMsg}"
                        callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.ERROR,
                                PublishMessage.ErrorCode.SERVER_ERROR, errorMessage))
                    }
                }, {
                    callback(this, PublishMessage(maxProgress, PublishMessage.PublishState.ERROR,
                            PublishMessage.ErrorCode.EXCEPTION, it.message))
                })

        return true
    }


}