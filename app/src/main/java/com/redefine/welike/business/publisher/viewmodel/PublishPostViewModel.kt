package com.redefine.welike.business.publisher.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.media.mediasdk.codec.FileUtil
import com.media.mediasdk.codec.common.VideoSize
import com.media.mediasdk.codec.info.INTInfo
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.foundation.utils.CommonHelper
import com.redefine.multimedia.photoselector.entity.Item
import com.redefine.multimedia.photoselector.entity.MimeType
import com.redefine.richtext.RichContent
import com.redefine.richtext.RichItem
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.feeds.management.bean.PollItemInfo
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser
import com.redefine.welike.business.feeds.management.parser.RichTextContentParser
import com.redefine.welike.business.feeds.ui.constant.FeedConstant
import com.redefine.welike.business.location.management.bean.Location
import com.redefine.welike.business.publisher.api.bean.SuperTopic
import com.redefine.welike.business.publisher.management.FeedPoster
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.*
import com.redefine.welike.business.publisher.management.draft.ArticleDraft
import java.io.File

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */
class PublishPostViewModel : AbsPublishViewModel<DraftPost>() {

    val mLocationLiveData: MutableLiveData<Location> = MutableLiveData()
    val mVideoLiveData: MutableLiveData<Item> = MutableLiveData()
    val mPhotoLiveData: MutableLiveData<List<Item>> = MutableLiveData()
    val mPollLiveData: MutableLiveData<List<PollItemInfo>> = MutableLiveData()
    val pollItems: ArrayList<PollItemInfo> = ArrayList()

    val mMenuStateLiveData: MutableLiveData<PostMenuState> = MutableLiveData()
    val mScrollToBottomLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var isTextOverLimit: Boolean = false
    private var isTextEmpty: Boolean = true
    private var isHasFullPoll: Boolean = false
    var isRestore: Boolean = false
    var from = ""

    override fun newDraftInstance(): DraftPost {
        return DraftPost()
    }

    override fun afterTextChanged(textLength: Int, text: CharSequence, topicCount: Int, hasFocus: Boolean,
                                  superTopicCount: Int) {
        isTextOverLimit = textLength > GlobalConfig.FEED_MAX_LENGTH
        isTextEmpty = TextUtils.getTrimmedLength(text) == 0
        isHasFullPoll = isHasFullPollInfo()

        val sendBtnEnable = false

        val hasVideo = mVideoLiveData.value != null
        val hasPic = !CollectionUtil.isEmpty(mPhotoLiveData.value)
        val hasPoll = !CollectionUtil.isEmpty(pollItems)

        val menuState = PostMenuState(hasVideo, hasPic, hasPoll, hasFocus, sendBtnEnable, topicCount,
                superTopicCount)

        updateMenuState(menuState)
    }

    override fun getInputTextMaxOverLimit(): Int {
        return GlobalConfig.PUBLISH_POST_INPUT_TEXT_MAX_OVER_LIMIT
    }

    override fun getInputTextMaxWarnLimit(): Int {
        return GlobalConfig.PUBLISH_POST_INPUT_TEXT_MAX_WARN_LIMIT
    }

    override fun publish(draft: DraftPost?) {

        draft?.let {
            val mPublishEventModel = PublishAnalyticsManager.getInstance().obtainEventModel(draft?.draftId)
            mPublishEventModel.videoNum = if (it.video == null) 0 else 1
            mPublishEventModel.pictureNum = if (it.picDraftList == null) 0 else it.picDraftList.size
            mPublishEventModel.setPollNum(if (it.pollItemInfos == null) 0 else it.pollItemInfos.size)
            mPublishEventModel.setPictureSize(PublisherEventManager.INSTANCE.pictrueSize)
        }

        FeedPoster.getInstance().publish(draft)
    }

