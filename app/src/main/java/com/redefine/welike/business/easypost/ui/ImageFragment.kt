package com.redefine.welike.business.easypost.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.redefine.commonui.fresco.loader.BigPicUrlLoader
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.im.e
import com.redefine.im.threadUIDelay
import com.redefine.welike.R
import com.redefine.welike.business.easypost.management.EasyPost
import com.redefine.welike.statistical.manager.PostStatusEventManager
import kotlinx.android.synthetic.main.easypost_fragment.*
import kotlinx.android.synthetic.main.easypost_fragment.view.*
import java.util.concurrent.atomic.AtomicBoolean


class ImageFragment : Fragment() {

    var post: EasyPost? = null
    lateinit var mEditText: EditText
    private val isDownloaded: AtomicBoolean by lazy { AtomicBoolean(false) }
    private val isAbort: AtomicBoolean by lazy { AtomicBoolean(false) }
    lateinit var mCallback: (Bitmap?) -> Unit
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.easypost_fragment, container, false)
        BigPicUrlLoader.getInstance().loadBigImage(view.easy_status_bg, post?.image) {
            it?.let {
                val isDownload = easy_status_bg.tag as Boolean
                isDownloaded.set(isDownload)
                doCopy()
            }
        }
        return view
    }

    fun updateImage(imagePath: String) {
        post?.image = imagePath
        BigPicUrlLoader.getInstance().loadBigImage(easy_status_bg, imagePath)
    }

    fun abort() {
        isAbort.set(true)
    }

    fun copyView(callback: (Bitmap?) -> Unit) {
        mCallback = callback
        isAbort.set(false)
        doCopy()
    }

    fun doCopy() {
        if (isAbort.get()) {
            e("ImageFragment", "copyView failed,because the image not downloaded!")
            return
        }
        if (!isDownloaded.get()) {
            e("ImageFragment", "copyView failed,because the image not downloaded!")
            return
        }

        var editorText = mEditText.text.toString()

        PostStatusEventManager.INSTANCE.setImageid(post?.image)
        if (TextUtils.equals(post?.word, editorText)) {
            PostStatusEventManager.INSTANCE.setTextchanged(0)
        } else {
            PostStatusEventManager.INSTANCE.setTextchanged(1)
        }
        PostStatusEventManager.INSTANCE.text = editorText
        view?.let {

            InputMethodUtil.hideInputMethod(activity)
            threadUIDelay(500) {

                it.isDrawingCacheEnabled = true
                it.buildDrawingCache()
                var backgroundBitmap = Bitmap.createBitmap(it.drawingCache)

                mEditText.isDrawingCacheEnabled = true
                mEditText.buildDrawingCache()
                var textBitmap = Bitmap.createBitmap(mEditText.drawingCache)

                val canvas = Canvas(backgroundBitmap)
                canvas.drawBitmap(textBitmap, mEditText.x, mEditText.y, null)
                textBitmap.recycle()
                mCallback(backgroundBitmap)
            }
        }
    }

}