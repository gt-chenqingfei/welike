package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.feeds.management.UserPostsManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/8/21
 */
public class ProfilePostViewModel extends AndroidViewModel implements UserPostsManager.UserPostsCallback {

    private String mUid;
    private boolean mIsBrowse;
    private String mFeedSource;
    private UserPostsManager mUserPostsManager;

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<PageLoadMoreStatusEnum> mPageLoadMoreStatus = new MutableLiveData<>();
    private MutableLiveData<List<PostBase>> mPostLiveData = new MutableLiveData<>();

    public ProfilePostViewModel(@NonNull Application application) {
        super(application);
        mUserPostsManager = new UserPostsManager();
        mUserPostsManager.register(this);
        mIsBrowse = !AccountManager.getInstance().isLogin();

    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<List<PostBase>> getPostLiveData() {
        return mPostLiveData;
    }

    public MutableLiveData<PageLoadMoreStatusEnum> getPageLoadMoreStatus() {
        return mPageLoadMoreStatus;
    }

    public void refresh(String uid, String feedSource) {
        mUid = uid;

        this.mFeedSource = feedSource;
        mUserPostsManager.tryRefreshPosts(uid, mIsBrowse);
        mPageStatus.postValue(PageStatusEnum.LOADING);
    }

    public void loadMore() {
        mUserPostsManager.tryHisPosts(mUid, mIsBrowse);
        mPageLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOADING);
    }

    @Override
    public void onRefreshUserPosts(UserPostsManager manager, List<PostBase> posts, String uid, int newCount, int errCode) {
        if (mUserPostsManager != manager || !TextUtils.equals(uid, mUid)) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            List<PostBase> value = mPostLiveData.getValue();
            if (value == null) {
                value = new ArrayList<>();
            }
            if (posts != null) {
                value.clear();
                value.addAll(posts);
            }
            mPostLiveData.postValue(value);
            if (CollectionUtil.getCount(posts) == 0) {
                mPageStatus.postValue(PageStatusEnum.EMPTY);
            } else {
                mPageStatus.postValue(PageStatusEnum.CONTENT);
            }
        } else {
            if (CollectionUtil.getCount(posts) == 0) {
                mPageStatus.postValue(PageStatusEnum.ERROR);
            } else {
                mPageStatus.postValue(PageStatusEnum.CONTENT);
            }
        }
        mPageLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NONE);
        PostEventManager.INSTANCE.setType(mFeedSource);
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_PROFILE_POST_OWNER);
        PostEventManager.INSTANCE.sendEventStrategy(FeedHelper.subPosts(posts, newCount));
    }

    @Override
    public void onReceiveHisUserPosts(UserPostsManager manager, List<PostBase> posts, String uid, boolean last, int errCode) {
        if (mUserPostsManager != manager || !TextUtils.equals(uid, mUid)) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            List<PostBase> value = mPostLiveData.getValue();
            if (value == null) {
                value = new ArrayList<>();
            }
            if (posts != null) {
                value.addAll(posts);
            }
            mPostLiveData.postValue(value);
            if (last) {
                mPageLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
            } else {
                mPageLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH);
            }
        } else {
            mPageLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
        }
        PostEventManager.INSTANCE.setType(mFeedSource);
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_PROFILE_POST_OWNER);
        PostEventManager.INSTANCE.sendEventStrategy(posts);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mUserPostsManager.unregister(this);
    }
}
