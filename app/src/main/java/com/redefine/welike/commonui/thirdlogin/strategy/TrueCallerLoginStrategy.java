package com.redefine.welike.commonui.thirdlogin.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.commonui.thirdlogin.ILoginStrategy;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueButton;
import com.truecaller.android.sdk.TrueClient;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;

/**
 * Created by nianguowang on 2018/7/16
 */
public class TrueCallerLoginStrategy implements ILoginStrategy {

    private Context mContext;
    private TrueButton mTrueButton;
    private TrueClient mTrueClient;

    public TrueCallerLoginStrategy(Context context, TrueButton loginView) {
        mContext = context;
        mTrueButton = loginView;
    }

    @Override
    public void login(final ThirdLoginCallback callback) {
        if(mContext == null || !(mContext instanceof Activity)) {
            if(callback != null) {
                callback.onLoginFail(ThirdLoginType.TRUE_CALLER, ErrorCode.ERROR_UNKNOWN);
            }
            return;
        }
        try {
            mTrueClient = new TrueClient(mContext, new ITrueCallback() {
                @Override
                public void onSuccesProfileShared(@NonNull TrueProfile trueProfile) {
                    ThirdLoginProfile profile = new ThirdLoginProfile();
                    profile.mTrueCallerProfile = trueProfile;
                    if(callback != null) {
                        callback.onLoginSuccess(ThirdLoginType.TRUE_CALLER, profile);
                    }
                }

                @Override
                public void onFailureProfileShared(@NonNull TrueError trueError) {
                    if(callback != null) {
                        callback.onLoginFail(ThirdLoginType.TRUE_CALLER, ErrorCode.ERROR_UNKNOWN);
                    }
                }
            });
            mTrueButton.setTrueClient(mTrueClient);
            mTrueClient.getTruecallerUserProfile((Activity)mContext);
        } catch (Exception e) {
            if(callback != null) {
                callback.onLoginFail(ThirdLoginType.TRUE_CALLER, ErrorCode.ERROR_UNKNOWN);
            }
        }
    }

    @Override
    public void logout() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mTrueClient != null && data != null) {
            mTrueClient.onActivityResult(requestCode, resultCode, data);
        }
    }
}
