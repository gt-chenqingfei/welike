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
import com.redefine.welike.business.feeds.management.SearchManager;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/5/15
 */
public class BlockSearchUserViewModel extends AndroidViewModel implements SearchManager.SearchManagerListener {

    private static final boolean NEED_AUTH = true;

    private MutableLiveData<List<User>> mBlockUsers = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<PageLoadMoreStatusEnum> mLoadMoreStatus = new MutableLiveData<>();

    private SearchManager mUserManager;

    public BlockSearchUserViewModel(@NonNull Application application) {
        super(application);
        mUserManager = new SearchManager();
        mUserManager.setSearchType(SearchManager.SEARCH_MANAGER_TYPE_USERS);
        mUserManager.register(this);
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
    public void onNewSearchResult(List<Object> contents, int searchType, int errCode) {
        if(errCode == ErrorCode.ERROR_SUCCESS) {
            List<User> users = fileType(contents);
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
    public void onMoreSearchResult(List<Object> contents, int searchType, boolean last, int errCode) {
        if(errCode == ErrorCode.ERROR_SUCCESS) {
            List<User> users = fileType(contents);
            List<User> data = mBlockUsers.getValue();
            if (!CollectionUtil.isEmpty(users)) {
                if (data == null) {
                    data = new ArrayList<>();
                }
                data.addAll(users);
                mBlockUsers.setValue(data);
            }

            if (last) {
                mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.NO_MORE);
            } else {
                mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.FINISH);
            }
        } else {
            mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.LOAD_ERROR);
        }
    }

    private List<User> fileType(List<Object> contents) {
        List<User> users = new ArrayList<>();
        if (CollectionUtil.isEmpty(contents)) {
            return users;
        }
        for (Object o : contents) {
            if (o instanceof User) {
                users.add((User) o);
            }
        }
        return users;
    }

    public void refresh(String text) {
//        mPageStatus.setValue(PageStatusEnum.LOADING);
        mUserManager.search(text, NEED_AUTH);
    }

    public void loadMore() {
        mLoadMoreStatus.setValue(PageLoadMoreStatusEnum.LOADING);
        mUserManager.loadMore(NEED_AUTH);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        mUserManager.unregister(this);
    }

}
