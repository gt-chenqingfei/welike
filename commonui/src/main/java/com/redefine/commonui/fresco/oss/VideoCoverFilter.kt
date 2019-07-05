package com.redefine.commonui.fresco.oss

/**
 * Created by liwenbo on 2018/2/24.
 */

class VideoCoverFilter : BaseUrlFilter() {
    private var mVideoCoverMaxWidth: Int = 0
    private var mVideoCoverMaxHeight: Int = 0
    private val VIDEO_COVER_MAX_WIDTH = 330
    private val VIDEO_COVER_MAX_HEIGHT = 186

    init {
        mVideoCoverMaxWidth = (VIDEO_COVER_MAX_WIDTH * getCoefficient()).toInt()
        mVideoCoverMaxHeight = (VIDEO_COVER_MAX_HEIGHT * getCoefficient()).toInt()
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return doRealmFillResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return doRealmFillResizeFilter(url, mVideoCoverMaxHeight, mVideoCoverMaxWidth)
    }

}
