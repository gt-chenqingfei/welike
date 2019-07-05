package com.redefine.welike.business.user.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.user.management.request.FollowUserRequest;
import com.redefine.welike.business.user.management.request.UnfollowUserRequest;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.event.reporter.FollowReporter;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/21.
 */

public class FollowUserManager extends BroadcastManagerBase implements RequestCallback {
    private static final String USER_INFO_KEY_EVENT_MODEL = "follow_event_model";
    private final List<FollowUserRequest> followRequests = new ArrayList<>();
    private final List<UnfollowUserRequest> unfollowRequests = new ArrayList<>();

    public interface FollowUserCallback {

        void onFollowCompleted(String uid, int errCode);

        void onUnfollowCompleted(String uid, int errCode);

    }

    private static class FollowUserManagerHolder {
        public static FollowUserManager instance = new FollowUserManager();
    }

    private FollowUserManager() {
    }

    public static FollowUserManager getInstance() {
        return FollowUserManagerHolder.instance;
    }

    public void register(FollowUserCallback listener) {
        super.register(listener);
    }

    public void unregister(FollowUserCallback listener) {
        super.unregister(listener);
    }

    public void follow(String uid, FollowEventModel eventModel) {
        if (followActionInPool(uid)) return;
        AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_FOLLOW);
        FollowUserRequest request = new FollowUserRequest(uid, MyApplication.getAppContext());
        request.putUserInfo(USER_INFO_KEY_EVENT_MODEL, eventModel);
        try {
            request.req(this);
            synchronized (followRequests) {
                followRequests.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            broadcastFollowCompleted(uid, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public void unfollow(String uid, FollowEventModel eventModel) {
        if (unfollowActionInPool(uid)) return;

        UnfollowUserRequest request = new UnfollowUserRequest(uid, MyApplication.getAppContext());
        request.putUserInfo(USER_INFO_KEY_EVENT_MODEL, eventModel);
        try {
            request.req(this);
            synchronized (unfollowRequests) {
                unfollowRequests.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            broadcastUnfollowCompleted(uid, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public boolean isFollowRequesting(String uid) {
        return followActionInPool(uid);
    }

    public boolean isUnfollowRequesting(String uid) {
        return unfollowActionInPool(uid);
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request instanceof FollowUserRequest) {
            FollowUserRequest followUserRequest = (FollowUserRequest) request;
            synchronized (followRequests) {
                followRequests.remove(followUserRequest);
            }
            String uid = getFollowUidFromRequest(followUserRequest);
            broadcastFollowCompleted(uid, errCode);
        } else if (request instanceof UnfollowUserRequest) {
            UnfollowUserRequest unfollowUserRequest = (UnfollowUserRequest) request;
            synchronized (unfollowRequests) {
                unfollowRequests.remove(unfollowUserRequest);
            }
            String uid = getUnfollowUidFromRequest(unfollowUserRequest);
            broadcastUnfollowCompleted(uid, errCode);
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request instanceof FollowUserRequest) {
            FollowUserRequest followUserRequest = (FollowUserRequest) request;
            synchronized (followRequests) {
                followRequests.remove(followUserRequest);
            }
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                int count = account.getFollowUsersCount() + 1;
                account.setFollowUsersCount(count);
                AccountManager.getInstance().updateAccount(account);
            }
            String uid = getFollowUidFromRequest(followUserRequest);
            broadcastFollowCompleted(uid, ErrorCode.ERROR_SUCCESS);
            FollowEventModel userInfo = (FollowEventModel) followUserRequest.getUserInfo(USER_INFO_KEY_EVENT_MODEL);
            if (userInfo != null) {
                FollowReporter.Companion.reportFollow(userInfo, uid);
            }

            HalfLoginManager.updateClickCount(new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));

        } else if (request instanceof UnfollowUserRequest) {
            UnfollowUserRequest unfollowUserRequest = (UnfollowUserRequest) request;
            synchronized (unfollowRequests) {
                unfollowRequests.remove(unfollowUserRequest);
            }
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                int followedCount = account.getFollowUsersCount() - 1;
                if (followedCount < 0) followedCount = 0;
                account.setFollowUsersCount(followedCount);
                AccountManager.getInstance().updateAccount(account);
            }
            String uid = getUnfollowUidFromRequest(unfollowUserRequest);
            broadcastUnfollowCompleted(uid, ErrorCode.ERROR_SUCCESS);
            FollowEventModel userInfo = (FollowEventModel) unfollowUserRequest.getUserInfo(USER_INFO_KEY_EVENT_MODEL);
            if (userInfo != null) {
                FollowReporter.Companion.reportUnFollow(userInfo, uid);
            }
        }
    }

    private void broadcastFollowCompleted(final String uid, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof FollowUserCallback) {
                            FollowUserCallback listener = (FollowUserCallback) l;
                            listener.onFollowCompleted(uid, errCode);
                        }
                    }
                }
            }

        });
    }

    private void broadcastUnfollowCompleted(final String uid, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof FollowUserCallback) {
                            FollowUserCallback listener = (FollowUserCallback) l;
                            listener.onUnfollowCompleted(uid, errCode);
                        }
                    }
                }
            }

        });
    }

    private String getFollowUidFromRequest(FollowUserRequest request) {
        String uid = null;
        Object o = request.getUserInfo(FollowUserRequest.FOLLOW_UID_KEY);
        if (o != null && o instanceof String) {
            uid = (String) o;
        }
        return uid;
    }

    private String getUnfollowUidFromRequest(UnfollowUserRequest request) {
        String uid = null;
        Object o = request.getUserInfo(UnfollowUserRequest.UNFOLLOW_UID_KEY);
        if (o != null && o instanceof String) {
            uid = (String) o;
        }
        return uid;
    }

    private boolean followActionInPool(String uid) {
        synchronized (followRequests) {
            for (int i = 0; i < followRequests.size(); i++) {
                FollowUserRequest request = followRequests.get(i);
                String ud = getFollowUidFromRequest(request);
                if (!TextUtils.isEmpty(ud)) {
                    if (TextUtils.equals(ud, uid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean unfollowActionInPool(String uid) {
        synchronized (unfollowRequests) {
            for (int i = 0; i < unfollowRequests.size(); i++) {
                UnfollowUserRequest request = unfollowRequests.get(i);
                String ud = getUnfollowUidFromRequest(request);
                if (!TextUtils.isEmpty(ud)) {
                    if (TextUtils.equals(ud, uid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
