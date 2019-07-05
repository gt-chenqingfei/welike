package com.redefine.commonui.fresco.loader;

import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.foundation.utils.ScreenUtils;

import java.io.File;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class LocalImageLoader {
    private static final float MAX_PIC_SELECTOR_SIZE = 100;

    private LocalImageLoader() {

    }

    private static class LocalImageLoaderHolder {
        private static final LocalImageLoader INSTANCE = new LocalImageLoader();
        private LocalImageLoaderHolder() {

        }
    }

    public static LocalImageLoader getInstance() {
        return LocalImageLoaderHolder.INSTANCE;
    }

    /**
     * 加载固定宽高的draweeView
     * @param draweeView
     * @param filePath
     */
    public void loadFixedSizeImageFromLocal(SimpleDraweeView draweeView, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            draweeView.setImageURI("");
            return ;
        }
        Uri uri = Uri.fromFile(new File(filePath));
        if (uri == null) {
            draweeView.setImageURI("");
            return ;
        }
        ViewGroup.LayoutParams params = draweeView.getLayoutParams();
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        ImageDecodeOptions options = ImageDecodeOptions.newBuilder().setForceStaticImage(true).setDecodePreviewFrame(true).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(params.width, params.height))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setImageDecodeOptions(options)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController()).setImageRequest(request);
        draweeView.setController(builder.build());
    }

    public void loadFileForPicSelectorGrid(SimpleDraweeView draweeView, String filePath, int overrideWidth, int overrideHeight) {
        if (overrideWidth <= 0) {
            overrideWidth = ScreenUtils.dip2Px(draweeView.getContext(), MAX_PIC_SELECTOR_SIZE);
        }
        if (overrideHeight <= 0) {
            overrideHeight = ScreenUtils.dip2Px(draweeView.getContext(), MAX_PIC_SELECTOR_SIZE);
        }
        Uri uri = Uri.fromFile(new File(filePath));
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(overrideWidth, overrideHeight))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(true)
                .setImageRequest(request);
        draweeView.setController(builder.build());
    }

    public void loadFileForPicSelectorGrid(SimpleDraweeView draweeView, Uri uri, int overrideWidth, int overrideHeight) {
        if (overrideWidth <= 0) {
            overrideWidth = ScreenUtils.dip2Px(draweeView.getContext(), MAX_PIC_SELECTOR_SIZE);
        }
        if (overrideHeight <= 0) {
            overrideHeight = ScreenUtils.dip2Px(draweeView.getContext(), MAX_PIC_SELECTOR_SIZE);
        }
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(overrideWidth, overrideHeight))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setRotationOptions(RotationOptions.autoRotate())
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(true)
                .setImageRequest(request);
        draweeView.setController(builder.build());
    }

}
