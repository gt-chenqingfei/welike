package com.redefine.welike.business.publisher.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.redefine.welike.business.publisher.management.PublishManager
import com.redefine.welike.business.publisher.management.UiDispatcher
import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.draft.PublishPostDraft
import com.redefine.welike.business.publisher.management.handler.PublishMessage
import com.redefine.welike.business.publisher.management.task.IPublishTask

/**
 *
 * Name: PublishViewModel
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-20 18:31
 *
 */

class PublishViewModel(application: Application) : AndroidViewModel(application), PublishManager.IPublishCallback {

    private val mTasks = mutableMapOf<String, Int>()
    val mPublishState = MutableLiveData<PublishState>()
    val mPublishProgress = MutableLiveData<Int>()
    val mPublishOtherNewData = MutableLiveData<IPublishDraft>()

    override fun onPublishProgress(task: IPublishTask, progress: Int) {
        mTasks[task.getTaskId()] = progress
        mPublishProgress.postValue(progress)
    }

    override fun onPublishCompleted(task: IPublishTask, publishState: PublishMessage.PublishState?) {
        mTasks.remove(task.getTaskId())
        mPublishOtherNewData.postValue(null)
        if (mTasks.isEmpty()) {
            if (publishState == PublishMessage.PublishState.SUCCESS) {
                if (task.getPublishDraft() is PublishPostDraft) {
                    mPublishState.postValue(PublishState.PUBLISH_POST_SUCCESS)
                } else {
                    mPublishState.postValue(PublishState.PUBLISH_OTHER_SUCCESS)
                }
            } else if (publishState == PublishMessage.PublishState.CANCELED || publishState
                    == PublishMessage.PublishState.ERROR) {
                mPublishState.postValue(PublishState.PUBLISH_ONCE_FAILED)
            }
            UiDispatcher.dispatchDelay({
                mPublishState.postValue(PublishState.PUBLISH_END)
            }, 100L)
        } else {
            if (publishState == PublishMessage.PublishState.SUCCESS) {
                if (task.getPublishDraft() is PublishPostDraft) {
                    mPublishState.postValue(PublishState.PUBLISH_POST_SUCCESS)
                } else {
                    mPublishState.postValue(PublishState.PUBLISH_OTHER_SUCCESS)
                }
            } else if (publishState == PublishMessage.PublishState.CANCELED || publishState
                    == PublishMessage.PublishState.ERROR) {
                mPublishState.postValue(PublishState.PUBLISH_ONCE_FAILED)
            }
        }
    }


    override fun onPublishStart(task: IPublishTask) {
        mTasks[task.getTaskId()] = 0
        mPublishState.postValue(PublishState.PUBLISH_ONCE_START)
        mPublishOtherNewData.postValue(task.getPublishDraft())
    }

    init {
//        PublishManager.getInstance().registerPublishCallback(this)
    }


    override fun onCleared() {
        super.onCleared()
//        PublishManager.getInstance().unRegisterPublishCallback(this)
    }


    enum class PublishState {
        PUBLISH_END, PUBLISH_ONCE_START, PUBLISH_POST_SUCCESS, PUBLISH_ONCE_FAILED, PUBLISH_OTHER_SUCCESS
    }


}