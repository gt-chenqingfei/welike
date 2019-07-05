package com.redefine.commonui.fresco.oss

import com.redefine.foundation.utils.ScreenUtils

/**
 * Created by liwenbo on 2018/3/22.
 */

class BannerUrlFilter : BaseUrlFilter() {

    private val BANNER_HEIGHT_SIZE = 113
    private val BANNER_WIDTH_SIZE = 360

    private var mHeightMaxSize: Int = 0
    private var mWidthMaxSize: Int = 0

    init {
        mHeightMaxSize = (BANNER_HEIGHT_SIZE * getCoefficient()).toInt()
        mWidthMaxSize = (BANNER_WIDTH_SIZE * getCoefficient()).toInt()
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return doRealmFitResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return doRealmFitResizeFilter(url, mHeightMaxSize, mWidthMaxSize)
    }

}
