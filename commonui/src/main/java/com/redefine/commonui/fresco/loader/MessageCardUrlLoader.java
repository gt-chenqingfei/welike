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
import com.redefine.commonui.fresco.oss.IUrlFilter;
import com.redefine.commonui.fresco.oss.MessageCardUrlFilter;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class MessageCardUrlLoader {

    private final IUrlFilter mFilter;

    private MessageCardUrlLoader() {
        mFilter = new MessageCardUrlFilter();
    }

    private static class MessageCardUrlLoaderHolder {
        private static final MessageCardUrlLoader INSTANCE = new MessageCardUrlLoader();
        private MessageCardUrlLoaderHolder() {

        }
    }

    public static MessageCardUrlLoader getInstance() {
        return MessageCardUrlLoaderHolder.INSTANCE;
    }

    public void loadMessageCardUrl(SimpleDraweeView draweeView, String url) {
        url = mFilter.filter(url);
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return ;
        }
        Uri uri = Uri.parse(url);
        loadMessageCardUri(draweeView, uri);
    }

    private void loadMessageCardUri(SimpleDraweeView draweeView, Uri uri) {
        if (uri == null) {
            draweeView.setImageURI("");
            return ;
        }
        ViewGroup.LayoutParams params = draweeView.getLayoutParams();
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER);
        ImageDecodeOptions options = ImageDecodeOptions.newBuilder().setForceStaticImage(true).setDecodePreviewFrame(true).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(params.width, params.height))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setImageDecodeOptions(options)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setImageRequest(request);
        draweeView.setController(builder.build());
    }

}
