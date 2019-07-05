package com.redefine.welike.commonui.thirdlogin.strategy;

import android.content.Context;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.commonui.thirdlogin.ILoginStrategy;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;

/**
 * Created by nianguowang on 2018/7/16
 */
public class FacebookLoginStrategy implements ILoginStrategy {

    private Context mContext;
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;

    public FacebookLoginStrategy(Context context, LoginButton loginView) {
        this.mContext = context;
        this.mLoginButton = loginView;
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void login(final ThirdLoginCallback callback) {
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                ThirdLoginProfile profile = new ThirdLoginProfile();
                profile.facebookProfile = loginResult;
                if(callback != null) {
                    callback.onLoginSuccess(ThirdLoginType.FACEBOOK, profile);
                }
            }

            @Override
            public void onCancel() {
                if(callback != null) {
                    callback.onLoginCancel(ThirdLoginType.FACEBOOK);
                }
            }

            @Override
            public void onError(FacebookException error) {
                if(callback != null) {
                    callback.onLoginFail(ThirdLoginType.FACEBOOK, ErrorCode.ERROR_UNKNOWN);
                }
            }
        });

        if (AccessToken.getCurrentAccessToken() == null) {
            mLoginButton.callOnClick();
        } else {
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    public void logout() {
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
