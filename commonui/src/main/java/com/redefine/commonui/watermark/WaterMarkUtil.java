package com.redefine.commonui.watermark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.media.mediasdk.UI.IWaterProcessor;
import com.media.mediasdk.codec.common.VideoSize;
import com.media.mediasdk.core.watermark.ImageUtil;
import com.redefine.commonui.R;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.io.WeLikeFileManager;

import java.io.File;

public class WaterMarkUtil {

    public static boolean markVideo(Context context, String absolutePath, String tag, final IWaterProcessor.IWaterProcessorCallback callback) {
        final IWaterProcessor waterProcessor = IWaterProcessor.CreateProcessorInstance();
//todo i say nothing.
        if (waterProcessor == null || 1 == 1) {
            return false;
        }

        waterProcessor.setProcessorType(IWaterProcessor.MEDIA_VIDEO);
        waterProcessor.SupportPreview(false);
        VideoSize videoSize = new VideoSize();
        boolean isSupport = waterProcessor.Support(absolutePath, videoSize);
        if (!isSupport) {
            return false;
        }
        waterProcessor.SetFilePath_In(absolutePath);
        waterProcessor.SetFilePath_Out(absolutePath + GlobalConfig.PUBLISH_PIC_SUFFIX);
        float scale;
        try {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextSize(ScreenUtils.dip2Px(12));
            Bitmap textBitmap = ImageUtil.createWaterMask_Text(tag, paint, ScreenUtils.dip2Px(4));
            int width = textBitmap.getWidth();

            Bitmap textScaleBitmap = createTextScaleBitmap(textBitmap, videoSize.nWidth, videoSize.nHeight);
            textBitmap.recycle();
            int scaleWidth = textScaleBitmap.getWidth();
            int scaleHeight = textScaleBitmap.getHeight();
            scale = scaleWidth * 1.0f / width;
            Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_water_mark);
            int logoWidth = logoBitmap.getWidth();

            Bitmap scaleLogoBitmap = createScaleBitmap(logoBitmap, videoSize.nWidth, videoSize.nHeight);
            int scaleLogoWidth = scaleLogoBitmap.getWidth();
            int scaleLogoHeight = scaleLogoBitmap.getHeight();
            if (scale == 1f) {
                scale = scaleLogoWidth * 1.0f / logoWidth;
            }
            logoBitmap.recycle();

            int maxWaterMarkWidth = Math.max(scaleWidth, scaleLogoWidth);
            int maxWaterMarkHeight = Math.min(scaleLogoHeight + scaleHeight, videoSize.nHeight);
            int padding = ScreenUtils.dip2Px(12 * scale);
            Bitmap resultBitmap = ImageUtil.CreateBitmap(maxWaterMarkWidth + padding * 2, maxWaterMarkHeight + padding * 2);
            resultBitmap = ImageUtil.createWaterMask_LeftBottom(resultBitmap, textScaleBitmap,
                    ScreenUtils.dip2Px(12 * scale), ScreenUtils.dip2Px(12 * scale));
            textScaleBitmap.recycle();
            resultBitmap = ImageUtil.createWaterMask_LeftBottom(resultBitmap, scaleLogoBitmap, ScreenUtils.dip2Px(12 * scale),
                    scaleHeight + ScreenUtils.dip2Px(12 * scale));
            scaleLogoBitmap.recycle();

            waterProcessor.SetWaterMark(resultBitmap, 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight());

            waterProcessor.SetWaterMarkListener(new IWaterProcessor.IWaterProcessorCallback() {
                @Override
                public void OnWaterProcessCallback(boolean b, String s, int i, int i1, long l) {
                    if (callback != null) {
                        callback.OnWaterProcessCallback(b, s, i, i1, l);
                    }
                }

            });

            if (!waterProcessor.Process()) {
                callback.OnWaterProcessCallback(false, absolutePath + GlobalConfig.PUBLISH_PIC_SUFFIX, videoSize.nWidth, videoSize.nHeight, 0);
            }
            return true;
        } catch (Throwable e) {
            // do nothing
        }
        return false;
    }

    public static boolean doWaterMark(Context context, File result, String tag) {
        if (isGif(result.getAbsolutePath()) || isWebP(result.getAbsolutePath())) {
            return true;
        }
        try {
            Bitmap bitmap = WeLikeFileManager.readBitmap(result);
            if (bitmap == null) {
                return false;
            }
            int scaleHeight;
            float scale;
            Bitmap resultBitmap;
            if (!TextUtils.isEmpty(tag)) {
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(ContextCompat.getColor(context, R.color.white));
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextSize(ScreenUtils.dip2Px(12));
                paint.setShadowLayer(1, 1, 1, 0x3d000000);
                Bitmap textBitmap = ImageUtil.createWaterMask_Text(tag, paint, ScreenUtils.dip2Px(4));
                int width = textBitmap.getWidth();

                Bitmap textScaleBitmap = createTextScaleBitmap(textBitmap, bitmap.getWidth(), bitmap.getHeight());
                textBitmap.recycle();
                int scaleWidth = textScaleBitmap.getWidth();
                scaleHeight = textScaleBitmap.getHeight();
                scale = scaleWidth * 1.0f / width;
                resultBitmap = ImageUtil.createWaterMask_LeftBottom(bitmap, textScaleBitmap,
                        ScreenUtils.dip2Px(12 * scale), ScreenUtils.dip2Px(12 * scale));
            } else {
                scaleHeight = 0;
                resultBitmap = bitmap;
                scale = 1f;
            }

            bitmap.recycle();
            Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_water_mark);
            int logoWidth = logoBitmap.getWidth();

            Bitmap scaleLogoBitmap = createScaleBitmap(logoBitmap, resultBitmap.getWidth(), resultBitmap.getHeight());
            int scaleLogoWidth = scaleLogoBitmap.getWidth();
            if (scale == 1f) {
                scale = scaleLogoWidth * 1.0f / logoWidth;
            }
            logoBitmap.recycle();
            Bitmap logoWaterMark = ImageUtil.createWaterMask_LeftBottom(resultBitmap, scaleLogoBitmap, ScreenUtils.dip2Px(12 * scale),
                    scaleHeight + ScreenUtils.dip2Px(12 * scale));
            WeLikeFileManager.saveBitmap(result, logoWaterMark);
            return true;
        } catch (Throwable e) {
            // do nothing
        }
        return false;
    }

    public static Bitmap createScaleBitmap(Bitmap bitmap, int width, int height) {
        double precent = width > height ? 0.17 : 0.26;

        double logoPrecent = bitmap.getWidth() * 1.0f / bitmap.getHeight();

        double realWidth = width * 1.0f * precent;

        double realHeight = realWidth / logoPrecent;
        return ImageUtil.scaleWithWH(bitmap, realWidth, realHeight);

    }

    public static Bitmap createTextScaleBitmap(Bitmap bitmap, int width, int height) {
        double precent = width > height ? 0.033 : 0.05;

        double logoPrecent = bitmap.getWidth() * 1.0f / bitmap.getHeight();

        double realHeight = width * 1.0f * precent;

        double realWidth = realHeight * logoPrecent;
        return ImageUtil.scaleWithWH(bitmap, realWidth, realHeight);
    }


    public static boolean isGif(String thumbUrl) {
        if (TextUtils.isEmpty(thumbUrl)) {
            return false;
        }
        if (thumbUrl.endsWith("/")) {
            thumbUrl = thumbUrl.substring(0, thumbUrl.length() - 1);
        }
        if (TextUtils.isEmpty(thumbUrl)) {
            return false;
        }
        if (thumbUrl.endsWith("gif") || thumbUrl.endsWith("GIF")) {
            return true;
        }
        return false;
    }

    public static boolean isWebP(String thumbUrl) {
        if (TextUtils.isEmpty(thumbUrl)) {
            return false;
        }
        if (thumbUrl.endsWith("/")) {
            thumbUrl = thumbUrl.substring(0, thumbUrl.length() - 1);
        }
        if (TextUtils.isEmpty(thumbUrl)) {
            return false;
        }
        if (thumbUrl.endsWith("webp") || thumbUrl.endsWith("WEBP")) {
            return true;
        }
        return false;
    }

    private static final int minSize = 400;

    public static boolean markWithHeader(Context context, Bitmap bitmap, String nickName, String imagePath, String topicName) {

        try {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
            int originalWidth = imageBitmap.getWidth();
            int originalHeight = imageBitmap.getHeight();
            float bannerScale = 32 * 1.0f / 360;
            //画布 Width
            int resultWidth = originalWidth < minSize ? minSize : originalWidth;

            float bannerHeight = resultWidth * bannerScale;
            //画布 Height

            int resultHeight = originalHeight < minSize ? minSize : originalHeight;
            resultHeight = (int) (resultHeight + 2 * bannerHeight);

            //取比例
            float scaleX = minSize * 1.0f / originalWidth;
            float scaleY = minSize * 1.0f / originalHeight;

            Bitmap resultBitmap = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
            //判断原图是否 需要放大
            if (scaleX > 1 && scaleY > 1) {
                float scale = Math.min(scaleX, scaleY);
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            }

//            Bitmap resultBitmap = Bitmap.createBitmap(originalWidth, (int) (originalHeight + 2 * bannerHeight), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(imageBitmap, (resultBitmap.getWidth() - imageBitmap.getWidth()) / 2, (resultBitmap.getHeight() - imageBitmap.getHeight()) / 2, null);
            imageBitmap.recycle();
            float imageScale = 24 * 1.0f / 360;
//            float imageSize = originalWidth * imageScale;
            float imageSize = resultWidth * imageScale;
            float padding = (bannerHeight - imageSize) / 2;
            float nickPadding = padding;
            if (bitmap != null) {
                // 处理头像
                RectF rect = new RectF(padding, padding, imageSize + padding, imageSize + padding);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                float r = (float) Math.sqrt(2) * imageSize / 2;
                float innerWidth = r - imageSize / 2;
                paint.setStrokeWidth(innerWidth);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.WHITE);
                canvas.drawBitmap(bitmap, null, rect, null);
                canvas.save();
                canvas.clipRect(rect);
                canvas.drawCircle(rect.centerX(), rect.centerY(), imageSize / 2 + innerWidth / 2, paint);
                canvas.restore();
                paint.setStrokeWidth(1);
                paint.setColor(0xffdddddd);
                canvas.drawCircle(rect.centerX(), rect.centerY(), imageSize / 2, paint);
                nickPadding = padding + imageSize;
            }

            // 画昵称
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(0xff313131);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setShadowLayer(1, 1, 1, 0x1A000000);
            Bitmap textBitmap = ImageUtil.createWaterMask_Text(nickName, paint, ScreenUtils.dip2Px(4));

            double precent = 20 * 1.0f / 360;

            double textPrecent = textBitmap.getWidth() * 1.0f / textBitmap.getHeight();

//            double realHeight = originalWidth * 1.0f * precent;
            double realHeight = resultWidth * 1.0f * precent;
            double realWidth = realHeight * textPrecent;
            Bitmap realTextBitmap = ImageUtil.scaleWithWH(textBitmap, realWidth, realHeight);
            textBitmap.recycle();

            canvas.drawBitmap(realTextBitmap, nickPadding, (float) ((bannerHeight - realHeight) / 2), null);
            realTextBitmap.recycle();

            // 画topic
            if (!TextUtils.isEmpty(topicName)) {
                paint.setColor(0xff2B98EE);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setShadowLayer(1, 1, 1, 0x1A000000);
                Bitmap topicBitmap = ImageUtil.createWaterMask_Text(topicName, paint, ScreenUtils.dip2Px(4));
                double topicPercent = 20 * 1.0f / 360;

                double topicBitmapPercent = topicBitmap.getWidth() * 1.0f / topicBitmap.getHeight();

//                double topicRealHeight = originalWidth * 1.0f * topicPercent;
                double topicRealHeight = resultWidth * 1.0f * topicPercent;
                double topicRealWidth = topicRealHeight * topicBitmapPercent;
                padding = (float) ((bannerHeight - topicRealHeight) / 2);
                Bitmap topicRealTextBitmap = ImageUtil.scaleWithWH(topicBitmap, topicRealWidth, topicRealHeight);
                topicBitmap.recycle();

                canvas.drawBitmap(topicRealTextBitmap, 0, (float) (resultBitmap.getHeight() - padding - topicRealHeight), null);
                topicRealTextBitmap.recycle();
            }

            // 画logo
            Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.water_mark_logo_black);

            float logoScale = 74 * 1.0f / 360;
