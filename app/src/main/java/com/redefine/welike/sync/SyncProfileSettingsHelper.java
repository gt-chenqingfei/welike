package com.redefine.welike.sync;

import android.content.Context;

/**
 * Created by liubin on 2018/2/11.
 */

public class SyncProfileSettingsHelper {
    public static final String IM_MESSAGES_CURSOR_KEY = "imMessagesCursor";

    public static void modifyIMMessagesCursor(String cursor, Context context) {

//        JSONObject settingsJSON = new JSONObject();
//        if (!TextUtils.isEmpty(cursor)) {
//            settingsJSON.put(IM_MESSAGES_CURSOR_KEY, cursor);
//        }
//
//        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(settingsJSON.toJSONString(), context);
//        try {
//            updateAccountRequest.req(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

//    public static void modifyAll(Map<String, String> map, Context context) {
//
//        JSONObject settingsJSON = new JSONObject();
//
//        settingsJSON.putAll(map);
//
//        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(settingsJSON.toJSONString(), context);
//        try {
//            updateAccountRequest.req(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void saveToLocal(String settings) {
//        JSONObject settingsJSON = JSONObject.parseObject(settings);
//        String imMessagesCursor = null;
//        try {
//            imMessagesCursor = settingsJSON.getString(IM_MESSAGES_CURSOR_KEY);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (!TextUtils.isEmpty(imMessagesCursor)) {
//            ImAccountSettingCache.getInstance().setImMessageStamp(imMessagesCursor);
//        }
    }

}
