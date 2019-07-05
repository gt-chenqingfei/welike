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
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.fresco.oss.BannerUrlFilter;
import com.redefine.foundation.utils.ScreenUtils;

/**
 * Created by liwenbo on 2018/3/22.
 */

public class BannerUrlLoader {


    private final BannerUrlFilter mFilter;

    private BannerUrlLoader() {
        mFilter = new BannerUrlFilter();
    }

    private static class BannerUrlLoaderHolder {
        private static final BannerUrlLoader INSTANCE = new BannerUrlLoader();
        private BannerUrlLoaderHolder() {

        }
    }

    public static BannerUrlLoader getInstance() {
        return BannerUrlLoaderHolder.INSTANCE;
    }

    public void loadBannerUrl(SimpleDraweeView draweeView, String url) {

        url = mFilter.filter(url);

        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return ;
        }

        Uri uri = Uri.parse(url);
        loadBannerUrl(draweeView, uri);
    }

    private void loadBannerUrl(SimpleDraweeView draweeView, Uri uri) {
        if (uri == null) {
            draweeView.setImageURI("");
            return ;
        }
        int width = ScreenUtils.getSreenWidth(draweeView.getContext());
        ViewGroup.LayoutParams params = draweeView.getLayoutParams();
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        ImageDecodeOptions options = ImageDecodeOptions.newBuilder().setForceStaticImage(true).setDecodePreviewFrame(true).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, params.height))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setImageDecodeOptions(options)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setImageRequest(request);
        draweeView.setController(builder.build());
    }
}
