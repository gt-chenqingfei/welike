package com.redefine.welike.commonui.thirdlogin;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.redefine.welike.R;

/**
 * Created by nianguowang on 2018/7/16
 */
public class ThirdLoginLayout extends ConstraintLayout {

    private ThirdLoginDelegate mThirdLoginDelegate;
//    private int color;
//    private Boolean isBold;
//    private float textSize ;

    public ThirdLoginLayout(Context context) {
        this(context, null);
    }

    public ThirdLoginLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThirdLoginLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        if (attrs != null) {
//            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ThirdLoginLayout);
//            color = ta.getColor(R.styleable.ThirdLoginLayout_textColor, ContextCompat.getColor(getContext(), R.color.color_31));
//            isBold = ta.getBoolean(R.styleable.ThirdLoginLayout_textStyle, true);
//            textSize = ta.getInt(R.styleable.ThirdLoginLayout_textSize, 14);
//            ta.recycle();
//        }

        init();
    }

    private void init() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.third_login_layout, this, true);
        mThirdLoginDelegate = new ThirdLoginDelegate(this, v);

    }

    public void registerCallback(ThirdLoginCallback callback) {
        mThirdLoginDelegate.registerCallback(callback);
    }

    public void setClickButton(int source) {

        mThirdLoginDelegate.onClick(source);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mThirdLoginDelegate.onActivityResult(requestCode, resultCode, data);
    }

    public boolean truecallerUsable() {
        return mThirdLoginDelegate.truecallerUsable();
    }

    public void phoneInsteadTruecallerOnUnusable() {
        mThirdLoginDelegate.phoneInsteadTruecallerOnUnusable();
    }

    public void truecallerHideOnUnusable() {
        mThirdLoginDelegate.truecallerHideOnUnusable();
    }

    public void showButton(boolean facebook, boolean google) {
        mThirdLoginDelegate.showButton(facebook, google);
    }

    public void setThirdClickEnable(boolean enble) {

        mThirdLoginDelegate.setThirdClickEnable(enble);
    }

    public void setThirdClick(ThirdLoginType type) {

        mThirdLoginDelegate.setThirdClick(type);

    }

}
