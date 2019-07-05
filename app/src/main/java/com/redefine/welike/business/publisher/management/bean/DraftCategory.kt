package com.redefine.welike.business.publisher.management.bean

/**
 * Copyright (C) 2018 redefine , Inc.
 *
 * @author qingfei.chen
 */
object DraftCategory {
    const val INVALID_TYPE = -1
    const val POST = 0     // 原创微博
    const val FORWARD = 1  // 转发微博
    const val COMMENT = 3   // 评论
    const val REPLY = 4  // 回复评论
    const val COMMENT_REPLY_BACK = 5  // 评论回复评论
}
