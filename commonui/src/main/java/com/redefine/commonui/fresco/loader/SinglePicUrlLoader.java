package com.redefine.commonui.fresco.loader;

import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.fresco.oss.IUrlFilter;
import com.redefine.commonui.fresco.oss.SinglePicUrlFilter;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.foundation.utils.ScreenUtils;

import java.io.File;

/**
 * Created by liwenbo on 2018/2/24.
 */
@Deprecated
public class SinglePicUrlLoader {

    private static final float NINE_GRID_MARGIN = 40;
    private IUrlFilter mFilter;

    private SinglePicUrlLoader() {
        mFilter = new SinglePicUrlFilter();
    }

    private static class SinglePicUrlLoaderHolder {
        private static final SinglePicUrlLoader INSTANCE = new SinglePicUrlLoader();

        private SinglePicUrlLoaderHolder() {

        }
    }

    public static SinglePicUrlLoader getInstance() {
        return SinglePicUrlLoaderHolder.INSTANCE;
    }

    public void loadLocalSinglePic(SimpleDraweeView view, int width, int height, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            view.setImageURI("");
            return;
        }
        SinglePicCrop.PicCropInfo cropInfo = SinglePicCrop.getSinglePicShowRect(view.getContext(), width, height);
        if (cropInfo == null || cropInfo.rectF.width() == 0 || cropInfo.rectF.height() == 0) {
            int itemSize = (ScreenUtils.getSreenWidth(view.getContext()) - ScreenUtils.dip2Px(view.getContext(), NINE_GRID_MARGIN)) / 3;
            cropInfo = new SinglePicCrop.PicCropInfo();
            cropInfo.rectF = new RectF(0, 0, itemSize, itemSize);
            cropInfo.picType = SinglePicCrop.PicType.SQUARE;
        }
        loadSinglePicUri(view, cropInfo, Uri.fromFile(new File(filePath)));
    }

    public void loadSinglePicUrl(SimpleDraweeView view, int width, int height, String url) {

        SinglePicCrop.PicCropInfo cropInfo = SinglePicCrop.getSinglePicShowRect(view.getContext(), width, height);
        if (cropInfo == null || cropInfo.rectF.width() == 0 || cropInfo.rectF.height() == 0) {
            int itemSize = (ScreenUtils.getSreenWidth(view.getContext()) - ScreenUtils.dip2Px(view.getContext(), NINE_GRID_MARGIN)) / 3;
            cropInfo = new SinglePicCrop.PicCropInfo();
            cropInfo.rectF = new RectF(0, 0, itemSize, itemSize);
            cropInfo.picType = SinglePicCrop.PicType.SQUARE;
        }
        width = (int) cropInfo.rectF.width();
        height = (int) cropInfo.rectF.height();
        url = mFilter.filter(url, width, height);
        if (TextUtils.isEmpty(url)) {
            view.setImageURI("");
            return;
        }

        loadSinglePicUri(view, cropInfo, Uri.parse(url));
    }

    private void loadSinglePicUri(final SimpleDraweeView draweeView, SinglePicCrop.PicCropInfo cropInfo, Uri uri) {
        if (uri == null) {
            draweeView.setImageURI("");
            return;
        }
        if (cropInfo.picType == SinglePicCrop.PicType.LONG_HOR || cropInfo.picType == SinglePicCrop.PicType.SQUARE) {
            draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        } else {
            draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
            draweeView.getHierarchy().setActualImageFocusPoint(new PointF(0, 0));
        }

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
