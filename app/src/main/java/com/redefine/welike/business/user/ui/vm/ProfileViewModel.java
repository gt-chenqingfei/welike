package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.redefine.commonui.enums.BlockStatusEnum;
import com.redefine.commonui.enums.FollowStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.management.UserDetailManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by nianguowang on 2018/8/21
 */
public class ProfileViewModel extends AndroidViewModel implements UserDetailManager.UserDetailCallback, FollowUserManager.FollowUserCallback, BlockUserManager.BlockUserCallback, AccountManager.AccountCallback {

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<BlockStatusEnum> mBlockStatus = new MutableLiveData<>();
    private MutableLiveData<FollowStatusEnum> mFollowStatus = new MutableLiveData<>();
    private MutableLiveData<User> mUser = new MutableLiveData<>();

    private UserDetailManager mUserDetailManager;
    private boolean mAuth;
    private String mUid;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        mUserDetailManager = new UserDetailManager();
        mUserDetailManager.setDetailListener(this);
        FollowUserManager.getInstance().register(this);
        BlockUserManager.getInstance().register(this);
        AccountManager.getInstance().register(this);
        mAuth = AccountManager.getInstance().isLogin();
}

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    public MutableLiveData<BlockStatusEnum> getBlockStatus() {
        return mBlockStatus;
    }

    public MutableLiveData<FollowStatusEnum> getFollowStatus() {
        return mFollowStatus;
    }

    public void loadUser(String uid) {
        mUid = uid;

        mPageStatus.postValue(PageStatusEnum.LOADING);
        if (mAuth) {
            mUserDetailManager.loadContactDetail(uid);
        } else {
            mUserDetailManager.loadContactDetail4H5(uid);
        }
    }

    public void blockUser() {
        mBlockStatus.postValue(BlockStatusEnum.LOADING);
        BlockUserManager.getInstance().block(mUid);
    }

    public void unblockUser() {
        mBlockStatus.postValue(BlockStatusEnum.LOADING);
        BlockUserManager.getInstance().unBlock(mUid);
    }

    public void followUser() {
        mFollowStatus.postValue(FollowStatusEnum.LOADING);
        FollowUserManager.getInstance().follow(mUid, new FollowEventModel(EventConstants.FEED_PAGE_PROFILE, null));
    }

    public void unfollowUser() {
        mFollowStatus.postValue(FollowStatusEnum.LOADING);
        FollowUserManager.getInstance().unfollow(mUid, new FollowEventModel(EventConstants.FEED_PAGE_PROFILE, null));
    }

    @Override
    public void onContactDetailCompleted(UserDetailManager manager, User user, int errCode) {
        if (mUserDetailManager != manager) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            mUser.postValue(user);
        } else {
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }
        Account account = AccountManager.getInstance().getAccount();
        if (account != null && user != null) {
            if (TextUtils.equals(account.getUid(), user.getUid())) {
                account.setNickName(user.getNickName());
                account.setHeadUrl(user.getHeadUrl());
                account.setPostsCount(user.getPostsCount());
                account.setAllowUpdateNickName(user.isAllowUpdateNickName());
                account.setAllowUpdateSex(user.isAllowUpdateSex());
                account.setNextUpdateNickNameDate(user.getNextUpdateNickNameDate());
                account.setSexUpdateCount(user.getSexUpdateCount());
                account.setFollowUsersCount(user.getFollowUsersCount());
                account.setFollowedUsersCount(user.getFollowedUsersCount());
                account.setIntroduction(user.getIntroduction());
                account.setSex(user.getSex());
                account.setVip(user.getVip());
                account.setLinks(user.getLinks());
                account.setCurLevel(user.getCurLevel());
                account.setChangeNameCount(user.getChangeNameCount());
                AccountManager.getInstance().updateAccount(account);
            }
        }
    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (!TextUtils.equals(uid, mUid)) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mFollowStatus.postValue(FollowStatusEnum.FOLLOW_SUCCESS);
        } else {
            mFollowStatus.postValue(FollowStatusEnum.FOLLOW_FAIL);
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (!TextUtils.equals(uid, mUid)) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mFollowStatus.postValue(FollowStatusEnum.UNFOLLOW_SUCCESS);
        } else {
            mFollowStatus.postValue(FollowStatusEnum.UNFOLLOW_FAIL);
        }
    }

    @Override
    public void onBlockCompleted(String uid, int errCode) {
        if (!TextUtils.equals(uid, mUid)) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mBlockStatus.postValue(BlockStatusEnum.BLOCK_SUCCESS);
        } else {
            mBlockStatus.postValue(BlockStatusEnum.BLOCK_FAIL);
        }
    }

    @Override
    public void onUnBlockCompleted(String uid, int errCode) {
        if (!TextUtils.equals(uid, mUid)) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mBlockStatus.postValue(BlockStatusEnum.UNBLOCK_SUCCESS);
        } else {
            mBlockStatus.postValue(BlockStatusEnum.UNBLOCK_FAIL);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mUserDetailManager.setDetailListener(null);
        BlockUserManager.getInstance().unregister(this);
        FollowUserManager.getInstance().unregister(this);
    }

    @Override
    public void onModified() {
        mUserDetailManager.loadContactDetail(mUid);
    }

    @Override
    public void onModifyFailed(int errCode) {

    }
}
