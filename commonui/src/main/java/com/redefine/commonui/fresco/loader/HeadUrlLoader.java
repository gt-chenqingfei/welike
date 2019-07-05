package com.redefine.commonui.fresco.loader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.redefine.commonui.R;
import com.redefine.commonui.fresco.oss.HeadUrlFilter;
import com.redefine.commonui.fresco.oss.IUrlFilter;
import com.redefine.commonui.fresco.oss.LoadPicCallback;
import com.redefine.commonui.util.RoundBitmapUtils;
import com.redefine.foundation.utils.LogUtil;

import java.io.File;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class HeadUrlLoader {
    private static final int FollowerPost = 14;
    private static final int SMALL_PIC_BUSINESS = 201;
    private static final int BIG_PIC_BUSINESS = 202;
    private static final int BIG_TEXT_BUSINESS = 203;

    private IUrlFilter mFilter;

    private HeadUrlLoader() {
        mFilter = new HeadUrlFilter();
    }

    private static class HeadUrlLoaderHolder {
        private static final HeadUrlLoader INSTANCE = new HeadUrlLoader();

        private HeadUrlLoaderHolder() {

        }
    }

    public static HeadUrlLoader getInstance() {
        return HeadUrlLoaderHolder.INSTANCE;
    }

//    /**
//     * 头像采取居中剪切，且完全显示在容器里，并且宽高需强指定
//     * 使用small disk cache，防止因为大图造成diskcache被清除
//     *
//     * @param draweeView
//     * @param url
//     */
//    public void loadHeaderUrl(SimpleDraweeView draweeView, String url) {
//        url = mFilter.filter(url);
//        if (TextUtils.isEmpty(url)) {
//            draweeView.setImageURI("");
//            return ;
//        }
//        loadHeaderUri(draweeView, Uri.parse(url));
//    }

    /**
     * 头像采取居中剪切，且完全显示在容器里，并且宽高需强指定
     * 使用small disk cache，防止因为大图造成diskcache被清除
     *
     * @param draweeView
     * @param url
     */
    public void loadHeaderUrl2(SimpleDraweeView draweeView, String url) {
        url = mFilter.filter(url);
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return;
        }
        ViewGroup.LayoutParams params = draweeView.getLayoutParams();
        loadHeaderUri(draweeView, Uri.parse(url), params.width, params.height);
    }

    public void supportLoad(SimpleDraweeView draweeView, String url, int width, int height) {
        url = mFilter.filter(url);
        if (TextUtils.isEmpty(url)) {
            draweeView.setImageURI("");
            return;
        }
        loadHeaderUri(draweeView, Uri.parse(url), width, height);
    }

    public void loadHeaderFile(SimpleDraweeView draweeView, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            draweeView.setImageURI("");
            return;
        }
        ViewGroup.LayoutParams params = draweeView.getLayoutParams();
        loadHeaderUri(draweeView, Uri.fromFile(new File(filePath)), params.width, params.height);
    }

    private void loadHeaderUri(SimpleDraweeView draweeView, Uri uri, int width, int height) {
        if (uri == null) {
            draweeView.setImageURI("");
            return;
        }
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        ImageDecodeOptions imageDecodeOptions = new ImageDecodeOptionsBuilder()
                .setForceStaticImage(true)
                .build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setCacheChoice(ImageRequest.CacheChoice.SMALL)
                .setImageDecodeOptions(imageDecodeOptions)
                .build();
        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(draweeView.getController())
                .setImageRequest(request);
        draweeView.setController(builder.build());
    }
    public void LoadPicFromNet(final Context context, String url, int width, int height, final LoadPicCallback callback,final int type){
        if(type!=BIG_PIC_BUSINESS){
            url = mFilter.filter(url);
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(width, height))
                .setCacheChoice(ImageRequest.CacheChoice.SMALL)
                .build();

        DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline()
                .fetchDecodedImage(request, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                //to do for lizard bitmap destory
                if(null!=bitmap){

                       Bitmap pic= null;
                                  if(type!=SMALL_PIC_BUSINESS&&type!=BIG_PIC_BUSINESS&&type!=BIG_TEXT_BUSINESS){
                                      pic= RoundBitmapUtils.createCircleImage(bitmap,200);
                                  }else {
                                      pic=bitmap;
                                  }

                       callback.callback(pic);

                   }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                //to do for lizard bitmap destory

                Resources res = context.getResources();
                Bitmap bitmap=BitmapFactory.decodeResource(res, R.drawable.user_default_head);
                callback.callback(bitmap);
            }
        }, CallerThreadExecutor.getInstance());

    }



}
