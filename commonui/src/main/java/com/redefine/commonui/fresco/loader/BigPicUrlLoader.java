package com.redefine.commonui.fresco.loader;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.fresco.oss.BigPicUrlFilter;
import com.redefine.commonui.photoview.PhotoDraweeView;
import com.redefine.foundation.utils.ScreenUtils;

import java.io.File;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class BigPicUrlLoader {

    private final BigPicUrlFilter mFilter;

    private BigPicUrlLoader() {
        mFilter = new BigPicUrlFilter();
    }

    private static class BigPicUrlLoaderHolder {
        private static final BigPicUrlLoader INSTANCE = new BigPicUrlLoader();

        private BigPicUrlLoaderHolder() {

        }
    }

    public static BigPicUrlLoader getInstance() {
        return BigPicUrlLoaderHolder.INSTANCE;
    }

    /**
     * 图片选择框架，加载本地图片
     *
     * @param draweeView
     * @param uriStr
     * @param isHttp
     */
    public void loadBigImage(final PhotoDraweeView draweeView, String uriStr, boolean isHttp) {
        if (TextUtils.isEmpty(uriStr)) {
            draweeView.setImageURI("");
            return;
        }
        Uri uri;
        if (isHttp) {
            uriStr = mFilter.filter(uriStr);
            uri = Uri.parse(uriStr);
        } else {
            uri = Uri.fromFile(new File(uriStr));
        }

        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(ScreenUtils.getSreenWidth(draweeView.getContext()), ScreenUtils.getScreenHeight(draweeView.getContext())))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .build();
        draweeView.setEnableDraweeMatrix(false);
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        draweeView.setEnableDraweeMatrix(false);
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        draweeView.setEnableDraweeMatrix(true);
                        if (imageInfo != null) {
                            draweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                        draweeView.setEnableDraweeMatrix(false);
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);
                        draweeView.setEnableDraweeMatrix(true);
                        if (imageInfo != null) {
                            draweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }
                });
        draweeView.setController(builder.build());
    }

    public ImageRequest getImageRequest(Context context, String path, boolean isHttp) {
        Uri uri;
        if (isHttp) {
            path = mFilter.filter(path);
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(ScreenUtils.getSreenWidth(context), ScreenUtils.getScreenHeight(context)))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .build();
        return request;
    }

    public ImageRequest getImageLargeRequest(Context context, String path, boolean isHttp) {
        Uri uri;
        if (isHttp) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(ScreenUtils.getSreenWidth(context), ScreenUtils.getScreenHeight(context)))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .build();
        return request;
    }

    public ImageRequest getImageLargeRequest(Context context, String path, int width, int height, boolean isHttp) {
        Uri uri;
        if (isHttp) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .build();
        return request;
    }


    public void loadBigImage(final SimpleDraweeView draweeView, String uriStr) {
        loadBigImage(draweeView, uriStr, null);
    }

    public void loadBigImage(final SimpleDraweeView draweeView, String uriStr, final OnImageLoadListener listener) {
        if (draweeView == null) {
            return;
        }
        if (TextUtils.isEmpty(uriStr)) {
            draweeView.setImageURI("");
            return;
        }

        boolean isHttp = (uriStr.contains("http") || uriStr.contains("https"));

        Uri uri;
        if (isHttp) {
            uriStr = mFilter.filter(uriStr);
            uri = Uri.parse(uriStr);
        } else {
            uri = Uri.fromFile(new File(uriStr));
        }

        final String url = uriStr;

        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setControllerListener(new ControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {
                        Log.e("chenqingfei", "loadBigImage onSubmit: " + url);
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        Log.e("chenqingfei", "loadBigImage onFinalImageSet:" + url);
                        draweeView.setTag(true);
                        if (listener != null) {
                            listener.onLoaded(draweeView);
                        }
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        Log.e("chenqingfei", "loadBigImage onIntermediateImageSet :" + url);
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        Log.e("chenqingfei", "loadBigImage onIntermediateImageFailed  ");
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        Log.e("chenqingfei", "loadBigImage onFailure :" + url);
                        if (listener != null) {
                            listener.onLoaded(null);
                        }
                    }

                    @Override
                    public void onRelease(String id) {
                        Log.e("chenqingfei", "loadBigImage onRelease :" + url);
                    }
                })
                .setImageRequest(request);
//                .setAutoPlayAnimations(true);
        draweeView.setController(builder.build());
    }

    public void prefeatchToDiskCache(String url) {
        if (isCacheInDisc(url)) {
            Log.e("chenqingfei", "prefeatchToDiskCache:" + url + " already cache in disc!");
            return;
        }
        Log.e("chenqingfei", "prefeatchToDiskCache:" + url);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .build();
        Fresco.getImagePipeline()
                .prefetchToDiskCache(request, null);
    }

    public boolean isCacheInMemory(String url) {
        return Fresco.getImagePipelineFactory().getMainBufferedDiskCache
                ().containsSync(new SimpleCacheKey(url));
    }

    public boolean isCacheInDisc(String url) {
        return Fresco.getImagePipelineFactory().getMainFileCache()
                .hasKey(new SimpleCacheKey(url));
    }

    public interface OnImageLoadListener {
        void onLoaded(SimpleDraweeView draweeView);
    }
}