    override fun buildDraft(richContent: RichContent?): DraftPost? {
        richContent ?: return null

        mDraft.pollItemInfos = null
        mDraft.picDraftList = null
        mDraft.video = null
        if (!CollectionUtil.isEmpty(pollItems)) {
            pollItems?.let { polls ->
                mDraft.pollItemInfos = polls
                val picDraftList = ArrayList<DraftPicAttachment>()
                mDraft.pollItemInfos.forEach {
                    if (!TextUtils.isEmpty(it.pollItemPic)) {
                        val draft = DraftPicAttachment(it.pollItemPic)
                        draft.height = it.height
                        draft.width = it.width
                        picDraftList.add(draft)
                    }
                }
                mDraft.picDraftList = picDraftList
            }
        } else {

            if (!CollectionUtil.isEmpty(mPhotoLiveData.value)) {
                mPhotoLiveData.value?.let { photos ->
                    val list = ArrayList<DraftPicAttachment>()
                    photos.forEach {
                        val draftPic = DraftPicAttachment(it.filePath)
                        draftPic.mimeType = it.mimeType
                        draftPic.width = it.width
                        draftPic.height = it.height
                        if (it.filePath.contains("http://") || it.filePath.contains("https://")) {
                            draftPic.url = it.filePath
                        }
                        list.add(draftPic)
                        PublisherEventManager.INSTANCE.addPicture_size(it.size)
                    }

                    mDraft.picDraftList = list
                }
            }

            mVideoLiveData.value?.let {
                mDraft.video = DraftVideoAttachment(it.filePath)
                mDraft.video.duration = it.duration
                mDraft.video.mimeType = it.mimeType
                mDraft.video.height = it.height
                mDraft.video.width = it.width
                mDraft.video.isFromRecorder = it.isFromRecorder
                if (it.filePath.contains("http://") || it.filePath.contains("https://")) {
                    mDraft.video.url = it.filePath
                }

                if (!TextUtils.isEmpty(it.coverPath)) {
                    if (it.coverPath.contains("http://") || it.coverPath.contains("https://")) {
                        mDraft.videoThumb = DraftPicAttachment(it.coverPath)
                        mDraft.videoThumb.mimeType = MimeType.JPEG.toString()
                        mDraft.videoThumb.url = it.coverPath
                        mDraft.videoThumb.height = it.height
                        mDraft.videoThumb.width = it.width
                    }
                }
            }

        }


        mLocationLiveData.value?.let {
            mDraft.location = it
        }
        mDraft.content = richContent
        mDraft.from = from

        if (TextUtils.isEmpty(mDraft.content.text)) {
            mDraft.content.text = getDefaultContentByPost(mDraft)
            mDraft.content.summary = mDraft.content.text
        }

        return mDraft
    }

    override fun restoreDraft(post: DraftPost?) {
        if (post == null) {
            return
        }
        isRestore = true
        if (!TextUtils.isEmpty(post.draftId)) {
            mDraft.draftId = post.draftId
        }

        if (!CollectionUtil.isEmpty(post.pollItemInfos)) {
            restorePollInfoFromDraft(post.pollItemInfos, post.expiredTime)
        } else {
            restoreVideoFromDraft(post.video, post.videoThumb)
            restorePhotosFromDraft(post.picDraftList)
        }
        updateRichContent(post.content)
        restoreArticle(post.article)
        updateLocation(post.location)
    }

    fun parseTail(hashID: String?, hashTail: String?, hashType: String?) {
        if (TextUtils.isEmpty(hashTail)) {
            return
        }

        if (TextUtils.equals(hashType, RichItem.RICH_TYPE_MENTION)) {
            if (!TextUtils.isEmpty(hashID) && !TextUtils.isEmpty(hashTail)) {
                updateMention(hashTail, hashID, true)
            } else {
                updateContent(hashTail)
            }
        } else if (TextUtils.equals(hashType, RichItem.RICH_TYPE_TOPIC)) {
            val bean = TopicSearchSugBean.TopicBean()
            bean.id = hashID
            bean.name = hashTail
            updateTopic(bean, true)
        } else if (TextUtils.equals(hashType, RichItem.RICH_TYPE_LINK)) {
            updateLink(hashTail)
        } else if (TextUtils.equals(hashType, RichItem.RICH_TYPE_SUPER_TOPIC)) {
            mDraft.superTopicId = hashID
            updateSuperTopic(SuperTopic(hashID!!, hashTail!!, null))
        } else {
            updateContent(hashTail)
        }
    }

    fun parsePublishPostContent(content: String?) {
        if (TextUtils.isEmpty(content)) {
            return
        }

        val jsonObject = JSONObject.parseObject(content) ?: return
        val post = jsonObject.getJSONObject("post") ?: return
        val attachments = post.getJSONArray("attachments")

        val richContent = RichContent()
        richContent.richItemList = RichTextContentParser.parserRichJSONArray(attachments)
        richContent.text = post.getString("content")


        val video = RichTextContentParser.parserVideo(attachments)
        val poll = RichTextContentParser.parsePoll(attachments)
        val images = RichTextContentParser.parserImages(attachments)
        if (video != null) {
            updateVideo(video)
        } else if (poll != null && images != null) {
            val pollInfo = PostsDataSourceParser.getPollInfo(poll)
            if (pollInfo != null) {

                pollInfo.pollItemInfoList.forEach {

                    val item = images.find { item -> item.filePath == it.pollItemPic }
                    if (item != null) {
                        it.width = item.width
                        it.height = item.height
                    }
                }
                restorePollInfoFromDraft(pollInfo.pollItemInfoList, pollInfo.expiredTime)
            }
        } else if (!CollectionUtil.isEmpty(images)) {
            updatePhotos(images)
        }

        updateRichContent(richContent)

    }

