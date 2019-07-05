package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName


/**
 * Copyright (C) 2018 redefine , Inc.
 * @author qingfei.chen
 */
data class RequestCommentBean(@SerializedName("comment") val comment: CommentBean,
                              @SerializedName("post") var post: PostBean?)






