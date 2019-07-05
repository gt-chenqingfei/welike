package com.redefine.welike.business.publisher.viewmodel

import com.google.gson.annotations.SerializedName

/**
 * 用于外部调用，发布页时候的数据结构
 */
class PushPayload(
        @SerializedName("type")
        var type: Int,

        @SerializedName("content")
        var content: String?,

        @SerializedName("id")
        var id: String?,

        @SerializedName("s")
        var s: Int)


