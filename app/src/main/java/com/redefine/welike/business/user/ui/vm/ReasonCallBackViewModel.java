package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.user.management.DeactivateManager;
import com.redefine.welike.business.user.management.bean.DeactivateReasonBean;
import com.redefine.welike.business.user.ui.listener.IReasonCallBackListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/5/8.
 */

public class ReasonCallBackViewModel extends AndroidViewModel implements IReasonCallBackListener {

    private MutableLiveData<List<DeactivateReasonBean>> mReasons = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();


    public ReasonCallBackViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<List<DeactivateReasonBean>> getmReasons() {
        return mReasons;
    }

    public MutableLiveData<PageStatusEnum> getmPageStatus() {
        return mPageStatus;
    }

    public void getReason() {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        DeactivateManager.getInstance().getReason(this);
    }


    @Override
    public void onReason(List<DeactivateReasonBean> list, int errorCode) {

        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            List<DeactivateReasonBean> data = list;
            if (data == null) {
                data = new ArrayList<>();
            }

            for (DeactivateReasonBean reasonBean : data) {
                reasonBean.setChecked(false);
            }

            if (CollectionUtil.isEmpty(data)) {
                mReasons.postValue(data);
                mPageStatus.postValue(PageStatusEnum.EMPTY);
            } else {
                mReasons.postValue(data);
                mPageStatus.postValue(PageStatusEnum.CONTENT);
            }

        } else {
            List<DeactivateReasonBean> data = mReasons.getValue();
            if (CollectionUtil.isEmpty(data)) {
                mPageStatus.postValue(PageStatusEnum.ERROR);
            } else {
                mPageStatus.postValue(PageStatusEnum.CONTENT);
            }
        }


    }
}
