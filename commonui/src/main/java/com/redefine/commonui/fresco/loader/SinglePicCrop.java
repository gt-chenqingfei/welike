package com.redefine.commonui.fresco.loader;

import android.content.Context;
import android.graphics.RectF;

import com.redefine.foundation.utils.ScreenUtils;

/**
 * Created by liwenbo on 2018/3/17.
 */

public class SinglePicCrop {

    public static PicCropInfo getSinglePicShowRect(Context context, int width, int height) {
        if (width == 0 || height == 0) {
            width =  height = 1;
        }
        int screenWidth = ScreenUtils.getSreenWidth(context);
        int feedWidth = screenWidth;
        PicCropInfo info = new PicCropInfo();
        RectF rect = new RectF();
        float s = height * 1.0f / width * 1.0f;
        if (s > (3.0f / 2)) {
            // 竖长图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = rect.right * 4.0f / 3.0f;
            info.picType = PicType.LONG_VER;
        } else if (s > 1 && s <= (3.0f / 2)) {
            // 竖图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = s * rect.right;
            info.picType = PicType.VER;
        } else if (s == 1) {
            // 方图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = feedWidth;
            info.picType = PicType.SQUARE;
        } else if (s >= (3.0f / 4) && s < 1) {
            // 横图a
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = feedWidth * 1.0f * s;
            info.picType = PicType.HOR_A;
        } else if (s >= (9.0f / 16.0f) && s < (3.0f / 4.0f)) {
            // 横图b
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = s * rect.right;
            info.picType = PicType.HOR_B;
        } else if (s < (9.0f / 16.0f)) {
            // 横长图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = feedWidth * 9.0f / 16.0f;
            info.picType = PicType.LONG_HOR;
        }

        info.rectF = rect;
        return info;
    }


    public static PicCropInfo getSinglePicShowRectTest(Context context, int width, int height) {
        if (width == 0 || height == 0) {
            width =  height = 1;
        }
        int screenWidth = ScreenUtils.getSreenWidth(context);
        int feedWidth = screenWidth;
        PicCropInfo info = new PicCropInfo();
        RectF rect = new RectF();
        float s = height * 1.0f / width * 1.0f;
        if (s > (3.0f / 2)) {
            // 竖长图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
//            rect.bottom = rect.right * 4.0f / 3.0f;
            rect.bottom = feedWidth;
            info.picType = PicType.LONG_VER;
        } else if (s > 1 && s <= (3.0f / 2)) {
            // 竖图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
//            rect.bottom = s * rect.right;
            rect.bottom = feedWidth;
            info.picType = PicType.LONG_VER;
        } else if (s == 1) {
            // 方图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = feedWidth;
            info.picType = PicType.SQUARE;
        } else if (s >= (3.0f / 4) && s < 1) {
            // 横图a
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = feedWidth * 1.0f * s;
            info.picType = PicType.HOR_A;
        } else if (s >= (9.0f / 16.0f) && s < (3.0f / 4.0f)) {
            // 横图b
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = s * rect.right;
            info.picType = PicType.HOR_B;
        } else if (s < (9.0f / 16.0f)) {
            // 横长图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = feedWidth * 9.0f / 16.0f;
            info.picType = PicType.LONG_HOR;
        }

        info.rectF = rect;
        return info;
    }


    public static PicCropInfo getSingleEditorPicShowRect(Context context, int width, int height) {
        if (width == 0 || height == 0) {
            return null;
        }
        int screenWidth = ScreenUtils.getSreenWidth(context);
        int feedWidth = screenWidth - ScreenUtils.dip2Px(24);
        PicCropInfo info = new PicCropInfo();
        RectF rect = new RectF();
        float s = height * 1.0f / width * 1.0f;
        if (s > (3.0f / 2)) {
            // 竖长图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth * 3.0f / 4.0f;
            rect.bottom = rect.right * 3.0f / 2.0f;
            info.picType = PicType.LONG_VER;
        } else if (s > 1 && s <= (3.0f / 2)) {
            // 竖图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth * 3.0f / 4.0f;
            rect.bottom = s * rect.right;
            info.picType = PicType.VER;
        } else if (s == 1) {
            // 方图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth * 3.0f / 4.0f;
            rect.bottom = feedWidth * 3.0f / 4.0f;
            info.picType = PicType.SQUARE;
        } else if (s >= (3.0f / 4) && s < 1) {
            // 横图a
            rect.left = 0;
            rect.top = 0;
            rect.bottom = feedWidth * 3.0f / 4.0f;
            rect.right = Math.min(rect.bottom * 1.0f / s, feedWidth);
            info.picType = PicType.HOR_A;
        } else if (s >= (9.0f / 16.0f) && s < (3.0f / 4.0f)) {
            // 横图b
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = s * rect.right;
            info.picType = PicType.HOR_B;
        } else if (s < (9.0f / 16.0f)) {
            // 横长图
            rect.left = 0;
            rect.top = 0;
            rect.right = feedWidth;
            rect.bottom = feedWidth * 9.0f / 16.0f;
            info.picType = PicType.LONG_HOR;
        }

        info.rectF = rect;
        return info;
    }


    public static PicCropInfo getDefaultVideoShowRect(Context context) {
        int screenWidth = ScreenUtils.getSreenWidth(context);
        int feedWidth = screenWidth - ScreenUtils.dip2Px(24);
        PicCropInfo info = new PicCropInfo();
        RectF rect = new RectF();
        rect.left = 0;
        rect.top = 0;
        rect.right = feedWidth;
        rect.bottom = feedWidth * 9.0f / 16.0f;
        info.picType = PicType.LONG_HOR;
        info.rectF = rect;
        return info;
    }

    public static PicCropInfo getDefaultFeedVideoShowRect(Context context) {
        int screenWidth = ScreenUtils.getSreenWidth(context);
        int feedWidth = screenWidth;
        PicCropInfo info = new PicCropInfo();
        RectF rect = new RectF();
        rect.left = 0;
        rect.top = 0;
        rect.right = feedWidth;
        rect.bottom = feedWidth * 9.0f / 16.0f;
        info.picType = PicType.LONG_HOR;
        info.rectF = rect;
        return info;
    }

    public static PicCropInfo getDefault2PicShowRect(Context context) {
        int screenWidth = ScreenUtils.getSreenWidth(context);
        int feedWidth = (screenWidth - ScreenUtils.dip2Px(6)) / 2;
        PicCropInfo info = new PicCropInfo();
        RectF rect = new RectF();
        rect.left = 0;
        rect.top = 0;
        rect.right = feedWidth;
        rect.bottom = feedWidth;
        info.rectF = rect;
        return info;
    }

    public static enum PicType {
        SQUARE, LONG_VER, VER, HOR_A, HOR_B, LONG_HOR
    }

    public static class PicCropInfo {
        public PicType picType;
        public RectF rectF;
    }
}
