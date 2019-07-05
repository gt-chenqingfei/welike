package com.redefine.commonui.fresco.oss

import com.redefine.foundation.utils.ScreenUtils

/**
 * Created by liwenbo on 2018/2/24.
 */

class BigPicUrlFilter : BaseUrlFilter() {
    private var mBigImgMaxWidth: Int = 0
    private var mBigImgMaxHeight: Int = 0

    private val BIG_IMG_MAX_WIDTH = 360
    private val BIG_IMG_MAX_HEIGHT = 640

    init {
        mBigImgMaxWidth = (BIG_IMG_MAX_WIDTH * getCoefficient()).toInt()
        mBigImgMaxHeight = (BIG_IMG_MAX_HEIGHT * getCoefficient()).toInt()
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return doReallFitResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return doReallFitResizeFilter(url, mBigImgMaxHeight, mBigImgMaxWidth)
    }


}
