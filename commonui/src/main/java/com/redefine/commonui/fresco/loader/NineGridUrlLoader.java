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
import com.redefine.commonui.fresco.oss.IUrlFilter;
import com.redefine.commonui.fresco.oss.NineGridUrlFilter;
import com.redefine.foundation.utils.ScreenUtils;

import java.io.File;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class NineGridUrlLoader {

    private static final float NINE_GRID_MARGIN = 40;
    private static final int COLUMN_COUNT = 3;
    private final IUrlFilter mFilter;

    private NineGridUrlLoader() {
        mFilter = new NineGridUrlFilter();
    }

    private static class NineGridUrlLoaderHolder {
        private static final NineGridUrlLoader INSTANCE = new NineGridUrlLoader();

        private NineGridUrlLoaderHolder() {

        }
    }

    public static NineGridUrlLoader getInstance() {
        return NineGridUrlLoaderHolder.INSTANCE;
    }
    /**
     * 加载feed9图
     * @param draweeView
     * @param file
     */
    public void loadNineGridFile(SimpleDraweeView draweeView, String file) {
        if (TextUtils.isEmpty(file)) {
            draweeView.setImageURI("");
            return ;
        }
        Uri uri = Uri.fromFile(new File(file));
        loadNineGridItemView(draweeView, uri, COLUMN_COUNT);
    }
    /**
     * 加载feed9图
     * @param draweeView
     * @param url
     */
    public void loadNineGridUrl(SimpleDraweeView draweeView, String url) {
        url = mFilter.filter(url);
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return ;
        }
        Uri uri = Uri.parse(url);
        loadNineGridItemView(draweeView, uri, COLUMN_COUNT);
    }

    /**
     * 加载feed9图
     * @param draweeView
     * @param url
     */
    public void loadNineGridUrl(SimpleDraweeView draweeView, String url,int count) {
        url = mFilter.filter(url);
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return ;
        }
        Uri uri = Uri.parse(url);
        loadNineGridItemView(draweeView, uri, count);
    }
    /**
     * 加载feed9图
     * @param draweeView
     * @param uri
     */
    public void loadNineGridItemView(SimpleDraweeView draweeView, Uri uri, int columnCount) {
        if (uri == null) {
            draweeView.setImageURI("");
            return ;
        }

        int width = ScreenUtils.getSreenWidth(draweeView.getContext());
        int itemSize = (width - ScreenUtils.dip2Px(draweeView.getContext(), NINE_GRID_MARGIN)) / columnCount;
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

        ImageDecodeOptions options = ImageDecodeOptions.newBuilder().setForceStaticImage(false).setDecodePreviewFrame(true).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(itemSize, itemSize))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setImageDecodeOptions(options)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setAutoPlayAnimations(false)
                .setOldController(draweeView.getController())
                .setImageRequest(request);
        draweeView.setController(builder.build());
    }
}
