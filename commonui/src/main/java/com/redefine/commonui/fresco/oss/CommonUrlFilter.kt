package com.redefine.commonui.fresco.oss

import com.redefine.foundation.utils.ScreenUtils

/**
 * Created by liwenbo on 2018/2/24.
 */

class CommonUrlFilter : BaseUrlFilter() {
    private var mCommonMaxSize: Int = 0
    private val COMMON_MAX_SIZE = 100

    init {
        mCommonMaxSize = (COMMON_MAX_SIZE * getCoefficient()).toInt()
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return doRealmFitResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return doRealmFitResizeFilter(url, mCommonMaxSize, mCommonMaxSize)
    }

}
