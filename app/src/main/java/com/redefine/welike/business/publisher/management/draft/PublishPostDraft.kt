package com.redefine.welike.business.publisher.management.draft

import android.text.TextUtils
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.richtext.RichContent
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.business.location.management.bean.Location
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.management.bean.DraftPoll
import com.redefine.welike.business.publisher.management.bean.DraftPost

/**
 * Name: PublishPostDraft
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 17:33
 */
class PublishPostDraft constructor(private val draftId: String) : IPublishDraft {


    var picAttachmentDraft: PicAttachmentDraft = PicAttachmentDraft()
    var videoAttachmentDraft: VideoAttachmentDraft? = null
    var mRichContent: RichContent? = null
    var mArticleDraft: ArticleDraft? = null
    var mPollDraft: DraftPoll? = null
    var mLocation: Location? = null
    var communityId: String? = null
    lateinit var draft: DraftBase

    constructor(postDraft: DraftPost) : this(postDraft.draftId) {
        draft = postDraft
        mArticleDraft = postDraft.article
        if (!CollectionUtil.isEmpty(postDraft.pollItemInfos)) {
            mPollDraft = DraftPoll(postDraft.pollItemInfos, postDraft.expiredTime)
        }
        communityId = postDraft.superTopicId
        mLocation = postDraft.location
        if (postDraft.video != null) {
            val videoItemDraft = VideoItemDraft(postDraft.video.localFileName
                    , postDraft.video.attachmentDraftId
                    , postDraft.video.uploadLocalFileName
                    , postDraft.video.url
                    , postDraft.video.objectKey
                    , postDraft.video.mimeType
                    , postDraft.video.width
                    , postDraft.video.height
                    , postDraft.video.duration
                    , isVideoFromRecorder = postDraft.video.isFromRecorder)


            var picItemDraft: PicItemDraft? = null
            if (postDraft.videoThumb != null) {
                picItemDraft = PicItemDraft(postDraft.videoThumb.localFileName)
                picItemDraft.url = postDraft.videoThumb.url
                picItemDraft.width = postDraft.videoThumb.width
                picItemDraft.height = postDraft.videoThumb.height
                picItemDraft.mimeType = postDraft.videoThumb.mimeType

                videoAttachmentDraft?.videoCoverDraft = picItemDraft
            }
            videoAttachmentDraft = VideoAttachmentDraft(videoItemDraft, picItemDraft)


        } else if (!CollectionUtil.isEmpty(postDraft.picDraftList)) {
            picAttachmentDraft = PicAttachmentDraft()
            picAttachmentDraft.picList = ArrayList()
            postDraft.picDraftList.forEach {
                val picItemDraft = PicItemDraft(it.localFileName
                        , it.attachmentDraftId
                        , it.uploadLocalFileName
                        , it.url
                        , it.objectKey
                        , it.mimeType
                        , it.width
                        , it.height)
                picAttachmentDraft.picList.add(picItemDraft)
            }
        }
        mRichContent = postDraft.content
    }

    override fun getDraftSerializable(): DraftBase {
        return draft
    }

    override fun getUploadDraft(): IUploadAttachmentDraft? {
        if (videoAttachmentDraft == null) {
            return picAttachmentDraft
        }
        return videoAttachmentDraft
    }

    override fun getArticleDraft(): ArticleDraft? {
        return mArticleDraft
    }

    override fun getDraftId(): String {
        return draftId
    }

    override fun getRichContent(): RichContent? {
        return mRichContent
    }

    fun setArticleDraft(articleDraft: ArticleDraft) {
        mArticleDraft = articleDraft
    }

    fun setRichContent(result: RichContent) {
        mRichContent = result
        val picList = mutableListOf<PicItemDraft>()

        result.richItemList?.forEach {
            if (it.isInnerImageItem) {
                val target = if (TextUtils.isEmpty(it.icon)) it.source else it.icon
                val picItemDraft = PicItemDraft(target)
                picItemDraft.fileSize = it.size
                picItemDraft.height = it.height
                picItemDraft.width = it.width
                picItemDraft.mimeType = it.mimeType
                picList.add(picItemDraft)
            }
        }
        picAttachmentDraft.picList = ArrayList(picList.filter {
            !WeLikeFileManager.isHttp(it.localFileName)
        })
    }

    override fun hasUploadDraft(): Boolean {
        return !CollectionUtil.isEmpty(picAttachmentDraft.picList) || videoAttachmentDraft != null
    }

    override fun getForwardRichContent(): RichContent? {
        return null
    }
}
