package com.redefine.welike.commonui.thirdlogin;

import android.content.Intent;

/**
 * Created by nianguowang on 2018/7/16
 */
public interface ILoginStrategy {

    void login(ThirdLoginCallback callback);

    void logout();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
