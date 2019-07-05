package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName

/**
 *
 * Name: PostAttachment
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-17 23:06
 *
 */
object AttachmentCategory {
    const val MENTION = "MENTION"
    const val TOPIC = "TOPIC"
    const val VIDEO = "VIDEO"
    const val LINK = "LINK"
    const val ARTICLE = "ARTICLE"
    const val SUPER_TOPIC = "SUPER_TOPIC"
    const val POLL = "POLL"
    const val IMAGE = "IMAGE"
}

open class BaseAttachment(@SerializedName("type") var type: String,
                          @SerializedName("source") var source: String)

class AttachmentPollBean(source: String,
                         @SerializedName("poll") var poll: PollBean)
    : BaseAttachment(AttachmentCategory.POLL, source)

class AttachmentMentionBean(source: String,
                            @SerializedName("display") var display: String,
                            @SerializedName("length") var length: Int,
                            @SerializedName("richId") var richId: String,
                            @SerializedName("index") var index: Int)
    : BaseAttachment(AttachmentCategory.MENTION, source)

class AttachmentVideoBean(source: String,
                          @SerializedName("icon") var icon: String,
                          @SerializedName("video-width") var videoWidth: Int,
                          @SerializedName("video-height") var videoHeight: Int,
                          @SerializedName("duration") var duration: Long? = 0)
    : BaseAttachment(AttachmentCategory.VIDEO, source)

class AttachmentSuperTopicBean(source: String,
                               @SerializedName("display") var display: String,
                               @SerializedName("richId") var richId: String,
                               @SerializedName("classify") var classify: String,
                               @SerializedName("length") var length: Int,
                               @SerializedName("index") var index: Int)
    : BaseAttachment(AttachmentCategory.SUPER_TOPIC, source)

class AttachmentArticleBean(source: String,
                            @SerializedName("display") var display: String,
                            @SerializedName("length") var length: Int,
                            @SerializedName("index") var index: Int)
    : BaseAttachment(AttachmentCategory.ARTICLE, source)

class AttachmentLinkBean(source: String,
                         @SerializedName("display") var display: String,
                         @SerializedName("target") var target: String,
                         @SerializedName("length") var length: Int,
                         @SerializedName("index") var index: Int)
    : BaseAttachment(AttachmentCategory.LINK, source)

class AttachmentImageBean(source: String,
                          @SerializedName("image-width") var imageWidth: Int,
                          @SerializedName("image-height") var imageHeight: Int,
                          @SerializedName("classify") var classify: String? = null,
                          @SerializedName("display") var display: String? = null,
                          @SerializedName("icon") var icon: String? = null,
                          @SerializedName("index") var index: Int? = null,
                          @SerializedName("length") var length: Int? = null)
    : BaseAttachment(AttachmentCategory.IMAGE, source)

class AttachmentTopicBean(source: String,
                          @SerializedName("display") var display: String,
                          @SerializedName("length") var length: Int,
                          @SerializedName("index") var index: Int)
    : BaseAttachment(AttachmentCategory.TOPIC, source)

class PollBean(@SerializedName("expiredTime") var expiredTime: Long,
               @SerializedName("choices") var choices: ArrayList<ChoiceBean>)

class ChoiceBean(@SerializedName("choiceName") var choiceName: String? = "",
                 @SerializedName("choiceImageUrl") var choiceImageUrl: String? = null)
