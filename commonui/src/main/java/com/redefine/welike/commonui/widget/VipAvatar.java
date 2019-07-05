package com.redefine.welike.commonui.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.R;
import com.redefine.foundation.utils.ScreenUtils;

/**
 * Created by daining on 2018/4/18.
 */

public class VipAvatar extends FrameLayout {
    public SimpleDraweeView getAvatar() {
        return avatar;
    }

    SimpleDraweeView avatar;

    public AppCompatImageView getVip() {
        return vip;
    }

    AppCompatImageView vip;

    public VipAvatar(Context context) {
        this(context, null);
    }

    public VipAvatar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initView();
    }

    public VipAvatar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        avatar = new SimpleDraweeView(getContext());
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setPlaceholderImage(R.drawable.user_default_head)
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setRoundingParams(RoundingParams.asCircle()).build();
        avatar.setHierarchy(hierarchy);
        FrameLayout.LayoutParams avatarParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        avatarParams.topMargin = avatarParams.rightMargin = avatarParams.leftMargin = avatarParams.bottomMargin = ScreenUtils.dip2Px(1);
        addView(avatar, avatarParams);

        FrameLayout.LayoutParams vipParams = new FrameLayout.LayoutParams(ScreenUtils.dip2Px(17), ScreenUtils.dip2Px(17));
        vip = new AppCompatImageView(getContext());
        vipParams.gravity = Gravity.BOTTOM|Gravity.RIGHT;
        addView(vip, vipParams);
    }

    public void setVip(@DrawableRes int resId) {
        vip.setImageResource(resId);
    }
}