    fun parseMultiContentFromPush(content: String?) {
        if (TextUtils.isEmpty(content)) {
            return
        }

        try {
            Gson().fromJson<List<PushPayload>>(content, object : TypeToken<List<PushPayload>>() {

            }.type)?.sortedBy { it.s }?.forEach {
                when (it.type) {
                    FeedConstant.TYPE_PAYLOAD_TEXT -> updateContent(it.content)
                    FeedConstant.TYPE_PAYLOAD_MENTION -> if (!TextUtils.isEmpty(it.id)
                            && it.content != null && it.content!!.startsWith("@")) {
                        updateMention(it.content, it.id, true)
                    }
                    FeedConstant.TYPE_PAYLOAD_TOPIC -> {
                        val bean = TopicSearchSugBean.TopicBean()
                        bean.id = it.id
                        bean.name = it.content
                        updateTopic(bean, true)
                    }
                    FeedConstant.TYPE_PAYLOAD_LINK -> updateLink(it.content)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun restoreVideoFromDraft(videoAttachment: DraftVideoAttachment?, videoThumb: DraftPicAttachment?) {
        videoAttachment?.let {
            val videoItem = Item(Uri.parse(it.localFileName),
                    it.localFileName, it.mimeType, 0, it.duration, it.width, it.height)

            videoThumb?.let { thumb ->
                videoItem.coverPath = thumb.url
            }
            updateVideo(videoItem)
        }
    }

    private fun restorePhotosFromDraft(picAttachment: List<DraftPicAttachment>?) {
        picAttachment?.let { photos ->
            val photosList = ArrayList<Item>()
            photos.forEach {
                val photoItem = Item(Uri.fromFile(File(it.localFileName)),
                        it.localFileName, it.mimeType, 0, 0, it.width, it.height)
                photosList.add(photoItem)
            }
            updatePhotos(photosList)
        }
    }

    private fun restorePollInfoFromDraft(polls: List<PollItemInfo>?, pollExpireTime: Long) {
        this.mDraft.expiredTime = pollExpireTime
        polls?.let {
            pollItems.addAll(polls)
            mPollLiveData.postValue(pollItems)
        }
        updatePollState()
    }

    private fun restoreArticle(articleDraft: ArticleDraft?) {
        articleDraft?.let {
            mArticleLiveData.postValue(articleDraft)
            mDraft.article = articleDraft
        }
    }

    private fun updateMenuState(menuState: PostMenuState) {
        if (isTextEmpty) {
            menuState.sendBtnEnable = false
            mDraftEnable = false
        }

        when {
            menuState.hasPoll -> {
                menuState.sendBtnEnable = isHasFullPoll
                mDraftEnable = isHasFullPoll
            }
            menuState.hasVideo -> {
                menuState.sendBtnEnable = true
                mDraftEnable = true
            }
            menuState.hasPic -> {
                menuState.sendBtnEnable = true
                mDraftEnable = true
            }
            else -> {
                menuState.sendBtnEnable = !isTextEmpty
                mDraftEnable = !isTextEmpty
            }
        }

        mDraftEnable = AccountManager.getInstance().isLogin && mDraftEnable
        if (isTextOverLimit) {
            menuState.sendBtnEnable = false
        }

        mMenuStateLiveData.value = menuState
    }


    fun updateLocation(location: Location?) {
        mLocationLiveData.postValue(location)
    }

    fun updateVideoAndPhotos(items: List<Item>?) {
        items ?: return
        val item = items.firstOrNull()
        if (item == null) {
            updateVideo(null)
            updatePhotos(null)
            return
        }

        if (item.isVideo) {
            updateVideo(item)
        } else if (item.isImage) {
            updatePhotos(items)
        }
    }

    fun updateVideo(it: Item?) {
        val menuState = getMenuState()
        if (it == null) {
            menuState.hasVideo = false
            updateMenuState(menuState)
            mVideoLiveData.postValue(null)
            return
        }

        val mPublishEventModel = PublishAnalyticsManager.getInstance().obtainEventModel(mDraft.draftId)
        mPublishEventModel.videoSize = it.size

        mPhotoLiveData.postValue(null)
        val videoSize = VideoSize()
        FileUtil.GetMetaInfo(it.filePath, videoSize, INTInfo())
        var width = it.width
        var height = it.height
        if (videoSize.nHeight != 0 && videoSize.nWidth != 0) {
            width = videoSize.nWidth
            height = videoSize.nHeight
        }
        val fixedVideo = Item(it.uri, it.filePath, it.mimeType, it.size, it.duration, width, height)
        fixedVideo.isFromRecorder = it.isFromRecorder
        fixedVideo.coverPath = it.coverPath
        mVideoLiveData.postValue(fixedVideo)
        menuState.hasPic = false
        menuState.hasVideo = true
        updateMenuState(menuState)
    }

    fun updatePhotos(photos: List<Item>?) {
        val menuState = getMenuState()

        if (photos == null) {
            menuState.hasPic = false
            updateMenuState(menuState)
            mPhotoLiveData.postValue(null)
            return
        }

        mVideoLiveData.postValue(null)
        mPhotoLiveData.postValue(photos)

        menuState.hasPic = true
        menuState.hasVideo = false

        updateMenuState(menuState)
        mScrollToBottomLiveData.postValue(true)
    }

    fun updatePhoto(photo: Item) {
        var photos: ArrayList<Item>? = mPhotoLiveData.value as ArrayList<Item>?
        if (photos == null) {
            photos = ArrayList()
        }
        if (photos.size >= GlobalConfig.POST_PIC_MAX_COUNT) {
            photos.removeAt(photos.size - 1)
        }
        photos.add(photo)
        mPhotoLiveData.postValue(photos)
        val menuState = getMenuState()
        menuState.hasPic = true
        menuState.hasVideo = false
        updateMenuState(menuState)
    }

    fun generateInitPoll(pollTopic: TopicSearchSugBean.TopicBean) {
        pollItems.clear()

        pollItems.add(PollItemInfo(CommonHelper.generateUUID()))
        pollItems.add(PollItemInfo(CommonHelper.generateUUID()))


        mPollLiveData.value = pollItems
        updateTopic(pollTopic, false)
    }

    private fun updatePollState() {
        var menuState = getMenuState()
        menuState.hasPoll = true
        updateMenuState(menuState)
    }

    fun addPoll() {
        val pollItem = PollItemInfo(CommonHelper.generateUUID())
        pollItems.add(pollItem)
        mPollLiveData.value = pollItems
        mScrollToBottomLiveData.postValue(true)
    }

    fun removePoll(poll: PollItemInfo, position: Int) {
        pollItems?.let {
            pollItems.removeAt(position)
            mPollLiveData.value = pollItems
        }
    }

    fun updatePollImage(image: Item?, position: Int) {
        if (position >= pollItems.size) {
            return
        }
        image?.let {

            pollItems[position].pollItemPic = it.filePath
            mPollLiveData.postValue(pollItems)
        }
    }

    fun updatePollItemText(poll: PollItemInfo, position: Int) {
        if (position >= pollItems.size) {
            return
        }
        pollItems.let {
            it[position] = poll
            isHasFullPoll = isHasFullPollInfo()
            updateMenuState(getMenuState())
        }
    }

    fun updatePollExpireTime(expiredTime: Long) {
        mDraft.expiredTime = expiredTime
    }

    fun getPollExpireTime(): Long {
        return mDraft?.expiredTime
    }

    private fun getMenuState(): PostMenuState {
        var menuState = mMenuStateLiveData.value
        if (menuState == null) {
            menuState = PostMenuState()
        }
        return menuState
    }

    private fun isHasFullPollInfo(): Boolean {
        var has = false
        if (!CollectionUtil.isEmpty(pollItems)) {
            var isAllHaveImg = true
            var isAllHaveText = true
            var isSomeOnePage = false
            pollItems?.let {
                it.forEach {
                    isAllHaveImg = isAllHaveImg and !TextUtils.isEmpty(it.pollItemPic)
                    isAllHaveText = isAllHaveText and !TextUtils.isEmpty(it.pollItemText)
                    isSomeOnePage = isSomeOnePage or !TextUtils.isEmpty(it.pollItemPic)
                }
            }
            has = isAllHaveText && isAllHaveImg || isAllHaveText && !isSomeOnePage
        }
        return has
    }


    private fun getDefaultContentByPost(draftBase: DraftPost): String {
        if (!CollectionUtil.isEmpty(draftBase.picDraftList)) {
            return ResourceTool.getString("publish_share_picture_empty_content")
        } else if (draftBase.video != null) {
            return ResourceTool.getString("publish_share_video_empty_content")
        }
        return ""
    }

}