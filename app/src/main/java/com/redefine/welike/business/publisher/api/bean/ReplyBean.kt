package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName

/**
 * Copyright (C) 2018 redefine , Inc.
 * @author qingfei.chen
 */
data class ReplayBean(@SerializedName("comment") val comment: String,
                      @SerializedName("reply") val reply: String? = null,
                      @SerializedName("content") val content: String,
                      @SerializedName("attachments") var attachments: ArrayList<BaseAttachment?>?)