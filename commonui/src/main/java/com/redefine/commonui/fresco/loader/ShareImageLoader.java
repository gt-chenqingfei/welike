package com.redefine.commonui.fresco.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.fresco.oss.HeadUrlFilter;
import com.redefine.foundation.io.FileManager;
import com.redefine.foundation.utils.MD5Helper;
import com.redefine.welike.base.io.WeLikeFileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by nianguowang on 2018/5/16
 */
public class ShareImageLoader {

    private ShareImageLoader() {
    }

    private static class ShareImageLoaderHolder {
        private static ShareImageLoader INSTANCE = new ShareImageLoader();
    }

    public static ShareImageLoader getInstance() {
        return ShareImageLoaderHolder.INSTANCE;
    }

    public interface OnImageBitmapLoadListener {
        void onImageLoad(boolean success, Bitmap filePath);
    }

    public void loadSharePic(final Context context, final String url, final OnImageBitmapLoadListener listener) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(new HeadUrlFilter().doFilter(url)))
                .setProgressiveRenderingEnabled(true).build();

        DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline()
                .fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(final Bitmap bitmap) {
                Schedulers.newThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onImageLoad(true, bitmap);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            if (listener != null) {
                                listener.onImageLoad(false, null);
                            }
                        }
                    }
                });
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (listener != null) {
                    listener.onImageLoad(false, null);
                }
            }
        }, CallerThreadExecutor.getInstance());
    }

}
