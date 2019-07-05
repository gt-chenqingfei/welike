package com.redefine.welike.business.publisher.management

import android.arch.lifecycle.MutableLiveData
import com.redefine.foundation.io.FileManager
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.bean.DraftPost
import com.redefine.welike.business.publisher.management.draft.PublishPostDraft
import com.redefine.welike.business.publisher.management.handler.PublishMessage
import com.redefine.welike.business.publisher.management.task.IPublishTask
import com.redefine.welike.commonui.util.ToastUtils

/**
 * Created by liubin on 2018/1/15.
 */

class FeedPoster private constructor() : AbsFeedPublish<DraftPost>() {
    val mSendStateLiveData: MutableLiveData<PublishStateBean> = MutableLiveData()
    val mProgressLiveData: MutableLiveData<ProgressBean> = MutableLiveData()

    private val mDraftPostList: ArrayList<DraftPost> = ArrayList()
    private var isRestorePublish: Boolean = true
    private val mPublishMap: HashMap<String, String> = HashMap()

    override fun onPublishProgress(task: IPublishTask, progress: Int) {
        super.onPublishProgress(task, progress)
        mProgressLiveData.postValue(ProgressBean(task.getPublishDraft().getDraftId(), progress))
    }

    override fun publish(postDraft: DraftPost?) {
        super.publish(postDraft)

        if (postDraft == null) {
            return
        }

        if (!canPublish(postDraft)) {
            return
        }
        if (postDraft.state == DraftPost.STATE_UPLOADING) {
            return
        }

        synchronized(mPublishMap) {
            mPublishMap.put(postDraft.draftId, postDraft.draftId)
        }
        if (!postDraft.content.hasArticleItem()) {
            postDraft.article = null
        }
        postDraft.state = DraftPost.STATE_UPLOADING
        postDraft.isShow = false
        DraftManager.getInstance().insertOrUpdate(postDraft)
        PublishManager.getInstance().publish(PublishPostDraft(postDraft), this)
    }

    override fun onPublishStart(task: IPublishTask) {
        super.onPublishStart(task)
        mSendStateLiveData.postValue(PublishStateBean(task.getPublishDraft().getDraftId(),
                PublishMessage.PublishState.START))
    }

    override fun onPublishSuccess(task: IPublishTask, state: PublishMessage.PublishState?) {
        synchronized(mPublishMap) {
            if (mPublishMap.containsKey(task.getPublishDraft().getDraftId())) {
                ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "editor_send_successed"))
                updatePublishState(task, DraftPost.STATE_UPLOAD_SUCCESS)
                mPublishMap.remove(task.getPublishDraft().getDraftId())
                mSendStateLiveData.postValue(PublishStateBean(task.getPublishDraft().getDraftId(), state))
                Thread.sleep(1 * 1000)
                removeDraftFromDb(task.getPublishDraft())
                deleteFileIfNeed(task)
            }
        }
    }

    fun deleteFileIfNeed(task: IPublishTask) {
        val draftPost = task.getPublishDraft().getDraftSerializable() as DraftPost
        if (CollectionUtil.isEmpty(draftPost.picDraftList)) {
            return
        }

        val richItem = draftPost.content.richItemList.find { "TOPIC" == it.type }
        richItem?.let {
            val picAttachment = draftPost.picDraftList.getOrNull(0)
            picAttachment?.let { picAttachment ->
                val file = FileManager.getInstance().getFileInSDRoot(WeLikeFileManager.PHOTO_SAVE_DIR)
                if (file != null && picAttachment.localFileName.startsWith(file.path)) {
                    WeLikeFileManager.deleteFile(picAttachment.localFileName)
                }
            }
        }
    }

    override fun onPublishError(task: IPublishTask, state: PublishMessage.PublishState?) {
        if (mPublishMap.containsKey(task.getPublishDraft().getDraftId())) {
            task.getPublishDraft().getDraftSerializable().state = DraftPost.STATE_UPLOAD_FAILED
            updateDraftVisibleOnPublishError(task.getPublishDraft())
            val errorCode = if (state === PublishMessage.PublishState.SUCCESS)
                ErrorCode.ERROR_SUCCESS else ErrorCode.ERROR_NETWORK_INVALID
            ToastUtils.showShort(ErrorCode.showErrCodeText(errorCode))
        }
    }

    fun remove(postDraft: DraftPost?) {
        postDraft?.let {
            synchronized(mPublishMap) {
                if (mPublishMap.containsKey(it.draftId)) {
                    mPublishMap.remove(it.draftId)
                }
                it.state = DraftPost.STATE_NONE
                DraftManager.getInstance().insertOrUpdate(it)
            }
        }
    }

    fun notifyDraftPostData(data: ArrayList<DraftPost>) {
        e("FeedPoster", "notifyDraftPostData：size=${data.size}")
        mDraftPostList.clear()
        mDraftPostList.addAll(data)
        restorePublishIfNeeded()
    }

    private fun restorePublishIfNeeded() {
        if (isRestorePublish) {
            mDraftPostList.forEach {
                it.state = DraftPost.STATE_NONE
                publish(it)
            }
            isRestorePublish = false
        }
    }

    private fun canPublish(postDraft: DraftPost): Boolean {
        val isOverLimit = mPublishMap.size >= 5
        val isInPostQueue = mPublishMap.containsKey(postDraft.draftId)
        e("FeedPoster", "isOverLimit：size=${mPublishMap.size},isInPostQueue=$isInPostQueue")
        if (!isOverLimit) {
            return true
        }
        if (isInPostQueue) {
            return true
        }
        ToastUtils.showShort(ResourceTool.getString("publish_post_over_limit"))
        return false
    }

    fun isPostLimitOver(): Boolean {
        val isOverLimit = mPublishMap.size >= 5
        if (isOverLimit) {
            ToastUtils.showShort(ResourceTool.getString("publish_post_over_limit"))
            return true
        }
        return false
    }

    private fun updatePublishState(post: IPublishTask?, state: Int) {
        post?.let {
            val post = it.getPublishDraft().getDraftSerializable()
            post.state = state
            DraftManager.getInstance().insertOrUpdate(post)
        }
    }

    companion object {
        fun getInstance(): FeedPoster {
            return Holder.INSTANCE
        }
    }

    private object Holder {
        val INSTANCE = FeedPoster()
    }

}
