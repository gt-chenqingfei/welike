package com.redefine.welike.business.browse.ui.viewmodel;


import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.startup.management.VerticalSuggester;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.common.BrowseManager;
import com.redefine.welike.frameworkmvvm.AndroidViewModel;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by honglin on 2018/7/9.
 * <p>
 * viewmodel
 */
public class BrowseHomeViewModel extends AndroidViewModel implements
        VerticalSuggester.IntrestsSuggesterCallback {

    private boolean isInterestRefreshFinish = false;

    private Map<String, Boolean> intrestRequestFinish = new HashMap<>();

    private VerticalSuggester intrestsSuggester;

    private boolean isBottomTarShow = true;

    public boolean isBottomTarShow() {
        return isBottomTarShow;
    }

    private MutableLiveData<Boolean> showSnakeOnMain = new MutableLiveData<>();

    public void setBottomTarShow(boolean bottomTarShow) {
        isBottomTarShow = bottomTarShow;
    }

    private MutableLiveData<List<UserBase.Intrest>> mIntrests = new MutableLiveData<>();

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<PageLoadMoreStatusEnum> mLoadMoreStatus = new MutableLiveData<>();

    public BrowseHomeViewModel(@NonNull Application application) {
        super(application);
        intrestsSuggester = new VerticalSuggester();
        intrestsSuggester.setAuth(false);
        intrestsSuggester.setListener(this);
    }

    public void autoRefresh() {
        if (BrowseManager.VIDMATE_ABTEST)
            intrestsSuggester.refresh();
        PostEventManager.INSTANCE.setAction(3);
    }

    public void refreshInterest() {
        if (BrowseManager.VIDMATE_ABTEST && !isInterestRefreshFinish)
            intrestsSuggester.refresh();
    }

    public MutableLiveData<Boolean> getShowSnakeOnMain() {
        return showSnakeOnMain;
    }

    public MutableLiveData<List<UserBase.Intrest>> getIntrests() {
        return mIntrests;
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<PageLoadMoreStatusEnum> getLoadMoreStatus() {
        return mLoadMoreStatus;
    }


    @Override
    public void onRefreshIntrestSuggestions(List<UserBase.Intrest> intrests, int errCode, ReferrerInfo info) {

        if (!CollectionUtil.isEmpty(intrests) && !isInterestRefreshFinish) {
            isInterestRefreshFinish = true;
        }
        if (intrests != null) {
            for (UserBase.Intrest item : intrests) {
                intrestRequestFinish.put(item.getIid(), false);
            }
            mIntrests.postValue(intrests);
        }
    }

    @Override
    public void onHisIntrestSuggestions(List<UserBase.Intrest> intrests, boolean last, int errCode) {

    }

}
