package com.redefine.welike.commonui.thirdlogin.bean;

import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.truecaller.android.sdk.TrueProfile;

/**
 * Created by nianguowang on 2018/7/16
 */
public class ThirdLoginProfile {

    public LoginResult facebookProfile;

    public GoogleSignInAccount googleProfile;

    public TrueProfile mTrueCallerProfile;
}
