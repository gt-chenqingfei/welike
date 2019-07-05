package com.redefine.welike.commonui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.h5.MissionDelegate;
import com.redefine.commonui.share.ShareDelegate;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.foundation.framework.Event;
import com.redefine.shortcutbadger.ShortcutBadger;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.LogEvent;
import com.redefine.welike.base.upgraded.UpdateHelper;
import com.redefine.welike.business.browse.ui.activity.AvoidTokenDiscoverActivity;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.feeds.management.RecommendFollowManager;
import com.redefine.welike.business.feeds.ui.page.MainPage;
import com.redefine.welike.business.im.CountManager;
import com.redefine.welike.business.publisher.management.BrowsePublishManager;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.user.ui.activity.RecommendFollowActivity;
import com.redefine.welike.business.user.ui.dialog.UpdateDialog;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.common.WindowUtil;
import com.redefine.welike.commonui.framework.PageStackManager;
import com.redefine.welike.commonui.util.ShortCutHelper;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.event.BrowseSchemeDispatcher;
import com.redefine.welike.event.IBrowseSchemeDispatcher;
import com.redefine.welike.event.IEventDispatcher;
import com.redefine.welike.event.ILogDispatcher;
import com.redefine.welike.event.IMessageDispatcher;
import com.redefine.welike.event.IRouteDispatcher;
import com.redefine.welike.event.LogEventDispatcher;
import com.redefine.welike.event.PageEventDispatcher;
import com.redefine.welike.event.PageMessageDispatcher;
import com.redefine.welike.event.RouteDispatcher;
import com.redefine.welike.push.PushIntentWrapper;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.NewShareEventManager;
import com.redefine.welike.statistical.manager.NewStartEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liwenbo on 2018/2/1.
 */

