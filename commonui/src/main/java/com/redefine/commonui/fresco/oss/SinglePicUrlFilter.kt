package com.redefine.commonui.fresco.oss

/**
 * Created by liwenbo on 2018/2/24.
 */

class SinglePicUrlFilter : BaseUrlFilter() {

    private var mNineGridMaxSize: Int = 0
    private val NINE_GRID_MAX_SIZE = 100

    init {
        mNineGridMaxSize = (NINE_GRID_MAX_SIZE * getCoefficient()).toInt()
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return doRealmFillResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return doRealmFillResizeFilter(url, mNineGridMaxSize, mNineGridMaxSize)
    }

}
