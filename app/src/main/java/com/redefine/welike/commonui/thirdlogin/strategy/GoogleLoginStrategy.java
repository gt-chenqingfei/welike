package com.redefine.welike.commonui.thirdlogin.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.redefine.welike.AppConfig;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.startup.ui.activity.RegistActivity;
import com.redefine.welike.commonui.thirdlogin.ILoginStrategy;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;

/**
 * Created by nianguowang on 2018/7/16
 */
public class GoogleLoginStrategy implements ILoginStrategy {

    public static final int RC_GET_AUTH_CODE = 9003;

    private Context mContext;
    private SignInButton mSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private ThirdLoginCallback mCallback;

    public GoogleLoginStrategy(Context context, SignInButton loginView) {
        mContext = context;
        mSignInButton = loginView;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AppConfig.GOOGLE_APP_ID)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    @Override
    public void login(ThirdLoginCallback callback) {
        mCallback = callback;

        if(mContext == null || !(mContext instanceof Activity)) {
            if(mCallback != null) {
                mCallback.onLoginFail(ThirdLoginType.GOOGLE, ErrorCode.ERROR_UNKNOWN);
            }
            return;
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        if (account != null && !TextUtils.isEmpty(account.getIdToken())) {
            mGoogleSignInClient.signOut().addOnCompleteListener((Activity)mContext, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    ((Activity) mContext).startActivityForResult(signInIntent, RC_GET_AUTH_CODE);
                }
            });
        } else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            ((Activity)mContext).startActivityForResult(signInIntent, RegistActivity.RC_GET_AUTH_CODE);
        }
    }

    @Override
    public void logout() {
        mGoogleSignInClient.signOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GET_AUTH_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount result = task.getResult(ApiException.class);
                ThirdLoginProfile profile = new ThirdLoginProfile();
                profile.googleProfile = result;
                if(mCallback != null) {
                    mCallback.onLoginSuccess(ThirdLoginType.GOOGLE, profile);
                }
            } catch (ApiException e) {
                e.printStackTrace();
                int statusCode = ErrorCode.networkExceptionToErrCode(e);
                if (statusCode == ErrorCode.ERROR_GOOGLE_LOGIN_CANCEL) {
                    if(mCallback != null) {
                        mCallback.onLoginCancel(ThirdLoginType.GOOGLE);
                    }
                } else {
                    if(mCallback != null) {
                        mCallback.onLoginFail(ThirdLoginType.GOOGLE, ErrorCode.ERROR_UNKNOWN);
                    }
                }
            }
        }
    }
}
