package com.redefine.welike.commonui.thirdlogin;

import android.content.Intent;
import android.view.View;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.redefine.welike.R;
import com.redefine.welike.commonui.thirdlogin.strategy.FacebookLoginStrategy;
import com.redefine.welike.commonui.thirdlogin.strategy.GoogleLoginStrategy;
import com.redefine.welike.commonui.thirdlogin.strategy.TrueCallerLoginStrategy;
import com.redefine.welike.commonui.thirdlogin.util.Utils;
import com.truecaller.android.sdk.TrueButton;

/**
 * Created by nianguowang on 2018/7/16
 */
public class ThirdLoginDelegate1 implements View.OnClickListener {

    private View mThirdLoginLayout;
    private ILoginStrategy mLoginStrategy;
    private LoginButton mFacebookButton;
    private SignInButton mGoogleButton;
    private TrueButton mTrueCallerButton;
    private View mBtTrueCallerLogin;
    private View mBtGoogleLogin;
    private View mBtFbLogin;

    private ThirdLoginCallback mCallback;
    private boolean mIsTruecallerUsable;
    private boolean thirdClickEnable = true;


    ThirdLoginDelegate1(View loginLayout, View loginView) {
        mThirdLoginLayout = loginLayout;
        mFacebookButton = loginView.findViewById(R.id.fb_button);
        mBtFbLogin = loginView.findViewById(R.id.cl_fb);

        mGoogleButton = loginView.findViewById(R.id.google_login_button);
        mBtGoogleLogin = loginView.findViewById(R.id.cl_google);

        mTrueCallerButton = loginView.findViewById(R.id.com_truecaller_android_sdk_truebutton);
        mBtTrueCallerLogin = loginView.findViewById(R.id.cl_true_caller);

        mIsTruecallerUsable = Utils.isTruecallerInstalled(loginView.getContext()) && mTrueCallerButton.isUsable();
        mBtTrueCallerLogin.setTag(ThirdLoginType.TRUE_CALLER);

        mBtFbLogin.setOnClickListener(this);
        mBtGoogleLogin.setOnClickListener(this);
        mBtTrueCallerLogin.setOnClickListener(this);
    }

    public boolean truecallerUsable() {
        return mIsTruecallerUsable;
    }

    public void phoneInsteadTruecallerOnUnusable() {
        if (mIsTruecallerUsable) {
            mBtTrueCallerLogin.setTag(ThirdLoginType.TRUE_CALLER);
        } else {
            mBtTrueCallerLogin.setTag(ThirdLoginType.PHONE);
        }
    }

    public void truecallerHideOnUnusable() {
        if (mIsTruecallerUsable) {
            mBtTrueCallerLogin.setVisibility(View.VISIBLE);
        } else {
            mBtTrueCallerLogin.setVisibility(View.GONE);
        }
    }

    public void showButton(boolean facebook, boolean google) {
        mThirdLoginLayout.setVisibility(View.VISIBLE);
        mBtFbLogin.setVisibility((facebook ? View.VISIBLE : View.GONE));
        mBtGoogleLogin.setVisibility(google ? View.VISIBLE : View.GONE);
        truecallerHideOnUnusable();
    }

    public void registerCallback(ThirdLoginCallback callback) {
        mCallback = callback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mLoginStrategy != null) {
            mLoginStrategy.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_fb:
                if (thirdClickEnable) {
                    mLoginStrategy = new FacebookLoginStrategy(v.getContext(), mFacebookButton);
                    mLoginStrategy.login(mCallback);
                }
                if (mCallback != null) {
                    mCallback.onLoginBtnClick(ThirdLoginType.FACEBOOK);
                }
                break;
            case R.id.cl_google:
                if (thirdClickEnable) {
                    mLoginStrategy = new GoogleLoginStrategy(v.getContext(), mGoogleButton);
                    mLoginStrategy.login(mCallback);
                }
                if (mCallback != null) {
                    mCallback.onLoginBtnClick(ThirdLoginType.GOOGLE);
                }
                break;
            case R.id.cl_true_caller:
                Object tag = v.getTag();
                if (tag != null) {
                    if (tag == ThirdLoginType.TRUE_CALLER) {
                        if (thirdClickEnable) {
                            mLoginStrategy = new TrueCallerLoginStrategy(v.getContext(), mTrueCallerButton);
                            mLoginStrategy.login(mCallback);
                        }
                    }
                    if (mCallback != null) {
                        mCallback.onLoginBtnClick((ThirdLoginType) tag);
                    }
                }
                break;
        }
    }

    public void setThirdClickEnable(boolean enable) {
        thirdClickEnable = enable;
    }

    public void setThirdClick(ThirdLoginType type) {
        switch (type) {
            case FACEBOOK:
                if (mFacebookButton != null) {
                    mLoginStrategy = new FacebookLoginStrategy(mFacebookButton.getContext(), mFacebookButton);
                    mLoginStrategy.login(mCallback);

                    if (mCallback != null) {
                        mCallback.onLoginBtnClick(ThirdLoginType.FACEBOOK);
                    }
                }
                break;
            case GOOGLE:
                if (mGoogleButton != null) {
                    mLoginStrategy = new GoogleLoginStrategy(mGoogleButton.getContext(), mGoogleButton);
                    mLoginStrategy.login(mCallback);

                    if (mCallback != null) {
                        mCallback.onLoginBtnClick(ThirdLoginType.GOOGLE);
                    }
                }
                break;
            case TRUE_CALLER:
                if (mTrueCallerButton != null) {
                    mLoginStrategy = new TrueCallerLoginStrategy(mTrueCallerButton.getContext(), mTrueCallerButton);
                    mLoginStrategy.login(mCallback);
                    if (mCallback != null) {
                        mCallback.onLoginBtnClick(ThirdLoginType.TRUE_CALLER);
                    }
                }
                break;
        }
    }


}
