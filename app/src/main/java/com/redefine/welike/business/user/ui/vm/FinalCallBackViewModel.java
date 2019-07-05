package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.user.management.DeactivateManager;
import com.redefine.welike.business.user.management.bean.DeactivateInfoBean;
import com.redefine.welike.business.user.ui.listener.IUserInfoCallBackListener;

/**
 * Created by honglin on 2018/5/8.
 */

public class FinalCallBackViewModel extends AndroidViewModel implements IUserInfoCallBackListener {

    private MutableLiveData<DeactivateInfoBean> deactivateInfoBeanMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();


    public FinalCallBackViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<DeactivateInfoBean> getDeactivateInfoBeanMutableLiveData() {
        return deactivateInfoBeanMutableLiveData;
    }

    public MutableLiveData<PageStatusEnum> getmPageStatus() {
        return mPageStatus;
    }

    public void getUserInfo() {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        DeactivateManager.getInstance().getInfo(this);
    }


    @Override
    public void onResult(DeactivateInfoBean result, int errorCode) {

        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            if (result != null) {
                deactivateInfoBeanMutableLiveData.postValue(result);
                mPageStatus.postValue(PageStatusEnum.CONTENT);
            } else {
                mPageStatus.postValue(PageStatusEnum.EMPTY);
            }
        } else {
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }
    }
}
