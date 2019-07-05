package com.redefine.commonui.fresco.oss

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.base.URLCenter

abstract class BaseUrlFilter : IUrlFilter {
    companion object {
        private val X_OSS_MFIT_PROCESS = "?x-oss-process=image/resize,m_mfit"
        private val X_OSS_LFIT_PROCESS = "?x-oss-process=image/resize,m_lfit"
        private val X_OSS_MFILL_PROCESS = "?x-oss-process=image/resize,m_fill"
        private val OSS_WIDTH = ",w_"
        private val OSS_HEIGHT = ",h_"
        private val REFIX_IMG = "img.welike.in"
        private val REFIX_SOURCE = "source.welike.in"
        private val FORMAT_WEBP = "/format,webp"

    }

    override fun filter(url: String?): String {
        if (canFilter(url)) {
            val x = doFilter(url!!)
            return x
        }
        return url.orEmpty()
    }

    override fun filter(url: String?, width: Int, height: Int): String {
        if (canFilter(url)) {
            val x = doFilter(url!!, width, height)
            return x
        }
        return url.orEmpty()
    }

    protected abstract fun doFilter(url: String, width: Int, height: Int): String

    abstract fun doFilter(url: String): String

    override fun canFilter(url: String?): Boolean {
        if (url.isNullOrEmpty()) {
            return false
        }
        url?.let {
            if (it.contains(REFIX_IMG)) {
                return true
            }
            if (it.contains(REFIX_SOURCE)) {
                return true
            }
        }
        return false
    }

    protected fun doRealmFitResizeFilter(url: String, height: Int, width: Int): String {
        val builder = StringBuilder()
        builder.append(url).append(X_OSS_MFIT_PROCESS).append(OSS_HEIGHT).append(height)
                .append(OSS_WIDTH).append(width).append(FORMAT_WEBP)
        return builder.toString()
    }


    protected fun doReallFitResizeFilter(url: String, height: Int, width: Int): String {
        val builder = StringBuilder()
        builder.append(url).append(X_OSS_LFIT_PROCESS).append(OSS_HEIGHT).append(height)
                .append(OSS_WIDTH).append(width).append(FORMAT_WEBP)
        return builder.toString()
    }

    protected fun doRealmFillResizeFilter(url: String, height: Int, width: Int): String {
        val builder = StringBuilder()
        builder.append(url).append(X_OSS_MFILL_PROCESS).append(OSS_HEIGHT).append(height)
                .append(OSS_WIDTH).append(width).append(FORMAT_WEBP)
        return builder.toString()
    }


    fun getCoefficient(): Float {
//        return if (ScreenUtils.getScreenDensity() > 2.0f) {
//            3f
//        } else {
//            2f
//        }
        var x = ScreenUtils.getScreenDensity() - 0.5f
        if (x < 0.5f) {
            x = 0.5f
        }
        return x
    }
}


class HeadUrlFilter : BaseUrlFilter() {
    private val FACEBOOK_PATH = "https://graph.facebook.com"
    private val VMATE_PATH = "http://v.starhalo.mobi"

    private val FB_WIDTH = "width"
    private val FB_HEIGHT = "height"

    private var mHeadMaxSize: Int = 0
    private val HEAD_MAX_SIZE = 40

    init {
        mHeadMaxSize = (HEAD_MAX_SIZE * getCoefficient()).toInt()
    }

    override fun canFilter(url: String?): Boolean {
        return when {
            url.isNullOrEmpty() -> false
            url!!.startsWith(FACEBOOK_PATH) -> true
            url.startsWith(VMATE_PATH) -> true
            else -> super.canFilter(url)
        }
    }

    override fun doFilter(url: String, width: Int, height: Int): String {
        return if (url.startsWith(FACEBOOK_PATH)) {
            doFacebookFitResizeFilter(url, height, width)
        } else if (url.startsWith(VMATE_PATH)) {
            doVMateFitResizeFilter(url, height, width)
        } else
            doRealmFillResizeFilter(url, height, width)
    }

    override fun doFilter(url: String): String {
        return if (url.startsWith(FACEBOOK_PATH)) {
            doFacebookFitResizeFilter(url, mHeadMaxSize, mHeadMaxSize)
        } else if (url.startsWith(VMATE_PATH)) {
            doVMateFitResizeFilter(url, mHeadMaxSize, mHeadMaxSize)
        } else doRealmFillResizeFilter(url, mHeadMaxSize, mHeadMaxSize)
    }

    private fun doVMateFitResizeFilter(url: String, height: Int, width: Int): String {

        try {
            val uri = Uri.parse(url) ?: return url

            val builder = Uri.Builder()
            builder.scheme(uri.scheme)
                    .authority(uri.authority)
                    .path(uri.path)
                    .encodedFragment(uri.fragment)
            return doRealmFillResizeFilter(builder.toString(), height, width)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return url
    }

    private fun doFacebookFitResizeFilter(url: String, height: Int, width: Int): String {

        try {
            val uri = Uri.parse(url) ?: return url

            val builder = Uri.Builder()
            builder.scheme(uri.scheme)
                    .authority(uri.authority)
                    .path(uri.path)
                    .encodedFragment(uri.fragment)
            builder.appendQueryParameter(FB_WIDTH, width.toString())
            builder.appendQueryParameter(FB_HEIGHT, height.toString())
            return builder.build().toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return url
    }
}