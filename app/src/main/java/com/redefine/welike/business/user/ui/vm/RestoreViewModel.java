package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.user.management.DeactivateManager;
import com.redefine.welike.business.user.ui.listener.IAccountStatusChangeListener;

/**
 * Created by honglin on 2018/5/8.
 */

public class RestoreViewModel extends AndroidViewModel implements IAccountStatusChangeListener {

    private MutableLiveData<Integer> accounyStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> errcode = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();


    public RestoreViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getAccounyStatus() {
        return accounyStatus;
    }

    public MutableLiveData<Integer> getErrcode() {
        return errcode;
    }

    public MutableLiveData<PageStatusEnum> getmPageStatus() {
        return mPageStatus;
    }

    public void restore(int type) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        DeactivateManager.getInstance().updataAccountStatus(type, this);
    }


    @Override
    public void onChanged(int status, int errorCode) {
        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            accounyStatus.postValue(status);
            mPageStatus.postValue(PageStatusEnum.CONTENT);

        } else {
            errcode.postValue(errorCode);
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }
    }
}
