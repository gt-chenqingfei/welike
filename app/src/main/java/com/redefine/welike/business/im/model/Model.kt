package com.redefine.welike.business.im.model

import com.google.gson.annotations.SerializedName

class CardInfo(
        @SerializedName("type")
        var type: Int, //卡片类型

        @SerializedName("subType")
        var subType: Int,

        @SerializedName("title")
        var title: String, //卡片title

        @SerializedName("imageUrl")
        var imageUrl: String, //卡片图片地址

        @SerializedName("forwardUrl")
        var forwardUrl: String,//卡片跳转链接

        @SerializedName("postCount")
        var postCount: Int,

        @SerializedName("userCount")
        var userCount: Int,

        @SerializedName("content")
        var content: String //卡片详细内容: POST=博文内容 or TOPIC=话题说明 or PROFILE=用户简介 or 右侧展示slogan
) {
    companion object {
        const val TYPE_POST = 1
        const val TYPE_TOPIC = 2
        const val TYPE_PROFILE = 3
        const val TYPE_SHAREAPP = 4
        const val TYPE_CUSTOM = 5
        
        const val SUB_TYPE_TEXT = 1
        const val SUB_TYPE_IMAGE = 2
        const val SUB_TYPE_VIDEO = 3
        const val SUB_TYPE_VOTE = 4
    }
}