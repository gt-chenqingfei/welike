package com.redefine.welike.common;

import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.commonui.constant.UserConstants;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.commonui.widget.VipAvatar;

import java.util.HashMap;

import static com.redefine.welike.common.VipExtKt.getVipResource;
import static com.redefine.welike.common.VipExtKt.getVipSlideResource;

/**
 * Created by daining on 2018/4/18.
 */

public class VipUtil {

    @DrawableRes
    public static int getResource(int type) {
        return getVipResource(type);
    }

    @DrawableRes
    public static int getSlideResource(int type) {
        return getVipSlideResource(type);
    }

    public static void set(VipAvatar avatar, String url, int type) {
        ViewGroup.LayoutParams params = avatar.getLayoutParams();
        HeadUrlLoader.getInstance().supportLoad(avatar.getAvatar(), url, params.width, params.height);
        int x = params.width / 3;
        ViewGroup.LayoutParams lp = avatar.getVip().getLayoutParams();
        lp.height = lp.width = x;
//        lp.height = x;
        avatar.getVip().setLayoutParams(lp);
        avatar.getVip().setImageResource(getResource(type));
    }

    public static void setNickName(TextView nickNameView, int curLevel, String nickName) {
        if (nickNameView == null) {
            return;
        }
        nickNameView.setText(nickName);
        if (curLevel == UserConstants.USER_COMMON_USER) {
            nickNameView.setTextColor(nickNameView.getResources().getColor(R.color.common_text_color_31));
        } else if (curLevel == UserConstants.USER_PRO_USER) {
//            nickNameView.setTextColor(nickNameView.getResources().getColor(R.color.color_44B3FF));
            nickNameView.setTextColor(nickNameView.getResources().getColor(R.color.common_text_color_31));
        } else if (curLevel == UserConstants.USER_TOP_USER) {
            nickNameView.setTextColor(nickNameView.getResources().getColor(R.color.main_orange_dark));
        }
    }

    private static SparseArray<HashMap<String, String>> res = new SparseArray<>();
    private static String mCertifyAddress;
    private static String mProfileAddress;
    private static String mUserGradeAddress;

    public static String getDescription(int type) {
        String language = LocalizationManager.getInstance().getCurrentLanguage();
        if (TextUtils.isEmpty(language)) {
            language = "en";
        }
        String description = "";
        HashMap<String, String> map;
        if (res.indexOfKey(type) < 0) {
            //如果没有type ，取默认值。
            map = res.get(0);
        } else {
            map = res.get(type);
        }
        if (map != null) {
            if (!map.containsKey(language) && !language.equalsIgnoreCase("en")) {
                //如果没有 对应语言，尝试取en
                language = "en";
            }
            description = map.get(language);
        }
        return description;
    }

    public static void init(SparseArray<HashMap<String, String>> parser) {
        res = parser;
    }

    public static void setCertifyAddress(String address) {
        mCertifyAddress = address;
    }

    public static String getCertifyAddress() {
        return mCertifyAddress;
    }

    public static void setProfileAddress(String address) {
        mProfileAddress = address;
    }

    public static String getProfileAddress() {
        return mProfileAddress;
    }

    public static String getUserGradeAddress() {
        return mUserGradeAddress;
    }

    public static void setUserGradeAddress(String mUserGradeAddress) {
        VipUtil.mUserGradeAddress = mUserGradeAddress;
    }
}
