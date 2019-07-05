package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.browse.management.request.RecommendGroupRequest;
import com.redefine.welike.business.startup.management.request.FollowUsersRequest;
import com.redefine.welike.business.user.management.bean.RecommendTagBean;
import com.redefine.welike.business.user.management.bean.RecommendUserListWithTag;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import java.util.List;

public class RecommendUserViewModel1 extends AndroidViewModel {

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();

    private MutableLiveData<List<RecommendTagBean>> recommendTagBeans = new MutableLiveData<>();

    private MutableLiveData<Integer> mCode = new MutableLiveData<>();

    public RecommendUserViewModel1(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<Integer> getCode() {
        return mCode;
    }

    public MutableLiveData<List<RecommendTagBean>> getRecommendTagBeans() {
        return recommendTagBeans;
    }


    public void tryRefreshRecommendWithTag() {

        try {
            mPageStatus.postValue(PageStatusEnum.LOADING);
            new RecommendGroupRequest(getApplication()).req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                    mCode.postValue(errCode);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    Gson gson = new Gson();
                    RecommendUserListWithTag recommendUserListWithTag = gson.fromJson(result.toString(), RecommendUserListWithTag.class);
                    recommendTagBeans.postValue(recommendUserListWithTag.getList());
                    mPageStatus.postValue(PageStatusEnum.CONTENT);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }

    }


    public void reqFollowUsers(final List<String> followUids) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        FollowUsersRequest request = new FollowUsersRequest(followUids, MyApplication.getAppContext());
        try {
            request.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                    mCode.postValue(errCode);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    mPageStatus.postValue(PageStatusEnum.CONTENT);
                    mCode.postValue(ErrorCode.ERROR_SUCCESS);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
