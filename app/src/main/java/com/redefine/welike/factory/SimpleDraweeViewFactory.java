package com.redefine.welike.factory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by liwb on 2018/1/7.
 */

public class SimpleDraweeViewFactory {

    /**
     * TODO 待优化
     * @param context
     * @return
     */
    public static SimpleDraweeView newInstance(Context context) {
        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setFailureImage(new ColorDrawable(Color.GRAY))
                .setFailureImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setPlaceholderImage(new ColorDrawable(Color.GRAY))
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        simpleDraweeView.setHierarchy(hierarchy);
        return simpleDraweeView;
    }

    public static SimpleDraweeView wrapStatusUi(Context context, SimpleDraweeView draweeView) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setBackground(new ColorDrawable(Color.GRAY))
                .setFailureImage(new ColorDrawable(Color.GRAY))
                .setPlaceholderImage(new ColorDrawable(Color.GRAY))
                .setRetryImage(new ColorDrawable(Color.GRAY))
                .build();
        draweeView.setHierarchy(hierarchy);
        return draweeView;
    }
}
