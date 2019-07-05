package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.RecommendSlideRequest;
import com.redefine.welike.business.browse.management.request.RecommendUserRequest;
import com.redefine.welike.business.feeds.management.RecommendUserManager;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.management.bean.RecommendSlideUserList;
import com.redefine.welike.business.user.management.bean.RecommendTagBean;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.business.user.management.bean.RecommendUserList;

import java.util.List;

public class RecommendUserViewModel extends AndroidViewModel {

    private RecommendUserManager recommendUserManager;

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();

    private MutableLiveData<List<RecommendSlideUser>> recommedUsers = new MutableLiveData<>();

    private MutableLiveData<List<RecommendSlideUser>> recommedMoreUsers = new MutableLiveData<>();

    private MutableLiveData<List<RecommendTagBean>> recommendTagBeans = new MutableLiveData<>();

    private MutableLiveData<Boolean> hasNextPage = new MutableLiveData<>();

    private MutableLiveData<Integer> mCode = new MutableLiveData<>();

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<List<RecommendSlideUser>> getRecommedUsers() {
        return recommedUsers;
    }

    public MutableLiveData<List<RecommendSlideUser>> getRecommedMoreUsers() {
        return recommedMoreUsers;
    }

    public MutableLiveData<Boolean> getHasNextPage() {
        return hasNextPage;
    }

    public MutableLiveData<Integer> getCode() {
        return mCode;
    }

    public MutableLiveData<List<RecommendTagBean>> getRecommendTagBeans() {
        return recommendTagBeans;
    }

    public RecommendUserViewModel(@NonNull Application application) {
        super(application);
        recommendUserManager = new RecommendUserManager();
    }

    public void tryRefresh() {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        recommendUserManager.tryRefresh(getApplication(), new RecommendUserManager.OnRecommendUserListener() {
            @Override
            public void onSuccess(RecommendSlideUserList ls) {
                recommedUsers.postValue(ls.getList());
                mPageStatus.postValue(PageStatusEnum.CONTENT);
                hasNextPage.postValue(ls.getHasNextPage());
            }

            @Override
            public void onFail(int errCode) {
                mPageStatus.postValue(PageStatusEnum.ERROR);
                mCode.postValue(errCode);
            }
        });
    }

    public void tryLoadMore() {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        recommendUserManager.tryHis(getApplication(), new RecommendUserManager.OnRecommendUserListener() {
            @Override
            public void onSuccess(RecommendSlideUserList ls) {
                recommedMoreUsers.postValue(ls.getList());
                mPageStatus.postValue(PageStatusEnum.CONTENT);
                hasNextPage.postValue(ls.getHasNextPage());
            }

            @Override
            public void onFail(int errCode) {
                mPageStatus.postValue(PageStatusEnum.ERROR);
                mCode.postValue(errCode);
            }
        });
    }
}
