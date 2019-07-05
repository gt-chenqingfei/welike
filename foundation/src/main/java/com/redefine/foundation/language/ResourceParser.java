package com.redefine.foundation.language;

import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.LruCache;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;

/**
 * Created by liwenbo on 2018/2/7.
 */

public class ResourceParser {
    private static final String LANGUAGE_ROOT_PATH = "language";
    private static final LruCache<String, String> mLocalCache = new LruCache<String, String>(50);


    public static String getString(String fileName, String key) {
        return getString(fileName, key, false);
    }

    public static String getString(String fileName, String key, boolean noCache) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(key)) {
            return "";
        }
        String value;
        if (!noCache) {
            value = mLocalCache.get(key);
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }
        // 查询多语言xml
        value = parseStringFromAsset(fileName, key);
        if (!TextUtils.isEmpty(value) && !noCache) {
            mLocalCache.put(key, value);
        }
        return value;
    }

    private static String parseStringFromAsset(String fileName, String key) {
        // 拼接asset 文件
        String value = parseStringFromCurrentLanguage(fileName, key);
        if (TextUtils.isEmpty(value)) {
            value = parseStringFromEnglishLanguage(fileName, key);
        }
        return value;
    }

    private static String parseStringFromEnglishLanguage(String fileName, String key) {
        String currentPath = LANGUAGE_ROOT_PATH + File.separator
                + LocalizationManager.getInstance().getEnglishLanguage()
                + File.separator
                + fileName;
        return parseStringFromAssetFile(currentPath, key);
    }

    private static String parseStringFromCurrentLanguage(String fileName, String key) {
        String currentPath = LANGUAGE_ROOT_PATH + File.separator
                + LocalizationManager.getInstance().getCurrentLanguage()
                + File.separator
                + fileName;
        return parseStringFromAssetFile(currentPath, key);
    }

    private static String parseStringFromAssetFile(String filePath, String key) {
        AssetManager assetManager = LocalizationManager.getAppContext().getAssets();
        InputStream stream = null;
        try {
            String result = null;
            stream = assetManager.open(filePath);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//先获取XmlPullParserFactory实例
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(stream, "UTF-8");
            int event = parser.getEventType();
            while (XmlPullParser.END_DOCUMENT != event) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (TextUtils.equals(key, parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "name"))) {
                            // 取值返回
                            result = parser.nextText();
                            break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                    default:
                        break;
                }
                event = parser.next();
            }
            return result;
        } catch (Throwable e) {
            close(stream);
            // do nothing
        }
        return null;
    }


    private static void close(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Throwable e) {
                e.printStackTrace();
                // do nothing
            }
        }
    }


    public static void clearCache() {
        mLocalCache.evictAll();
    }
}