@Route(path = RouteConstant.MAIN_ROUTE_PATH)
public class MainActivityEx extends BaseActivity implements StartManager.StartListener, SpManager.SPDataListener
        , ShareDelegate.ShareCallback, MissionDelegate.OnStartMissionListener
        , EasyPermissions.PermissionCallbacks {
    private static final String MAIN_TAG = "main_activity";
    private static final String APP_EXIT_ACTION = "action.welike.exit";
    private AppExitReceiver exitReceiver = new AppExitReceiver();

    private IPageStackManager mPageStackManager;
    private FrameLayout mRootView;
    private IEventDispatcher mPageEventDispatcher;
    private IMessageDispatcher mMessageDispatcher;
    private IRouteDispatcher mRouteDispatcher;
    private IBrowseSchemeDispatcher mBrowseSchemeDispatcher;
    private ILogDispatcher mLogEventDuspatcher;
    private int softInputMode = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageStackManager = new PageStackManager(this);
        mRootView = new FrameLayout(this);
//        mRootView.setClipToPadding(false);
        mRootView.setFitsSystemWindows(true);
        mRootView.setBackgroundColor(Color.WHITE);
        ActivityCompat.setPermissionCompatDelegate(mPageStackManager.getPermissionCompatDelegate());
        mPageStackManager.bindPageStack(mRootView);
        mRouteDispatcher = new RouteDispatcher(this);
        mBrowseSchemeDispatcher = new BrowseSchemeDispatcher(mPageStackManager);
        mPageEventDispatcher = new PageEventDispatcher(mPageStackManager);
        mLogEventDuspatcher = new LogEventDispatcher();
        mMessageDispatcher = new PageMessageDispatcher(mPageStackManager);
        setContentView(mRootView);
        if (savedInstanceState == null) {
            mPageStackManager.pushPage(new PageConfig.Builder(MainPage.class).setPageBundle(getIntent().getExtras())
                    .setAlwaysRetain(true)
                    .setFitSystemWindow(false)
                    .setPushWithAnimation(false)
                    .setPopWithAnimation(false).build());
        }
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(APP_EXIT_ACTION);
        registerReceiver(exitReceiver, filter);

        StartManager.getInstance().register(this);

        SpManager.getInstance().register(this);

        UpdateHelper.getInstance().checkUpgraded();

        CountManager.INSTANCE.reload();

        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {


                if (mRouteDispatcher.enableTo(getIntent())) {
                    mRouteDispatcher.handleRouteMessage(getIntent());
                } else if (!TextUtils.isEmpty(BrowseSchemeManager.getInstance().getSchemeParam())) {
                    mBrowseSchemeDispatcher.handleRouteMessage(BrowseSchemeManager.getInstance().getSchemeParam());
                } else {
                    Account account = AccountManager.getInstance().getAccount();
                    if (account != null && account.isFirst()) {
                        RecommendFollowActivity.launch();
                        account.setFirst(false);
                    }
                }
                BrowseSchemeManager.getInstance().clear();

            }
        }, 500, TimeUnit.MILLISECONDS);

        EventLog.LaunchApp.report1(1);
        NewStartEventManager.INSTANCE.report19();
        NewStartEventManager.INSTANCE.reset();
        EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN, null));
        ShareDelegate.INSTANCE.registerShareCallback(this);
        MissionDelegate.INSTANCE.registerMissionListener(this);
        MissionManager.INSTANCE.sign();
        RecommendFollowManager.INSTANCE.updateFollowCountBase();

        BrowsePublishManager.getInstance().doSendComment();
        long lastShowSelfStartTime = SpManager.Setting.getNotificationSelfStartTime(this);
        boolean isTimeAllowShowSelfStart = System.currentTimeMillis() - lastShowSelfStartTime > 60 * 60 * 1000;

        if (!SpManager.Setting.getNotificationSelfStart(this) && SpManager.Setting.getNotificationSelfStartCount(this) < 2 && isTimeAllowShowSelfStart) {
            PushIntentWrapper.whiteListMatters(this, "");

        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mRouteDispatcher.handleRouteMessage(intent);
    }

    @Override
    protected void onPause() {
        mPageStackManager.onActivityPause();
        super.onPause();
        softInputMode = WindowUtil.INSTANCE.pauseSoftInputMode(getWindow());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPageStackManager.onActivitySaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPageStackManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mPageStackManager.saveStartForResultPage();
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPageStackManager.onActivityRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPageStackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageStackManager.onActivityResume();
        WindowUtil.INSTANCE.resumeSoftInputMode(getWindow(), softInputMode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPageStackManager.onActivityStart();
        ShortcutBadger.removeCount(MainActivityEx.this);
        SpManager.NotificationSp.resetNotificationCount(MainActivityEx.this);

        /* create shortcut*/
        boolean hasCreate = SpManager.Setting.getShrotcutHasCreate(this);
        if (!hasCreate) {
            ShortCutHelper.createShortCut(this);
            SpManager.Setting.setShortcutHasCreate(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPageStackManager.onActivityStop();
    }

    @Override
    public void onBackPressed() {
        if (!mPageStackManager.onBackPressed()) {
            if (mExitTimer == null) {
                mExitTimer = new ExitCountDownTimer(2000, 500);
            }
            mExitTimer.start();

            mBackPressedCount++;
            if (mBackPressedCount < 2) {
                ToastUtils.showShort(ResourceTool.getString("click_back_again"));
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mPageStackManager.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id) {
            finish();
            return;
        }/* else if (EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_MINE == event.id) {
            recreate();
        }*/
        mPageEventDispatcher.handleEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Message message) {
        mMessageDispatcher.handleMessage(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogEvent(LogEvent logEvent) {
        mLogEventDuspatcher.handleLogMessage(logEvent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPageStackManager.onActivityDestroy();
        unregisterReceiver(exitReceiver);
        ActivityCompat.setPermissionCompatDelegate(null);
        SpManager.getInstance().unregister(this);
        StartManager.getInstance().unregister(this);
        ShareDelegate.INSTANCE.unregisterShareCallback(this);
        MissionDelegate.INSTANCE.unregisterMissionListener(this);
        if (mExitTimer != null) {
            mExitTimer.cancel();
        }
    }

    @Override
    public void goProcess(int state) {
        if (state == StartConstant.START_STATE_THIRD_LOGIN) {
            logout();
        }
    }

    public static void show(Context context) {

        Intent intent = new Intent();
        intent.setClass(context, MainActivityEx.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void exitApp(Context context) {
        show(context);
        Intent intent = new Intent();
        intent.setAction(APP_EXIT_ACTION);
        context.sendBroadcast(intent);
    }


    private byte mBackPressedCount = 0;
    private ExitCountDownTimer mExitTimer;

    @Override
    public void onShareToChannel(SharePackageFactory.SharePackage channel, int from) {
        if (from == ShareModel.SHARE_MODEL_TYPE_PROFILE) {
            MissionManager.INSTANCE.notifyEvent(MissionType.SHARE_PROFILE);
        } else if (from == ShareModel.SHARE_MODEL_TYPE_POST) {
            MissionManager.INSTANCE.notifyEvent(MissionType.SHARE_POST);
        }
        ShareEventManager.INSTANCE.setChannel(channel.ordinal() + 1);
        ShareEventManager.INSTANCE.setSource(from);
        ShareEventManager.INSTANCE.report2();

        NewShareEventManager.INSTANCE.setShareChannel(channel.ordinal() + 1);
        NewShareEventManager.INSTANCE.setContentType(from);

    }

    @Override
    public void onSharePageShow(int from) {
        ShareEventManager.INSTANCE.setSource(from);
        ShareEventManager.INSTANCE.report1();
    }

    @Override
    public void startMission(int type) {
        MissionManager.INSTANCE.startMission(type, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        ((PageStackManager) mPageStackManager).onPermissionsGranted(requestCode, perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ((PageStackManager) mPageStackManager).onPermissionsDenied(requestCode, perms);

    }

    private class ExitCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public ExitCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mBackPressedCount = 0;
        }
    }

    private class AppExitReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }

    }

    @Override
    public void onSPDataChanged(String spTypeName, String spKeyName) {
        if ((TextUtils.equals(spTypeName, SpManager.sharePreferencesSettingName)) &&
                (TextUtils.equals(spKeyName, SpManager.settingUpgradedForceKeyName) || TextUtils.equals(spKeyName, SpManager.settingUpgradedKeyName))) {
            boolean isUpgrade = SpManager.Setting.getCurrentUpgraded(MyApplication.getAppContext());
            boolean isForceUpgrade = SpManager.Setting.getCurrentForceUpgraded(MyApplication.getAppContext());
            String currentDate = getCurrentDate();
            if (isForceUpgrade) {

                UpdateDialog updateDialog = new UpdateDialog(this, true);
                updateDialog.show();
            } else if (isUpgrade && !TextUtils.equals(currentDate, SpManager.Setting.getUpgradedShowTime(this))) {
                SpManager.Setting.setUpgradedShowTime(getCurrentDate(), this);
                UpdateDialog updateDialog = new UpdateDialog(this, false);
                updateDialog.show();

            }

        }
    }

    private String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        return time;
    }
//    @Override
//    public void onIMTokenInvalid() {
//        logout();
//    }

    private void logout() {
        AvoidTokenDiscoverActivity.Companion.launch(this);
        EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT));
    }

//    @Override
//    public void onIMServiceFactoryDeathException() {
//        Account account = AccountManager.getInstance().getAccount();
//        if (account != null) {
//            LogUtil.d("welike-liubin", "mainActivity onIMServiceFactoryDeathException");
//            IMManager.getInstance().bind(this, this, account.getAccessToken(), account.getUid());
//            IMManager.getInstance().setListener(this);
//        }
//    }
}
