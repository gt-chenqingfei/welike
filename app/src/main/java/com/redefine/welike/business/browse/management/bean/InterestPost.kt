package com.redefine.welike.business.browse.management.bean

import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.user.management.bean.Interest

/**
 * Created by honglin on 2018/7/20.
 */
class InterestPost : PostBase {

    constructor() {
        type = PostBase.POST_TYPE_INTEREST
    }

    var list: List<Interest>? = null

}