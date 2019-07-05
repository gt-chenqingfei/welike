package com.redefine.welike.business.user.management.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.business.user.management.bean.DeactivateInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honlin on 2018/5/16.
 */

public class DeactivateUserInfoParser {

    public static final String KEY_0 = "detentionDesc";
    public static final String KEY_1 = "relationShipDesc";
    public static final String KEY_2 = "activeDesc";
    public static final String KEY_3 = "followings";
    public static final String KEY_4 = "avatarUrl";


    public static DeactivateInfoBean parseData(JSONObject result) {

        DeactivateInfoBean bean = null;

        if (result != null) {
            bean = new DeactivateInfoBean();

            if (result.containsKey(KEY_0)) {
                bean.setDetentionDesc(result.getString(KEY_0));
            } else {
                bean.setDetentionDesc("");
            }
            if (result.containsKey(KEY_1)) {
                bean.setRelationShipDesc(result.getString(KEY_1));
            } else {
                bean.setRelationShipDesc("");
            }
            if (result.containsKey(KEY_2)) {
                bean.setActiveDesc(result.getString(KEY_2));
            } else {
                bean.setActiveDesc("");
            }
            if (result.containsKey(KEY_3)) {
                JSONArray jsonArray = result.getJSONArray(KEY_3);

                if (jsonArray != null) {

                    List<String> avatars = new ArrayList<>();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        if (jo.containsKey(KEY_4)) {
                            String avatar = jo.getString(KEY_4);
                            if (!TextUtils.isEmpty(avatar))
                                avatars.add(avatar);
                        }

                    }

                    bean.setFollowings(avatars);
                } else {
                    bean.setFollowings(null);
                }
            } else bean.setFollowings(null);

        }


        return bean;
    }


}
