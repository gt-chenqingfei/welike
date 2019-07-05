package com.redefine.welike.business.init;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;
import com.redefine.commonui.fresco.init.FrescoInitHelper;
import com.redefine.commonui.h5.PublicParamProvider;
import com.redefine.commonui.share.ShareManager;
import com.redefine.foundation.http.HttpManager;
import com.redefine.foundation.io.FileManager;
import com.redefine.foundation.utils.ChannelHelper;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.im.management.UniqueFilter;
import com.redefine.oss.adapter.AliyunOssUploadConfig;
import com.redefine.oss.adapter.GatewayUploadConfig;
import com.redefine.oss.adapter.UploadMgr;
import com.redefine.richtext.emoji.EmojiManager;
import com.redefine.welike.BuildConfig;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.DBStore;
import com.redefine.welike.base.StorageDBStore;
import com.redefine.welike.base.URLCenter;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.upgraded.UpdateHelper;
import com.redefine.welike.base.uploading.UploadingManager;
import com.redefine.welike.base.util.LifecycleHandler;
import com.redefine.welike.base.util.LocationExt;
import com.redefine.welike.business.common.GuideManager;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.easypost.management.EasyPostManager;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.publisher.management.DraftManager;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.common.FacebookManager;
import com.redefine.welike.common.ScoreManager;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;
import com.redefine.welike.common.vip.VipManager;
import com.redefine.welike.commonui.event.model.PublishEventModel;
import com.redefine.welike.keepalive.JobAwakeService;
import com.redefine.welike.keepalive.LocalService;
import com.redefine.welike.keepalive.WorkManagerInstance;
import com.redefine.welike.keepalive.account.SyncService;
import com.redefine.welike.net.HttpHeaderInterceptor;
import com.redefine.welike.net.HttpUploadRequestInterceptor;
import com.redefine.welike.net.OkHttpFactory;
import com.redefine.welike.net.RetrofitManager;
import com.redefine.welike.push.PushService;
import com.redefine.welike.statistical.EventManager;
import com.redefine.welike.statistical.lifecyclehelper.ActivityLifecycleHelper;
import com.redefine.welike.statistical.lifecyclehelper.AppLifecycleDetector;
import com.redefine.welike.statistical.utils.GoogleUtil;

import io.fabric.sdk.android.Fabric;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;

import static com.facebook.FacebookSdk.getApplicationContext;


public class InitApp {

    public static void onCreateApplication(final Application app) {
        Fabric.with(app, new Crashlytics());

        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        Log.d("lizard", Build.BRAND);

        if(Build.VERSION.SDK_INT <Build.VERSION_CODES.O && !Build.BRAND.equals("samsung")){
            if(Build.VERSION.SDK_INT <Build.VERSION_CODES.LOLLIPOP){
                app.startService(new Intent(app, LocalService.class));

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&&Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                app.startService(new Intent(app, JobAwakeService.class));
            }
        }


        if ((Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) && Build.BRAND.equals("vivo")) {
            WorkManagerInstance.getInstance().startVivo7Alive();
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            SyncService.startAccountSync(app);
        }


        ARouter.init(app);
        URLCenter.init(app);
        FileManager.init(app);
        FrescoInitHelper.init(app, WeLikeFileManager.getFrescoCacheDir());
        StartManager.getInstance().init();
        FirebaseApp.initializeApp(app);
//        TrackerUtil.init(app, BuildConfig.DEBUG);
        MissionManager.INSTANCE.init(app);
        PublicParamProvider.INSTANCE.init(app);
        ABTest.INSTANCE.init();
        EventManager.getInstance().init(app);
        FacebookManager.getInstance().init();
        //Setting Stetho.
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(app);
        }
        OkHttpFactory.Companion.setDebug(BuildConfig.DEBUG);
        RetrofitManager.Companion.init(URLCenter.getHost(), OkHttpFactory.Companion.getInstance().getOkHttpClient());

