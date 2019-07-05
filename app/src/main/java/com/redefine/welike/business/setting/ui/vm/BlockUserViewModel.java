package com.redefine.welike.business.setting.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.setting.management.provider.BlockUsersProvider;
import com.redefine.welike.business.user.management.UsersManager;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

public class BlockUserViewModel extends AndroidViewModel implements UsersManager.UsersCallback {

    private MutableLiveData<List<User>> mBlockUsers = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<PageLoadMoreStatusEnum> mLoadMoreStatus = new MutableLiveData<>();

    private UsersManager mUserManager;

    public BlockUserViewModel(@NonNull Application application) {
        super(application);
        mUserManager = new UsersManager();
        mUserManager.setDataSourceProvider(new BlockUsersProvider());
        mUserManager.setListener(this);
    }

    public LiveData<List<User>> getBlockUsers() {
        return mBlockUsers;
    }

    public LiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public LiveData<PageLoadMoreStatusEnum> getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    @Override
    public void onRefreshUsers(UsersManager manager, List<User> users, String uid, int newCount, int errCode) {
        if (mUserManager != manager) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            List<User> data = mBlockUsers.getValue();
            if (!CollectionUtil.isEmpty(users)) {
                if (data == null) {
                    data = new ArrayList<>();
                } else {
                    data.clear();
                }
                data.addAll(users);
            }
            mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.NONE);
            if (CollectionUtil.isEmpty(data)) {
                mBlockUsers.setValue(data);
                mPageStatus.setValue(PageStatusEnum.EMPTY);
            } else {
                mBlockUsers.setValue(data);
                mPageStatus.setValue(PageStatusEnum.CONTENT);
            }

        } else {
            List<User> data = mBlockUsers.getValue();
            if (CollectionUtil.isEmpty(data)) {
                mPageStatus.setValue(PageStatusEnum.ERROR);
            } else {
                mPageStatus.setValue(PageStatusEnum.CONTENT);
            }
        }
    }

    @Override
    public void onReceiveHisUsers(UsersManager manager, List<User> users, String uid, boolean last, int errCode) {
        if (mUserManager != manager) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            List<User> data = mBlockUsers.getValue();
            if (!CollectionUtil.isEmpty(users)) {
                if (data == null) {
                    data = new ArrayList<>();
                }
                data.addAll(users);
            }
            mBlockUsers.setValue(data);

            if (last) {
                mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.NO_MORE);
            } else {
                mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.FINISH);
            }
        } else {
            mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.LOAD_ERROR);
        }
    }

    public void refresh() {
        mPageStatus.setValue(PageStatusEnum.LOADING);
        mUserManager.tryRefreshContacts(AccountManager.getInstance().getAccount().getUid());
    }

    public void loadMore() {
        mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.LOADING);
        mUserManager.tryHisContacts(AccountManager.getInstance().getAccount().getUid());
    }

    public void removeBlockUser(String uid) {
        List<User> data = mBlockUsers.getValue();
        if (data == null) {
            data = new ArrayList<>();
        }
        for (User user : data) {
            if (TextUtils.equals(uid, user.getUid())) {
                data.remove(user);
                break;
            }
        }
        mBlockUsers.setValue(data);
    }
}

