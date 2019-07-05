package com.redefine.foundation.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.EventLog;

import com.redefine.foundation.utils.walle.ChannelInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelHelper {
    private static ArrayList<String> tags = new ArrayList<>();
    public static String tagGroup;
    private static ReentrantLock lock = new ReentrantLock();

    public static ArrayList<String> getTags(Context context) {
        if (tags.size() > 0) {
            return tags;
        }
        //read from walle
        parserWalle(context);
        if (tags.size() > 0) {
            return tags;
        }
        //try to read from SP. saved from Firebase Dynamic Link
        parserDynamicLinkTag(context);
        if (tags.size() > 0) {
            return tags;
        }
        //try to read from SP. saved from AppsFlyer
        parserAF(context);
        if (tags.size() > 0) {
            return tags;
        }
        parserManifest(context);
        return tags;
    }

    /**
     * read from walle
     */
    private static void parserWalle(Context context) {
        ChannelInfo infoWalle = new WalleChannelReader().getChannelInfo(context);
        if (infoWalle != null) {
            Map<String, String> mapping = infoWalle.getExtraInfo();
            if (mapping != null) {
                for (Map.Entry<String, String> entry : mapping.entrySet()) {
                    if (entry.getKey().startsWith("tag")) {
                        String value = entry.getValue();
                        updateTag(value, null);
                    }
                }
            }
        }
    }

    /**
     * read from Manifest
     */
    private static void parserManifest(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info != null && info.metaData != null) {
                String tag = info.metaData.getString("Builtin_Tag");
                if (tag != null && tag.startsWith("tag")) {
                    String value = tag.substring(3);
                    updateTag(value, null);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static void saveAF(String adSet, String adGroup, Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ad_set", adSet);
            jsonObject.put("ad_group", adGroup);
            context.getSharedPreferences("Referrer", Context.MODE_PRIVATE).edit().putString("af_info", jsonObject.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateTag(adSet, adGroup);
    }

    /**
     * read from AppsFlyer
     */
    private static void parserAF(Context context) {
        String jsonString = context.getSharedPreferences("Referrer", Context.MODE_PRIVATE).getString("af_info", "");
        try {
            JSONObject jo = new JSONObject(jsonString);
            String adSet = jo.optString("ad_set");
            String adGroup = jo.optString("ad_group");
            updateTag(adSet, adGroup);
        } catch (Exception e) {
        }
    }

    /**
     * Save the tag info from Firebase Dynamic Link
     */
    public static void saveDynamicLinkTag(String tag, Context context) {
        context.getSharedPreferences("DynamicLink", Context.MODE_PRIVATE).edit().putString("tag", tag).apply();
    }

    /**
     * read from Firebase Dynamic Link
     */
    private static void parserDynamicLinkTag(Context context) {
        String tag = context.getSharedPreferences("DynamicLink", Context.MODE_PRIVATE).getString("tag", "");
        updateTag(tag, null);
    }

    public static void updateTag(String tag, String adGroup) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        lock.lock();
        try {
            tags.clear();
            tags.add(tag);
        } finally {
            lock.unlock();
        }
        if (!TextUtils.isEmpty(adGroup)) {
            tagGroup = adGroup;
        }

    }

    public static String getTagString(Context context) {
        ArrayList<String> tags = getTags(context);
        StringBuilder buffer = new StringBuilder();
        for (String tag : tags) {
            buffer.append(tag);
            buffer.append(" ");
        }
        return buffer.toString();
    }
}
