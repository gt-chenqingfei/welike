package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName


/**
 * Copyright (C) 2018 redefine , Inc.
 * @author qingfei.chen
 */
data class RequestReplyBean(@SerializedName("reply") val reply: ReplayBean,
                            @SerializedName("post") val post: PostBean? = null)






