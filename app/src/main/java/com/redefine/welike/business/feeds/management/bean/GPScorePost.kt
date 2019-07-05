package com.redefine.welike.business.feeds.management.bean


/**
 * Created by honglin on 2018/7/20.
 */
class GPScorePost : PostBase {

    constructor() {
        type = PostBase.POST_TYPE_GP_SCORE
    }

    var currentSelect: Int = -1

    var currentType: Int = 0

}