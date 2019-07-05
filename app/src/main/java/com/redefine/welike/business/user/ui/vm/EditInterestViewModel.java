package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.business.user.management.IntrestsManager;
import com.redefine.welike.business.user.management.UserDetailManager;
import com.redefine.welike.business.user.management.bean.Interest;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/5/8.
 */

public class EditInterestViewModel extends AndroidViewModel implements IntrestsManager.IntrestsSuggesterCallback, UserDetailManager.UserDetailCallback {

    private MutableLiveData<ArrayList<Interest>> mInterst = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<Account> mAccount = new MutableLiveData<>();

    private IntrestsManager mModel;
    private Account account;
    private UserDetailManager userDetailManager;

    public EditInterestViewModel(@NonNull Application application) {
        super(application);
        mModel = new IntrestsManager();
        mModel.setListener(this);
        account = AccountManager.getInstance().getAccount();
        userDetailManager = new UserDetailManager();
        userDetailManager.setDetailListener(this);
    }


    public MutableLiveData<ArrayList<Interest>> getmInterst() {
        return mInterst;
    }

    public MutableLiveData<Account> getmAccount() {
        return mAccount;
    }

    public MutableLiveData<PageStatusEnum> getmPageStatus() {
        return mPageStatus;
    }

    public void refresh() {
        getPersonalInfo();
    }

    private void getPersonalInfo() {
        mPageStatus.setValue(PageStatusEnum.LOADING);
        userDetailManager.loadContactDetail(account.getUid());
    }


    public void getInterest() {
        mPageStatus.setValue(PageStatusEnum.LOADING);
        mModel.refreshProfile();
    }


    @Override
    public void onRefreshIntrestSuggestions(ArrayList<Interest> intrests, int errCode, ReferrerInfo info) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            ArrayList<Interest> data = intrests;
            if (data == null) {
                data = new ArrayList<>();
            }

            for (Interest intrest : data) {
                intrest.setChecked(0);
            }

            if (account != null && account.getIntrests() != null)
                initInterest(data);

            if (CollectionUtil.isEmpty(data)) {
                mInterst.setValue(data);
                mPageStatus.setValue(PageStatusEnum.EMPTY);
            } else {
                mInterst.setValue(data);
                mPageStatus.setValue(PageStatusEnum.CONTENT);
            }

        } else {
            List<Interest> data = mInterst.getValue();
            if (CollectionUtil.isEmpty(data)) {
                mPageStatus.setValue(PageStatusEnum.ERROR);
            } else {
                mPageStatus.setValue(PageStatusEnum.CONTENT);
            }
        }
    }

    public void initInterest(ArrayList<Interest> data) {
        for (Interest intrest : data) {
            ArrayList<Interest> subset = intrest.getSubset();
            if (subset != null && subset.size() > 0) {
                initInterest(intrest.getSubset());
            }
            for (UserBase.Intrest intrest1 : account.getIntrests()) {

                if (intrest1.getSubInterest() != null && intrest1.getSubInterest().size() > 0) {
                    for (UserBase.Intrest intrest2 : intrest1.getSubInterest()) {
                        if (TextUtils.equals(intrest.getId(), (intrest2.getIid()))) {
                            intrest.setSelected(true);
                            break;
                        }
                    }
                }

                if (TextUtils.equals(intrest.getId(), (intrest1.getIid()))) {
                    intrest.setSelected(true);
                    break;
                }
            }
        }
    }


    @Override
    public void onContactDetailCompleted(UserDetailManager manager, User user, int errCode) {

        if (manager == userDetailManager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                Account account = AccountManager.getInstance().getAccount();
                if (TextUtils.equals(account.getUid(), user.getUid())) {
                    mAccount.postValue(account);
//                    mModel.refresh();
                }
            } else {
                mPageStatus.setValue(PageStatusEnum.ERROR);
            }
        }
    }
}
