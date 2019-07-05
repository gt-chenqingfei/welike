package com.redefine.welike.business.startup.management;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.request.DeleteToken;
import com.redefine.welike.business.easypost.management.EasyPostManager;
import com.redefine.welike.business.feeds.management.cache.SearchHistoryCache;
import com.redefine.welike.business.init.InitApp;
import com.redefine.welike.business.publisher.management.DraftManager;
import com.redefine.welike.business.publisher.management.cache.TopicSearchHistoryCache;
import com.redefine.welike.business.startup.management.bean.UserinfoBean;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.user.management.ContactsManager;
import com.redefine.welike.common.Life;
import com.redefine.welike.event.RouteDispatcher;
import com.redefine.welike.push.PushService;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.UnLoginEventManager;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static io.reactivex.internal.util.NotificationLite.next;


/**
 * Created by liubin on 2018/1/5.
 */

public class StartManager extends BroadcastManagerBase {


    private int state;
    private Uri uri;

    public interface StartListener {

        void goProcess(int state);

    }

    private static class StartHandlerHolder {
        public static StartManager instance = new StartManager();
    }

    public static StartManager getInstance() {
        return StartHandlerHolder.instance;
    }

    public void register(StartListener listener) {
        super.register(listener);
    }

    public void unregister(StartListener listener) {
        super.unregister(listener);
    }

    public void init() {
        InitApp.onCreateStartManager(MyApplication.getApp());
    }

    public void accountTasksInitialized(Account account, boolean isNeedSyncAccountSetting) {
        // 登录后初始化都在这里
//        IMMultiAccountDBStore.getInstance().init(account.getUid(), MyApplication.getAppContext());
        ContactsManager.getInstance().refreshAll();
        DraftManager.getInstance().init();
//        new PushService().checkToken(MyApplication.getAppContext());
//        SpManager.GooglePlayScoreSp.resetCount(MyApplication.getAppContext());
        if (isNeedSyncAccountSetting) {
//            SyncProfileSettingsHelper.modifyAll(getAllSyncProfileData(), MyApplication.getAppContext());
        }
        EasyPostManager.INSTANCE.init(MyApplication.getAppContext());
    }


    public void start(Uri uri) {
        this.uri = uri;
        if (RouteDispatcher.validUri(uri)) {
            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
        }
    }

    public void logout() {
        if (AccountManager.getInstance().isLogin()) {
            Life.logout();
            reportLogout();
            AccountManager.getInstance().logout();
            BrowseEventStore.INSTANCE.clearClickCount();
            next(StartConstant.START_STATE_THIRD_LOGIN);
//            SpManager.GooglePlayScoreSp.resetCount(MyApplication.getAppContext());
//            next(START_STATE_THIRD_LOGIN);
            StartEventManager.getInstance().setPage_source(2);
            HalfLoginManager.getInstancce().checkIsExistAccount();
        }
    }


    public void logout4new() {
        if (AccountManager.getInstance().isLogin()) {
            Life.logout();
            reportLogout();
            AccountManager.getInstance().logout();
            StartEventManager.getInstance().setPage_source(2);
//            UnLoginEventManager.INSTANCE.setPageSource(EventConstants.UNLOGIN_PAGE_SOURCE_LOGOUT);

        }
    }

    private void reportLogout() {
        try {
            new DeleteToken(MyApplication.getApp()).req(null);
        } catch (Exception e) {

        }
    }

    public void restore() {
        if (AccountManager.getInstance().isLogin()) {
            Life.logout();
            reportLogout();
            AccountManager.getInstance().logout();
            StartEventManager.getInstance().setPage_source(2);
        }
    }

    public void restoreReady(JSONObject result) {

        Gson gson = new Gson();
        UserinfoBean resultBean = gson.fromJson(result.toString(), UserinfoBean.class);
        Account oldAccount = AccountManager.getInstance().getAccount();
        if (!TextUtils.isEmpty(resultBean.getId())) {
            oldAccount.setUid(resultBean.getId());
            ContactsManager.getInstance().clear();
//            DraftManager.getInstance().clear();
            SearchHistoryCache.getInstance().cleanAll();
            TopicSearchHistoryCache.getInstance().cleanAll();
            SpManager.getInstance().clear(MyApplication.getAppContext());
            oldAccount.setCompleteLevel(resultBean.getFinishLevel());
            oldAccount.setNickName("");
            oldAccount.setFirst(false);
            oldAccount.setHeadUrl("");
            oldAccount.setAllowUpdateNickName(true);
            oldAccount.setAllowUpdateSex(true);
            oldAccount.setSexUpdateCount(0);
            oldAccount.setPostsCount(0);
            oldAccount.setFollowUsersCount(0);
            oldAccount.setFollowedUsersCount(0);
            oldAccount.setLikedMyPostsCount(0);
            oldAccount.setMyLikedPostsCount(0);
            oldAccount.setLogin(true);
            oldAccount.setStatus(0);
            oldAccount.setVip(0);
            AccountManager.getInstance().updateAccount(oldAccount);
            accountTasksInitialized(oldAccount, false);
//            MessageBoxCountObserver.getInstance().onMessageNotificationCountChanged();
//            TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder().setCategory(TrackerConstant.EVENT_EV_CT).setAction(TrackerConstant.EVENT_PIN_SUCCESS).build());
        }
        Life.login();
    }


    public void next(int state) {
        switch (state) {
            case StartConstant.START_STATE_LOGIN_MOBILE: {
                this.state = StartConstant.START_STATE_LOGIN_MOBILE;
                broadcast();
                break;
            }
            case StartConstant.START_STATE_THIRD_LOGIN: {
                this.state = StartConstant.START_STATE_THIRD_LOGIN;
                broadcast();
                break;
            }
            default:
                break;
        }
    }

    private void broadcast() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof StartListener) {
                            StartListener listener = (StartListener) l;
                            listener.goProcess(state);
                        }
                    }
                }
            }

        });
    }
}
