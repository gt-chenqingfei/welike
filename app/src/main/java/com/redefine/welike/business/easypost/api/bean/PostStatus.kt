package com.redefine.welike.business.easypost.api.bean

import com.google.gson.annotations.SerializedName

/**
 * Copyright (C) 2018 redefine , Inc.
 * @author qingfei.chen
 * @date 2018-10-18 16:06:52
 */
class PostStatusList(
        @SerializedName("list")
        val list: List<PostStatus>)

data class PostStatus(
        @SerializedName("id")
        var id: Int,
        @SerializedName("topic")
        var topic: String = "",
        @SerializedName("text")
        var text: String,
        @SerializedName("picUrlList")
        var picUrlList: List<String>,
        @SerializedName("contentList")
        var contentList: List<String>
)