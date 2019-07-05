package com.redefine.welike.base.resource;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.redefine.foundation.language.LocalizationManager;
import com.redefine.foundation.language.ResourceParser;

/**
 * Created by liwenbo on 2018/2/26.
 */

@Deprecated
public class ResourceTool {
    public static enum ResourceFileEnum {
        COMMON("common.xml"), ERROR_CODE("error.xml"), FEED("feed.xml"), PIC_SELECTOR("pic_sel.xml"), USER("user.xml"), IM("im.xml"), PUBLISH("publish.xml"), SEARCH("search.xml"), REGISTER("register.xml"), LOCATION("location.xml"), TOPIC("topic.xml");
        private String fileName;

        ResourceFileEnum(String s) {
            fileName = s;
        }
    }

    public static String getString(Context context, String key) {

        try {

            int text_id = context.getResources()
                    .getIdentifier(key, "string", context.getPackageName());
            if (text_id == 0x00000000) return "";
            return context.getResources().getString(text_id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getString(String key) {

        return getString(LocalizationManager.getAppContext(), key);
    }

    public static String getString(ResourceFileEnum file, String key) {

//        return ResourceParser.getString(file.fileName, key);
        return getString(LocalizationManager.getAppContext(), key);
    }

    public static String getString(ResourceFileEnum file, String key, boolean isCache) {
//        return ResourceParser.getString(file.fileName, key, isCache);
        return getString(LocalizationManager.getAppContext(), key);
    }


    public static Drawable getBoundDrawable(Resources resources, int id) {
        Drawable drawable = resources.getDrawable(id);
        if (drawable == null) {
            return null;
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    public static Drawable getBoundDrawable(Resources resources, int id, int width, int height) {
        Drawable drawable = resources.getDrawable(id);
        if (drawable == null) {
            return null;
        }
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }

}