//            float logoWidth = originalWidth * logoScale;
            float logoWidth = resultWidth * logoScale;
            float logoHeight = logoWidth / (logoBitmap.getWidth() * 1.0f / logoBitmap.getHeight());
            padding = ((bannerHeight - logoHeight) / 2);
//            RectF rect = new RectF(originalWidth - logoWidth - padding, padding, originalWidth - padding, logoHeight + padding);
            RectF rect = new RectF(resultWidth - logoWidth - padding, padding, resultWidth - padding, logoHeight + padding);
            canvas.drawBitmap(logoBitmap, null, rect, null);

            logoBitmap.recycle();

            // 画商店
            Bitmap appStoreBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.water_mark_app_store);

            float appStoreScale = 130 * 1.0f / 360;
//            float appStoreWidth = originalWidth * appStoreScale;
            float appStoreWidth = resultWidth * appStoreScale;
            float appStoreHeight = appStoreWidth / (appStoreBitmap.getWidth() * 1.0f / appStoreBitmap.getHeight());
            padding = ((bannerHeight - appStoreHeight) / 2);
//            RectF appStoreRect = new RectF(originalWidth - appStoreWidth - padding, resultBitmap.getHeight() - padding - appStoreHeight, originalWidth - padding, resultBitmap.getHeight() - padding);
            RectF appStoreRect = new RectF(resultWidth - appStoreWidth - padding, resultBitmap.getHeight() - padding - appStoreHeight, resultWidth - padding, resultBitmap.getHeight() - padding);
            canvas.drawBitmap(appStoreBitmap, null, appStoreRect, null);
            appStoreBitmap.recycle();
            WeLikeFileManager.saveBitmap(new File(imagePath), resultBitmap);
            return true;
        } catch (Throwable e) {
            // do nothing
            e.printStackTrace();
        }
        return false;
    }

    public static boolean markWithNick(Context context, String nickName, String imagePath, String topicName) {
        return markWithHeader(context, null, nickName, imagePath, topicName);
    }
}
