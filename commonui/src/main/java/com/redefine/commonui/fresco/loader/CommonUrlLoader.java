package com.redefine.commonui.fresco.loader;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.fresco.oss.CommonUrlFilter;
import com.redefine.commonui.fresco.oss.IUrlFilter;
import com.redefine.commonui.fresco.size.ISizeProvider;

import java.io.File;

public class CommonUrlLoader {


    private IUrlFilter mFilter;

    private CommonUrlLoader() {
        mFilter = new CommonUrlFilter();
    }

    private static class CommonUrlLoaderHolder {
        private static final CommonUrlLoader INSTANCE = new CommonUrlLoader();

        private CommonUrlLoaderHolder() {

        }
    }

    public static CommonUrlLoader getInstance() {
        return CommonUrlLoader.CommonUrlLoaderHolder.INSTANCE;
    }
    /**
     * 加载feed9图
     * @param draweeView
     * @param file
     */
    public void loadLocalFile(final SimpleDraweeView draweeView, String file, ISizeProvider provider) {
        if (TextUtils.isEmpty(file)) {
            draweeView.setImageURI("");
            return ;
        }
        if (provider == null) {
            provider = new ISizeProvider() {
                @Override
                public int getWidth() {
                    return draweeView.getLayoutParams().width;
                }

                @Override
                public int getHeight() {
                    return draweeView.getLayoutParams().height;
                }
            };
        }

        Uri uri = Uri.fromFile(new File(file));
        loadUrlToItemView(draweeView, uri, provider.getWidth(), provider.getHeight());
    }
    /**
     * 加载feed9图
     * @param draweeView
     * @param url
     */
    public void loadUrl(final SimpleDraweeView draweeView, String url, ISizeProvider provider) {
        if (provider == null) {
            provider = new ISizeProvider() {
                @Override
                public int getWidth() {
                    return draweeView.getLayoutParams().width;
                }

                @Override
                public int getHeight() {
                    return draweeView.getLayoutParams().height;
                }
            };
        }

        url = mFilter.filter(url, provider.getHeight(), provider.getHeight());
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return ;
        }
        Uri uri = Uri.parse(url);
        loadUrlToItemView(draweeView, uri, provider.getWidth(), provider.getHeight());
    }
    /**
     * 加载feed9图
     * @param draweeView
     * @param uri
     */
    private void loadUrlToItemView(SimpleDraweeView draweeView, Uri uri, int width, int height) {
        if (uri == null) {
            draweeView.setImageURI("");
            return ;
        }
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

        ImageDecodeOptions options = ImageDecodeOptions.newBuilder().setForceStaticImage(true).setDecodePreviewFrame(true).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setImageDecodeOptions(options)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(false)
                .setImageRequest(request);
        draweeView.setController(builder.build());
    }
}