        HttpManager.getInstance().setDebug(BuildConfig.DEBUG);
        ShareManager.getInstance().init(app);
        GuideManager.INSTANCE.init(app);
//        MissionManager.INSTANCE.init(app);
        app.registerActivityLifecycleCallbacks(new LifecycleHandler());
        app.registerActivityLifecycleCallbacks(new ActivityLifecycleHelper());
        app.registerActivityLifecycleCallbacks(new AppLifecycleDetector());
        UniqueFilter.INSTANCE.init(app);
        IMHelper.INSTANCE.init();
        AFGAEventManager.getInstance().init(app);


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    EmojiManager.getInstance().init(app);
                } catch (Exception e) {

                }
            }
        }.start();


        new Thread() {
            @Override
            public void run() {
                try {
                    GoogleUtil.INSTANCE.getGAID(app, new Function1<String, Unit>() {
                        @Override
                        public Unit invoke(String s) {
                            CommonHelper.GAID = s;
                            return null;
                        }
                    });
                } catch (Exception e) {

                }
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    ChannelHelper.getTags(app);
                } catch (Exception e) {

                }
            }
        }.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationExt.INSTANCE.initOrRefreshLocation(app);
            }
        }).start();

        new Thread() {
            @Override
            public void run() {
                super.run();
                DraftManager.getInstance().init();
            }
        }.start();

        app.registerActivityLifecycleCallbacks(new LifecycleHandler() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                app.unregisterActivityLifecycleCallbacks(this);
                onFirstPage();
            }
        });
        // UMengManager.INSTANCE.initUMeng(app, CommonHelper.getAppVersionName(app), "um");
    }

    /**
     * 当第一个页面调用。
     */
    private static void onFirstPage() {
        VipManager.INSTANCE.init();
        PushService.INSTANCE.init();
        EasyPostManager.INSTANCE.init(MyApplication.getAppContext());
        new Thread() {
            @Override
            public void run() {
                try {
                    GoogleUtil.INSTANCE.getGAID(MyApplication.getAppContext(), new Function1<String, Unit>() {
                        @Override
                        public Unit invoke(String s) {
                            ABTest.INSTANCE.update();
                            HalfLoginManager.getInstancce().checkIsExistAccount();
                            ScoreManager.getToastStatus(MyApplication.getAppContext());
                            return null;
                        }
                    });
                } catch (Exception e) {

                }
            }
        }.start();
    }

    public static void onCreateStartManager(final Application app) {
        DBStore.getInstance().init("d28c1f06eda44c7a85b09d53e6b1c385", app);

//        //兼容2.7.2版本 需要手动建表
//        DraftDao.createTable(DBStore.getInstance().getDaoSession().getDatabase(),true);

        StorageDBStore.getInstance().init("d28fff0d3dad4fda9ad3d6c2a17e18f7", app);
        // im数据库改为多账户，老数据库直接干掉


        new Thread() {
            @Override
            public void run() {
                app.deleteDatabase("a2323212255d48ca8cfb62534b0014b8");
                WeLikeFileManager.clearTempCacheDir();
            }
        }.start();


        AccountManager.getInstance().getLiveAccount().observeForever(new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account account) {
                if (account == null || !account.isLogin()) {
                    UploadMgr.INSTANCE.stopAll();
                } else {
                    if (ABTest.INSTANCE.check(ABKeys.TEST_UPLOAD) == 0) {
                        UploadMgr.INSTANCE.setEnv(new AliyunOssUploadConfig(app, URLCenter.getAliyunOssSts()
                                , URLCenter.getHostAliyunEndPoint(), URLCenter.getHostAliyunBucket()
                                , URLCenter.getHostAliyunDownloadHost()));
                        PublishEventModel.uploadType = "1";
                    } else {
                        OkHttpClient okHttpClient = HttpManager.getInstance().getHttpUploadClient(new HttpUploadRequestInterceptor(), new HttpHeaderInterceptor());
                        UploadMgr.INSTANCE.setEnv(new GatewayUploadConfig(URLCenter.getHost(), account.getAccessToken(), okHttpClient));
                        PublishEventModel.uploadType = "2";
                    }
                }
            }
        });
        AccountManager.getInstance().init(app);
        UpdateHelper.getInstance().init(app);
        UploadingManager.getInstance().init(app);
    }
}
