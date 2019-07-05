package com.redefine.welike.business.publisher.management

import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.draft.IPublishDraft
import com.redefine.welike.business.publisher.management.handler.PublishMessage
import com.redefine.welike.business.publisher.management.task.IPublishTask
import com.redefine.welike.commonui.util.ToastUtils

/**
 * @author qingfei.chen
 * @date 2018/12/27
 * Copyright (C) 2018 redefine , Inc.
 */
abstract class AbsFeedPublish<T : DraftBase> : PublishSubject<T>, PublishManager.IPublishCallback {

    override fun publish(draft: T?) {
        if (draft == null) {
            return
        }
//        PublishManager.getInstance().registerPublishCallback(this)
    }

    override fun onPublishStart(task: IPublishTask) {
        saveDraftIfNeeded(task.getPublishDraft())
    }

    override fun onPublishProgress(task: IPublishTask, progress: Int) {

    }

    override fun onPublishCompleted(task: IPublishTask, state: PublishMessage.PublishState?) {
        when (state) {
            PublishMessage.PublishState.SUCCESS -> {
                onPublishSuccess(task, state)
            }
            PublishMessage.PublishState.ERROR -> {
                onPublishError(task, state)
            }
            PublishMessage.PublishState.CANCELED -> {
                onPublishCancel(task, state)
            }
        }
    }

    open fun onPublishSuccess(task: IPublishTask, state: PublishMessage.PublishState?) {
        removeDraftFromDb(task.getPublishDraft())
        ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "editor_send_successed"))
    }

    open fun onPublishError(task: IPublishTask, state: PublishMessage.PublishState?) {
        updateDraftVisibleOnPublishError(task.getPublishDraft())
        val errorCode = if (state === PublishMessage.PublishState.SUCCESS)
            ErrorCode.ERROR_SUCCESS else ErrorCode.ERROR_NETWORK_INVALID
        ToastUtils.showShort(ErrorCode.showErrCodeText(errorCode))
    }

    fun onPublishCancel(task: IPublishTask, state: PublishMessage.PublishState?) {
        removeDraftFromDb(task.getPublishDraft())
        val errorCode = if (state === PublishMessage.PublishState.SUCCESS)
            ErrorCode.ERROR_SUCCESS else ErrorCode.ERROR_NETWORK_INVALID
        ToastUtils.showShort(ErrorCode.showErrCodeText(errorCode))
    }

    /**
     * Save to database before publishing, delete after publish completed
     */
    private fun saveDraftIfNeeded(draft: IPublishDraft) {
        val draftSerializable = draft.getDraftSerializable()
        if (!draftSerializable.isSaveDB) {
            return
        }
        draftSerializable.isShow = false
        DraftManager.getInstance().insertOrUpdate(draftSerializable)
    }

    /**
     * Delete after publish completed
     */
    fun removeDraftFromDb(draft: IPublishDraft) {
        val draftSerializable = draft.getDraftSerializable()
        if (!draftSerializable.isSaveDB) {
            return
        }
        DraftManager.getInstance().delete(draftSerializable)
    }

    /**
     * Update draft show when publish error
     */
    fun updateDraftVisibleOnPublishError(draft: IPublishDraft) {
        val draftSerializable = draft.getDraftSerializable()
        if (!draftSerializable.isSaveDB) {
            return
        }

        draftSerializable.isShow = true
        DraftManager.getInstance().resetDraftUncompletedResource(draftSerializable)
        DraftManager.getInstance().insertOrUpdate(draftSerializable)
    }

}

class ProgressBean(val draftId: String, val progress: Int)

class PublishStateBean(val draftId: String, val state: PublishMessage.PublishState?)
