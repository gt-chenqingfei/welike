package com.redefine.welike.business.browse.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.bean.Campaign;
import com.redefine.welike.business.browse.management.bean.CampaignList;
import com.redefine.welike.business.browse.management.request.LatestCampaignRequest;

import java.util.ArrayList;

public class BrowseLatestViewModel extends AndroidViewModel {


    private boolean isLoadingCampaign = false;


    private MutableLiveData<ArrayList<Campaign>> mCompaigns = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();


    public BrowseLatestViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<ArrayList<Campaign>> getCompaigns() {
        return mCompaigns;
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }


    public void refresh() {
        mPageStatus.postValue(PageStatusEnum.LOADING);

        refreshCampaign(MyApplication.getAppContext());

    }

    private void refreshCampaign(Context context) {

        try {

            if (isLoadingCampaign) return;
            isLoadingCampaign = true;
            new LatestCampaignRequest(context).request(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    isLoadingCampaign = false;
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {

                    Gson gson = new Gson();

                    CampaignList campaignListBean = gson.fromJson(result.toJSONString(), CampaignList.class);

                    mCompaigns.postValue(campaignListBean.getList());

                    isLoadingCampaign = false;
                    mPageStatus.postValue(PageStatusEnum.CONTENT);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isLoadingCampaign = false;
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }

    }

}
