package com.redefine.welike.business.feeds.management.provider;

import com.redefine.welike.base.profile.bean.UserBase;

import java.util.List;

public interface IInterestCategoryCallback {

    void onInterestCategorySuccess(List<UserBase.Intrest> result);

    void onInterestCategoryFail();
}
