package com.redefine.commonui.fresco.oss

import com.redefine.foundation.utils.ScreenUtils

/**
 * Created by liwenbo on 2018/2/24.
 */

class ChatUrlFilter : BaseUrlFilter() {
    private val BIG_IMG_MAX_WIDTH = 100
    private val BIG_IMG_MAX_HEIGHT = 100

    private var mBigImgMaxWidth: Int = 0
    private var mBigImgMaxHeight: Int = 0

    init {
        mBigImgMaxWidth = (BIG_IMG_MAX_WIDTH * getCoefficient()).toInt()
        mBigImgMaxHeight = (BIG_IMG_MAX_HEIGHT * getCoefficient()).toInt()
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return doRealmFitResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return doRealmFitResizeFilter(url, mBigImgMaxHeight, mBigImgMaxWidth)
    }

}
