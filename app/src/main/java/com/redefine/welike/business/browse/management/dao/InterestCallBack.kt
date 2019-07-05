package com.redefine.welike.business.browse.management.dao

import com.redefine.welike.business.browse.management.bean.Interest

/**
 * Created by honglin on 2018/7/20.
 */
interface InterestCallBack {
    fun onLoadEntity(interests: List<Interest>)
}