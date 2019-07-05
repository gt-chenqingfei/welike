package com.redefine.commonui.fresco.oss

import com.redefine.foundation.utils.ScreenUtils

/**
 * Created by liwenbo on 2018/2/24.
 */

class MessageCardUrlFilter : BaseUrlFilter() {
    private var mMessageCardMaxSize: Int = 0
    private val MESSAGE_CARD_MAX_SIZE = 30

    init {
        mMessageCardMaxSize = (MESSAGE_CARD_MAX_SIZE * getCoefficient()).toInt()
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return doRealmFitResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return doRealmFitResizeFilter(url, mMessageCardMaxSize, mMessageCardMaxSize)
    }

}
