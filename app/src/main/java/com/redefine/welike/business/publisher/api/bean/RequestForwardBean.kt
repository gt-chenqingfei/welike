package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName


/**
 * Copyright (C) 2018 redefine , Inc.
 * @author qingfei.chen
 */
data class RequestForwardBean(@SerializedName("post") val post: PostBean,
                              @SerializedName("comment") var comment: CommentBean?)






