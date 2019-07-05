package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.PatternUtils;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.constant.UserConstant;

/**
 * Created by nianguowang on 2018/8/16
 */
public class UserSocialViewModel extends AndroidViewModel implements AccountManager.AccountCallback {

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();

    public UserSocialViewModel(@NonNull Application application) {
        super(application);
        AccountManager.getInstance().register(this);
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public void addLink(int type, String url) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        AccountManager.getInstance().addAccountLink(type, url);
    }

    public void updateLink(User.Link link) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        if (link == null) {
            mPageStatus.postValue(PageStatusEnum.ERROR);
            return;
        }
        AccountManager.getInstance().updateAccountLink(link.getLinkId(), link.getLinkType(), link.getLink());
    }

    public boolean validUrl(String url) {
        return PatternUtils.isUrl(url);
    }

    public boolean matchSocialHost(String url, int socialType) {
        String socialHost = UserConstant.USER_SOCIAL_HOST_FACEBOOK;
        if (socialType == UserConstant.USER_SOCIAL_LINK_YOUTUBE) {
            socialHost = UserConstant.USER_SOCIAL_HOST_YOUTUBE;
        } else if (socialType == UserConstant.USER_SOCIAL_LINK_INSTAGRAM) {
            socialHost = UserConstant.USER_SOCIAL_HOST_INSTAGRAM;
        } else if (socialType == UserConstant.USER_SOCIAL_LINK_FACEBOOK) {
            socialHost = UserConstant.USER_SOCIAL_HOST_FACEBOOK;
        }
        try {
            Uri parse = Uri.parse(url);
            String host = parse.getHost();
            return host.toLowerCase().contains(socialHost);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onModified() {
        mPageStatus.postValue(PageStatusEnum.CONTENT);
    }

    @Override
    public void onModifyFailed(int errCode) {
        mPageStatus.postValue(PageStatusEnum.ERROR);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AccountManager.getInstance().unregister(this);
    }
}
