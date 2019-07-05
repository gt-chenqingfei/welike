package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName

/**
 * Copyright (C) 2018 redefine , Inc.
 * @author qingfei.chen
 */
data class CommentBean(@SerializedName("post") var post: String,
                       @SerializedName("content") var content: String,
                       @SerializedName("attachments") var attachments: ArrayList<BaseAttachment?>?)