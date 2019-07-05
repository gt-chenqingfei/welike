package com.redefine.welike.business.feeds.management.provider;

import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.startup.management.VerticalSuggester;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;

import java.util.List;

public class InterestCategoryRemoteStrategy implements IInterestCategoryStrategy, VerticalSuggester.IntrestsSuggesterCallback {

    private VerticalSuggester intrestsSuggester;
    private IInterestCategoryCallback callback;

    public InterestCategoryRemoteStrategy() {
        intrestsSuggester = new VerticalSuggester();
        intrestsSuggester.setListener(this);
    }
    @Override
    public void provide(IInterestCategoryCallback callback) {
        this.callback = callback;
        intrestsSuggester.refresh();
    }

    @Override
    public void onRefreshIntrestSuggestions(List<UserBase.Intrest> intrests, int errCode, ReferrerInfo info) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            callback.onInterestCategorySuccess(intrests);
        } else {
            callback.onInterestCategoryFail();
        }
    }

    @Override
    public void onHisIntrestSuggestions(List<UserBase.Intrest> intrests, boolean last, int errCode) {

    }
}
