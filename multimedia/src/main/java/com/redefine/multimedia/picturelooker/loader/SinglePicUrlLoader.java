package com.redefine.multimedia.picturelooker.loader;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
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
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.fresco.loader.BigPicUrlLoader;
import com.redefine.commonui.fresco.loader.SinglePicCrop;
import com.redefine.commonui.fresco.oss.SinglePicUrlFilter;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.R;
import com.redefine.multimedia.picturelooker.config.PictureMimeType;
import com.redefine.multimedia.picturelooker.widget.longimage.ImageSource;
import com.redefine.multimedia.picturelooker.widget.longimage.ImageViewState;
import com.redefine.multimedia.picturelooker.widget.longimage.SubsamplingScaleImageView;
import com.redefine.welike.base.io.WeLikeFileManager;

import java.io.File;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by honlin on 2018/6/19.
 */

public class SinglePicUrlLoader {

    private final SinglePicUrlFilter mFilter;
    private static final float NINE_GRID_MARGIN = 40;

    private SinglePicUrlLoader() {
        mFilter = new SinglePicUrlFilter();
    }

    private static class BigPicUrlLoaderHolder {
        private static final SinglePicUrlLoader INSTANCE = new SinglePicUrlLoader();

        private BigPicUrlLoaderHolder() {

        }
    }

    public static SinglePicUrlLoader getInstance() {
        return BigPicUrlLoaderHolder.INSTANCE;
    }


    public SinglePicCrop.PicType loadSinglePicUrlTest(SimpleDraweeView view, SubsamplingScaleImageView scaleImageView, int width, int height, String url) {

        SinglePicCrop.PicCropInfo cropInfo = SinglePicCrop.getSinglePicShowRectTest(view.getContext(), width, height);
        if (cropInfo == null || cropInfo.rectF.width() == 0 || cropInfo.rectF.height() == 0) {
            int itemSize = ScreenUtils.getSreenWidth(view.getContext());
            cropInfo = new SinglePicCrop.PicCropInfo();
            cropInfo.rectF = new RectF(0, 0, itemSize, itemSize);
            cropInfo.picType = SinglePicCrop.PicType.SQUARE;
        }

//        if (cropInfo.picType != SinglePicCrop.PicType.LONG_VER) {
        width = (int) cropInfo.rectF.width();
        height = (int) cropInfo.rectF.height();
//        }

        if (cropInfo.picType == SinglePicCrop.PicType.LONG_HOR || cropInfo.picType == SinglePicCrop.PicType.SQUARE) {
            view.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        } else if (cropInfo.picType == SinglePicCrop.PicType.LONG_VER) {
            view.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
            view.getHierarchy().setActualImageFocusPoint(new PointF(.5f, 0));
        } else {
            view.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
            view.getHierarchy().setActualImageFocusPoint(new PointF(0, 0));
        }

        url = mFilter.filter(url, width, height);
        if (TextUtils.isEmpty(url)) {
            view.setImageURI("");
        } else
            loadSingleImage(scaleImageView, view, width, height, cropInfo.picType == SinglePicCrop.PicType.LONG_VER, url);
        return cropInfo.picType;
    }


    public SinglePicCrop.PicType loadSinglePicUrl(SimpleDraweeView view, SubsamplingScaleImageView scaleImageView, int width, int height, String url) {

        SinglePicCrop.PicCropInfo cropInfo = SinglePicCrop.getSinglePicShowRect(view.getContext(), width, height);
        if (cropInfo == null || cropInfo.rectF.width() == 0 || cropInfo.rectF.height() == 0) {
            int itemSize = ScreenUtils.getSreenWidth(view.getContext());
            cropInfo = new SinglePicCrop.PicCropInfo();
            cropInfo.rectF = new RectF(0, 0, itemSize, itemSize);
            cropInfo.picType = SinglePicCrop.PicType.SQUARE;
        }

//        if (cropInfo.picType != SinglePicCrop.PicType.LONG_VER) {
        width = (int) cropInfo.rectF.width();
        height = (int) cropInfo.rectF.height();
//        }

        if (cropInfo.picType == SinglePicCrop.PicType.LONG_HOR || cropInfo.picType == SinglePicCrop.PicType.SQUARE) {
            view.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        } else {
            view.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
            view.getHierarchy().setActualImageFocusPoint(new PointF(0, 0));
        }

        url = mFilter.filter(url, width, height);
        if (TextUtils.isEmpty(url)) {
            view.setImageURI("");
        } else
            loadSingleImage(scaleImageView, view, width, height, cropInfo.picType == SinglePicCrop.PicType.LONG_VER, url);
        return cropInfo.picType;
    }

    /**
     * 图片选择框架
     *
     * @param draweeView test oldurl
     */
    private void loadSingleImage(final SubsamplingScaleImageView scaleImageView, final SimpleDraweeView draweeView, int width, int height, final boolean isLarge, String url) {

        Uri uri = Uri.parse(url);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setProgressiveRenderingEnabled(true)
                .build();
        final String finalUriStr = url;
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo != null) {
                            if (isLarge) {
                                scaleImageView.setVisibility(View.VISIBLE);
                                getBitmap(finalUriStr, scaleImageView);
                            } else {
                                scaleImageView.setVisibility(View.GONE);
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);

                        if (imageInfo != null) {
                            if (isLarge) {
                                scaleImageView.setVisibility(View.VISIBLE);
                                getBitmap(finalUriStr, scaleImageView);
                            } else {
                                scaleImageView.setVisibility(View.GONE);
                            }
                        } else {
                        }
                    }
                });
        draweeView.setController(builder.build());
    }


    private void getBitmap(String path, final SubsamplingScaleImageView longImg) {
        boolean isHttp = PictureMimeType.isHttp(path);
        if (isHttp) {
            ImageRequest imageRequest = BigPicUrlLoader.getInstance().getImageLargeRequest(longImg.getContext(), path, true);
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            if (resource instanceof FileBinaryResource) {
                File cacheFile = ((FileBinaryResource) resource).getFile();
                if (cacheFile != null && cacheFile.exists()) {
                    displayLongPic(Uri.fromFile(cacheFile), longImg);
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
                        }
                    });
                }

                @Override
                public void onFailure(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                    // 本地缓存找不到，直接提示保存失败
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }

                @Override
                public void onCancellation(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                    // 本地缓存找不到，直接提示保存失败
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
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
