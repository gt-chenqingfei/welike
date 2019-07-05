package com.redefine.commonui.fresco.loader;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;

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
import com.redefine.commonui.fresco.oss.LinkThumbUrlFilter;
import com.redefine.foundation.utils.LogUtil;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class LinkThumbUrlLoader {

    private final LinkThumbUrlFilter mFilter;

    private LinkThumbUrlLoader() {
        mFilter = new LinkThumbUrlFilter();
    }

    private static class LinkThumbUrlLoaderHolder {
        private static final LinkThumbUrlLoader INSTANCE = new LinkThumbUrlLoader();
        private LinkThumbUrlLoaderHolder() {

        }
    }

    public static LinkThumbUrlLoader getInstance() {
        return LinkThumbUrlLoaderHolder.INSTANCE;
    }

    public void loadLinkThumbUrl(SimpleDraweeView draweeView, String url) {
        url = mFilter.filter(url);
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return ;
        }
        loadLinkThumbUri(draweeView, Uri.parse(url));
    }

    private void loadLinkThumbUri(SimpleDraweeView draweeView, Uri uri) {
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
}
