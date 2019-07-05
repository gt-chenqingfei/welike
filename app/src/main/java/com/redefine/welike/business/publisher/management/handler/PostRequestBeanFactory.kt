package com.redefine.welike.business.publisher.management.handler


import android.text.TextUtils
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.foundation.utils.CommonHelper
import com.redefine.richtext.helper.RichTextHelper
import com.redefine.welike.MyApplication
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.SpManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.publisher.api.bean.*
import com.redefine.welike.business.publisher.management.PublishConfig
import com.redefine.welike.business.publisher.management.draft.PicItemDraft
import com.redefine.welike.business.publisher.management.draft.PublishPostDraft
import com.redefine.welike.kext.translate


object PostRequestBeanFactory {

    fun buildPublishBean(postDraft: PublishPostDraft): RequestPostBean {
        /** 处理post的机型 **/
        var source: String? = null
        if (!SpManager.Setting.getCurrentMobileModelSetting(MyApplication.getAppContext())) {
            source = CommonHelper.getDeviceModel()
        }

        var location: LocationBean? = null
        if (postDraft.mLocation != null) {
            location = LocationBean(postDraft.mLocation?.longitude.toString(), postDraft.mLocation?.latitude.toString()
                    , postDraft.mLocation?.placeId, postDraft.mLocation?.place)
        }
        /** 处理article **/
        var article: ArticleBean? = null
        if (postDraft.mArticleDraft != null) {
            /** 处理article封面 **/
            val list = postDraft.mArticleDraft!!.mRichContent!!.richItemList?.filter {
                it.isInnerImageItem
            }
            val cover = if (!CollectionUtil.isEmpty(list)) {
                val target = if (TextUtils.isEmpty(list!![0].icon)) list[0].source else list[0].icon
                target
            } else if (postDraft.mArticleDraft!!.videoAttachmentDraft != null && !TextUtils.isEmpty(postDraft.mArticleDraft!!.videoAttachmentDraft?.videoCoverDraft?.url)) {
                postDraft.mArticleDraft!!.videoAttachmentDraft?.videoCoverDraft!!.url!!
            } else PublishConfig.DEFAULT_ARTICLE_COVER_URL

            val linkStartFlag = RichTextHelper.article_
            val isOnlyTitle = postDraft.mRichContent?.text?.startsWith(linkStartFlag) ?: false
                    && postDraft.mRichContent?.text?.endsWith(postDraft.mArticleDraft?.mPostName
                    ?: "") ?: false
            if (isOnlyTitle) {
                val defaultTitle = "article_publish".translate(ResourceTool.ResourceFileEnum.PUBLISH) + "< " + postDraft.mArticleDraft?.mPostName + " >"
                val richContent = RichTextHelper.mergeRichText(defaultTitle, postDraft.mRichContent)
                postDraft.mRichContent = richContent
            }

            /** 处理article attachments **/
            val articleAttachments = ArrayList<BaseAttachment?>()
            /** 将article视频整合到article attachments里 **/
            if (postDraft.mArticleDraft!!.videoAttachmentDraft != null) {

                val videoAttachment = AttachmentVideoBean(postDraft.mArticleDraft!!.videoAttachmentDraft?.videoItemDraft?.url
                        ?: "",
                        postDraft.mArticleDraft?.videoAttachmentDraft?.videoCoverDraft?.url ?: "",
                        postDraft.mArticleDraft?.videoAttachmentDraft?.videoItemDraft?.width ?: 0,
                        postDraft.mArticleDraft?.videoAttachmentDraft?.videoItemDraft?.height ?: 0,
                        postDraft.mArticleDraft?.videoAttachmentDraft?.videoItemDraft?.duration)

                articleAttachments.add(videoAttachment)
            }

            postDraft.mArticleDraft!!.getRichContent()?.richItemList?.forEach {
                /** 将article 内嵌图片整合到article attachments里 **/
                if (it.isInnerImageItem) {
                    var picAttachment = AttachmentImageBean(it.source, it.width, it.height, it.classify, it.display, it.icon, it.index, it.length)
                    articleAttachments.add(picAttachment)
                } else {
                    /** 将article 其他富文本整合到article attachments里 **/

                    val attachment = AttachmentFactory.obtainAttachment(it)
                    articleAttachments.add(attachment)
                }
            }
            article = ArticleBean(postDraft.mArticleDraft!!.mPostName
                    , postDraft.mArticleDraft!!.mRichContent!!.text
                    , cover
                    , articleAttachments)
        }
        val postAttachments = ArrayList<BaseAttachment?>()
        /** 将post里的视频整合到attachments里 **/
        if (postDraft.videoAttachmentDraft != null) {

            val videoAttachment = AttachmentVideoBean(postDraft.videoAttachmentDraft?.videoItemDraft?.url
                    ?: "",
                    postDraft.videoAttachmentDraft?.videoCoverDraft?.url ?: "",
                    postDraft.videoAttachmentDraft?.videoItemDraft?.width ?: 0,
                    postDraft.videoAttachmentDraft?.videoItemDraft?.height ?: 0,
                    postDraft.videoAttachmentDraft?.videoItemDraft?.duration)
            postAttachments.add(videoAttachment)
        }

        /** 将post里的图片整合到attachments里 **/
        if (!CollectionUtil.isEmpty(postDraft.picAttachmentDraft.picList)) {
            postDraft.picAttachmentDraft.picList.forEach {

                val picAttachment = AttachmentImageBean(it.url ?: "", it.width, it.height)
                postAttachments.add(picAttachment)
            }
        }

        /** 将post里的其他富文本整合到attachments里 **/
        var topicCount = 0
        var superTopicCount = 0
        val tags = mutableListOf<String>()
        var community: String? = null
        postDraft.getRichContent()?.richItemList?.forEach {
            if (it.isSuperTopicItem) {
                superTopicCount++
                if (superTopicCount > GlobalConfig.FEED_MAX_SUPER_TOPIC) {
                    return@forEach
                }
                community = it.id
            }
            if (it.isTopicItem || it.isSuperTopicItem) {
                topicCount++
                if (topicCount > GlobalConfig.FEED_MAX_TOPIC) {
                    return@forEach
                }
            }
            if (it.isTopicItem || it.isSuperTopicItem) {
                if (!TextUtils.isEmpty(it.classify) && !tags.contains(it.classify)) {
                    tags.add(it.classify)
                }
            }
            if (it.isArticleItem && article == null) {
                return@forEach
            }
            val attachment = AttachmentFactory.obtainAttachment(it)
            postAttachments.add(attachment)
        }
        if (!TextUtils.isEmpty(postDraft.communityId)) {
            community = postDraft.communityId
        }
        /**
         * 处理投票数据
         */
        if (postDraft.mPollDraft != null && !CollectionUtil.isEmpty(postDraft.mPollDraft?.mPollItemInfos)) {
            val pollItemList = ArrayList<ChoiceBean>()

            if (!CollectionUtil.isEmpty(postDraft.picAttachmentDraft.picList)) {
                postDraft.picAttachmentDraft.picList.forEachIndexed { i: Int, picItemDraft: PicItemDraft ->
                    if (i < postDraft.mPollDraft!!.mPollItemInfos.size) {
                        postDraft.mPollDraft!!.mPollItemInfos[i].pollItemPic = picItemDraft.url
                    }
                }
            }
            postDraft.mPollDraft?.mPollItemInfos?.forEach {
                pollItemList.add(ChoiceBean(it.pollItemText, it.pollItemPic))
            }
            val pollRequestBean = PollBean(postDraft.mPollDraft?.expiredTime
                    ?: 0, pollItemList)
            val pollAttachmentBean = AttachmentPollBean(AttachmentCategory.POLL, pollRequestBean)
            postAttachments.add(pollAttachmentBean)
        }
        val content = postDraft.getRichContent()?.text ?: ""
        val summary = postDraft.getRichContent()?.summary ?: ""
        var tagList: ArrayList<String>? = null
        if (!tags.isEmpty()) {
            tagList = ArrayList(tags)
        }
        return RequestPostBean(PostBean(content, summary, postAttachments, article, origin = 1, source = source, location = location, tags = tagList, community = community, shareSource = postDraft.getDraftSerializable().from))
    }


}