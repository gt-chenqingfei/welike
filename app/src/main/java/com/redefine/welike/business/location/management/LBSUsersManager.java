package com.redefine.welike.business.location.management;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.management.provider.LBSNearUsersProvider;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/26.
 */

public class LBSUsersManager extends SingleListenerManagerBase implements LBSNearUsersProvider.LBSNearUsersProviderCallback {
    private final LBSNearUsersProvider provider;

    public interface LBSUsersCallback {

        void onRefreshLBSUsers(List<LBSUser> users, int errCode);

        void onReceiveHisLBSUsers(List<LBSUser> users, boolean last, int errCode);

    }

    public LBSUsersManager(String placeId) {
        provider = new LBSNearUsersProvider(placeId);
        provider.setListener(this);
    }

    public void setListener(LBSUsersCallback listener) {
        super.setListener(listener);
    }

    public void tryRefreshUsers() {
        provider.tryRefreshUsers();
    }

    public void tryHisUsers() {
        provider.tryHisUsers();
    }

    @Override
    public void onRefreshLBSNearUsers(final List<LBSUser> users, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                LBSUsersCallback callback = getCallback();
                if (callback != null) {
                    callback.onRefreshLBSUsers(users, errCode);
                }
            }

        });
    }

    @Override
    public void onReceiveHisLBSNearUsers(final List<LBSUser> users, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                LBSUsersCallback callback = getCallback();
                if (callback != null) {
                    callback.onReceiveHisLBSUsers(users, last, errCode);
                }
            }

        });
    }

    private LBSUsersCallback getCallback() {
        LBSUsersCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof LBSUsersCallback) {
            callback = (LBSUsersCallback)l;
        }
        return callback;
    }

}
