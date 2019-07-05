package com.redefine.welike.commonui.thirdlogin;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.redefine.welike.R;

/**
 * Created by nianguowang on 2018/7/16
 */
public class ThirdLoginLayout1 extends ConstraintLayout {

    private ThirdLoginDelegate1 mThirdLoginDelegate;

    protected int mViewType = 0;

    public ThirdLoginLayout1(Context context) {
        this(context, null);
    }

    public ThirdLoginLayout1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThirdLoginLayout1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ThirdLoginLayout1);
            mViewType = ta.getInt(R.styleable.ThirdLoginLayout1_layoutType, 0);
            ta.recycle();
        }
        if (mViewType == 0)
            init();
        if (mViewType == 2)
            init2();
        else {
            init1();
        }
    }

    private void init() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.third_login_layout1, this, true);
        mThirdLoginDelegate = new ThirdLoginDelegate1(this, v);
    }

    private void init1() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.third_login_layout2, this, true);
        mThirdLoginDelegate = new ThirdLoginDelegate1(this, v);
    }

    private void init2() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.third_login_layout3, this, true);
        mThirdLoginDelegate = new ThirdLoginDelegate1(this, v);
    }

    public void registerCallback(ThirdLoginCallback callback) {
        mThirdLoginDelegate.registerCallback(callback);
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
