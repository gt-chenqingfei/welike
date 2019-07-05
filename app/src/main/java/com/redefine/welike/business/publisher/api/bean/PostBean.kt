package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName

data class PostBean(@SerializedName("content") val content: String
                    , @SerializedName("summary") val summary: String
                    , @SerializedName("attachments") val attachments: ArrayList<BaseAttachment?>?
                    , @SerializedName("article") val article: ArticleBean? = null
                    , @SerializedName("origin") val origin: Int = 1
                    , @SerializedName("source") val source: String? = null
                    , @SerializedName("location") val location: LocationBean? = null
                    , @SerializedName("tags") val tags: ArrayList<String>? = null
                    , @SerializedName("community") val community: String? = null
                    , @SerializedName("forwardPost") val forwardPost: String? = null
                    , @SerializedName("shareSource") val shareSource: String? = null)