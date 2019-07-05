package com.redefine.commonui.share;

import com.redefine.commonui.R;
import com.redefine.commonui.share.facebook.FacebookPackageManager;
import com.redefine.commonui.share.instagram.InstagramPackageManager;
import com.redefine.commonui.share.shareapk.ShareApkPackageManager;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.commonui.share.system.SystemCopyManager;
import com.redefine.commonui.share.system.SystemPackageManager;
import com.redefine.commonui.share.whatsapp.WhatsAppPackageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gongguan on 2018/3/20.
 */

public class SharePackageFactory {
    public static final int MASTER = 0x11111111;

//    public static final int PACKAGE_INVALID = 0;
//    public static final int PACKAGE_SYSTEM = 1;
//    public static final int PACKAGE_FACEBOOK = 1 << 1;
//    public static final int PACKAGE_INSTAGTRAM = 1 << 2;
//    public static final int PACKAGE_WHATS_APP = 1 << 3;
//    public static final int PACKAGE_COPY = 1 << 4;

    public enum SharePackage {
        FACEBOOK, SYSYTEM, INSTAGTRAM, WHATS_APP, COPY, CUSTOM_COPY, SHARE_APK, SHARE_APK_LONG, EMPTY, MENU1, MENU2, MENU3, MENU4, MENU5, MENU6
    }

    public static SharePackageModel create(SharePackage sharePackage) {
        switch (sharePackage) {
            case EMPTY:
                return new SharePackageModel("",
                        R.color.common_color_f8f8f8,
                        SharePackage.EMPTY,
                        new EmptyMenuShareManager(SharePackage.EMPTY));
            case INSTAGTRAM:
                return new SharePackageModel(getName(SharePackage.INSTAGTRAM),
                        R.drawable.instagram_share_icon,
                        SharePackage.INSTAGTRAM,
                        getSharePackage(sharePackage));
            case FACEBOOK:
                return new SharePackageModel(getName(SharePackage.FACEBOOK),
                        R.drawable.facebook_share_icon,
                        SharePackage.FACEBOOK,
                        getSharePackage(sharePackage));
            case COPY:
                return new SharePackageModel(getName(SharePackage.COPY),
                        R.drawable.copy_share_icon,
                        SharePackage.COPY,
                        getSharePackage(sharePackage));
            case SYSYTEM:
                return new SharePackageModel(getName(SharePackage.SYSYTEM),
                        R.drawable.more_share_icon,
                        SharePackage.SYSYTEM,
                        getSharePackage(SharePackage.SYSYTEM));
            case WHATS_APP:
                return new SharePackageModel(getName(SharePackage.WHATS_APP),
                        R.drawable.whats_app_icon,
                        SharePackage.WHATS_APP,
                        getSharePackage(sharePackage));
            case SHARE_APK:
                return new SharePackageModel(getName(SharePackage.SHARE_APK),
                        R.drawable.apk_share_icon,
                        SharePackage.SHARE_APK,
                        getSharePackage(sharePackage));
            default:
                return new SharePackageModel("",
                        R.color.common_color_f8f8f8,
                        SharePackage.EMPTY,
                        new EmptyMenuShareManager(SharePackage.EMPTY));
        }
    }

    public static String getName(SharePackage sharePackage) {
        String name = "More";
        switch (sharePackage) {
            case FACEBOOK:
                name = "Facebook";
                break;
            case SYSYTEM:
                name = "More";
                break;
            case INSTAGTRAM:
                name = "Instagram";
                break;
            case WHATS_APP:
                name = "WhatsApp";
                break;
            case COPY:
                name = "Copy";
                break;
            case SHARE_APK:
                name = "App Package";
                break;
        }
        return name;
    }

    public static String getName2(SharePackage sharePackage) {
        String name = "more";
        switch (sharePackage) {
            case FACEBOOK:
                name = "fb";
                break;
            case SYSYTEM:
                name = "more";
                break;
            case INSTAGTRAM:
                name = "ins";
                break;
            case WHATS_APP:
                name = "wapp";
                break;
            case COPY:
                name = "copy";
                break;
            case SHARE_APK:
                name = "apk";
                break;
        }
        return name;
    }


    public static Map<SharePackage, ISharePackageManager> getSharePackages() {
        Map<SharePackage, ISharePackageManager> shareMap = new HashMap<>();

        for (SharePackage sharePackage : SharePackage.values()) {
            ISharePackageManager manager = getSharePackage(sharePackage);
            if (manager != null) {
                shareMap.put(sharePackage, manager);
            }
        }

        return shareMap;
    }

    static ISharePackageManager getSharePackage(SharePackage sharePackage) {
        ISharePackageManager manager = null;
        switch (sharePackage) {
            case SYSYTEM:
                manager = new SystemPackageManager(sharePackage);
                break;
            case FACEBOOK:
                manager = new FacebookPackageManager(sharePackage);
                break;
            case INSTAGTRAM:
                manager = new InstagramPackageManager(sharePackage);
                break;
            case WHATS_APP:
                manager = new WhatsAppPackageManager(sharePackage);
                break;
            case COPY:
                manager = new SystemCopyManager(sharePackage);
                break;
            case EMPTY:
                manager = new EmptyMenuShareManager(sharePackage);
                break;
            case SHARE_APK:
                manager = new ShareApkPackageManager(sharePackage);
                break;
        }
        return manager;
    }

}
