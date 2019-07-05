package com.redefine.welike.business.user.management;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.provider.IUsersProvider;
import com.redefine.welike.business.user.management.provider.UsersProviderCallback;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/6.
 */

public class UsersManager extends SingleListenerManagerBase implements UsersProviderCallback {
    private IUsersProvider contactsProvider;

    public interface UsersCallback {

        void onRefreshUsers(UsersManager manager, List<User> users, String uid, int newCount, int errCode);

        void onReceiveHisUsers(UsersManager manager, List<User> users, String uid, boolean last, int errCode);

    }

    public UsersManager() {}

    public void setListener(UsersCallback listener) {
        super.setListener(listener);
    }

    public void setDataSourceProvider(IUsersProvider provider) {
        contactsProvider = provider;
        contactsProvider.setListener(this);
    }

    public void tryRefreshContacts(String uid) {
        if (contactsProvider != null) {
            contactsProvider.tryRefreshUsers(uid);
        }
    }

    public void tryHisContacts(String uid) {
        if (contactsProvider != null) {
            contactsProvider.tryHisUsers(uid);
        }
    }

    @Override
    public void onRefreshUsers(final List<User> users, final String uid, final int newCount, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                UsersCallback callback = getCallback();
                if (callback != null) {
                    callback.onRefreshUsers(UsersManager.this, users, uid, newCount, errCode);
                }
            }

        });
    }

    @Override
    public void onReceiveHisUsers(final List<User> users, final String uid, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                UsersCallback callback = getCallback();
                if (callback != null) {
                    callback.onReceiveHisUsers(UsersManager.this, users, uid, last, errCode);
                }
            }

        });
    }

    private UsersCallback getCallback() {
        UsersCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof UsersCallback) {
            callback = (UsersCallback)l;
        }
        return callback;
    }

}
