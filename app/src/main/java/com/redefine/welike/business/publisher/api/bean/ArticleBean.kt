package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName

/**
 *
 * Name: ArticleBean
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-23 15:20
 *
 */

class ArticleBean(@SerializedName("title") val title: String,
                  @SerializedName("content") val content: String,
                  @SerializedName("cover") val cover: String = "",
                  @SerializedName("attachments") val attachments: ArrayList<BaseAttachment?>)