package com.redefine.multimedia.picturelooker.loader;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.fresco.loader.BigPicUrlLoader;
import com.redefine.commonui.fresco.oss.BigPicUrlFilter;
import com.redefine.commonui.photoview.PhotoDraweeView;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.picturelooker.config.PictureMimeType;
import com.redefine.multimedia.picturelooker.listener.OnLargeImageLoadListener;
import com.redefine.multimedia.picturelooker.widget.longimage.ImageSource;
import com.redefine.multimedia.picturelooker.widget.longimage.ImageViewState;
import com.redefine.multimedia.picturelooker.widget.longimage.SubsamplingScaleImageView;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.io.WeLikeFileManager;

import java.io.File;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class LargePicUrlLoader {

    private final BigPicUrlFilter mFilter;

    private LargePicUrlLoader() {
        mFilter = new BigPicUrlFilter();
    }

    private static class BigPicUrlLoaderHolder {
        private static final LargePicUrlLoader INSTANCE = new LargePicUrlLoader();

        private BigPicUrlLoaderHolder() {

        }
    }

    public static LargePicUrlLoader getInstance() {
        return BigPicUrlLoaderHolder.INSTANCE;
    }


    /**
     * 图片选择框架
     *
     * @param draweeView
     * @param media
     * @param isHttp
     */
    public void loadBigImage(final SubsamplingScaleImageView scaleImageView, final PhotoDraweeView draweeView, Item media,
                             boolean isHttp, final OnLargeImageLoadListener largeImageLoadListener) {
        if (TextUtils.isEmpty(media.filePath)) {
            draweeView.setImageURI("");
            return;
        }
        final String pictureType = media.mimeType;
        Uri uri;
        String uriStr;
        if (isHttp) {
            if (PictureMimeType.isLongImg(media.width, media.height, draweeView.getContext())) {
                uriStr = media.filePath;
            } else {
                uriStr = mFilter.filter(media.filePath);
            }

            uri = Uri.parse(uriStr);
        } else {
            uri = media.getContentUri();
            uriStr = media.filePath;
        }

        int width, height;

        if (media.height == 0 || media.width == 0) {
            width = ScreenUtils.getSreenWidth(draweeView.getContext());
            height = ScreenUtils.getScreenHeight(draweeView.getContext());
        } else {
            width = media.width;
            height = media.height;
        }

        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setRotationOptions(RotationOptions.autoRotate())
                .build();
        draweeView.setEnableDraweeMatrix(false);
        final String finalUriStr = uriStr;
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_UNKNOWN);
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo != null) {
                            boolean eqLongImg = PictureMimeType.isLongImg(imageInfo.getWidth(), imageInfo.getHeight(), draweeView.getContext());
                            boolean isGif = PictureMimeType.isGif(pictureType);
                            if (eqLongImg && !isGif) {
                                draweeView.setVisibility(View.GONE);
                                scaleImageView.setVisibility(View.VISIBLE);
                                getBitmap(finalUriStr, scaleImageView, largeImageLoadListener);
                            } else {
                                draweeView.setEnableDraweeMatrix(true);
                                scaleImageView.setVisibility(View.GONE);
                                largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_SUCCESS);
                                draweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                            }
                        } else {
                            largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_UNKNOWN);
                        }
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                        largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_UNKNOWN);
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);

                        if (imageInfo != null) {
                            boolean eqLongImg = PictureMimeType.isLongImg(imageInfo.getWidth(), imageInfo.getHeight(), draweeView.getContext());
                            boolean isGif = PictureMimeType.isGif(pictureType);
                            if (eqLongImg && !isGif) {
                                draweeView.setVisibility(View.GONE);
                                scaleImageView.setVisibility(View.VISIBLE);
                                getBitmap(finalUriStr, scaleImageView, largeImageLoadListener);
                            } else {
                                draweeView.setEnableDraweeMatrix(true);
                                scaleImageView.setVisibility(View.GONE);
                                largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_SUCCESS);
                                draweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                            }
                        } else {
                            largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_UNKNOWN);
                        }
                    }
                });
        draweeView.setController(builder.build());
    }


    private void getBitmap(String path, final SubsamplingScaleImageView longImg, final OnLargeImageLoadListener largeImageLoadListener) {
        boolean isHttp = PictureMimeType.isHttp(path);
        if (isHttp) {
            ImageRequest imageRequest = BigPicUrlLoader.getInstance().getImageLargeRequest(longImg.getContext(), path, true);
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            if (resource instanceof FileBinaryResource) {
                File cacheFile = ((FileBinaryResource) resource).getFile();
                if (cacheFile != null && cacheFile.exists()) {
                    displayLongPic(Uri.fromFile(cacheFile), longImg);
                    largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_SUCCESS);
                    return;
                }
            }
            CacheKey bitmapCacheKey = DefaultCacheKeyFactory.getInstance().getBitmapCacheKey(imageRequest, null);
            CloseableReference<CloseableImage> bitmapCloseableReference = ImagePipelineFactory.getInstance().getBitmapMemoryCache().get(bitmapCacheKey);
            if (bitmapCloseableReference != null) {
                CloseableImage image = bitmapCloseableReference.get();
                if (image instanceof CloseableBitmap) {
                    Bitmap bitmap = ((CloseableBitmap) image).getUnderlyingBitmap();
                    if (bitmap != null) {
                        displayLongPic(bitmap, longImg);
                        largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_SUCCESS);
                        return;
                    }
                }
            }
            final String suffix = PictureMimeType.getLastImgType(path);
            // 还是找不到，说明可能是动图，并且本地cache已经被清空，获取未解码的图片
            DataSource<CloseableReference<PooledByteBuffer>> dataSource = ImagePipelineFactory.getInstance().getImagePipeline().fetchEncodedImage(imageRequest, null);
            dataSource.subscribe(new DataSubscriber<CloseableReference<PooledByteBuffer>>() {
                @Override
                public void onNewResult(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                    if (dataSource.isFinished()) {
                        CloseableReference<PooledByteBuffer> ref = dataSource.getResult();
                        if (ref != null) {
                            PooledByteBuffer result = ref.get();
                            InputStream is = new PooledByteBufferInputStream(result);
                            final File saveFile = WeLikeFileManager.saveToPhotoSaveDir(is, System.currentTimeMillis() + suffix);
                            if (saveFile != null && saveFile.exists()) {
//                                            // 保存成功
                                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                    @Override
                                    public void run() {
                                        displayLongPic(Uri.fromFile(saveFile), longImg);
                                        largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_SUCCESS);

                                    }
                                });
                                return;
                            }
                        }
                    }

                    // 本地缓存找不到，直接提示保存失败
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_UNKNOWN);
                        }
                    });
                }

                @Override
                public void onFailure(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                    // 本地缓存找不到，直接提示保存失败
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_UNKNOWN);
                        }
                    });
                }

                @Override
                public void onCancellation(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                    // 本地缓存找不到，直接提示保存失败
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_UNKNOWN);
                        }
                    });
                }

                @Override
                public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {

                }
            }, CallerThreadExecutor.getInstance());


        } else {
            final File cacheFile = new File(path);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    largeImageLoadListener.onLargeImageLoad(ErrorCode.ERROR_SUCCESS);
                    displayLongPic(Uri.fromFile(cacheFile), longImg);
                }
            });
        }
    }


    /**
     * 加载长图
     *
     * @param path
     * @param longImg
     */
    private void displayLongPic(Uri path, SubsamplingScaleImageView longImg) {

        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.uri(path), new ImageViewState(0, new PointF(0, 0), 0));
    }

    /**
     * 加载长图
     *
     * @param bimap
     * @param longImg
     */
    private void displayLongPic(Bitmap bimap, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.cachedBitmap(bimap), new ImageViewState(0, new PointF(0, 0), 0));
    }
}
