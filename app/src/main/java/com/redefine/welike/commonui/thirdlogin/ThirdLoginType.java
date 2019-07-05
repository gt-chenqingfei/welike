package com.redefine.welike.commonui.thirdlogin;

import com.redefine.welike.R;

/**
 * Created by nianguowang on 2018/7/16
 */
public enum ThirdLoginType {

    FACEBOOK(R.drawable.third_login_facebook, "Facebook"),
    GOOGLE(R.drawable.third_login_google, "Google"),
    TRUE_CALLER(R.drawable.third_login_truecaller, "Truecaller"),
    PHONE(R.drawable.third_login_phone, "Phone");

    public int iconRes;
    public String text;

    ThirdLoginType(int iconRes, String text) {
        this.iconRes = iconRes;
        this.text = text;
    }
}
