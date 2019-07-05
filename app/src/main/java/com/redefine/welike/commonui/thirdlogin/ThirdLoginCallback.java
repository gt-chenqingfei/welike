package com.redefine.welike.commonui.thirdlogin;

import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;

/**
 * Created by nianguowang on 2018/7/16
 */
public interface ThirdLoginCallback {

    void onLoginBtnClick(ThirdLoginType thirdLoginType);

    void onLoginSuccess(ThirdLoginType thirdLoginType, ThirdLoginProfile profile);

    //TODO 将Facebook，Google，TrueCaller的错误码统一。
    void onLoginFail(ThirdLoginType thirdLoginType, int errorCode);

    void onLoginCancel(ThirdLoginType thirdLoginType);
}
