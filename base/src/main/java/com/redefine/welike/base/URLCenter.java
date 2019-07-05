package com.redefine.welike.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用来 管理全部的 URL host.
 * Created by daining on 2018/5/3.
 */

public enum URLCenter {

    INSTANCE;
    //存储当前 type.
    SharedPreferences sp;
    private static String SP_KEY = "TYPE";


    public static final int DEV = 0;
    public static final int TEST = 1;
    public static final int ON_LINE = 2;

    public static String getHostM() {
        return hostM;
    }

    @IntDef({DEV, TEST, ON_LINE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }


    public static final String USER_SERVICE = "http://www.welike.in/privacy/protocol.html";
    /**
     * SORT: host, hostUpload, hostLog, hostIM
     */
    private final static String[] TEST_HOSTS = {"https://pre02-api.welike.in/"
            , "https://pre02-img.welike.in/"
            , "https://pre-gather.welike.in/"
            , "pre02-msg.welike.in"
            , "https://pre02-file.welike.in/"
            , "http://pre-fileservice.welike.in/"
            , "pre02-www.welike.in"
            , "https://pre02-m.welike.in/"
            , "https://aws-gather.welike.in"
            , "http://pre-sts.welike.in/token"
            , "pre-img-welike-in"
            , "http://oss-ap-south-1.aliyuncs.com"
            , "https://pre-img.welike.in/"};
    //        private final static String[] TEST_HOSTS = {"https://pre-api.welike.in/", "https://pre-img.welike.in/", "https://pre-gather.welike.in/", "pre-msg.welike.in", "https://pre-file.welike.in/", "http://pre-fileservice.welike.in/", "pre-www.welike.in"};
//    private final static String[] TEST_HOSTS = {
//            "https://ali-api.welike.in/",
//            "https://ali-img.welike.in/",
//            "https://ali-gather.welike.in/",
//            "ali-msg.welike.in",
//            "https://ali-file.welike.in/",
//            "http://ali-fileservice.welike.in/",
//            "www.welike.in",
//            "https://ali-m.welike.in/"
// };
    private final static String[] DEV_HOSTS = {"https://dev-api.welike.in/"
            , "https://dev-img.welike.in/"
            , "https://dev-gather.welike.in/"
            , "dev-msg.welike.in"
            , "https://dev-file.welike.in/"
            , "http://dev-fileservice.welike.in/"
            , "dev-www.welike.in"
            , "https://dev-m.welike.in/"
            , "https://aws-gather.welike.in"
            , "http://pre-sts.welike.in/token"
            , "pre-img-welike-in"
            , "http://oss-ap-south-1.aliyuncs.com"
            , "https://pre-img.welike.in/"};
    private final static String[] ON_LINE_HOSTS = {"https://api.welike.in/"
            , "https://img.welike.in/"
            , "https://gather.welike.in/"
            , "msg.welike.in"
            , "https://file.welike.in/"
            , "http://fileservice.welike.in/"
            , "welike.in"
            , "https://m.welike.in/"
            , "https://aws-gather.welike.in"/**/
            , "http://sts.welike.in/token"
            , "prod-img-welike-in"
            , "http://oss-ap-south-1.aliyuncs.com"
            , "https://img.welike.in/"};

    //默认使用 online 设置。
    private static String host = ON_LINE_HOSTS[0];
    private static String hostUpload = ON_LINE_HOSTS[1];
    private static String hostLog = ON_LINE_HOSTS[2];
    private static String hostIM = ON_LINE_HOSTS[3];
    private static String hostDownload = ON_LINE_HOSTS[4];
    private static String hostUpload1 = ON_LINE_HOSTS[5];
    private static String hostShare = ON_LINE_HOSTS[6];
    private static String hostM = ON_LINE_HOSTS[7];
    private static String hostAWSLog = ON_LINE_HOSTS[8];
    private static String hostAliyunOssSts = ON_LINE_HOSTS[9];
    private static String hostAliyunOssBucket = ON_LINE_HOSTS[10];
    private static String hostAliyunOssEndPoint = ON_LINE_HOSTS[11];
    private static String hostAliyunOssDownload = ON_LINE_HOSTS[12];

    /**
     * 初始化，只有debug模式，可以切换
     */
    public static void init(Context context) {
        if (BuildConfig.DEBUG) {
            INSTANCE.sp = context.getSharedPreferences("URL_CENTER", Context.MODE_PRIVATE);
            reset(INSTANCE.sp.getInt(SP_KEY, TEST));
        }
    }

    /**
     * 切换URL host，对外开放。只允许切换 TEST OR DEV
     */
    public static void use(@Type int type) {
        if (BuildConfig.DEBUG && INSTANCE.sp != null && type != ON_LINE) {
            INSTANCE.sp.edit().putInt(SP_KEY, type).apply();
            reset(type);
        }
    }

    /**
     * 内部逻辑，实现HOST 切换。
     */
    private static void reset(@Type int type) {
        try {
            String[] hosts = ON_LINE_HOSTS;
            if (type == DEV) {
                hosts = DEV_HOSTS;
            } else if (type == TEST) {
                hosts = TEST_HOSTS;
            }
            host = hosts[0];
            hostUpload = hosts[1];
            hostLog = hosts[2];
            hostIM = hosts[3];
            hostDownload = hosts[4];
            hostUpload1 = hosts[5];
            hostShare = hosts[6];
            hostM = hosts[7];
            hostAWSLog = hosts[8];
            hostAliyunOssSts = hosts[9];
            hostAliyunOssBucket = hosts[10];
            hostAliyunOssEndPoint = hosts[11];
            hostAliyunOssDownload = hosts[12];
        } catch (Exception ignored) {

        }
    }

    @Type
    public static int getCurrentType() {
        if (INSTANCE.sp != null) {
            return INSTANCE.sp.getInt(SP_KEY, TEST);
        }
        return ON_LINE;
    }


    public static String getHostUpload() {
        return hostUpload;
    }

    public static String getHost() {
        return host;
    }

    public static String getHostLog() {
        return hostLog;
    }

    public static String getHostAWSLog() {
        return hostAWSLog;
    }

    public static String getHostIM() {
        return hostIM;
    }

    public static String getDownloadHost() {
        return hostDownload;
    }

    public static String getHostShare() {
        return hostShare;
    }

    public static String getHostUpload1() {
        return hostUpload1;
    }

    public static String getAliyunOssSts() {
        return hostAliyunOssSts;
    }

    public static String getHostAliyunBucket() {
        return hostAliyunOssBucket;
    }

    public static String getHostAliyunEndPoint() {
        return hostAliyunOssEndPoint;
    }

    public static String getHostAliyunDownloadHost() {
        return hostAliyunOssDownload;
    }
}
