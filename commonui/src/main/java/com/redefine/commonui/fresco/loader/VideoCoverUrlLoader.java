package com.redefine.commonui.fresco.loader;

import android.graphics.PointF;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.fresco.oss.VideoCoverFilter;

import java.io.File;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class VideoCoverUrlLoader {
    private final VideoCoverFilter mFilter;

    private VideoCoverUrlLoader() {
        mFilter = new VideoCoverFilter();
    }

    private static class VideoCoverUrlLoaderHolder {
        private static final VideoCoverUrlLoader INSTANCE = new VideoCoverUrlLoader();

        private VideoCoverUrlLoaderHolder() {

        }
    }

    public static VideoCoverUrlLoader getInstance() {
        return VideoCoverUrlLoaderHolder.INSTANCE;
    }

    public void loadVideoThumbUrl(View rootView, SimpleDraweeView draweeView, String url, int width, int height, int marginTotalWidth) {
        loadVideoThumbUrl(rootView, draweeView, url, false, width, height, marginTotalWidth);
    }

    public void loadVideoThumbUrl(View rootView, SimpleDraweeView draweeView, String url, boolean test, int width, int height, int marginTotalWidth) {
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return;
        }
        SinglePicCrop.PicCropInfo cropInfo;
        if (width <= 0 || height <= 0) {
            cropInfo = SinglePicCrop.getDefaultFeedVideoShowRect(draweeView.getContext());
        } else {
//            if (test)
                cropInfo = SinglePicCrop.getSinglePicShowRectTest(draweeView.getContext(), width, height);
//            else
//                cropInfo = SinglePicCrop.getSinglePicShowRect(draweeView.getContext(), width, height);
        }
        if (cropInfo == null || cropInfo.rectF.width() == 0 || cropInfo.rectF.height() == 0) {
            cropInfo = SinglePicCrop.getDefaultFeedVideoShowRect(draweeView.getContext());
        }
        width = (int) cropInfo.rectF.width() - marginTotalWidth;
        height = (int) cropInfo.rectF.height();
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        if (params != null) {
            params.width = width;
            params.height = height;
            rootView.setLayoutParams(params);
        }
        url = mFilter.filter(url, width, height);
        Uri uri = Uri.parse(url);
        loadVideoThumbUri(draweeView, uri, cropInfo);
    }

    public void loadArtThumbUrl(View rootView, SimpleDraweeView draweeView, String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return;
        }
        SinglePicCrop.PicCropInfo cropInfo = SinglePicCrop.getSinglePicShowRect(draweeView.getContext(), width, height);
        if (cropInfo == null || cropInfo.rectF.width() == 0 || cropInfo.rectF.height() == 0) {
            cropInfo = SinglePicCrop.getDefaultFeedVideoShowRect(draweeView.getContext());
        }
        width = (int) cropInfo.rectF.width();
        height = (int) cropInfo.rectF.height();
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        if (params != null) {
            params.width = width;
            params.height = height;
            rootView.setLayoutParams(params);
        }
        Uri uri = Uri.parse(url);
        loadVideoThumbUri(draweeView, uri, cropInfo);
    }

    public void loadVideoThumbFile(View rootView, SimpleDraweeView draweeView, String filePath, int width, int height) {
        if (TextUtils.isEmpty(filePath)) {
            draweeView.setImageURI("");
            return;
        }
        SinglePicCrop.PicCropInfo cropInfo = SinglePicCrop.getSingleEditorPicShowRect(draweeView.getContext(), width, height);
        if (cropInfo == null || cropInfo.rectF.width() == 0 || cropInfo.rectF.height() == 0) {
            cropInfo = SinglePicCrop.getDefaultVideoShowRect(draweeView.getContext());
        }
        width = (int) cropInfo.rectF.width();
        height = (int) cropInfo.rectF.height();
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        if (params != null) {
            params.width = width;
            params.height = height;
            rootView.setLayoutParams(params);
        }

        Uri uri = Uri.fromFile(new File(filePath));
        if (filePath.contains("http://") || filePath.contains("https://")) {
            uri = Uri.parse(filePath);
        }
        loadVideoThumbUri(draweeView, uri, cropInfo);
    }

    private void loadVideoThumbUri(SimpleDraweeView draweeView, Uri uri, SinglePicCrop.PicCropInfo cropInfo) {
        if (uri == null) {
            draweeView.setImageURI("");
            return;
        }
        if (cropInfo.picType == SinglePicCrop.PicType.LONG_HOR || cropInfo.picType == SinglePicCrop.PicType.SQUARE) {
            draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        } else {
            draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
            draweeView.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0));
        }
//        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        ImageDecodeOptions options = ImageDecodeOptions.newBuilder().setForceStaticImage(true).setDecodePreviewFrame(true).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions((int) cropInfo.rectF.width(), (int) cropInfo.rectF.height()))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setImageDecodeOptions(options)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController()).setImageRequest(request);
        draweeView.setController(builder.build());
    }
}
