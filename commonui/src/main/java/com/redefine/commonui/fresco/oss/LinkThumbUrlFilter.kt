package com.redefine.commonui.fresco.oss

import com.redefine.foundation.utils.ScreenUtils

/**
 * Created by liwenbo on 2018/2/24.
 */

class LinkThumbUrlFilter : BaseUrlFilter() {
    private var mLinkMaxSize: Int = 0
    private val LINK_MAX_SIZE = 50

    init {
        mLinkMaxSize = (LINK_MAX_SIZE * getCoefficient()).toInt()
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return doRealmFitResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return doRealmFitResizeFilter(url, mLinkMaxSize, mLinkMaxSize)
    }


}
