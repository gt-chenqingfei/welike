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
import com.redefine.commonui.fresco.oss.ChatUrlFilter;
import com.redefine.foundation.utils.LogUtil;

import java.io.File;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class ChatPicUrlLoader {
    private final ChatUrlFilter mFilter;

    private ChatPicUrlLoader() {
        mFilter = new ChatUrlFilter();
    }

    private static class ChatPicUrlLoaderHolder {
        private static final ChatPicUrlLoader INSTANCE = new ChatPicUrlLoader();
        private ChatPicUrlLoaderHolder() {

        }
    }

    public static ChatPicUrlLoader getInstance() {
        return ChatPicUrlLoaderHolder.INSTANCE;
    }

    public void loadChatPicUrl(SimpleDraweeView draweeView, String url) {
        url = mFilter.filter(url);
        if (TextUtils.isEmpty(url)) {
            return ;
        }
        Uri uri = Uri.parse(url);
        loadChatPicUri(draweeView, uri);
    }

    public void loadChatPicFile(SimpleDraweeView draweeView, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return ;
        }
        Uri uri = Uri.fromFile(new File(filePath));
        loadChatPicUri(draweeView, uri);
    }

    private void loadChatPicUri(SimpleDraweeView draweeView, Uri uri) {
        if (uri == null) {
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
                .setOldController(draweeView.getController())
                .setImageRequest(request);
        draweeView.setController(builder.build());
    }
}
