package com.redefine.welike.business.user.ui.adapter

import android.graphics.drawable.Animatable
import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import io.reactivex.android.schedulers.AndroidSchedulers

class TempTool {

    companion object {
        fun loadSinglePicUri(draweeView: SimpleDraweeView, uri: Uri, t: (color: Int) -> Unit) {
            val request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setCacheChoice(ImageRequest.CacheChoice.DEFAULT).build()

            val builder = Fresco.getDraweeControllerBuilderSupplier().get().also {
                it.oldController = draweeView.controller
                it.controllerListener = (object : BaseControllerListener<ImageInfo>() {

                    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                        super.onFinalImageSet(id, imageInfo, animatable)
                        val color = getColor(request)
                        when (color != 0) {
                            true -> AndroidSchedulers.mainThread().scheduleDirect {
                                t.invoke(color)
                            }
                        }
                    }
                })
                it.imageRequest = request
            }
            draweeView.controller = builder.build()
        }


        fun getColor(imageRequest: ImageRequest): Int {
            val bitmapCacheKey = DefaultCacheKeyFactory.getInstance().getBitmapCacheKey(imageRequest, null)
            val bitmapCloseableReference = ImagePipelineFactory.getInstance().bitmapMemoryCache.get(bitmapCacheKey)
            if (bitmapCloseableReference != null) {
                bitmapCloseableReference.get().let {
                    if (it is CloseableBitmap) {
                        val bitmap = it.underlyingBitmap
                        if (bitmap != null) {
                            return bitmap.getPixel(1, 1)
                        }
                    }
                }
            }
            return 0
        }
    }

}