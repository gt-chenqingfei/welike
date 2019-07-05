package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.NickNameChecker;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.startup.management.WelikeUpdateManager;
import com.redefine.welike.business.startup.management.bean.NickNameCheckerBean;
import com.redefine.welike.business.startup.management.callback.UpdateCallback;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;

import org.jetbrains.annotations.NotNull;

/**
 * @author redefine honlin
 * @Date on 2018/10/30
 * @Description
 */
public class NickNameViewModel extends AndroidViewModel implements UpdateCallback {

    private String TAG = "honlin";

    private NickNameChecker nickNameChecker = new NickNameChecker();
    private WelikeUpdateManager updateManager;


    //CHECK nick
    private MutableLiveData<NickNameCheckerBean> nickNameBeanData = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> checkStatus = new MutableLiveData<>();


    public NickNameViewModel(@NonNull Application application) {
        super(application);
        updateManager = new WelikeUpdateManager();
    }


    public MutableLiveData<NickNameCheckerBean> getNickNameBeanData() {
        return nickNameBeanData;
    }

    public MutableLiveData<PageStatusEnum> getCheckStatus() {
        return checkStatus;
    }





    public void checkNickName(final String nick) {

        checkStatus.postValue(PageStatusEnum.LOADING);

        nickNameChecker.check2(nick, getApplication(), new NickNameChecker.NickNameCheckerCallback() {
            @Override
            public void onCheckResult(String result, int errCode) {
                NickNameCheckerBean bean;
                if (errCode == ErrorCode.ERROR_SUCCESS) {
                    Gson gson = new Gson();
                    bean = gson.fromJson(result, NickNameCheckerBean.class);
                    bean.setErrCode(errCode);
                } else {
                    bean = new NickNameCheckerBean(0, false, false, errCode);
                }
                nickNameBeanData.postValue(bean);
                checkStatus.postValue(PageStatusEnum.CONTENT);
            }
        });

    }



    @Override
    public void onUpdateCallback(@NotNull JSONObject result, int updateType, int errorCode) {
        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            Account account = AccountManager.getInstance().getAccount();
            AccountManager.getInstance().updateAccount(account);
            if (updateType == RegisteredConstant.LOGIN_UPDATE_FOLLOWS) {
                BrowseEventStore.INSTANCE.delete();
            }
        }
    }



}
